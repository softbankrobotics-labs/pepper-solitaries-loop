package com.softbankrobotics.dx.solitariesloop

import android.os.Bundle
import android.util.Log
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.aldebaran.qi.sdk.design.activity.RobotActivity
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayStrategy
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : RobotActivity(), RobotLifecycleCallbacks {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var qiContext: QiContext

    private lateinit var solitariesLoop: SolitariesLoop

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        QiSDK.register(this, this)
        setContentView(R.layout.activity_main)
        setSpeechBarDisplayStrategy(SpeechBarDisplayStrategy.IMMERSIVE)
    }

    override fun onRobotFocusGained(qiContext: QiContext) {
        Log.i(TAG, "onRobotFocusGained")
        this.qiContext = qiContext

        // Build the solitaries loop (60 sec. delay by default)
        solitariesLoop = SolitariesLoop(qiContext)

        // Look for humans around
        val activity = this
        qiContext.humanAwareness.addOnHumansAroundChangedListener {
            Log.d(TAG, "onHumansAroundChangedListener: ${it.size}")
            if (it.size == 0) {
                // When no human is around
                solitariesLoop.start()
                activity.runOnUiThread {
                    textView.text = getString(R.string.come)
                }
            } else {
                // When a human is detected
                solitariesLoop.stop()
                activity.runOnUiThread {
                    textView.text = getString(R.string.hello)
                }
            }
        }

        Log.d(TAG, "Humans around at start: ${qiContext.humanAwareness.humansAround.size}")
        // If no human is engaged by the robot at app launch, start the loop
        if (qiContext.humanAwareness.humansAround.size == 0) {
            solitariesLoop.start()
            runOnUiThread {
                textView.text = getString(R.string.come)
            }
        }
    }

    override fun onRobotFocusLost() {
        Log.i(TAG, "onRobotFocusLost")
        solitariesLoop.stop()
        qiContext.humanAwareness.removeAllOnHumansAroundChangedListeners()
    }

    override fun onRobotFocusRefused(reason: String?) {
        Log.e(TAG, "onRobotFocusRefused: $reason")
    }

    override fun onDestroy() {
        super.onDestroy()
        QiSDK.unregister(this, this)
    }
}
