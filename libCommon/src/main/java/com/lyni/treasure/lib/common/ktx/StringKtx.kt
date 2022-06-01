@file:Suppress("unused")

package com.lyni.treasure.lib.common.ktx

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.text.Collator
import android.icu.util.ULocale
import android.net.Uri
import android.util.Base64
import java.io.File
import java.util.*

/**
 * @date 2022/3/4
 * @author Liangyong Ni
 * description 字符串扩展
 */
/**
 * 进行正则校验
 * @param reg 正则表达式
 * @return 是否匹配成功
 */
fun String.match(reg: String): Boolean = this.matches(Regex(reg))

/**
 * Cast KTX
 * 带默认值的转换
 */
fun String?.toDoubleOrDefault(default: Double = 0.0): Double = this?.toDoubleOrNull() ?: default

fun String?.toFloatOrDefault(default: Float = 0.0F): Float = this?.toFloatOrNull() ?: default

fun String?.toLongOrDefault(default: Long = 0L): Long = this?.toLongOrNull() ?: default

fun String?.toIntOrDefault(default: Int = 0): Int = this?.toIntOrNull() ?: default

fun String?.toShortOrDefault(default: Short = 0): Short = this?.toShortOrNull() ?: default

fun String?.toByteOrDefault(default: Byte = 0): Byte = this?.toByteOrNull() ?: default

fun String?.toBooleanOrDefault(default: Boolean = false) = this?.toBooleanStrictOrNull() ?: default

/** Bitmap KTX */
/**
 * base64转为bitmap
 * @return
 */
fun String.toBitmap(): Bitmap {
    val bytes: ByteArray = Base64.decode(this, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}

fun String?.isAbsUrl() =
    this?.let {
        it.startsWith("http://", true) || it.startsWith("https://", true)
    } ?: false

fun String?.safeTrim() = if (this.isNullOrBlank()) null else this.trim()

fun String?.isContentScheme(): Boolean = this?.startsWith("content://") == true

fun String.parseToUri(): Uri {
    return if (isContentScheme()) {
        Uri.parse(this)
    } else {
        Uri.fromFile(File(this))
    }
}

fun String.cnCompare(other: String): Int {
    return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
        Collator.getInstance(ULocale.SIMPLIFIED_CHINESE).compare(this, other)
    } else {
        java.text.Collator.getInstance(Locale.CHINA).compare(this, other)
    }
}

/**
 * 将字符串拆分为单个字符,包含emoji
 */
fun String.toStringArray(): Array<String> {
    var codePointIndex = 0
    return try {
        Array(codePointCount(0, length)) {
            val start = codePointIndex
            codePointIndex = offsetByCodePoints(start, 1)
            substring(start, codePointIndex)
        }
    } catch (e: Exception) {
        split("").toTypedArray()
    }
}
