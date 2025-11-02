package com.example.myhobbiesapp.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.adapter.SolicitudAdapter
import com.example.myhobbiesapp.data.entity.RequestItem
import com.example.myhobbiesapp.firebase.FriendRepo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class SolicitudesFragment : Fragment(R.layout.fragment_solicitudes) {

    private lateinit var rv: RecyclerView
    private lateinit var tvEmpty: TextView

    private val adapter by lazy {
        SolicitudAdapter(
            onAccept = { item -> accept(item) },
            onDecline = { item -> decline(item) }
        )
    }

    private val auth by lazy { FirebaseAuth.getInstance() }
    private var listener: ValueEventListener? = null
    private var ref: DatabaseReference? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rv = view.findViewById(R.id.rvSolicitudes)
        tvEmpty = view.findViewById(R.id.tvVacioSolicitudes)

        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter
        listenRequests()
    }

    private fun listenRequests() {
        val me = auth.currentUser?.uid ?: return
        ref = FirebaseDatabase.getInstance().getReference("friendRequests").child(me)

        tvEmpty.text = "Cargando solicitudes..."
        tvEmpty.visibility = View.VISIBLE
        rv.visibility = View.GONE

        listener = ref!!.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(s: DataSnapshot) {
                if (!isAdded) return // Seguridad

                val items = s.children.mapNotNull { req ->
                    val fromUid = req.child("fromUid").getValue(String::class.java) ?: return@mapNotNull null
                    val status  = req.child("status").getValue(String::class.java) ?: "pending"
                    val id = req.key ?: return@mapNotNull null
                    RequestItem(id, fromUid, status)
                }.filter { it.status == "pending" }

                adapter.submitList(items)

                if (items.isEmpty()) {
                    tvEmpty.text = "No tienes solicitudes pendientes"
                    tvEmpty.visibility = View.VISIBLE
                    rv.visibility = View.GONE
                } else {
                    tvEmpty.visibility = View.GONE
                    rv.visibility = View.VISIBLE
                }
            }
            override fun onCancelled(error: DatabaseError) {
                if (isAdded) {
                    tvEmpty.text = "Error al cargar solicitudes"
                    tvEmpty.visibility = View.VISIBLE
                    rv.visibility = View.GONE
                }
            }
        })
    }

    private fun accept(item: RequestItem) {
        val me = auth.currentUser?.uid ?: return
        FriendRepo.acceptRequest(toUid = me, reqId = item.requestId, fromUid = item.fromUid) { ok ->
            if (!ok) Toast.makeText(requireContext(), "No se pudo aceptar", Toast.LENGTH_SHORT).show()
        }
    }

    private fun decline(item: RequestItem) {
        val me = auth.currentUser?.uid ?: return
        FriendRepo.declineRequest(me, item.requestId) { ok ->
            if (!ok) Toast.makeText(requireContext(), "No se pudo rechazar", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        listener?.let { ref?.removeEventListener(it) }
        super.onDestroyView()
    }
}