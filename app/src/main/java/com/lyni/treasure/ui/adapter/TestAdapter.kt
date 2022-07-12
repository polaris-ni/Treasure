package com.lyni.treasure.ui.adapter

import com.lyni.treasure.arch.rvx.BaseVBAdapter
import com.lyni.treasure.arch.rvx.vh.DefaultBindingViewHolder
import com.lyni.treasure.databinding.ItemMainBinding
import com.lyni.treasure.vm.model.TestItem

/**
 * @author Liangyong Ni
 * @date 2022/7/1
 * description [TestAdapter]
 */
//class TestAdapter : BaseRVAdapter<TestItem, DefaultBindingViewHolder<ItemMainBinding>>() {
//
//    override fun convert(
//        holder: DefaultBindingViewHolder<ItemMainBinding>,
//        item: TestItem
//    ) {
//        holder.binding.tvName.text = item.name
//    }
//
//    override fun requireNewViewHolder(parent: ViewGroup, viewType: Int): DefaultBindingViewHolder<ItemMainBinding> {
//        return DefaultBindingViewHolder(ItemMainBinding.inflate(LayoutInflater.from(context), parent, false))
//    }
//}
//class TestAdapter(context: Context) : BaseRVAdapter<TestItem, BaseViewHolder>(context) {
//    override fun requireNewViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
//        return BaseViewHolder(inflater.inflate(R.layout.item_main, parent, false))
//    }
//
//    override fun convert(holder: BaseViewHolder, item: TestItem, payloads: MutableList<Any>) {
//        holder.getView<TextView>(R.id.tvName).text = item.name
//    }
//
//}

class TestAdapter : BaseVBAdapter<TestItem, ItemMainBinding>() {

    override fun convert(
        holder: DefaultBindingViewHolder<ItemMainBinding>,
        item: TestItem
    ) {
        holder.binding.tvName.text = item.name
    }
}