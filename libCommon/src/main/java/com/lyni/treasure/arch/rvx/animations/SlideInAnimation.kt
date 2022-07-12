package com.lyni.treasure.arch.rvx.animations

import android.animation.Animator
import android.animation.ObjectAnimator
import android.view.View

/**
 * @date 2022/6/30
 * @author Liangyong Ni
 * description 滑入动画
 */
class SlideInAnimation(private val fromDirection: Animations.Direction = Animations.Direction.FROM_LEFT) :
    BaseAnimation {

    override fun getAnimators(view: View): Array<Animator> = when (fromDirection) {
        Animations.Direction.FROM_LEFT ->
            arrayOf(ObjectAnimator.ofFloat(view, "translationX", -view.rootView.width.toFloat(), 0f))
        Animations.Direction.FROM_BOTTOM ->
            arrayOf(ObjectAnimator.ofFloat(view, "translationY", view.measuredHeight.toFloat(), 0f))
        Animations.Direction.FROM_RIGHT ->
            arrayOf(ObjectAnimator.ofFloat(view, "translationX", view.rootView.width.toFloat(), 0f))
        Animations.Direction.FROM_TOP ->
            arrayOf(ObjectAnimator.ofFloat(view, "translationY", -view.measuredHeight.toFloat(), 0f))
    }
}
