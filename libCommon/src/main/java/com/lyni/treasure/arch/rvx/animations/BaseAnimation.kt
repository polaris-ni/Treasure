package com.lyni.treasure.arch.rvx.animations

import android.animation.Animator
import android.view.View

/**
 * @date 2022/6/30
 * @author Liangyong Ni
 * description adapter item 动画
 */
interface BaseAnimation {
    /**
     * Get animators
     *
     * @param view 控件
     * @return [Array] of [Animator]
     */
    fun getAnimators(view: View): Array<Animator>
}
