package com.example.myhobbiesapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.ui.activity.ChatMsg

class SimpleChatAdapter(
    private val items: MutableList<ChatMsg>,
    private val me: String
) : RecyclerView.Adapter<SimpleChatAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvMine: TextView  = v.findViewById(R.id.tvMine)
        val tvOther: TextView = v.findViewById(R.id.tvOther)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_msg, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(h: VH, position: Int) {
        val m = items[position]
        val mine = (m.fromUid == me)

        h.tvMine.visibility  = if (mine) View.VISIBLE else View.GONE
        h.tvOther.visibility = if (mine) View.GONE else View.VISIBLE

        if (mine) {
            h.tvMine.text = m.text
        } else {
            h.tvOther.text = m.text
        }
    }

    override fun getItemCount() = items.size

    fun replaceAll(newItems: List<ChatMsg>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun addOne(m: ChatMsg) {
        items.add(m)
        notifyItemInserted(items.lastIndex)
    }
}
