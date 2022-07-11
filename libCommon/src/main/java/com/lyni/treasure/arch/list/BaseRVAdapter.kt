package com.lyni.treasure.arch.list

import android.annotation.SuppressLint
import android.content.Context
import android.util.SparseArray
import android.view.ViewGroup
import androidx.annotation.IntDef
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lyni.treasure.arch.list.BaseRVAdapter.Companion.ACTION_ADD
import com.lyni.treasure.arch.list.BaseRVAdapter.Companion.ACTION_ADD_LIST
import com.lyni.treasure.arch.list.BaseRVAdapter.Companion.ACTION_SWAP
import com.lyni.treasure.arch.list.animations.ItemAnimation
import com.lyni.treasure.arch.list.listeners.ItemClickListener
import com.lyni.treasure.arch.list.listeners.ItemLongClickListener
import com.lyni.treasure.arch.list.vh.BaseViewHolder
import com.lyni.treasure.ktx.onClick
import com.lyni.treasure.ktx.onLongClick
import java.util.*

/**
 * @date 2022/6/30
 * @author Liangyong Ni
 * description 通用的adapter
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
abstract class BaseRVAdapter<ITEM, VH : BaseViewHolder> : RecyclerView.Adapter<VH>() {

    companion object {
        private const val TAG = "BaseRVAdapter"
        private const val TYPE_HEADER_VIEW = Int.MIN_VALUE
        private const val TYPE_FOOTER_VIEW = Int.MAX_VALUE - 999

        /**
         * 追加一个子项
         */
        const val ACTION_ADD = 0x10

        /**
         * 在某个位置追加一个子项列表
         */
        const val ACTION_ADD_LIST = 0x11

        /**
         * 删除某个子项
         */
        const val ACTION_DELETE = 0x20

        /**
         * 删除所有子项
         */
        const val ACTION_DELETE_ALL = 0x21

        /**
         * 重新设置某个子项
         */
        const val ACTION_RESET = 0x30

        /**
         * 重新设置所有子项
         */
        const val ACTION_RESET_ALL = 0x31

        /**
         * 交换两个子项
         */
        const val ACTION_SWAP = 0x40

        /**
         * 更新某个子项
         */
        const val ACTION_UPDATE = 0x50

        @IntDef(value = [ACTION_ADD, ACTION_ADD_LIST, ACTION_DELETE, ACTION_DELETE_ALL, ACTION_RESET, ACTION_RESET_ALL, ACTION_SWAP, ACTION_UPDATE])
        @Retention(AnnotationRetention.SOURCE)
        annotation class ActionType
    }

    /** member variables */

    /**
     * 列表顶部
     */
    private val headerItems: SparseArray<EdgeView> by lazy { SparseArray() }

    /**
     * 列表底部
     */
    private val footerItems: SparseArray<EdgeView> by lazy { SparseArray() }

    /**
     * 列表项
     */
    private val items: MutableList<ITEM> = mutableListOf()

    private var itemClickListener: ItemClickListener<ITEM, VH>? = null
    private var itemLongClickListener: ItemLongClickListener<ITEM, VH>? = null

    /**
     * 子项动画
     */
    var itemAnimation: ItemAnimation? = null

    var recyclerViewOrNull: RecyclerView? = null
        private set

    val recyclerView: RecyclerView
        get() {
            checkNotNull(recyclerViewOrNull) {
                "context with be assigned after onAttachedToRecyclerView()"
            }
            return recyclerViewOrNull!!
        }

    protected val context: Context
        get() = recyclerView.context

    fun setOnItemClickListener(listener: ItemClickListener<ITEM, VH>) {
        this.itemClickListener = listener
    }

    fun setOnItemLongClickListener(listener: ItemLongClickListener<ITEM, VH>) {
        this.itemLongClickListener = listener
    }

    /**
     * 添加列表顶部
     *
     * @param header    [EdgeView]
     */
    @Synchronized
    fun addHeaderView(header: EdgeView) {
        val index = headerItems.size()
        headerItems.put(TYPE_HEADER_VIEW + headerItems.size(), header)
        notifyItemInserted(index)
    }

    /**
     * 添加列表底部
     *
     * @param footer    [EdgeView]
     */
    @Synchronized
    fun addFooterView(footer: EdgeView) {
        val index = getItemsCount() + footerItems.size()
        footerItems.put(TYPE_FOOTER_VIEW + footerItems.size(), footer)
        notifyItemInserted(index)
    }

    /**
     * 移除列表顶部
     *
     * @param header 表头
     */
    @Synchronized
    fun removeHeaderView(header: EdgeView) {
        val index = headerItems.indexOfValue(header)
        if (index >= 0) {
            headerItems.remove(index)
            notifyItemRemoved(index)
        }
    }

    /**
     * 移除列表顶部
     *
     * @param position  位置
     */
    @Synchronized
    fun removeHeaderView(position: Int) {
        headerItems.remove(position)
        notifyItemRemoved(position)
    }

    /**
     * 移除列表底部
     *
     * @param footer 列表底部
     */
    @Synchronized
    fun removeFooterView(footer: EdgeView) {
        val index = footerItems.indexOfValue(footer)
        if (index >= 0) {
            footerItems.remove(index)
            notifyItemRemoved(getItemsCount() + index - 2)
        }
    }

    /**
     * 移除指定位置列表底部
     *
     * @param position  位置
     */
    @Synchronized
    fun removeFooterView(position: Int) {
        footerItems.remove(position)
        notifyItemRemoved(getItemsCount() + position - 2)
    }

    /**
     * 设置列表内容
     *
     * @param items [ITEM]
     */
    @SuppressLint("NotifyDataSetChanged")
    @Synchronized
    fun setItems(items: List<ITEM>?) {
        val oldSize = this.items.size
        if (this.items.isNotEmpty()) {
            this.items.clear()
        }
        if (items != null) {
            this.items.addAll(items)
        }
        notifyDataSetChanged()
        onCurrentListChanged(ACTION_RESET_ALL, oldSize)
    }

    /**
     * 设置列表内容，支持差异检测
     *
     * @param items         新列表
     * @param itemCallback  [DiffUtil.ItemCallback] - 差异检测
     * @see [DiffUtil.Callback]
     * @see [DiffUtil.ItemCallback]
     */
    @Synchronized
    fun setItems(items: List<ITEM>?, itemCallback: DiffUtil.ItemCallback<ITEM>) {
        val callback = object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return itemCount
            }

            override fun getNewListSize(): Int {
                return (items?.size ?: 0) + getHeaderCount() + getFooterCount()
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val oldItem = getItem(oldItemPosition - getHeaderCount())
                    ?: return true
                val newItem = items?.getOrNull(newItemPosition - getHeaderCount())
                    ?: return true
                return itemCallback.areItemsTheSame(oldItem, newItem)
            }

            override fun areContentsTheSame(
                oldItemPosition: Int,
                newItemPosition: Int
            ): Boolean {
                val oldItem = getItem(oldItemPosition - getHeaderCount())
                    ?: return true
                val newItem = items?.getOrNull(newItemPosition - getHeaderCount())
                    ?: return true
                return itemCallback.areContentsTheSame(oldItem, newItem)
            }

            override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
                val oldItem = getItem(oldItemPosition - getHeaderCount())
                    ?: return null
                val newItem = items?.getOrNull(newItemPosition - getHeaderCount())
                    ?: return null
                return itemCallback.getChangePayload(oldItem, newItem)
            }
        }
        val diffResult = DiffUtil.calculateDiff(callback)
        val oldSize = this.items.size
        if (this.items.isNotEmpty()) {
            this.items.clear()
        }
        if (items != null) {
            this.items.addAll(items)
        }
        diffResult.dispatchUpdatesTo(this)
        onCurrentListChanged(ACTION_RESET_ALL, oldSize)
    }

    /**
     * 将位于[position]的子项修改为[item]
     *
     * @param position  修改的位置
     * @param item      新子项
     * @throws IndexOutOfBoundsException [position]越界
     */
    @Synchronized
    @Throws(IndexOutOfBoundsException::class)
    fun setItem(position: Int, item: ITEM) {
        val oldSize = getItemsCount()
        if (position < 0 || position >= oldSize) {
            throw IndexOutOfBoundsException("the position($position) is under zero or bigger than size of list($oldSize).")
        }
        this.items[position] = item
        notifyItemChanged(position + getHeaderCount())
        onCurrentListChanged(ACTION_RESET, position)
    }

    /**
     * 更新子项，子项需要存在于[items]中
     *
     * @param item 被更新的子项
     */
    @Synchronized
    fun updateItem(item: ITEM) {
        updateItem(this.items.indexOf(item))
    }

    /**
     * 更新指定位置子项
     *
     * @param position  更新的子项位置
     */
    @Synchronized
    fun updateItem(position: Int) {
        notifyItemChanged(position + getHeaderCount())
        onCurrentListChanged(ACTION_UPDATE, position)
    }

    /**
     * 局部更新子项
     *
     * @param position  更新的位置
     * @param payload   负载因子
     */
    @Synchronized
    fun updateItem(position: Int, payload: Any) {
        val size = getItemsCount()
        if (position in 0 until size) {
            notifyItemChanged(position + getHeaderCount(), payload)
            onCurrentListChanged(ACTION_UPDATE, position)
        }
    }

    /**
     * 追加添加一个子项
     *
     * @param item 需要添加的子项
     * @return 是否追加成功
     */
    @Synchronized
    fun addItem(item: ITEM): Boolean {
        val oldSize = getItemsCount()
        if (this.items.add(item)) {
            notifyItemInserted(oldSize + getHeaderCount())
            onCurrentListChanged(ACTION_ADD)
            return true
        }
        return false
    }

    /**
     * 追加若干子项
     *
     * @param newItems  新子项
     * @return 是否追加成功
     */
    @SuppressLint("NotifyDataSetChanged")
    @Synchronized
    fun addItems(newItems: List<ITEM>): Boolean {
        val oldSize = getItemsCount()
        if (this.items.addAll(newItems)) {
            if (oldSize == 0 && getHeaderCount() == 0) {
                notifyDataSetChanged()
            } else {
                notifyItemRangeInserted(oldSize + getHeaderCount(), newItems.size)
            }
            onCurrentListChanged(ACTION_ADD_LIST, oldSize, newItems.size)
            return true
        }
        return false
    }

    /**
     * 在指定位置追加若干子项
     *
     * @param position  追加的位置
     * @param newItems  追加的新子项
     * @return 是否追加成功
     */
    @Synchronized
    fun addItems(position: Int, newItems: List<ITEM>): Boolean {
        if (this.items.addAll(position, newItems)) {
            notifyItemRangeInserted(position + getHeaderCount(), newItems.size)
            onCurrentListChanged(ACTION_ADD_LIST, position, newItems.size)
            return true
        }
        return false
    }

    /**
     * 移除某个位置的子项
     *
     * @param position 移除的子项位置
     * @return [Result]
     */
    @Synchronized
    fun removeItem(position: Int): Result<Boolean> {
        return if (this.items.removeAt(position) != null) {
            notifyItemRemoved(position + getHeaderCount())
            onCurrentListChanged(ACTION_DELETE, position)
            Result.success(true)
        } else {
            Result.failure(IllegalStateException("Item at the given position($position) is not deleted properly."))
        }
    }

    /**
     * 删除某个子项
     *
     * @param item  被删除的子项
     * @return [Result]
     */
    @Synchronized
    fun removeItem(item: ITEM): Result<Boolean> {
        val position = this.items.indexOf(item)
        return removeItem(position)
    }

    /**
     * 交换子项
     *
     * @param oldPosition   以前的位置
     * @param newPosition   现在的位置
     */
    @Synchronized
    fun swapItem(oldPosition: Int, newPosition: Int) {
        val size = getItemsCount()
        if (oldPosition < 0 || oldPosition >= size || newPosition < 0 || newPosition >= size) {
            throw IndexOutOfBoundsException("position is out of bounds, the size of list is $size")
        }
        val srcPosition = oldPosition + getHeaderCount()
        val targetPosition = newPosition + getHeaderCount()
        Collections.swap(this.items, srcPosition, targetPosition)
        notifyItemMoved(srcPosition, targetPosition)
        onCurrentListChanged(ACTION_SWAP, oldPosition, newPosition)
    }

    /**
     * 清除所有子项
     */
    @SuppressLint("NotifyDataSetChanged")
    @Synchronized
    fun clearItems() {
        val oldSize = this.items.size
        this.items.clear()
        notifyDataSetChanged()
        onCurrentListChanged(ACTION_DELETE_ALL, oldSize)
    }

    /**
     * 是否为空
     *
     * @return true or false
     */
    fun isEmpty(): Boolean {
        return items.isEmpty()
    }

    /**
     * 是否不为空
     *
     * @return true or false
     */
    fun isNotEmpty(): Boolean {
        return items.isNotEmpty()
    }

    /**
     * 得到所有子项[items]数量
     *
     * @return 子项数量，不包括头部与底部
     */
    fun getItemsCount(): Int {
        return items.size
    }

    /**
     * 得到所有头部[headerItems]数量
     *
     * @return 头部数量
     */
    fun getHeaderCount(): Int {
        return headerItems.size()
    }

    /**
     * 得到所有底部[footerItems]数量
     *
     * @return 底部数量
     */
    fun getFooterCount(): Int {
        return footerItems.size()
    }

    /**
     * 拿到某个位置的子项
     *
     * @param position  位置
     * @return [ITEM] - 子项，没有会抛出异常
     */
    fun getItem(position: Int): ITEM {
        return items[position]
    }

    /**
     * 子项
     *
     * @return [List] of [ITEM]
     */
    fun getItems(): List<ITEM> {
        return items
    }

    /**
     * 拿到子项视图类型
     *
     * @param item      子项
     * @param position  子项位置
     * @return 子项View的类型
     */
    protected open fun getItemViewType(item: ITEM, position: Int): Int {
        return 0
    }

    /**
     * 当[RecyclerView.LayoutManager]为[GridLayoutManager]时，重写此方法确定SpanSize
     *
     * @param viewType  视图类型
     * @param position  位置
     * @return SpanSize
     */
    protected open fun getSpanSize(viewType: Int, position: Int): Int {
        return 1
    }

    /**
     * 得到头部[headerItems]、子项[items]与底部[footerItems]的总数量，禁止子类重写
     * @return 总数量
     */
    final override fun getItemCount(): Int {
        return getItemsCount() + getHeaderCount() + getFooterCount()
    }

    /**
     * 得到所有子项（包括顶部[headerItems]与底部[footerItems]）的视图类型
     *
     * @param position 子项位置
     * @return 视图类型
     */
    final override fun getItemViewType(position: Int): Int {
        return when {
            isHeader(position) -> TYPE_HEADER_VIEW + position
            isFooter(position) -> TYPE_FOOTER_VIEW + position - getItemsCount() - getHeaderCount()
            else -> items.getOrNull(getItemPosition(position))?.let {
                getItemViewType(it, getItemPosition(position))
            } ?: 0
        }
    }

    /**
     * 当对子项[items]进行操作时回调
     *
     * @param action    操作类型[ActionType]
     * @param params    与该操作对应的参数，具体如下：
     * * [ACTION_ADD]           没有附带参数
     * * [ACTION_ADD_LIST]      新增子项的位置、新增子项列表的长度
     * * [ACTION_DELETE]        删除子项的位置
     * * [ACTION_DELETE_ALL]    删除子项的数量
     * * [ACTION_RESET]         重新设置的子项位置
     * * [ACTION_RESET_ALL]     重新设置之前的子项列表[items]的数量
     * * [ACTION_SWAP]          两个子项被交换之前的位置
     * * [ACTION_UPDATE]        更新的子项位置
     *
     * @see [ActionType]
     */
    open fun onCurrentListChanged(@ActionType action: Int, vararg params: Int) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return when {

            viewType < TYPE_HEADER_VIEW + getHeaderCount() -> requireHeaderView(
                parent,
                viewType,
                headerItems.get(viewType)
            )

            viewType >= TYPE_FOOTER_VIEW -> requireFooterView(parent, viewType, footerItems.get(viewType))

            else -> {
                val holder = requireNewViewHolder(parent, viewType)
                registerListener(holder)
                holder
            }
        }
    }

    /**
     * 子类重写此函数创建[VH]
     *
     * @param parent    [ViewGroup]
     * @param viewType  视图类型
     * @return [VH]
     */
    abstract fun requireNewViewHolder(parent: ViewGroup, viewType: Int): VH

    /**
     * 创建头部视图的ViewHolder，默认使用[BaseViewHolder]代替
     *
     * @param parent        [ViewGroup]
     * @param viewType      视图类型
     * @param initializer   [EdgeView]
     * @return [VH]
     */
    open fun requireHeaderView(
        parent: ViewGroup,
        viewType: Int,
        initializer: EdgeView
    ): VH {
        @Suppress("UNCHECKED_CAST")
        return BaseViewHolder(initializer.initView(parent, viewType)) as VH
    }

    /**
     * 创建底部视图的ViewHolder，默认使用[BaseViewHolder]代替
     *
     * @param parent        [ViewGroup]
     * @param viewType      视图类型
     * @param initializer   [EdgeView]
     * @return [VH]
     */
    open fun requireFooterView(
        parent: ViewGroup,
        viewType: Int,
        initializer: EdgeView
    ): VH {
        @Suppress("UNCHECKED_CAST")
        return BaseViewHolder(initializer.initView(parent, viewType)) as VH
    }

    final override fun onBindViewHolder(holder: VH, position: Int) {
        if (!isHeader(holder.layoutPosition) && !isFooter(holder.layoutPosition)) {
            getItem(getItemPosition(holder))?.let {
                convert(holder, it)
            }
        }
    }

    final override fun onBindViewHolder(holder: VH, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
            return
        }
        if (!isHeader(holder.layoutPosition) && !isFooter(holder.layoutPosition)) {
            getItem(getItemPosition(holder))?.let {
                convert(holder, it, payloads)
            }
        }
    }

    override fun onViewAttachedToWindow(holder: VH) {
        super.onViewAttachedToWindow(holder)
        if (!isHeader(holder.layoutPosition) && !isFooter(holder.layoutPosition)) {
            addAnimation(holder)
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerViewOrNull = recyclerView
        val manager = recyclerView.layoutManager
        if (manager is GridLayoutManager) {
            manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return getSpanSize(getItemViewType(position), position)
                }
            }
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        recyclerViewOrNull = null
    }

    /**
     * 判断某位置的子项是否是头部
     *
     * @param position  位置
     * @return true or false
     */
    fun isHeader(position: Int): Boolean {
        return position < getHeaderCount()
    }

    /**
     * 判断某位置的子项是否是底部
     *
     * @param position  位置
     * @return true or false
     */
    fun isFooter(position: Int): Boolean {
        return position >= getItemsCount() + getHeaderCount()
    }

    /**
     * 得到[layoutPosition]在[items]中的位置
     *
     * @param layoutPosition  位置
     */
    private fun getItemPosition(layoutPosition: Int): Int {
        return layoutPosition - getHeaderCount()
    }

    /**
     * 得到holder指代的子项在[items]中的位置
     *
     * @param holder    [BaseViewHolder]
     * @return [Int] - position
     */
    private fun getItemPosition(holder: VH): Int {
        return holder.layoutPosition - getHeaderCount()
    }

    /**
     * 添加动画
     *
     * @param holder    [VH]
     */
    private fun addAnimation(holder: VH) {
        itemAnimation?.let {
            if (it.itemAnimEnabled) {
                if (!it.itemAnimFirstOnly || holder.layoutPosition > it.itemAnimStartPosition) {
                    startAnimation(holder, it)
                    it.itemAnimStartPosition = holder.layoutPosition
                }
            }
        }
    }

    /**
     * 启动动画
     *
     * @param holder    [VH]
     * @param item      [ItemAnimation]
     */
    protected open fun startAnimation(holder: BaseViewHolder, item: ItemAnimation) {
        item.itemAnimation?.let {
            for (anim in it.getAnimators(holder.itemView)) {
                anim.setDuration(item.itemAnimDuration).start()
                anim.interpolator = item.itemAnimInterpolator
            }
        }
    }

    /**
     * 在此处进行View的显示，只会在子项[items]中回调，头部[headerItems]和底部[footerItems]不会回调此方法
     *
     * @param holder    [VH]
     * @param item      [ITEM]
     * @param payloads  负载因子集合
     */
    open fun convert(holder: VH, item: ITEM, payloads: MutableList<Any>) {}

    /**
     * 在此处进行View的显示，只会在子项[items]中回调，头部[headerItems]和底部[footerItems]不会回调此方法
     *
     * @param holder    [VH]
     * @param item      [ITEM]
     */
    abstract fun convert(holder: VH, item: ITEM)

    /**
     * 注册点击事件，只会在[onCreateViewHolder]时调用
     *
     * @param holder    [VH]
     */
    open fun registerListener(holder: VH) {
        itemClickListener?.let { listener ->
            holder.itemView.onClick {
                val position = getItemPosition(holder)
                listener.onItemClick(holder, getItem(position), position)
            }
        }
        itemLongClickListener?.let { listener ->
            holder.itemView.onLongClick {
                val position = getItemPosition(holder)
                listener.onItemLongClick(holder, getItem(position), position)
            }
        }
    }
}




