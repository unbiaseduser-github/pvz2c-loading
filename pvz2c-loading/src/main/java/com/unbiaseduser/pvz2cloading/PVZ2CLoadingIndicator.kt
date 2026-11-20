@file:JvmName("PVZ2CLoadingIndicator")

package com.unbiaseduser.pvz2cloading

import android.animation.ValueAnimator
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.math.cos
import kotlin.math.sin

fun getPvZ2CLoadingIndicatorAnimation(
    spinnerView: View,
    orbitRadiusPx: Float = 4f * spinnerView.resources.displayMetrics.density,
    durationMillis: Long = 1000L,
    angleOffsetForOrbit: Double = -120.0
): ValueAnimator {
    return ValueAnimator.ofFloat(0f, 360f).apply {
        duration = durationMillis
        interpolator = LinearInterpolator()
        repeatCount = ValueAnimator.INFINITE

        addUpdateListener { animator ->
            val angle = animator.animatedValue as Float
            val radians = Math.toRadians(angle.toDouble() + angleOffsetForOrbit)

            spinnerView.rotation = angle
            spinnerView.translationX = orbitRadiusPx * cos(radians).toFloat()
            spinnerView.translationY = orbitRadiusPx * sin(radians).toFloat()
        }
    }
}