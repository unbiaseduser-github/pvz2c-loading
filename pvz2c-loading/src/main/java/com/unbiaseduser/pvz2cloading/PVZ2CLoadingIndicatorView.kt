package com.unbiaseduser.pvz2cloading

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class PVZ2CLoadingIndicatorView : AppCompatImageView {

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val animator: ValueAnimator

    init {
        setImageDrawable(PVZ2CLoadingIndicatorDrawable())
        animator = getPvZ2CLoadingIndicatorAnimation(this)
    }

    private var running = true

    fun resume() {
        running = true
        if (!isAttachedToWindow) {
            return
        }
        when {
            animator.isPaused -> animator.resume()
            !animator.isStarted -> animator.start()
        }
    }

    fun pause() {
        running = false
        if (animator.isStarted && !animator.isPaused) {
            animator.pause()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (running && !animator.isStarted) {
            animator.start()
        }
    }

    override fun onDetachedFromWindow() {
        animator.cancel()
        super.onDetachedFromWindow()
    }

    private var aggregatedVisible = false

    override fun onVisibilityAggregated(isVisible: Boolean) {
        super.onVisibilityAggregated(isVisible)
        aggregatedVisible = isVisible
        updateAnimator()
    }

    private fun updateAnimator() {
        val shouldAnimate = running && isAttachedToWindow && aggregatedVisible
        if (shouldAnimate) {
            when {
                animator.isPaused -> animator.resume()
                !animator.isStarted -> animator.start()
            }
        } else {
            if (animator.isStarted && !animator.isPaused) {
                animator.pause()
            }
        }
    }
}