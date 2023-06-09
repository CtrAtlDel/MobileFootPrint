package com.ivankvasov.dg

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.BatteryManager
import android.os.Bundle
import android.os.Debug
import android.support.v7.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    private val FILE_NAME = "statistic.txt"
    private val SEPARATOR = "=================================================\n"
    private lateinit var file: File
    private val cpuInfo = HashMap<String, Long>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var fos: FileOutputStream? = null

        fos = FileOutputStream(getExternalPath())

        file = File(this.filesDir, "time.txt")  // Create a new file to store the time data

        val orientation = resources.configuration.orientation

        Timer().schedule(object :
            TimerTask() {  // Start a timer to write the current time to the file every minute
            override fun run() {
                fos.write(SEPARATOR.toByteArray())
                file.appendText("=================================================\n")

                val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()) // Get time

                val numCores = Runtime.getRuntime().availableProcessors() // Number of cores

                val orientationString =  // Get orientation
                    if (orientation == Configuration.ORIENTATION_PORTRAIT) "Portrait" else "Landscape"

                val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
                val runningProcesses = activityManager.runningAppProcesses.size

                val memoryInfo = ActivityManager.MemoryInfo()

                activityManager.getMemoryInfo(memoryInfo)

                val availMem = memoryInfo.availMem
                val threshold = memoryInfo.threshold
                val totalMem = memoryInfo.totalMem

                val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
                val temperatureSensor =
                    sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)

                file.appendText("Time: $currentTime\n") // Get time
                fos.write("Time: $currentTime\n".toByteArray())
                file.appendText("Core numbers: $numCores\n") // Get Number of cores
                fos.write("Core numbers: $numCores\n".toByteArray())
                file.appendText("Orientation: $orientationString\n") // Get orientation of device
                fos.write("Orientation: $orientationString\n".toByteArray())
                file.appendText("Running process: $runningProcesses\n") // Get orientation of device
                fos.write("Running process: $runningProcesses\n".toByteArray())
                file.appendText("Available memory: $availMem\n") // Get orientation of device
                fos.write("Available memory: $availMem\n".toByteArray())
                file.appendText("Threshold memory: $threshold\n") // Get orientation of device
                fos.write("Threshold memory: $threshold\n".toByteArray())
                file.appendText("Total memory: $totalMem\n") // Get orientation of device
                fos.write("Total memory: $totalMem\n".toByteArray())

                var temperature: Float
                if (temperatureSensor == null) {
                    // The device doesn't have a temperature sensor
                } else {
                    // The device has a temperature sensor
                    val sensorEventListener = object : SensorEventListener {
                        override fun onSensorChanged(event: SensorEvent?) {
                            if (event != null) {
                                var tempere = event.values[0]
                                temperature = tempere
                                file.appendText("Temperature: $temperature\n") // Get orientation of device
                                fos.write("Temperature: $temperature\n".toByteArray())
                            }
                        }

                        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                        }
                    }

                    sensorManager.registerListener(
                        sensorEventListener,
                        temperatureSensor,
                        SensorManager.SENSOR_DELAY_NORMAL
                    )
                }

                val availableCores =
                    Runtime.getRuntime().availableProcessors() // Max and min frequency
                for (i in 0 until availableCores) {
                    val maxFreqFile = File("/sys/devices/system/cpu/cpu$i/cpufreq/cpuinfo_max_freq")
                    val minFreqFile = File("/sys/devices/system/cpu/cpu$i/cpufreq/cpuinfo_min_freq")

                    if (maxFreqFile.exists() && minFreqFile.exists()) {
                        val maxFreq = maxFreqFile.readText().trim().toDouble() / 1000
                        val minFreq = minFreqFile.readText().trim().toDouble() / 1000

                        file.appendText("CPU Info: Core $i - Max Freq: $maxFreq GHz, Min Freq: $minFreq GHz\n")
                        fos.write("CPU Info: Core $i - Max Freq: $maxFreq GHz, Min Freq: $minFreq GHz\n".toByteArray())
                    }
                }

                activityManager.getMemoryInfo(memoryInfo)
                val usedMemory = (memoryInfo.totalMem - memoryInfo.availMem) / (1024 * 1024)
                val totalMemory = memoryInfo.totalMem / (1024 * 1024)
                file.appendText("Memory Info: Used memory: $usedMemory MB, Total memory: $totalMemory MB\n")
                fos.write("Memory Info: Used memory: $usedMemory MB, Total memory: $totalMemory MB\n".toByteArray())

                val batteryStatus =
                    registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

                val batteryLevel = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val batteryScale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                val batteryPercentage =
                    batteryLevel?.times(100)?.div(batteryScale?.toFloat() ?: 100f)

                val batteryStatusInt = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                val isCharging = batteryStatusInt == BatteryManager.BATTERY_STATUS_CHARGING ||
                        batteryStatusInt == BatteryManager.BATTERY_STATUS_FULL

                val batteryHealthInt = batteryStatus?.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)
                val batteryHealth = when (batteryHealthInt) {
                    BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
                    BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat"
                    BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
                    BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
                    BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "Unspecified Failure"
                    else -> "Unknown"
                }

                file.appendText("Battery Info: Level: $batteryPercentage%, Charging: $isCharging, Health: $batteryHealth\n")
                fos.write("Battery Info: Level: $batteryPercentage%, Charging: $isCharging, Health: $batteryHealth\n".toByteArray())
                val cpuStats = Debug.threadCpuTimeNanos()
                file.appendText("Cpu percent load: $cpuStats\n")
                fos.write("Cpu percent load: $cpuStats\n".toByteArray())
            }
        }, 0, 60000)
    }

    fun getExternalPath(): File? {
        return File(getExternalFilesDir(null), FILE_NAME)
    }
}