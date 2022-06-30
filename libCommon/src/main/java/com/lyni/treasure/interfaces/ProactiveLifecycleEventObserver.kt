package com.lyni.treasure.interfaces

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

/**
 * @date 2022/5/13
 * @author Liangyong Ni
 * description [ProactiveLifecycleEventObserver]. Only for calling [observe] proactively.
 */
interface ProactiveLifecycleEventObserver : LifecycleEventObserver {
    /**
     * Call the method to add an observer for lifecycle proactively.
     * No Other Actual Meaning.
     * @param lifecycle lifecycle that will be observed
     */
    fun observe(lifecycle: Lifecycle) {
        lifecycle.addObserver(this)
    }
}