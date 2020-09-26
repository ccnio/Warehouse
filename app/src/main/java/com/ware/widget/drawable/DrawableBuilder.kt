package com.ware.widget.drawable

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Build
import android.util.StateSet
import android.view.View

/**
 * Created by jianfeng.li on 2020/9/26.
 */
class DrawableBuilder {
    private val properties = DrawableProperties()

    fun build(view: View? = null): Drawable {
        val drawable: Drawable
        if (needStateListDrawable()) {
            drawable = StateListDrawable()
            buildStateListDrawable(drawable)
        } else {
            drawable = GradientDrawable()
            buildDrawable(drawable)
        }
        if (view != null) {
            view.background = drawable
        }
        return drawable
    }

    private fun buildDrawable(drawable: GradientDrawable) {
        properties.apply {
            drawable.shape = shape
            if (shape == GradientDrawable.RING) {
            }

            drawable.cornerRadii = getCornerRadii()

            if (useGradient) {
                drawable.gradientType = type
                drawable.gradientRadius = gradientRadius
                drawable.setGradientCenter(centerX, centerY)
                drawable.orientation = getOrientation()
                drawable.colors = getColors()
                drawable.useLevel = useLevelForGradient
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    drawable.color = getSolidColorStateList()
                } else {
                    drawable.setColor(solidColor)
                }
            }
            drawable.setSize(width, height)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                drawable.setStroke(strokeWidth, getStrokeColorStateList(), dashWidth.toFloat(), dashGap.toFloat())
            } else {
                drawable.setStroke(strokeWidth, strokeColor, dashWidth.toFloat(), dashGap.toFloat())
            }
        }
    }

    private fun buildStateListDrawable(stateListDrawable: StateListDrawable): Drawable {
        stateListDrawable.addState(intArrayOf(android.R.attr.state_pressed), buildPressedDrawable())
        stateListDrawable.addState(intArrayOf(-android.R.attr.state_enabled), buildDisabledDrawable())
        stateListDrawable.addState(intArrayOf(android.R.attr.state_selected), buildSelectedDrawable())
        stateListDrawable.addState(StateSet.WILD_CARD, buildNormalDrawable())
        return stateListDrawable
    }

    private fun needStateListDrawable(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP
                && (hasStrokeColorStateList() || (!properties.useGradient && hasSolidColorStateList()))
    }

    // <shape>
    fun shape(shape: Int) = apply { properties.shape = shape }
    fun rectangle() = apply { shape(GradientDrawable.RECTANGLE) }
    fun oval() = apply { shape(GradientDrawable.OVAL) }
    fun line() = apply { shape(GradientDrawable.LINE) }
    fun ring() = apply { shape(GradientDrawable.RING) }

    @JvmOverloads
    fun useLevelForRing(use: Boolean = true) = apply { properties.useLevelForRing = use }

    // <corner>
    fun radius(radius: Int) = apply { properties.cornerRadius = radius }
    fun topLeftRadius(topLeftRadius: Int) = apply { properties.topLeftRadius = topLeftRadius }
    fun topRightRadius(topRightRadius: Int) = apply { properties.topRightRadius = topRightRadius }
    fun bottomRightRadius(bottomRightRadius: Int) = apply { properties.bottomRightRadius = bottomRightRadius }
    fun bottomLeftRadius(bottomLeftRadius: Int) = apply { properties.bottomLeftRadius = bottomLeftRadius }
    fun cornerRadii(topLeftRadius: Int, topRightRadius: Int, bottomRightRadius: Int, bottomLeftRadius: Int) = apply {
        topLeftRadius(topLeftRadius); topRightRadius(topRightRadius); bottomRightRadius(bottomRightRadius); bottomLeftRadius(bottomLeftRadius)
    }

    // <gradient>
    @JvmOverloads
    fun gradient(useGradient: Boolean = true) = apply { properties.useGradient = useGradient }
    fun gradientType(type: Int) = apply { properties.type = type }
    fun linearGradient() = apply { gradientType(GradientDrawable.LINEAR_GRADIENT) }
    fun radialGradient() = apply { gradientType(GradientDrawable.RADIAL_GRADIENT) }
    fun sweepGradient() = apply { gradientType(GradientDrawable.SWEEP_GRADIENT) }
    fun angle(angle: Int) = apply { properties.angle = angle }
    fun centerX(centerX: Float) = apply { properties.centerX = centerX }
    fun centerY(centerY: Float) = apply { properties.centerY = centerY }
    fun center(centerX: Float, centerY: Float) = apply { centerX(centerX); centerY(centerY) }

    @JvmOverloads
    fun useCenterColor(useCenterColor: Boolean = true) = apply { properties.useCenterColor = useCenterColor }
    fun startColor(startColor: Int) = apply { properties.startColor = startColor }
    fun centerColor(centerColor: Int) = apply {
        properties.centerColor = centerColor
        useCenterColor(true)
    }

    fun endColor(endColor: Int) = apply { properties.endColor = endColor }
    fun gradientColors(startColor: Int, endColor: Int, centerColor: Int?) = apply {
        startColor(startColor); endColor(endColor)
        useCenterColor(centerColor != null)
        centerColor?.let {
            centerColor(it)
        }
    }

    fun gradientRadiusType(gradientRadiusType: Int) = apply { properties.gradientRadiusType = gradientRadiusType }
    fun gradientRadius(gradientRadius: Float) = apply { properties.gradientRadius = gradientRadius }
    fun gradientRadius(radius: Float, type: Int) = apply { gradientRadius(radius); gradientRadiusType(type) }
    fun gradientRadiusInPixel(radius: Float) = apply { gradientRadius(radius); gradientRadiusType(DrawableProperties.RADIUS_TYPE_PIXELS) }
    fun gradientRadiusInFraction(radius: Float) = apply { gradientRadius(radius); gradientRadiusType(DrawableProperties.RADIUS_TYPE_FRACTION) }
    fun useLevelForGradient(use: Boolean) = apply { properties.useLevelForGradient = use }
    fun useLevelForGradient() = apply { useLevelForGradient(true) }

    // <size>
    fun size(width: Int, height: Int) = apply {
        properties.width = width
        properties.height = height
    }

    // <solid>
    fun solidColor(color: Int) = apply { properties.solidColor = color }
    private var solidColorPressed: Int? = null
    fun solidColorPressed(color: Int?) = apply { solidColorPressed = color }
    private var solidColorDisabled: Int? = null
    fun solidColorDisabled(color: Int?) = apply { solidColorDisabled = color }
    private var solidColorSelected: Int? = null
    fun solidColorSelected(color: Int?) = apply { solidColorSelected = color }
    fun solidColorStateList(stateList: ColorStateList) = apply { properties.solidColorStateList = stateList }

    // <stroke>
    fun strokeWidth(strokeWidth: Int) = apply { properties.strokeWidth = strokeWidth }
    fun strokeColor(strokeColor: Int) = apply { properties.strokeColor = strokeColor }
    private var strokeColorPressed: Int? = null
    fun strokeColorPressed(color: Int?) = apply { strokeColorPressed = color }
    private var strokeColorDisabled: Int? = null
    fun strokeColorDisabled(color: Int?) = apply { strokeColorDisabled = color }
    private var strokeColorSelected: Int? = null
    fun strokeColorSelected(color: Int?) = apply { strokeColorSelected = color }
    fun strokeColorStateList(colorStateList: ColorStateList) = apply { properties.strokeColorStateList = colorStateList }

    fun dashWidth(dashWidth: Int) = apply { properties.dashWidth = dashWidth }
    fun dashGap(dashGap: Int) = apply { properties.dashGap = dashGap }
    fun hairlineBordered() = apply { strokeWidth(1) }
    fun shortDashed() = apply { dashWidth(12).dashGap(12) }
    fun mediumDashed() = apply { dashWidth(24).dashGap(24) }
    fun longDashed() = apply { dashWidth(36).dashGap(36) }
    fun dashed() = apply { mediumDashed() }

    private fun getSolidColorStateList(): ColorStateList {
        if (properties.solidColorStateList != null) {
            return properties.solidColorStateList!!
        }
        properties.solidColorStateList?.let { return it }

        val states = mutableListOf<IntArray>()
        val colors = mutableListOf<Int>()

        solidColorPressed?.let {
            states.add(intArrayOf(android.R.attr.state_pressed))
            colors.add(it)
        }
        solidColorDisabled?.let {
            states.add(intArrayOf(-android.R.attr.state_enabled))
            colors.add(it)
        }
        solidColorSelected?.let {
            states.add(intArrayOf(android.R.attr.state_selected))
            colors.add(it)
        }
        states.add(StateSet.WILD_CARD)
        colors.add(properties.solidColor)

        return ColorStateList(states.toTypedArray(), colors.toIntArray())
    }

    private fun getStrokeColorStateList(): ColorStateList {
        if (properties.strokeColorStateList != null) {
            return properties.strokeColorStateList!!
        }

        val states = mutableListOf<IntArray>()
        val colors = mutableListOf<Int>()

        strokeColorPressed?.let {
            states.add(intArrayOf(android.R.attr.state_pressed))
            colors.add(it)
        }
        strokeColorDisabled?.let {
            states.add(intArrayOf(-android.R.attr.state_enabled))
            colors.add(it)
        }
        strokeColorSelected?.let {
            states.add(intArrayOf(android.R.attr.state_selected))
            colors.add(it)
        }
        states.add(StateSet.WILD_CARD)
        colors.add(properties.strokeColor)

        return ColorStateList(states.toTypedArray(), colors.toIntArray())
    }

    private fun buildPressedDrawable(): Drawable? {
        if (solidColorPressed == null && strokeColorPressed == null) return null

        val pressedDrawable = GradientDrawable()
        buildDrawable(pressedDrawable)
        solidColorPressed?.let { pressedDrawable.setColor(it) }
        strokeColorPressed?.let { pressedDrawable.setStroke(properties.strokeWidth, it) }
        return pressedDrawable
    }

    private fun buildDisabledDrawable(): Drawable? {
        if (solidColorDisabled == null && strokeColorDisabled == null) return null
        val disabledDrawable = GradientDrawable()
        buildDrawable(disabledDrawable)
        solidColorDisabled?.let { disabledDrawable.setColor(it) }
        strokeColorDisabled?.let { disabledDrawable.setStroke(properties.strokeWidth, it) }
        return disabledDrawable
    }

    private fun buildSelectedDrawable(): Drawable? {
        if (solidColorSelected == null && strokeColorSelected == null) return null

        val selectedDrawable = GradientDrawable()
        buildDrawable(selectedDrawable)
        solidColorSelected?.let { selectedDrawable.setColor(it) }
        strokeColorPressed?.let { selectedDrawable.setStroke(properties.strokeWidth, it) }
        return selectedDrawable
    }

    private fun buildNormalDrawable(): Drawable {
        val pressedDrawable = GradientDrawable()
        buildDrawable(pressedDrawable)
        return pressedDrawable
    }

    private fun hasSolidColorStateList() = solidColorPressed != null || solidColorDisabled != null || solidColorSelected != null
    private fun hasStrokeColorStateList() = strokeColorPressed != null || strokeColorDisabled != null || strokeColorSelected != null
}