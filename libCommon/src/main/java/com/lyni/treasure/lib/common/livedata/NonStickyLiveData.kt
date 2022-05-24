package com.lyni.treasure.lib.common.livedata

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @date 2022/5/24
 * @author Liangyong Ni
 * description 防止数据倒灌的LiveData
 */
class NonStickyLiveData<T> : MutableLiveData<T>() {

    //消息通知锁
    private val mLock: AtomicBoolean = AtomicBoolean(true)

    open inner class WrapperObserver(
        private val stick: Boolean = false, //是否是粘性观察，是的话会收到订阅之前的消息
        private val observer: Observer<in T>
    ) : Observer<T> {
        /**
         * 是否是新加入的观察者
         */
        private val isFirstObserver: AtomicBoolean = AtomicBoolean(true)
        override fun onChanged(t: T) {
            if (mLock.get() && !stick) {
                // 当前是锁定状态非粘性观察者，且是新的观察者，设置成不是新观察者
                // 如果不是新观察者，则通知
                if (isFirstObserver.compareAndSet(false, false)) {
                    observer.onChanged(t)
                }
            } else {
                //非锁定状态或粘性观察者，修改为不是新观察者，接收通知
                isFirstObserver.set(false)
                observer.onChanged(t)
            }
        }
    }

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        observe(owner, false, observer)
    }


    fun observe(owner: LifecycleOwner, stick: Boolean = false, observer: Observer<in T>) {
        //新来的观察者锁住消息通知
        mLock.set(true)
        super.observe(owner, WrapperObserver(stick, observer))
    }

    override fun observeForever(observer: Observer<in T>) {
        observeForever(false, observer)
    }

    fun observeForever(stick: Boolean = false, observer: Observer<in T>) {
        //新来的观察者锁住消息通知
        mLock.set(true)
        super.observeForever(WrapperObserver(stick, observer))
    }

    @MainThread
    override fun setValue(t: T?) {
        mLock.set(false)
        super.setValue(t)
    }

    override fun postValue(value: T?) {
        mLock.set(false)
        super.postValue(value)
    }

    /**
     * Used for cases where T is Void, to make calls cleaner.
     */
    @MainThread
    fun call() {
        value = null
    }
}
