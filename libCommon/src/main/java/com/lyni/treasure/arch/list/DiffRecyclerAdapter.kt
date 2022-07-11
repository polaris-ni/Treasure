package com.lyni.treasure.arch.list

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.lyni.treasure.arch.list.animations.ItemAnimation
import com.lyni.treasure.arch.list.vh.BaseViewHolder
import com.lyni.treasure.ktx.onLongClick

@Deprecated("下一版本重做")
abstract class DiffRecyclerAdapter<ITEM, VB : ViewBinding>(protected val context: Context) :
    RecyclerView.Adapter<BaseViewHolder>() {

    /**
     * 布局加载器
     */
    val inflater: LayoutInflater = LayoutInflater.from(context)

    abstract val diffItemCallback: DiffUtil.ItemCallback<ITEM>

    private val asyncListDiffer: AsyncListDiffer<ITEM> by lazy {
        AsyncListDiffer(this, diffItemCallback).apply {
            addListListener { _, _ ->
                onCurrentListChanged()
            }
        }
    }

    private var itemClickListener: ((holder: BaseViewHolder, item: ITEM) -> Unit)? = null
    private var itemLongClickListener: ((holder: BaseViewHolder, item: ITEM) -> Boolean)? = null

    var itemAnimation: ItemAnimation? = null

    fun setOnItemClickListener(listener: (holder: BaseViewHolder, item: ITEM) -> Unit) {
        itemClickListener = listener
    }

    fun setOnItemLongClickListener(listener: (holder: BaseViewHolder, item: ITEM) -> Boolean) {
        itemLongClickListener = listener
    }

    fun bindToRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = this
    }

    fun setItems(items: List<ITEM>?) {
        kotlin.runCatching {
            asyncListDiffer.submitList(items?.toMutableList())
        }
    }

    fun setItem(position: Int, item: ITEM) {
        kotlin.runCatching {
            asyncListDiffer.currentList[position] = item
            notifyItemChanged(position)
        }
    }

    fun updateItem(item: ITEM) {
        kotlin.runCatching {
            val index = asyncListDiffer.currentList.indexOf(item)
            if (index >= 0) {
                asyncListDiffer.currentList[index] = item
                notifyItemChanged(index)
            }
        }
    }

    fun updateItem(position: Int, payload: Any) {
        kotlin.runCatching {
            val size = itemCount
            if (position in 0 until size) {
                notifyItemChanged(position, payload)
            }
        }
    }

    fun updateItems(fromPosition: Int, toPosition: Int, payloads: Any) {
        kotlin.runCatching {
            val size = itemCount
            if (fromPosition in 0 until size && toPosition in 0 until size) {
                notifyItemRangeChanged(
                    fromPosition,
                    toPosition - fromPosition + 1,
                    payloads
                )
            }
        }
    }

    fun isEmpty() = asyncListDiffer.currentList.isEmpty()

    fun isNotEmpty() = asyncListDiffer.currentList.isNotEmpty()

    fun getItem(position: Int): ITEM? = asyncListDiffer.currentList.getOrNull(position)

    fun getItems(): List<ITEM> = asyncListDiffer.currentList

    /**
     * grid 模式下使用
     */
    protected open fun getSpanSize(viewType: Int, position: Int) = 1

    final override fun getItemCount() = getItems().size

    final override fun getItemViewType(position: Int): Int {
        return 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val holder = BaseViewHolder(getViewBinding(parent).root)

        @Suppress("UNCHECKED_CAST")
//        registerListener(holder, (holder.binding as VB))

        if (itemClickListener != null) {
            holder.itemView.setOnClickListener {
                getItem(holder.layoutPosition)?.let {
                    itemClickListener?.invoke(holder, it)
                }
            }
        }

        if (itemLongClickListener != null) {
            holder.itemView.onLongClick {
                getItem(holder.layoutPosition)?.let {
                    itemLongClickListener?.invoke(holder, it)
                }
            }
        }

        return holder
    }

    protected abstract fun getViewBinding(parent: ViewGroup): VB

    final override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {}

    open fun onCurrentListChanged() {}

    @Suppress("UNCHECKED_CAST")
    final override fun onBindViewHolder(
        holder: BaseViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        getItem(holder.layoutPosition)?.let {
//            convert(holder, (holder.binding as VB), it, payloads)
        }
    }

    override fun onViewAttachedToWindow(holder: BaseViewHolder) {
        super.onViewAttachedToWindow(holder)
        addAnimation(holder)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val manager = recyclerView.layoutManager
        if (manager is GridLayoutManager) {
            manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return getSpanSize(getItemViewType(position), position)
                }
            }
        }
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

    /**
     * 如果使用了事件回调,回调里不要直接使用item,会出现不更新的问题,
     * 使用getItem(holder.layoutPosition)来获取item
     */
    abstract fun convert(
        holder: BaseViewHolder,
        binding: VB,
        item: ITEM,
        payloads: MutableList<Any>
    )

    /**
     * 注册事件
     */
    abstract fun registerListener(holder: BaseViewHolder, binding: VB)

}