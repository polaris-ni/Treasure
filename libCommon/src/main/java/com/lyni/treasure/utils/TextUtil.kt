package com.lyni.treasure.utils

/**
 * @author Liangyong Ni
 * @date 2022/7/1
 * description [TextUtil]
 */
object TextUtil {
    /**
     * 判断是否是emoji
     *
     * @param codePoint 码点
     * @return true or false
     */
    fun isEmoji(codePoint: Char): Boolean {
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
     * 判断是否是特殊字符
     *
     * @param codePoint 码点
     * @return true or false
     */
    fun isSpecialChar(codePoint: Char): Boolean {
        return Character.getType(codePoint) > Character.LETTER_NUMBER
    }
}