package com.ware.widget.drawable

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity

/**
 * Created by jianfeng.li on 2020/9/26.
 */
data class DrawableProperties(
        // <shape>
        var shape: Int = GradientDrawable.RECTANGLE,
        var innerRadius: Int = -1,
        var innerRadiusRatio: Float = 9f,
        var thickness: Int = -1,
        var thicknessRatio: Float = 3f,
        var useLevelForRing: Boolean = false,

        // <corner>
        private var _cornerRadius: Int = 0,
        var topLeftRadius: Int = 0,
        var topRightRadius: Int = 0,
        var bottomRightRadius: Int = 0,
        var bottomLeftRadius: Int = 0,

        // <gradient>
        var useGradient: Boolean = false,
        var type: Int = GradientDrawable.RADIAL_GRADIENT,
        var angle: Int = 0,
        var centerX: Float = 0.5f,
        var centerY: Float = 0.5f,
        var useCenterColor: Boolean = false,
        var startColor: Int = Color.BLUE,
        var centerColor: Int? = null,
        var endColor: Int = 0x7FFFFFFF,
        var gradientRadiusType: Int = RADIUS_TYPE_FRACTION,
        var gradientRadius: Float = 0.5f,
        var useLevelForGradient: Boolean = false,

        // <size>
        var width: Int = -1,
        var height: Int = -1,

        // <solid>
        var solidColor: Int = Color.TRANSPARENT,
        var solidColorStateList: ColorStateList? = null,

        // <stroke>
        var strokeWidth: Int = 0,
        var strokeColor: Int = Color.DKGRAY,
        var strokeColorStateList: ColorStateList? = null,
        var dashWidth: Int = 0,
        var dashGap: Int = 0,

        // <rotate>
        var useRotate: Boolean = false,
        var pivotX: Float = 0.5f,
        var pivotY: Float = 0.5f,
        var fromDegrees: Float = 0f,
        var toDegrees: Float = 0f,

        // <scale>
        var useScale: Boolean = false,
        var scaleLevel: Int = 10000,
        var scaleGravity: Int = Gravity.CENTER,
        var scaleWidth: Float = 0f,
        var scaleHeight: Float = 0f) {

    companion object {
        const val RADIUS_TYPE_PIXELS = 0
        const val RADIUS_TYPE_FRACTION = 1
    }

    var cornerRadius: Int = _cornerRadius
        set(value) {
            _cornerRadius = value
            topLeftRadius = value
            topRightRadius = value
            bottomRightRadius = value
            bottomLeftRadius = value
        }

    fun getCornerRadii(): FloatArray {
        return floatArrayOf(topLeftRadius.toFloat(), topLeftRadius.toFloat(),
                topRightRadius.toFloat(), topRightRadius.toFloat(),
                bottomRightRadius.toFloat(), bottomRightRadius.toFloat(),
                bottomLeftRadius.toFloat(), bottomLeftRadius.toFloat())
    }

    fun getOrientation(): GradientDrawable.Orientation {
        val angle = this.angle % 360
        val orientation: GradientDrawable.Orientation
        orientation = when (angle) {
            0 -> GradientDrawable.Orientation.LEFT_RIGHT
            45 -> GradientDrawable.Orientation.BL_TR
            90 -> GradientDrawable.Orientation.BOTTOM_TOP
            135 -> GradientDrawable.Orientation.BR_TL
            180 -> GradientDrawable.Orientation.RIGHT_LEFT
            225 -> GradientDrawable.Orientation.TR_BL
            270 -> GradientDrawable.Orientation.TOP_BOTTOM
            315 -> GradientDrawable.Orientation.TL_BR
            else -> throw IllegalArgumentException("Unsupported angle: $angle")
        }
        return orientation
    }

    fun getColors(): IntArray {
        return if (useCenterColor && centerColor != null) {
            intArrayOf(startColor, centerColor!!, endColor)
        } else intArrayOf(startColor, endColor)
    }
}