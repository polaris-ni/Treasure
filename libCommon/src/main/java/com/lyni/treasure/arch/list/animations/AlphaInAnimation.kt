package com.lyni.treasure.arch.list.animations

import android.animation.Animator
import android.animation.ObjectAnimator
import android.view.View

/**
 * @date 2022/6/30
 * @author Liangyong Ni
 * description AlphaInAnimation 带有透明度变化的进入动画
 */
class AlphaInAnimation constructor(private val mFrom: Float = 0f) : BaseAnimation {
    override fun getAnimators(view: View): Array<Animator> =
        arrayOf(ObjectAnimator.ofFloat(view, "alpha", mFrom, 1f))
}
