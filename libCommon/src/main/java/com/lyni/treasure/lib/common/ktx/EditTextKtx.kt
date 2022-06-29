@file:Suppress("unused")

package com.lyni.treasure.lib.common.ktx

import android.text.InputFilter
import android.widget.EditText
import com.lyni.treasure.lib.common.utils.EditTextUtil

/**
 * @author Liangyong Ni
 * @date 2022/6/30
 * description EditText的扩展函数
 */
/**
 * 获取有效的Edittext字符串
 * @return str
 */
fun EditText.getEffectiveText(): String {
    return this.text.toString().trim()
}

/**
 * EditText拦截emoji
 */
fun EditText.prohibitEmoji(emojiCall: () -> Unit = {}) {
    this.addFilter(EditTextUtil.getInputFilterProhibitEmoji(emojiCall))
}

/**
 * EditText拦截特殊字符
 */
fun EditText.prohibitSP() {
    this.addFilter(EditTextUtil.getInputFilterProhibitSP())
}

/**
 * EditText拦截空格
 */
fun EditText.prohibitSpace() {
    this.addFilter(EditTextUtil.getInputFilterProhibitSpace())
}


/**
 * EditText新增拦截器
 * @param filter 拦截器
 */
fun EditText.addFilter(filter: InputFilter) {
    this.filters = filters.toMutableList().apply {
        add(filter)
    }.toTypedArray()
}