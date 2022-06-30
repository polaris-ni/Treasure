@file:Suppress("unused")

package com.lyni.treasure.utils

import android.content.ContentResolver
import android.content.Context
import android.database.ContentObserver
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Handler
import android.os.HandlerThread
import android.provider.MediaStore
import java.util.*

/**
 * @date 2022/3/4
 * @author Liangyong Ni
 * description 截图工具类
 */
object ScreenShot {
    private const val TAG = "ScreenShot"

    private var isRVShot = false
    var bitmapCache: Bitmap? = null

    private val MEDIA_PROJECTIONS = arrayOf(
        MediaStore.Images.ImageColumns.DATA,
        MediaStore.Images.ImageColumns.DATE_MODIFIED,
        MediaStore.Images.ImageColumns.DATE_ADDED
    )

    /**
     * 截屏依据中的路径判断关键字
     */
    private val KEYWORDS = arrayOf(
        "screenshot", "screen_shot", "screen-shot", "screen shot", "screencapture",
        "screen_capture", "screen-capture", "screen capture", "screencap", "screen_cap",
        "screen-cap", "screen cap", "截屏"
    )

    private var mContentResolver: ContentResolver? = null
    private var mCallbackListener: CallbackListener? = null
    private var mInternalObserver: MediaContentObserver? = null
    private var mExternalObserver: MediaContentObserver? = null

    //private val mExitTime: Long = 0

    /**
     * 注册
     *
     * @param context          上下文
     * @param callbackListener 回调监听
     */
    fun register(context: Context, callbackListener: CallbackListener?) {
        mContentResolver = context.contentResolver
        mCallbackListener = callbackListener
        val handlerThread = HandlerThread(TAG)
        handlerThread.start()
        val handler = Handler(handlerThread.looper)
        mInternalObserver =
            MediaContentObserver(MediaStore.Images.Media.INTERNAL_CONTENT_URI, handler)
        mExternalObserver =
            MediaContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, handler)
        mContentResolver?.registerContentObserver(
            MediaStore.Images.Media.INTERNAL_CONTENT_URI,
            true, mInternalObserver!!
        )
        mContentResolver?.registerContentObserver(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            true, mExternalObserver!!
        )
    }

    /**
     * 注销
     */
    fun unregister() {
        if (mContentResolver != null) {
            mContentResolver!!.unregisterContentObserver(mInternalObserver!!)
            mContentResolver!!.unregisterContentObserver(mExternalObserver!!)
        }
        mCallbackListener?.let {
            mCallbackListener = null

        }
        mContentResolver?.let {
            mContentResolver = null
        }

    }

    private fun handleMediaContentChange(uri: Uri) {
        var cursor: Cursor? = null
        try {
            // 数据改变时，查询数据库中最后加入的一条数据
            cursor = mContentResolver!!.query(
                uri, MEDIA_PROJECTIONS, null, null,
                MediaStore.Images.ImageColumns.DATE_ADDED + " desc limit 1"
            )
            if (cursor == null) {
                return
            }
            if (!cursor.moveToFirst()) {
                return
            }
            val dataIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            // 处理获取到的第一行数据
            handleMediaRowData(cursor.getString(dataIndex))
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
        }
    }

    /**
     * 处理监听到的资源
     */
    private fun handleMediaRowData(path: String) {
        if (checkScreenShot(path)) {
            if (mCallbackListener != null) {
                println(Thread.currentThread().name + " begin time: " + System.currentTimeMillis())
                mCallbackListener!!.onShot(path)
                println(Thread.currentThread().name + " end time: " + System.currentTimeMillis())
            }
        }
    }

    /**
     * 判断是否是截屏
     */
    private fun checkScreenShot(data: String): Boolean {
        val mData = data.lowercase(Locale.getDefault())
        // 判断图片路径是否含有指定的关键字之一, 如果有, 则认为当前截屏了
        for (keyWork in KEYWORDS) {
            if (mData.contains(keyWork)) {
                return true
            }
        }
        return false
    }

    private var lastTime: Long = 0

    /**
     * 媒体内容观察者
     */
    private class MediaContentObserver(
        private val mUri: Uri,
        handler: Handler?
    ) : ContentObserver(handler) {
        override fun onChange(selfChange: Boolean) {
            super.onChange(selfChange)
            val current = System.currentTimeMillis()
            if (current - lastTime > 1000) {
                lastTime = current
                Log.d(TAG, "图片数据库发生变化：$current")
                handleMediaContentChange(mUri)
            }
        }
    }

    /**
     * 回调监听器
     */
    interface CallbackListener {
        /**
         * 截屏
         *
         * @param path 图片路径
         */
        fun onShot(path: String?)
    }

    fun startRvShoot() {
        isRVShot = true
    }

    fun finishRvShoot() {
        isRVShot = false
        bitmapCache?.recycle()
        bitmapCache = null
    }
}