package com.lyni.treasure.arch.list.animations

import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator

/**
 * @date 2022/6/30
 * @author Liangyong Ni
 * description adapter的子项动画
 */
@Suppress("unused")
class ItemAnimation private constructor() {

    var itemAnimEnabled = false
    var itemAnimFirstOnly = true
    var itemAnimation: BaseAnimation? = null
    var itemAnimInterpolator: Interpolator = LinearInterpolator()
    var itemAnimDuration: Long = 300L
    var itemAnimStartPosition: Int = -1

    fun interpolator(interpolator: Interpolator) = apply {
        itemAnimInterpolator = interpolator
    }

    fun duration(duration: Long) = apply {
        itemAnimDuration = duration
    }

    fun startPosition(startPos: Int) = apply {
        itemAnimStartPosition = startPos
    }

    /**
     * Animation with DefaultAnimation
     *
     * @param type      [Animations.Type] - type of animation
     * @param direction [Animations.Direction] - direction of animation, null if animation does not require direction
     */
    fun animation(
        type: Animations.Type = Animations.Type.NONE,
        direction: Animations.Direction? = null
    ) = apply {
        itemAnimation = when (type) {
            Animations.Type.NONE -> null
            Animations.Type.FADE_IN -> AlphaInAnimation()
            Animations.Type.SCALE_IN -> ScaleInAnimation()
            Animations.Type.SLIDE_IN -> {
                direction?.let { SlideInAnimation(it) }
                    ?: throw IllegalArgumentException("SlideInAnimation must have a direction")
            }
        }
    }

    /**
     * Use Customized Animation
     *
     * @param animation Customized Animation which implements [BaseAnimation]
     */
    fun animation(animation: BaseAnimation) = apply {
        itemAnimation = animation
    }

    fun enabled(enabled: Boolean) = apply {
        itemAnimEnabled = enabled
    }

    fun firstOnly(firstOnly: Boolean) = apply {
        itemAnimFirstOnly = firstOnly
    }

    companion object {
        fun create() = ItemAnimation()
    }
}