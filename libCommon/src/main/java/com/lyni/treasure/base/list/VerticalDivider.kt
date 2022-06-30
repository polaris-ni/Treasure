package com.lyni.treasure.base.list

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import com.lyni.treasure.common.R

/**
 * @date 2022/6/1
 * @author Liangyong Ni
 * description VerticalDivider
 */
class VerticalDivider(context: Context) : DividerItemDecoration(context, VERTICAL) {

    init {
        ContextCompat.getDrawable(context, R.drawable.ic_divider)?.let {
            this.setDrawable(it)
        }
    }

}