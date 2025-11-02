package com.example.myhobbiesapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.data.entity.RequestItem
import com.google.android.material.button.MaterialButton

class SolicitudAdapter(
    private val onAccept: (RequestItem) -> Unit,
    private val onDecline: (RequestItem) -> Unit
) : ListAdapter<RequestItem, SolicitudAdapter.VH>(DIFF) {

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<RequestItem>() {
            override fun areItemsTheSame(a: RequestItem, b: RequestItem) = a.requestId == b.requestId
            override fun areContentsTheSame(a: RequestItem, b: RequestItem) = a == b
        }
    }

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvFrom: TextView = v.findViewById(R.id.tvFromUid)
        val btnOk: MaterialButton = v.findViewById(R.id.btnAceptar)
        val btnNo: MaterialButton = v.findViewById(R.id.btnRechazar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_solicitud, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(h: VH, pos: Int) {
        val item = getItem(pos)
        h.tvFrom.text = item.fromUid
        h.btnOk.setOnClickListener { onAccept(item) }
        h.btnNo.setOnClickListener { onDecline(item) }
    }
}
