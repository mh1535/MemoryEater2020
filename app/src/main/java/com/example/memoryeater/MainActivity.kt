package com.example.memoryeater

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*

/**
 * This version of the app is a bit different from the version shown in the course. There are
 * now two versions: this one in the master branch uses the legacy AsyncTask architecture to
 * add items to memory in a background thread. Switch to the "coroutines" branch for a version
 * that uses Kotlin coroutines.
 *
 * VCS > Git > Branches, then check out the coroutines branch from the remote repo
 *
 * Try profiling memory usage with both versions and see if there's a difference.
 */
class MainActivity : AppCompatActivity() {

    private val mData = mutableListOf<String>()
    private var eater: EaterClass? = null
    private lateinit var tvOut: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        eat_button.setOnClickListener { eatMemory() }
        release_button.setOnClickListener { releaseMemory() }

        tvOut = findViewById(R.id.tvOut)
        updateDisplay()
    }

    override fun onDestroy() {
        eater?.cancel(true)
        super.onDestroy()
    }

    private fun eatMemory() {
        eater?.cancel(true)
        eater = EaterClass()
        eater?.execute()
    }

    private fun releaseMemory() {
        eater?.cancel(true)
        Thread.sleep(1000)
        mData.clear()
        updateDisplay()
    }

    private fun updateDisplay() {
        val report = "${getString(R.string.report_label)}: ${mData.size}"
        tvOut.text = report
    }

    @SuppressLint("StaticFieldLeak")
    inner class EaterClass : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg params: Void?): Void? {
            for (i in 1..1000) {

                // Add 10,000 items to the collection
                for (j in 1..10000) {
                    mData.add("Item $i:$j")
                }

                // Send a message to the UI thread
                publishProgress()

                // Wait half a second, then do it again if not cancelled
                Thread.sleep(500)
                if (isCancelled) break

            }
            return null
        }

        override fun onProgressUpdate(vararg values: Void?) {
            updateDisplay()
        }

    }

}
