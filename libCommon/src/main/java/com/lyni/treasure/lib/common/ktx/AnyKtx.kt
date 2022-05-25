@file:Suppress("unused")

package com.lyni.treasure.lib.common.ktx

import kotlin.reflect.KClass

/**
 * @date 2022/3/28
 * @author Liangyong Ni
 * description AnyKtx
 */
/**
 * 利用JSON实现深拷贝功能，可能存在性能问题
 * @return 深拷贝对象
 */
inline fun <reified T : Any> Any.deepCopy(): T = this.toJsonStr().jsonToObject(T::class.java)

fun <T : Any> Any.deepCopy(clz: KClass<T>): T = this.toJsonStr().jsonToObject(clz.java)