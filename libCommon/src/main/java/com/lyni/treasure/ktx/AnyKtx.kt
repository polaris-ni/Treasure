@file:Suppress("unused")

package com.lyni.treasure.ktx

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

/**
 * 打印该对象
 *
 * @param raw true表示直接输出该对象，否则调用该对象的[toString]方法后打印
 */
fun Any?.print(raw: Boolean = false) = print(if (raw) this else this.toString())

/**
 * 打印该对象并换行
 *
 * @param raw true表示直接输出该对象，否则调用该对象的[toString]方法后打印
 */
fun Any?.println(raw: Boolean = false) = println(if (raw) this else this.toString())