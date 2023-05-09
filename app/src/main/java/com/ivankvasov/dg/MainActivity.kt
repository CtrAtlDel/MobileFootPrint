package com.ivankvasov.dg

import android.app.ActivityManager
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var file: File
    private var handler: Handler? = null

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

                val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
                val runningProcesses = activityManager.runningAppProcesses.size

                val memoryInfo = ActivityManager.MemoryInfo()

                // Get memory info
                activityManager.getMemoryInfo(memoryInfo)

                // Print memory info
//                println("Total memory: ${memoryInfo.totalMem}")
                val availMem = memoryInfo.availMem
                val threshold = memoryInfo.threshold

                file.appendText("Time: $currentTime\n") // Get time
                file.appendText("Core numbers: $numCores\n") // Get Number of cores
                file.appendText("Orientation: $orientationString\n") // Get orientation of device
                file.appendText("Running process: $runningProcesses\n") // Get orientation of device
                file.appendText("Available memory: $availMem\n") // Get orientation of device
                file.appendText("Treshhold memory: $threshold\n") // Get orientation of device


            }
        }, 0, 60000)
    }

}