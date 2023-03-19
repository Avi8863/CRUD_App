package com.example.crudapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val actionBar = supportActionBar
        actionBar!!.hide()

        val handler = Handler()
        handler.postDelayed({
            startActivity(Intent(this@Splash, MainActivity::class.java))
            finish()
        }, 4000)
    }
}