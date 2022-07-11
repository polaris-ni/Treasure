package com.lyni.treasure.vm.model

/**
 * @author Liangyong Ni
 * @date 2022/7/1
 * description [TestItem]
 */
data class TestItem(
    var name: String,
    val action: Runnable
)