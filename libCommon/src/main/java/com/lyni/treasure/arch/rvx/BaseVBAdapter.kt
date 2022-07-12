package com.lyni.treasure.arch.rvx

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.lyni.treasure.arch.rvx.vh.DefaultBindingViewHolder
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType

/**
 * @author Liangyong Ni
 * @date 2022/7/11
 * description [BaseVBAdapter]
 */
abstract class BaseVBAdapter<ITEM, VB : ViewBinding> : BaseRVAdapter<ITEM, DefaultBindingViewHolder<VB>>() {

    private val inflateMethod: Method
    private val inflater by lazy {
        LayoutInflater.from(context)
    }

    init {
        val type = javaClass.genericSuperclass
        if (type is ParameterizedType) {
            @Suppress("UNCHECKED_CAST")
            val clazz = type.actualTypeArguments[1] as Class<VB>
            inflateMethod =
                clazz.getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java)
        } else {
            throw IllegalArgumentException("Failed to parse generic class.")
        }
    }

    @Suppress("UNCHECKED_CAST")
    final override fun requireNewViewHolder(parent: ViewGroup, viewType: Int): DefaultBindingViewHolder<VB> {
        return DefaultBindingViewHolder(inflateMethod.invoke(null, inflater, parent, false) as VB)
    }

}