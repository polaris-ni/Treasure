@file:Suppress("unused")

package com.lyni.treasure.lib.common.ktx

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.lyni.treasure.lib.common.utils.GsonUtil

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

/** GSON KTX */
/**
 * 将json转成T类型对象
 *
 * @param clz class
 * @return T
 */
fun <T> String.jsonToObject(clz: Class<T>): T = GsonUtil.getGsonInstance().fromJson(this, clz)

/**
 * json字符串转成list
 *
 * @return list<T>
 */
fun <T> String.jsonToList(): List<T> = GsonUtil.getGsonInstance().fromJson(this, object : TypeToken<List<T>>() {}.type)

/**
 * json字符串转换成可变数组
 * @param cls 类型
 * @return MutableList<T>
 */
fun <T> String.jsonToMutableList(cls: Class<T>): MutableList<T> {
    val jsonArray = JsonParser.parseString(this).asJsonArray
    val list = mutableListOf<T>()
    for (jsonElement in jsonArray) {
        list.add(GsonUtil.getGsonInstance().fromJson(jsonElement, cls))
    }
    return list
}

/**
 * json字符串转成map的
 *
 * @return Map<String, T>
 */
fun <T> String.jsonToMap(): Map<String, T> =
    GsonUtil.getGsonInstance().fromJson(this, object : TypeToken<Map<String, T>>() {}.type)

/**
 * json字符串转成list中有map的
 *
 * @return List<Map<String, T>>
 */
fun <T> String.jsonToListOfMap(): List<Map<String, T>> =
    GsonUtil.getGsonInstance().fromJson(this, object : TypeToken<List<Map<String, T>>>() {}.type)

/** Bitmap KTX */
/**
 * base64转为bitmap
 * @return
 */
fun String.toBitmap(): Bitmap {
    val bytes: ByteArray = Base64.decode(this, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}

