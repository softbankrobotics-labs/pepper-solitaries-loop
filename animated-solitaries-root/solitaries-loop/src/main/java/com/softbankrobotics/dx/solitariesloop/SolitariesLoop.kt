package com.softbankrobotics.dx.solitariesloop

import com.aldebaran.qi.Future
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.builder.AnimateBuilder
import com.aldebaran.qi.sdk.builder.AnimationBuilder
import com.aldebaran.qi.sdk.util.FutureUtils
import java.util.concurrent.TimeUnit

/**
 * An animated solitary loop player, that can be started and stopped.
 *
 * @property delayInSeconds The time between animations, in seconds.
 */
class SolitariesLoop(private val qiContext: QiContext, private val delayInSeconds: Int = 60) {

    private val animationNames = arrayOf(
        "CheckLeft_01.qianim",
        "CheckRight_01.qianim",
        "Funny_01.qianim",
        "Funny_02.qianim",
        "LookAtSidesLeft_01.qianim",
        "LookAtSidesRight_01.qianim",
        "LookBumpersLeft_01.qianim",
        "LookBumpersRight_01.qianim",
        "LookFarLeft_01.qianim",
        "LookFarRight_01.qianim",
        "LookHandLeft_01.qianim",
        "LookHandRight_01.qianim",
        "Looking_around_01.qianim",
        "Looking_around_wide_01.qianim",
        "LookLeft_01.qianim",
        "LookRight_01.qianim",
        "MotorSoundLeft_01.qianim",
        "MotorSoundRight_01.qianim",
        "PlayWithHandLeft_01.qianim",
        "PlayWithHandRight_01.qianim"
    )
    private var animationNamesQueue = mutableListOf<String>()
    private var lastAnimationName = ""

    private lateinit var animationFuture: Future<Void>

    private fun buildAndRunAnimate(): Future<Void> {
        return FutureUtils.wait(delayInSeconds.toLong(), TimeUnit.SECONDS)
            .andThenCompose {
                lastAnimationName = chooseAnimationName()
                AnimationBuilder.with(qiContext)
                    .withAssets(lastAnimationName)
                    .buildAsync()
            }
            .andThenCompose { animation ->
                AnimateBuilder.with(qiContext)
                    .withAnimation(animation)
                    .buildAsync()
            }
            .andThenCompose { animate ->
                animate.async().run()
            }
            .thenCompose {
                if (it.isCancelled) {
                    buildAndRunAnimate()
                } else {
                    FutureUtils.wait(0, TimeUnit.NANOSECONDS)
                }
            }
    }

    private fun chooseAnimationName(): String {
        if (animationNamesQueue.isEmpty()) {
            animationNamesQueue = animationNames.toMutableList()
            animationNamesQueue.shuffle()
            // Sometimes the first item in the new list happens to be the last one we played
            // We want to avoid duplicate animations, so:
            if (animationNamesQueue[0] == lastAnimationName) {
                // Swap with the last item
                val lastIndex = animationNamesQueue.size - 1
                animationNamesQueue[0] = animationNamesQueue[lastIndex]
                animationNamesQueue[lastIndex] = lastAnimationName
            }
        }

        return animationNamesQueue.removeAt(0)
    }

    /**
     * Starts periodically playing random animations, until stopped.
     */
    fun start() {
        animationFuture = buildAndRunAnimate()
    }

    /**
     * Stops the loop and cancels the current animation (if any); returns a future (that will finish
     * when the animation has effectively been stopped.
     */
    fun stop(): Future<Void> {
        animationFuture.requestCancellation()
        return animationFuture
    }
}