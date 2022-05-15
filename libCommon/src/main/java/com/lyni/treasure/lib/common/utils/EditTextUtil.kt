@file:Suppress("unused")

package com.lyni.treasure.lib.common.utils

import android.text.InputFilter
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import com.lyni.treasure.lib.common.ktx.match

/**
 * @date 2022/3/4
 * @author Liangyong Ni
 * description EditText输入拦截器帮助类
 */
object EditTextUtil {
    /**
     * 限制emoji输入
     */
    @JvmStatic
    fun getInputFilterProhibitEmoji(emojiCall: () -> Unit = {}): InputFilter {
        return InputFilter { source, start, end, _, _, _ ->
            val buffer = StringBuffer()
            var i = start
            while (i < end) {
                val codePoint = source[i]
                if (!getIsEmoji(codePoint)) {
                    buffer.append(codePoint)
                } else {
                    i++
                    i++
                    emojiCall()
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

    private fun getIsEmoji(codePoint: Char): Boolean {
        return if (codePoint.code == 0x0 || codePoint.code == 0x9 || codePoint.code == 0xA
            || codePoint.code == 0xD
            || codePoint.code in 0x20..0xD7FF
            || codePoint.code in 0xE000..0xFFFD
            || codePoint.code in 0x10000..0x10FFFF
        ) false else {
            return true
        }
    }

    /**
     * 限制特殊字符输入
     */
    @JvmStatic
    fun getInputFilterProhibitSP(): InputFilter {
        return InputFilter { source, start, end, _, _, _ ->
            val buffer = StringBuffer()
            var i = start
            while (i < end) {
                val codePoint = source[i]
                if (!getIsSp(codePoint)) {
                    buffer.append(codePoint)
                } else {
                    i++
                    i++
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

    fun getInputFilterProhibitSPWithoutAt(): InputFilter {
        return InputFilter { source, _, _, _, _, _ ->
            if (source.toString().match(
                    "[`~!#\$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]"
                )
            ) {
                return@InputFilter ""
            } else {
                return@InputFilter null
            }
        }
    }

    /**
     * 限制空格输入
     */
    fun getInputFilterProhibitSpace(): InputFilter {
        return InputFilter { source, _, _, _, _, _ ->
            if (source.equals(" ")) {
                ""
            } else {
                null
            }
        }
    }

    private fun getIsSp(codePoint: Char): Boolean {
        return Character.getType(codePoint) > Character.LETTER_NUMBER
    }

    /**
     * 限制输入字数
     * @param max 最大输入字数
     * @param onOverLimit 超出字数回调
     */
    fun getLengthLimitInputFilter(max: Int, onOverLimit: (() -> Unit)?): InputFilter {
        return object : InputFilter {
            override fun filter(
                source: CharSequence, start: Int, end: Int, dest: Spanned,
                dstart: Int, dend: Int
            ): CharSequence? {
                var keep = max - (dest.length - (dend - dstart))
                return when {
                    keep <= 0 -> {
                        onOverLimit?.invoke()
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