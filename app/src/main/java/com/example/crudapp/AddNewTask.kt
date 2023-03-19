package com.example.crudapp

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class AddNewTask : BottomSheetDialogFragment() {
    private var setDueDate: TextView? = null
    private var mTaskEdit: EditText? = null
    private var mSaveBtn: Button? = null
    private var firestore: FirebaseFirestore? = null
    private var context: Context? = null
    private var dueDate = ""
    private var id: String? = ""
    private var dueDateUpdate: String? = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_new_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDueDate = view.findViewById(R.id.set_due_tv)
        mTaskEdit = view.findViewById(R.id.task_edittext)
        mSaveBtn = view.findViewById(R.id.save_btn)
        firestore = FirebaseFirestore.getInstance()
        var isUpdate = false
        val bundle = arguments
        if (bundle != null) {
            isUpdate = true
            val task = bundle.getString("task")
            id = bundle.getString("id")
            dueDateUpdate = bundle.getString("due")
            mTaskEdit!!.setText(task)
            setDueDate!!.setText(dueDateUpdate)
            if (task!!.length > 0) {
                mSaveBtn!!.setEnabled(false)
                mSaveBtn!!.setBackgroundColor(Color.GRAY)
            }
        }
        mTaskEdit!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString() == "") {
                    mSaveBtn!!.setEnabled(false)
                    mSaveBtn!!.setBackgroundColor(Color.GRAY)
                } else {
                    mSaveBtn!!.setEnabled(true)
                    mSaveBtn!!.setBackgroundColor(resources.getColor(R.color.green_blue))
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        setDueDate!!.setOnClickListener(View.OnClickListener {
            val calendar = Calendar.getInstance()
            val MONTH = calendar[Calendar.MONTH]
            val YEAR = calendar[Calendar.YEAR]
            val DAY = calendar[Calendar.DATE]
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { view, year, month, dayOfMonth ->
                    var month = month
                    month = month + 1
                    setDueDate!!.setText("$dayOfMonth/$month/$year")
                    dueDate = "$dayOfMonth/$month/$year"
                }, YEAR, MONTH, DAY
            )
            datePickerDialog.show()
        })
        val finalIsUpdate = isUpdate
        mSaveBtn!!.setOnClickListener(View.OnClickListener {
            val task = mTaskEdit!!.getText().toString()
            if (finalIsUpdate) {
                firestore!!.collection("task").document(id!!).update("task", task, "due", dueDate)
                Toast.makeText(context, "Task Updated", Toast.LENGTH_SHORT).show()
            } else {
                if (task.isEmpty()) {
                    Toast.makeText(context, "Empty task not Allowed !!", Toast.LENGTH_SHORT).show()
                } else {
                    val taskMap: MutableMap<String, Any> = HashMap()
                    taskMap["task"] = task
                    taskMap["due"] = dueDate
                    taskMap["status"] = 0
                    taskMap["time"] = FieldValue.serverTimestamp()
                    firestore!!.collection("task").add(taskMap).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, "Task Saved", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(
                                context,
                                task.exception!!.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }.addOnFailureListener { e ->
                        Toast.makeText(
                            context,
                            e.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            dismiss()
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        val activity: Activity? = activity
        if (activity is OnDialogCloseListner) {
            (activity as OnDialogCloseListner?)?.onDialogClose(dialog)
        }
    }

    companion object {
        const val TAG = "AddNewTask"
        fun newInstance(): AddNewTask {
            return AddNewTask()
        }
    }
}
