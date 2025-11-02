package com.example.myhobbiesapp.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.adapter.UsuarioAdapter
import com.example.myhobbiesapp.adapter.UsuarioItem
import com.example.myhobbiesapp.ui.dialog.DialogOpcionesExplora
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ExploraFragment : Fragment(R.layout.fragment_explora) {

    private lateinit var rv: RecyclerView
    private lateinit var tvEmpty: TextView
    private val adapter by lazy { UsuarioAdapter(::onVerMas) }

    private var userIndexListener: ValueEventListener? = null
    private var userIndexRef: DatabaseReference? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rv = view.findViewById(R.id.rvPerfiles)
        tvEmpty = view.findViewById(R.id.tvVacioExplora)

        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter

        cargarPerfilesDeFirebase()
    }

    private fun cargarPerfilesDeFirebase() {
        val myUid = FirebaseAuth.getInstance().currentUser?.uid
        if (myUid == null) {
            tvEmpty.text = "No se pudo cargar (sin sesión)"
            tvEmpty.visibility = View.VISIBLE
            rv.visibility = View.GONE
            return
        }

        userIndexListener?.let { userIndexRef?.removeEventListener(it) }

        val ref = FirebaseDatabase.getInstance().getReference("userIndex")
        userIndexRef = ref

        userIndexListener = ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!isAdded) return

                val listaDeUsuarios = mutableListOf<UsuarioItem>()

                snapshot.children.forEach { userSnapshot ->
                    val uid = userSnapshot.key

                    if (uid != null && uid != myUid) {


                        val nombre = userSnapshot.child("nombre").getValue(String::class.java) ?: ""
                        val apPaterno = userSnapshot.child("apellidoPaterno").getValue(String::class.java) ?: ""
                        val correo = userSnapshot.child("correo").getValue(String::class.java) ?: ""

                        if (correo.isNotEmpty()) {
                            listaDeUsuarios.add(
                                UsuarioItem(
                                    uid = uid,
                                    nombre = "$nombre $apPaterno".trim(),
                                    correo = correo
                                )
                            )
                        }
                    }
                }

                adapter.submitList(listaDeUsuarios)

                if (listaDeUsuarios.isEmpty()) {
                    tvEmpty.text = "Aún no hay otros usuarios"
                    tvEmpty.visibility = View.VISIBLE
                    rv.visibility = View.GONE
                } else {
                    tvEmpty.visibility = View.GONE
                    rv.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                if (!isAdded) return
                tvEmpty.text = "Error al cargar usuarios"
                tvEmpty.visibility = View.VISIBLE
                rv.visibility = View.GONE
                Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun onVerMas(uid: String) {
        DialogOpcionesExplora.newInstance(uid)
            .show(parentFragmentManager, "dialog_opciones_explora")
    }

    override fun onDestroyView() {
        userIndexListener?.let { userIndexRef?.removeEventListener(it) }
        userIndexListener = null
        userIndexRef = null
        super.onDestroyView()
    }
}