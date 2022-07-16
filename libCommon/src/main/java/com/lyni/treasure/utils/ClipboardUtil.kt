@file:Suppress("UNUSED", "MemberVisibilityCanBePrivate")

package com.lyni.treasure.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import com.lyni.treasure.ktx.appContext

/**
 * @author Liangyong Ni
 * @date 2022/6/30
 * description [ClipboardUtil]
 */
object ClipboardUtil {

    /**
     * 将[CharSequence]复制到剪贴板中，可设置[label]，默认为null
     *
     * @param text  [CharSequence] - 内容
     * @param label [CharSequence] - 标签
     */
    @JvmStatic
    fun copyToClipboard(text: CharSequence, label: CharSequence? = null) {
        val clipData = ClipData.newPlainText(label, text)
        getClipboardManager().setPrimaryClip(clipData)
    }

    /**
     * 从剪贴板中获取内容
     *
     * @return  [String] - 内容，没有则返回空
     */
    @JvmStatic
    fun getTextFromClipboard(): String? {
        getClipboardManager().primaryClip?.let {
            if (it.itemCount > 0) {
                return it.getItemAt(0).text.toString().trim()
            }
        }
        return null
    }

    /**
     * 获取[ClipboardManager]
     */
    @JvmStatic
    fun getClipboardManager() = appContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
}