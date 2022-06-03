@file:Suppress("unused")

package com.lyni.treasure.lib.common.ktx

import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.lyni.treasure.lib.common.utils.GsonUtil

/**
 * @date 2022/5/25
 * @author Liangyong Ni
 * description JsonKtx
 */

/**
 * 将对象转成json格式
 *
 * @return json string
 */
fun Any?.toJsonStr(): String = GsonUtil.getGsonInstance().toJson(this)

/**
 * 将json转成T类型对象
 *
 * @param clz class
 * @return T
 * @throws JsonSyntaxException 解析错误
 */
@Throws(JsonSyntaxException::class)
fun <T> String.jsonToObject(clz: Class<T>): T = GsonUtil.getGsonInstance().fromJson(this, clz)

/**
 * 将json转成T类型对象，转换失败则返回空值
 *
 * @param clz class
 * @return T or null
 */
fun <T> String.jsonToObjectOrNull(clz: Class<T>): T? = try {
    GsonUtil.getGsonInstance().fromJson(this, clz)
} catch (e: Exception) {
    e.printOnDebug()
    null
}

/**
 * 将json转成T类型对象
 *
 * @return T
 * @throws JsonSyntaxException 解析错误
 */
@Throws(JsonSyntaxException::class)
inline fun <reified T> String.jsonToObject(): T = GsonUtil.getGsonInstance().fromJson(this, T::class.java)

/**
 * 将json转成T类型对象，转换失败则返回空值
 *
 * @return T or null
 */
inline fun <reified T> String.jsonToObjectOrNull(): T? = try {
    GsonUtil.getGsonInstance().fromJson(this, T::class.java)
} catch (e: Exception) {
    e.printOnDebug()
    null
}

/**
 * json字符串转成list
 *
 * @return list<T>
 * @throws JsonSyntaxException
 * @throws JsonParseException
 */
@Throws(JsonSyntaxException::class, JsonParseException::class)
fun <T> String.jsonToList(): List<T> = GsonUtil.getGsonInstance().fromJson(this, object : TypeToken<List<T>>() {}.type)

/**
 * json字符串转成list，转换失败则返回空值
 *
 * @return list<T> or null
 */
fun <T> String.jsonToListOrNull(): List<T>? = try {
    GsonUtil.getGsonInstance().fromJson(this, object : TypeToken<List<T>>() {}.type)
} catch (e: Exception) {
    e.printOnDebug()
    null
}

/**
 * json字符串转换成可变数组
 * @param cls 类型
 * @return MutableList<T>
 * @throws JsonSyntaxException
 * @throws JsonParseException
 */
@Throws(JsonSyntaxException::class, JsonParseException::class)
fun <T> String.jsonToMutableList(cls: Class<T>): MutableList<T> {
    val jsonArray = JsonParser.parseString(this).asJsonArray
    val list = mutableListOf<T>()
    for (jsonElement in jsonArray) {
        list.add(GsonUtil.getGsonInstance().fromJson(jsonElement, cls))
    }
    return list
}

/**
 * json字符串转换成可变数组，转换失败则返回空值
 * @param cls 类型
 * @return MutableList<T> or null
 */
fun <T> String.jsonToMutableListOrNull(cls: Class<T>): MutableList<T>? = try {
    val jsonArray = JsonParser.parseString(this).asJsonArray
    val list = mutableListOf<T>()
    for (jsonElement in jsonArray) {
        list.add(GsonUtil.getGsonInstance().fromJson(jsonElement, cls))
    }
    list
} catch (e: Exception) {
    e.printOnDebug()
    null
}

/**
 * json字符串转成map
 *
 * @return Map<String, T>
 * @throws JsonSyntaxException
 * @throws JsonParseException
 */
@Throws(JsonSyntaxException::class, JsonParseException::class)
fun <T> String.jsonToMap(): HashMap<String, T> =
    GsonUtil.getGsonInstance().fromJson(this, object : TypeToken<HashMap<String, T>>() {}.type)

/**
 * json字符串转成map的，转换失败则返回空值
 *
 * @return Map<String, T> or null
 */
fun <T> String.jsonToMapOrNull(): HashMap<String, T>? = try {
    GsonUtil.getGsonInstance().fromJson(this, object : TypeToken<HashMap<String, T>>() {}.type)
} catch (e: Exception) {
    e.printOnDebug()
    null
}

/**
 * json字符串转成list中有map的
 *
 * @return List<Map<String, T>>
 */
@Throws(JsonSyntaxException::class, JsonParseException::class)
fun <T> String.jsonToListOfMap(): MutableList<HashMap<String, T>> =
    GsonUtil.getGsonInstance().fromJson(this, object : TypeToken<MutableList<HashMap<String, T>>>() {}.type)

/**
 * json字符串转成list中有map的，转换失败则返回空值
 *
 * @return List<Map<String, T>> or null
 * @throws JsonSyntaxException
 * @throws JsonParseException
 */
fun <T> String.jsonToListOfMapOrNull(): MutableList<HashMap<String, T>>? = try {
    GsonUtil.getGsonInstance().fromJson(this, object : TypeToken<MutableList<HashMap<String, T>>>() {}.type)
} catch (e: Exception) {
    e.printOnDebug()
    null
}