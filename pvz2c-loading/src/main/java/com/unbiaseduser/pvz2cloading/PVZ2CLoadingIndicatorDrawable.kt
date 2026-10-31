package com.unbiaseduser.pvz2cloading

import android.graphics.*
import android.graphics.drawable.Drawable
import kotlin.math.cos
import kotlin.math.sin

class PVZ2CLoadingIndicatorDrawable(
    private val strokeWidthPx: Float,
    private val arcSweepDeg: Float = 270f,
    private val tailFractionOfArc: Float = 0.15f,
    private val arcStartDeg: Float = 120f
) : Drawable() {

    private val arcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = strokeWidthPx
        strokeCap = Paint.Cap.BUTT
    }

    private val arrowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.WHITE
    }

    private val arcBounds = RectF()
    private var sweepGradient: SweepGradient? = null

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)

        val inset = strokeWidthPx * 1.5f
        arcBounds.set(
            bounds.left + inset,
            bounds.top + inset,
            bounds.right - inset,
            bounds.bottom - inset
        )

        rebuildShader()
    }

    private fun rebuildShader() {
        val cx = arcBounds.centerX()
        val cy = arcBounds.centerY()

        val arcPortionOfFullCircle = arcSweepDeg / 360f
        val tailPortionOfFullCircle = arcPortionOfFullCircle * tailFractionOfArc

        // Keep the arc almost entirely opaque.
        // Only the first ~15% of the visible arc fades in.
        val colors = intArrayOf(
            Color.TRANSPARENT,
            Color.argb(80, 255, 255, 255),
            Color.WHITE,
            Color.WHITE,
            Color.TRANSPARENT,
            Color.TRANSPARENT
        )

        val positions = floatArrayOf(
            0f,
            tailPortionOfFullCircle * 0.45f,
            tailPortionOfFullCircle,
            arcPortionOfFullCircle - 0.001f,
            arcPortionOfFullCircle,
            1f
        )

        val shader = SweepGradient(cx, cy, colors, positions)

        // Align gradient start with the arc start.
        val matrix = Matrix()
        matrix.postRotate(arcStartDeg, cx, cy)
        shader.setLocalMatrix(matrix)

        sweepGradient = shader
        arcPaint.shader = shader
    }

    override fun draw(canvas: Canvas) {
        // Draw the arc
        canvas.drawArc(
            arcBounds,
            arcStartDeg,
            arcSweepDeg,
            false,
            arcPaint
        )

        // Draw the arrowhead at the arc end
        drawArrowHead(canvas)
    }

    private fun drawArrowHead(canvas: Canvas) {
        val cx = arcBounds.centerX()
        val cy = arcBounds.centerY()
        val radius = arcBounds.width() / 2f

        val angle = Math.toRadians(
            (arcStartDeg + arcSweepDeg).toDouble()
        )

        val radialX = cos(angle).toFloat()
        val radialY = sin(angle).toFloat()

        // Clockwise tangent direction.
        // Android's coordinate system has +Y downward, so this is
        // the direction of increasing drawArc() angles.
        val tangentX = -radialY
        val tangentY = radialX

        val endX = cx + radius * radialX
        val endY = cy + radius * radialY

        val arrowLength = strokeWidthPx * 1.8f
        val arrowHalfWidth = strokeWidthPx * 1.6f

        // Pull the arrow base slightly backward so it overlaps the arc.
        // Avoids the seam from putting the arrow and arc next to each other.
        val overlap = strokeWidthPx * 0.45f

        val baseCenterX = endX - tangentX * overlap
        val baseCenterY = endY - tangentY * overlap

        val tipX = baseCenterX + tangentX * arrowLength
        val tipY = baseCenterY + tangentY * arrowLength

        val base1X = baseCenterX + radialX * arrowHalfWidth
        val base1Y = baseCenterY + radialY * arrowHalfWidth

        val base2X = baseCenterX - radialX * arrowHalfWidth
        val base2Y = baseCenterY - radialY * arrowHalfWidth

        val path = Path().apply {
            moveTo(tipX, tipY)
            lineTo(base1X, base1Y)
            lineTo(base2X, base2Y)
            close()
        }

        canvas.drawPath(path, arrowPaint)
    }

    override fun setAlpha(alpha: Int) {
        arcPaint.alpha = alpha
        arrowPaint.alpha = alpha
        invalidateSelf()
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        arcPaint.colorFilter = colorFilter
        arrowPaint.colorFilter = colorFilter
        invalidateSelf()
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
}