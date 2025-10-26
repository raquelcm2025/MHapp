package com.example.myhobbiesapp.adapter

import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class GaleriaAdapter : RecyclerView.Adapter<GaleriaAdapter.VH>() {
    private val data = mutableListOf<String>() // URIs en string

    fun submit(list: List<String>) {
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(p: ViewGroup, vt: Int): VH {
        val d = p.resources.displayMetrics.density
        val size = (120 * d).toInt()
        val margin = (8 * d).toInt()

        val iv = ImageView(p.context).apply {
            layoutParams = ViewGroup.MarginLayoutParams(size, size).apply { rightMargin = margin }
            scaleType = ImageView.ScaleType.CENTER_CROP
            clipToOutline = true
        }
        return VH(iv)
    }

    override fun onBindViewHolder(h: VH, i: Int) {
        val uri = Uri.parse(data[i])
        (h.itemView as ImageView).setImageURI(uri)
    }

    override fun getItemCount(): Int = data.size

    class VH(v: View) : RecyclerView.ViewHolder(v)
}
