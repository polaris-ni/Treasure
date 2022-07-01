package com.lyni.treasure.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lyni.treasure.R
import com.lyni.treasure.ktx.mainHandler
import com.lyni.treasure.ktx.onClick
import com.lyni.treasure.vm.model.TestItem

/**
 * @author Liangyong Ni
 * @date 2022/7/1
 * description [TestAdapter]
 */
class TestAdapter(private val data: MutableList<TestItem>) : RecyclerView.Adapter<TestAdapter.TestViewHolder>() {
    inner class TestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvItem: TextView

        init {
            tvItem = itemView.findViewById(R.id.tvName)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_main, parent, false)
        return TestViewHolder(view)
    }

    override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
        holder.tvItem.text = data[position].name
        holder.itemView.onClick {
            mainHandler.post(data[position].action)
        }
    }

    override fun getItemCount(): Int = data.size
}