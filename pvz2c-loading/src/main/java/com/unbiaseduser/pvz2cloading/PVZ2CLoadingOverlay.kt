package com.unbiaseduser.pvz2cloading

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.withStyledAttributes
import com.unbiaseduser.pvz2cloading.databinding.Pvz2cLoadingOverlayBinding

class PVZ2CLoadingOverlay : FrameLayout {

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs) {
        performInit(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        performInit(context, attrs, defStyleAttr)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        performInit(context, attrs, defStyleAttr, defStyleRes)
    }

    private fun performInit(context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0, defStyleRes: Int = 0) {
        context.withStyledAttributes(attrs, R.styleable.PVZ2CLoadingOverlay, defStyleAttr, defStyleRes) {
            val textValue = TypedValue()
            if (getValue(R.styleable.PVZ2CLoadingOverlay_pvz2clo_text, textValue)) {
                val loadingText = when (textValue.type) {
                    TypedValue.TYPE_STRING -> textValue.string
                    TypedValue.TYPE_REFERENCE -> context.getText(textValue.resourceId)
                    else -> null
                }
                if (loadingText != null) {
                    text.text = loadingText
                }
            }
        }
    }

    private val text: TextView
    private val image: ImageView
    private val animator: ValueAnimator

    init {
        val binding = Pvz2cLoadingOverlayBinding.inflate(LayoutInflater.from(context), this, true)
        text = binding.text
        image = binding.image
        animator = getPvZ2CLoadingIndicatorAnimation(image)
        setBackgroundColor(Color.argb(64, 0, 0, 0))
        image.setImageDrawable(PVZ2CLoadingIndicatorDrawable(15F))
    }

    fun setLoadingText(loadingText: CharSequence) {
        text.text = loadingText
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