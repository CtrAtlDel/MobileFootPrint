package com.ivankvasov.dg

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var file: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        file = File(this.filesDir, "time.txt")  // Create a new file to store the time data

        Timer().schedule(object :
            TimerTask() {  // Start a timer to write the current time to the file every minute
            override fun run() {
                val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
                file.appendText("$currentTime\n")
            }
        }, 0, 60000)
    }


}