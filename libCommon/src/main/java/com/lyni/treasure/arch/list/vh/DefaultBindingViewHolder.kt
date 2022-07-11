package com.lyni.treasure.arch.list.vh

import androidx.viewbinding.ViewBinding

/**
 * @author Liangyong Ni
 * @date 2022/7/4
 * description [DefaultBindingViewHolder]
 */
class DefaultBindingViewHolder<VB : ViewBinding>(@JvmField val binding: VB) : BaseViewHolder(binding.root)