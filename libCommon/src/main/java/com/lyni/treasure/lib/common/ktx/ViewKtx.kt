@file:Suppress("unused")

package com.lyni.treasure.lib.common.ktx

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.text.InputFilter
import android.util.LruCache
import android.view.MotionEvent
import android.view.TouchDelegate
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.coordinatorlayout.widget.ViewGroupUtils
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.lyni.treasure.lib.common.utils.EditTextUtil
import com.lyni.treasure.lib.common.utils.ScreenShot

/**
 * @date 2022/3/4
 * @author Liangyong Ni
 * description View的扩展类
 */
/**
 * 使View visible
 */
fun View.visible() {
    if (!isVisible) {
        visibility = View.VISIBLE
    }
}

/**
 * 使View gone
 */
fun View.gone() {
    visibility = View.GONE
}

/**
 * 使View invisible
 */
fun View.invisible() {
    visibility = View.INVISIBLE
}

/**
 * View是否可见
 */
val View.isVisible: Boolean
    get() {
        return visibility == View.VISIBLE
    }

/**
 * 设置点击事件
 * @param listener 点击事件
 */
fun View.onClick(listener: (View) -> Unit) {
    this.setOnClickListener(listener)
}

/**
 * 设置长按事件
 * @param consume 是否被消费
 * @param listener 执行时间
 */
inline fun View.onLongClick(
    consume: Boolean = true,
    crossinline listener: () -> Unit
) = setOnLongClickListener { listener.invoke(); consume }

/**
 * 获取有效的Edittext字符串
 * @return str
 */
fun EditText.getEffectiveText(): String {
    return this.text.toString().trim()
}

/**
 * EditText拦截emoji
 */
fun EditText.prohibitEmoji(emojiCall: () -> Unit = {}) {
    this.addFilter(EditTextUtil.getInputFilterProhibitEmoji(emojiCall))
}

/**
 * EditText拦截特殊字符
 */
fun EditText.prohibitSP() {
    this.addFilter(EditTextUtil.getInputFilterProhibitSP())
}

/**
 * EditText拦截空格
 */
fun EditText.prohibitSpace() {
    this.addFilter(EditTextUtil.getInputFilterProhibitSpace())
}


/**
 * EditText新增拦截器
 * @param filter 拦截器
 */
fun EditText.addFilter(filter: InputFilter) {
    this.filters = filters.toMutableList().apply {
        add(filter)
    }.toTypedArray()
}

/**
 * 扩大view的点击区域
 */
@SuppressLint("RestrictedApi")
fun View.expand(dx: Int, dy: Int) {
    // 将刚才定义代理类放到方法内部，调用方不需要了解这些细节
    class MultiTouchDelegate(bound: Rect? = null, delegateView: View) :
        TouchDelegate(bound, delegateView) {
        val delegateViewMap = mutableMapOf<View, Rect>()
        private var delegateView: View? = null

        override fun onTouchEvent(event: MotionEvent): Boolean {
            val x = event.x.toInt()
            val y = event.y.toInt()
            var handled = false
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    delegateView = findDelegateViewUnder(x, y)
                }
                MotionEvent.ACTION_CANCEL -> {
                    delegateView = null
                }
            }
            delegateView?.let {
                event.setLocation(it.width / 2f, it.height / 2f)
                handled = it.dispatchTouchEvent(event)
            }
            return handled
        }

        private fun findDelegateViewUnder(x: Int, y: Int): View? {
            delegateViewMap.forEach { entry -> if (entry.value.contains(x, y)) return entry.key }
            return null
        }
    }

    // 获取当前控件的父控件
    val parentView = parent as? ViewGroup
    // 若父控件不是 ViewGroup, 则直接返回
    parentView ?: return
    // 若父控件未设置触摸代理，则构建 MultiTouchDelegate 并设置给它
    if (parentView.touchDelegate == null) parentView.touchDelegate =
        MultiTouchDelegate(delegateView = this)
    post {
        val rect = Rect()
        // 获取子控件在父控件中的区域
        ViewGroupUtils.getDescendantRect(parentView, this, rect)
        // 将响应区域扩大
        rect.inset(-dx, -dy)
        // 将子控件作为代理控件添加到 MultiTouchDelegate 中
        (parentView.touchDelegate as? MultiTouchDelegate)?.delegateViewMap?.put(this, rect)
    }
}

/**
 * 获取和View一样的Bitmap对象
 * @return bitmap
 */
fun View.getBitmap(): Bitmap {
    //创建一个和View大小一样的Bitmap
    val bitmap: Bitmap = Bitmap.createBitmap(
        this.width, this.height,
        Bitmap.Config.RGB_565
    )
    //使用上面的Bitmap创建canvas
    val canvas = Canvas(bitmap)
    canvas.drawColor(Color.WHITE)
    //把View画到Bitmap上
    this.draw(canvas)
    return bitmap
}

/**
 * 获取recyclerview的整个屏幕截图
 * @param  onFinish 结束回调
 */
fun RecyclerView.getBitmap(onFinish: (Bitmap) -> Unit) {
    ScreenShot.startRvShoot()
    val adapter = this.adapter!!
    //创建保存截图的bitmap
    val bigBitmap: Bitmap?
    //获取item的数量
    val size = adapter.itemCount
    //recycler的完整高度 用于创建bitmap时使用
    var height = 0
    //获取最大可用内存
    val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()

    // 使用1/8的缓存
    val cacheSize = maxMemory / 8
    //把每个item的绘图缓存存储在LruCache中
    val bitmapCache: LruCache<String, Bitmap> = LruCache(cacheSize)
    val viewCache = arrayListOf<View>()
    for (i in 0 until size) {
        //手动调用创建和绑定ViewHolder方法，
        val holder = adapter.createViewHolder(this, adapter.getItemViewType(i))
        adapter.onBindViewHolder(holder, i)
        //测量
        holder.itemView.measure(
            View.MeasureSpec.makeMeasureSpec(this.width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        //布局
        holder.itemView.layout(
            0, 0, holder.itemView.measuredWidth,
            holder.itemView.measuredHeight
        )
        if (holder.itemView.javaClass != View::class.java) {
            viewCache.add(holder.itemView)
        }
    }

    for ((index, view) in viewCache.withIndex()) {
        //开启绘图缓存
        view.isDrawingCacheEnabled = true
        view.buildDrawingCache()
        val drawingCache = view.drawingCache
        if (drawingCache != null) {
            bitmapCache.put(index.toString(), drawingCache)
            height += drawingCache.height
        } else {
            if (view is ViewGroup) {
                for (j in 0 until view.childCount) {
                    if (view[j] is ImageView)
                        ScreenShot.bitmapCache?.let {
                            bitmapCache.put(index.toString(), it)
                            height += it.height
                            ScreenShot.bitmapCache = null
                        }
                }
            }
        }

    }
    bigBitmap = Bitmap.createBitmap(this.measuredWidth, height, Bitmap.Config.RGB_565)

    //创建一个canvas画板
    val canvas = Canvas(bigBitmap!!)
    //获取recyclerView的背景颜色
    val background: Drawable = ColorDrawable(Color.WHITE)
    //画出recyclerView的背景色 这里只用了color一种 有需要也可以自己扩展
    if (background is ColorDrawable) {
        val color = background.color
        canvas.drawColor(color)
    }
    //当前bitmap的高度
    var top = 0
    //画笔
    val paint = Paint()
    for (i in 0 until bitmapCache.size()) {
        val bitmap = bitmapCache[i.toString()]
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, 0f, top.toFloat(), paint)
            top += bitmap.height
        }
    }
    onFinish(bigBitmap)
    ScreenShot.finishRvShoot()
}

/**
 * 获取NestedScrollView截图
 * @return bitmap
 */
fun NestedScrollView.shotScrollView(): Bitmap {
    var h = 0
    var i = 0
    while (i < this.childCount) {
        h += this.getChildAt(i).height
        this.getChildAt(i).setBackgroundColor(Color.parseColor("#ffffff"))
        i++
    }
    val bitmap = Bitmap.createBitmap(this.width, h, Bitmap.Config.RGB_565)
    val canvas = Canvas(bitmap)
    this.draw(canvas)
    return bitmap
}

/**
 * Recyclerview滑动到顶部
 */
fun RecyclerView.scrollToTop() {
    val smoothScroller: LinearSmoothScroller = LinearTopSmoothScroller(this.context)
    smoothScroller.targetPosition = 0
    this.layoutManager?.startSmoothScroll(smoothScroller)
}

/**
 * RecyclerView滑动到底部
 */
fun RecyclerView.scrollToBottom() {
    val smoothScroller: LinearSmoothScroller = LinearBottomSmoothScroller(this.context)
    val position = (this.adapter?.itemCount ?: 1) - 1
    smoothScroller.targetPosition = position
    this.layoutManager?.startSmoothScroll(smoothScroller)
}

class LinearTopSmoothScroller(context: Context?) :
    LinearSmoothScroller(context) {
    override fun getVerticalSnapPreference(): Int {
        return SNAP_TO_START
    }
}

class LinearBottomSmoothScroller(context: Context?) :
    LinearSmoothScroller(context) {
    override fun getVerticalSnapPreference(): Int {
        return SNAP_TO_END
    }
}

