package com.lyni.treasure.arch.rvx

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lyni.treasure.arch.rvx.animations.ItemAnimation
import com.lyni.treasure.arch.rvx.listeners.ItemClickListener
import com.lyni.treasure.arch.rvx.listeners.ItemLongClickListener
import com.lyni.treasure.arch.rvx.vh.BaseViewHolder
import com.lyni.treasure.ktx.onClick
import com.lyni.treasure.ktx.onLongClick

/**
 * @author Liangyong Ni
 * @date 2022/7/12
 * description [BaseDiffRVAdapter]
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
abstract class BaseDiffRVAdapter<ITEM, VH : BaseViewHolder> : RecyclerView.Adapter<VH>() {

    abstract val diffItemCallback: DiffUtil.ItemCallback<ITEM>

    private val asyncListDiffer: AsyncListDiffer<ITEM> by lazy {
        AsyncListDiffer(this, diffItemCallback).apply {
            addListListener { _, _ ->
                onCurrentListChanged()
            }
        }
    }

    private var itemClickListener: ItemClickListener<ITEM, VH>? = null
    private var itemLongClickListener: ItemLongClickListener<ITEM, VH>? = null

    private var itemAnimation: ItemAnimation? = null

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
        itemClickListener = listener
    }

    fun setOnItemLongClickListener(listener: ItemLongClickListener<ITEM, VH>) {
        itemLongClickListener = listener
    }

    fun setItemAnimation(animation: ItemAnimation) {
        this.itemAnimation = animation
    }

    fun setItems(items: List<ITEM>?) {
        asyncListDiffer.submitList(items)
    }

    fun setItem(position: Int, item: ITEM) {
        asyncListDiffer.currentList[position] = item
        notifyItemChanged(position)
    }

    fun updateItem(item: ITEM) {
        val index = asyncListDiffer.currentList.indexOf(item)
        if (index >= 0) {
            asyncListDiffer.currentList[index] = item
            notifyItemChanged(index)
        }
    }

    fun updateItem(position: Int, payload: Any) {
        val size = itemCount
        if (position in 0 until size) {
            notifyItemChanged(position, payload)
        }
    }

    fun updateItems(fromPosition: Int, toPosition: Int, payloads: Any) {
        val size = itemCount
        if (fromPosition in 0 until size && toPosition in 0 until size) {
            notifyItemRangeChanged(
                fromPosition,
                toPosition - fromPosition + 1,
                payloads
            )
        }
    }

    fun isEmpty() = asyncListDiffer.currentList.isEmpty()

    fun isNotEmpty() = asyncListDiffer.currentList.isNotEmpty()

    fun getItem(position: Int): ITEM = asyncListDiffer.currentList[position]

    /**
     * 拿到某个holder对应的子项，此方法适合在子项的点击事件中调用，确保拿到最新的[ITEM]
     *
     * @param holder    [VH]
     * @return [ITEM] - 子项，没有会抛出异常
     */
    fun getItem(holder: VH): ITEM {
        return getItem(holder.layoutPosition)
    }

    fun getItemPosition(holder: VH): Int {
        return holder.layoutPosition
    }

    fun getItems(): List<ITEM> = asyncListDiffer.currentList

    protected open fun getSpanSize(viewType: Int, position: Int) = 1

    final override fun getItemCount() = getItems().size

    final override fun getItemViewType(position: Int): Int {
        return 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val holder = requireNewViewHolder(parent, viewType)
        registerListener(holder)
        return holder
    }

    abstract fun requireNewViewHolder(parent: ViewGroup, viewType: Int): VH

    final override fun onBindViewHolder(holder: VH, position: Int) {
        getItem(getItemPosition(holder))?.let {
            convert(holder, it)
        }
    }

    open fun onCurrentListChanged() {}

    final override fun onBindViewHolder(
        holder: VH,
        position: Int,
        payloads: MutableList<Any>
    ) {
        getItem(holder.layoutPosition)?.let {
            convert(holder, it, payloads)
        }
    }

    override fun onViewAttachedToWindow(holder: VH) {
        super.onViewAttachedToWindow(holder)
        addAnimation(holder)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val manager = recyclerView.layoutManager
        recyclerViewOrNull = recyclerView
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

    private fun addAnimation(holder: BaseViewHolder) {
        itemAnimation?.let {
            if (it.itemAnimEnabled) {
                if (!it.itemAnimFirstOnly || holder.layoutPosition > it.itemAnimStartPosition) {
                    startAnimation(holder, it)
                    it.itemAnimStartPosition = holder.layoutPosition
                }
            }
        }
    }

    protected open fun startAnimation(holder: BaseViewHolder, item: ItemAnimation) {
        item.itemAnimation?.let {
            for (anim in it.getAnimators(holder.itemView)) {
                anim.setDuration(item.itemAnimDuration).start()
                anim.interpolator = item.itemAnimInterpolator
            }
        }
    }

    protected open fun convert(holder: VH, item: ITEM, payloads: MutableList<Any>) {
    }

    abstract fun convert(holder: VH, item: ITEM)

    protected open fun registerListener(holder: VH) {
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