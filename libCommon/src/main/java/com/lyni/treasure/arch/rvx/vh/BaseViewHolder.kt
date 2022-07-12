@file:Suppress("unused")

package com.lyni.treasure.arch.rvx.vh

import android.view.View
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView

/**
 * @date 2022/6/30
 * @author Liangyong Ni
 * description 基于ViewBinding的[RecyclerView.ViewHolder]
 */
open class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val views = HashMap<Int, View>()

    open fun <T : View> getView(@IdRes viewId: Int): T {
        val view = getViewOrNull<T>(viewId)
        checkNotNull(view) { "$viewId not found" }
        return view
    }

    @Suppress("UNCHECKED_CAST")
    open fun <T : View> getViewOrNull(@IdRes viewId: Int): T? {
        val view = views[viewId]
        if (view == null) {
            itemView.findViewById<T>(viewId)?.let {
                views[viewId] = it
                return it
            }
        }
        return view as? T
    }

    fun <T : View> Int.findView(): T {
        return itemView.findViewById(this)
    }
}