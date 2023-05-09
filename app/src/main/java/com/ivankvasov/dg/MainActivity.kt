package com.ivankvasov.dg

import android.content.res.Configuration
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Flow


class MainActivity : AppCompatActivity() {
    private lateinit var file: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        file = File(this.filesDir, "time.txt")  // Create a new file to store the time data

        Timer().schedule(object :
            TimerTask() {  // Start a timer to write the current time to the file every minute
            override fun run() {

                val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()) // Get time

                val numCores = Runtime.getRuntime().availableProcessors() // Number of cores

                val orientation = resources.configuration.orientation // Get orientation
                val orientationString =
                    if (orientation == Configuration.ORIENTATION_PORTRAIT) "Portrait" else "Landscape"

                file.appendText("Time: $currentTime\n") // Get time
                file.appendText("Core numbers: $numCores\n") // Get Number of cores
                file.appendText("Orientation: $orientationString\n") // Get orientation of device

            }
        }, 0, 60000)
    }


}