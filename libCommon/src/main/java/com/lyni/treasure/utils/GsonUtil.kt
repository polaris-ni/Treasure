@file:Suppress("unused")

package com.lyni.treasure.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser


/**
 * @author Liangyong Ni
 * description GsonUtil
 * @date 2022/3/19
 */
object GsonUtil {
    /**
     * 不用创建对象, 直接使用就可以调用方法
     * 当使用GsonBuilder方式时属性为空的时候输出来的json字符串是有键值key的,显示形式是"key":null，而直接new出来的就没有"key":null的
     */
    private var gson: Gson = GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create()

    fun getGsonInstance() = gson

    /**
     * json 转对象
     *
     * @param strJson json string
     * @return JsonObject
     */
    fun stringToObject(strJson: String?): JsonObject {
        return JsonParser.parseString(strJson).asJsonObject
    }

    fun <T> objectToJson(obj: T): String = gson.toJson(obj)
}