@file:Suppress("unused")

package com.lyni.treasure.utils

import android.text.InputFilter
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import com.lyni.treasure.utils.TextUtil.isEmoji
import com.lyni.treasure.utils.TextUtil.isSpecialChar

/**
 * @date 2022/3/4
 * @author Liangyong Ni
 * description EditText输入拦截器帮助类
 */
object EditTextUtil {
    /**
     * 限制emoji输入
     * @param onLimit 当输入是Emoji时回调
     */
    @JvmStatic
    fun getInputFilterProhibitEmoji(onLimit: (() -> Unit)? = null): InputFilter {
        return InputFilter { source, start, end, _, _, _ ->
            val buffer = StringBuffer()
            var i = start
            while (i < end) {
                val codePoint = source[i]
                if (!isEmoji(codePoint)) {
                    buffer.append(codePoint)
                } else {
                    i++
                    i++
                    onLimit?.invoke()
                    continue
                }
                i++
            }
            if (source is Spanned) {
                val sp = SpannableString(buffer)
                TextUtils.copySpansFrom(
                    source, start, end, null,
                    sp, 0
                )
                sp
            } else {
                buffer
            }
        }
    }

    /**
     * 限制特殊字符输入
     */
    @JvmStatic
    fun getInputFilterProhibitSP(onLimit: (() -> Unit)? = null): InputFilter {
        return InputFilter { source, start, end, _, _, _ ->
            val buffer = StringBuffer()
            var i = start
            while (i < end) {
                val codePoint = source[i]
                if (!isSpecialChar(codePoint)) {
                    buffer.append(codePoint)
                } else {
                    i++
                    i++
                    onLimit?.invoke()
                    continue
                }
                i++
            }
            if (source is Spanned) {
                val sp = SpannableString(buffer)
                try {
                    TextUtils.copySpansFrom(
                        source, start, end, null,
                        sp, 0
                    )
                } catch (e: Exception) {

                }
                sp
            } else {
                buffer
            }
        }
    }

    /**
     * 限制空格输入
     */
    @JvmStatic
    fun getInputFilterProhibitSpace(onLimit: (() -> Unit)? = null): InputFilter {
        return InputFilter { source, _, _, _, _, _ ->
            if (source.equals(" ")) {
                onLimit?.invoke()
                ""
            } else null
        }
    }

    /**
     * 限制输入字数
     *
     * @param max           最大输入字数
     * @param onLimit   超出字数回调
     */
    @JvmStatic
    fun getLengthLimitInputFilter(max: Int, onLimit: (() -> Unit)? = null): InputFilter {
        return object : InputFilter {
            override fun filter(
                source: CharSequence, start: Int, end: Int, dest: Spanned,
                dstart: Int, dend: Int
            ): CharSequence? {
                var keep = max - (dest.length - (dend - dstart))
                return when {
                    keep <= 0 -> {
                        onLimit?.invoke()
                        ""
                    }
                    keep >= end - start -> null
                    else -> {
                        keep += start
                        if (Character.isHighSurrogate(source[keep - 1])) {
                            --keep
                            if (keep == start) {
                                return ""
                            }
                        }
                        source.subSequence(start, keep)
                    }
                }
            }
        }
    }
}