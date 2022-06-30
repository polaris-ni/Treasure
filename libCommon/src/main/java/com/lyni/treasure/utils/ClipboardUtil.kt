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
    fun copyToClipboard(text: String, action: (() -> Unit)? = null) {
        val clipData = ClipData.newPlainText(null, text)
        getClipboardManager().setPrimaryClip(clipData)
        action?.invoke()
    }

    fun getTextFromClipboard(): String? {
        getClipboardManager().primaryClip?.let {
            if (it.itemCount > 0) {
                return it.getItemAt(0).text.toString().trim()
            }
        }
        return null
    }

    fun getClipboardManager() = appContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
}