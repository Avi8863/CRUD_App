package com.example.crudapp

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.crudapp.adapter.ToDoAdapter
import com.example.crudapp.model.ToDoModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.*

class MainActivity : AppCompatActivity(), OnDialogCloseListner {
    private var recyclerView: RecyclerView? = null
    private var mFab: FloatingActionButton? = null
    private var firestore: FirebaseFirestore? = null
    private var adapter: ToDoAdapter? = null
    private var mList: MutableList<ToDoModel>? = null
    private var query: Query? = null
    private var listenerRegistration: ListenerRegistration? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.recycerlview)
        mFab = findViewById(R.id.floatingActionButton)
        firestore = FirebaseFirestore.getInstance()
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.setLayoutManager(LinearLayoutManager(this@MainActivity))
        mFab!!.setOnClickListener(View.OnClickListener {
            AddNewTask.newInstance().show(supportFragmentManager, AddNewTask.TAG)
        })
        mList = ArrayList<ToDoModel>()
        adapter = ToDoAdapter(this@MainActivity, mList!!)

        showData()
        recyclerView!!.setAdapter(adapter)
    }

    private fun showData() {
        query = firestore!!.collection("task").orderBy("time", Query.Direction.DESCENDING)
        listenerRegistration = query!!.addSnapshotListener { value, error ->
            for (documentChange in value!!.documentChanges) {
                if (documentChange.type == DocumentChange.Type.ADDED) {
                    val id = documentChange.document.id
                    val toDoModel: ToDoModel =
                        documentChange.document.toObject(ToDoModel::class.java).withId(id)
                    mList!!.add(toDoModel)
                    adapter?.notifyDataSetChanged()
                }
            }
            listenerRegistration!!.remove()
        }
    }

    override fun onDialogClose(dialogInterface: DialogInterface?) {
        mList!!.clear()
        showData()
        adapter?.notifyDataSetChanged()
    }
}