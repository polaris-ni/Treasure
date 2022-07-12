package com.lyni.treasure.arch.rvx.animations

import android.animation.Animator
import android.animation.ObjectAnimator
import android.view.View

/**
 * @date 2022/6/30
 * @author Liangyong Ni
 * description 缩放进入动画
 */
class ScaleInAnimation @JvmOverloads constructor(private val mFromX: Float = 0.5f, private val mFromY: Float = 0.5f) :
    BaseAnimation {

    override fun getAnimators(view: View): Array<Animator> {
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", mFromX, 1f)
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", mFromY, 1f)
        return arrayOf(scaleX, scaleY)
    }
}
