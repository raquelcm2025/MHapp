package com.example.myhobbiesapp.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.data.dao.UsuarioDAO
import com.example.myhobbiesapp.sesion.SesionActiva
import com.example.myhobbiesapp.ui.activity.*
import com.google.android.material.button.MaterialButton

class PerfilFragment : Fragment(R.layout.fragment_perfil) {

    private lateinit var tvNomComp: TextView
    private lateinit var tvCor: TextView
    private lateinit var tvCel: TextView
    private lateinit var ivAva: ImageView
    private lateinit var btnEditar: MaterialButton
    private lateinit var btnHobbies: MaterialButton
    private lateinit var btnCerrar: MaterialButton
    private lateinit var btnAgregarFoto: MaterialButton
    private lateinit var rvGaleria: RecyclerView

    private var userIdArg: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userIdArg = arguments?.getInt(ARG_USER_ID)
    }

    override fun onViewCreated(v: View, s: Bundle?) {
        super.onViewCreated(v, s)
        tvNomComp = v.findViewById(R.id.tvNombreCompleto)
        tvCor = v.findViewById(R.id.tvCorreo)
        tvCel = v.findViewById(R.id.tvCelular)
        ivAva = v.findViewById(R.id.ivAvatar)
        btnEditar = v.findViewById(R.id.btnEditarPerfil)
        btnHobbies = v.findViewById(R.id.btnEditarHobbies)
        btnCerrar = v.findViewById(R.id.btnCerrarSesion)
        btnAgregarFoto = v.findViewById(R.id.btnAgregarFoto)
        rvGaleria = v.findViewById(R.id.rvGaleria)


        val fotos = listOf(
            R.mipmap.natacion3,
            R.mipmap.hobby_cocina,
            R.mipmap.hobby_ceramica
        )

        rvGaleria.layoutManager = GridLayoutManager(requireContext(), 3)
        rvGaleria.clipToPadding = false
        rvGaleria.setHasFixedSize(true)
        rvGaleria.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            private fun dp(px: Int) = (px * resources.displayMetrics.density).toInt()
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                val size = dp(96) // tamaño de cada miniatura (ajusta si quieres)
                val iv = ImageView(parent.context).apply {
                    layoutParams = ViewGroup.MarginLayoutParams(size, size).apply {
                        rightMargin = dp(6); bottomMargin = dp(6)
                    }
                    scaleType = ScaleType.CENTER_CROP

                }
                return object : RecyclerView.ViewHolder(iv) {}
            }
            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                (holder.itemView as ImageView).setImageResource(fotos[position % fotos.size])
            }
            override fun getItemCount(): Int = fotos.size
        }

        // Escuchar cuando el diálogo de EP avise que hay cambios
        parentFragmentManager.setFragmentResultListener("perfil_editado", viewLifecycleOwner) { _, _ ->
            poblar()
        }

        // Abrir diálogo Editar Perfil
        btnEditar.setOnClickListener {
            val idSesion = SesionActiva.usuarioActual?.id
            val idPantalla = usuarioIdParaMostrar()
            if (idSesion != null && idSesion == idPantalla) {
                com.example.myhobbiesapp.ui.dialog.DialogEditarPerfil()
                    .show(parentFragmentManager, "editarPerfil")
            } else {
                Toast.makeText(requireContext(), "Solo puedes editar tu propio perfil", Toast.LENGTH_SHORT).show()
            }
        }

        // Abrir Lista de Hobbies
        btnHobbies.setOnClickListener {
            val id = SesionActiva.usuarioActual?.id ?: usuarioIdParaMostrar().takeIf { it > 0 }
            if (id == null) {
                Toast.makeText(requireContext(), "Inicia sesión para editar tus hobbies", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val itn = Intent(requireContext(), ListaHobbiesActivity::class.java)
            itn.putExtra("idUsuario", id)
            startActivity(itn)
        }

        // Cerrar sesión
        btnCerrar.setOnClickListener {
            android.app.AlertDialog.Builder(requireContext())
                .setTitle("Cerrar sesión")
                .setMessage("¿Estás seguro de que deseas cerrar tu sesión?")
                .setPositiveButton("Confirmar") { _, _ ->
                    SesionActiva.usuarioActual = null
                    val intent = Intent(requireContext(), AccesoActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    Toast.makeText(requireContext(), "Sesión cerrada", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        btnAgregarFoto.setOnClickListener {
            Toast.makeText(requireContext(), "Próximamente: agregar foto", Toast.LENGTH_SHORT).show()
        }

        poblar()
    }

    override fun onResume() {
        super.onResume()
        poblar()
    }

    private fun usuarioIdParaMostrar(): Int {
        return userIdArg ?: SesionActiva.usuarioActual?.id ?: 0
    }

    private fun poblar() {
        val idMostrar = usuarioIdParaMostrar()
        val u = UsuarioDAO(requireContext()).getById(idMostrar)
        if (u == null) {
            Toast.makeText(requireContext(), "No se encontró el usuario", Toast.LENGTH_SHORT).show()
            tvNomComp.text = ""
            tvCor.text = ""
            tvCel.text = ""
            ivAva.setImageResource(R.drawable.ic_person)
            return
        }
        tvNomComp.text = "${u.nombre} ${u.apellido}".trim()
        tvCor.text = u.correo
        tvCel.text = u.celular
        ivAva.setImageResource(if (u.foto != 0) u.foto else R.drawable.ic_person)
    }

    companion object {
        private const val ARG_USER_ID = "arg_user_id"
        fun newInstance(userId: Int) = PerfilFragment().apply {
            arguments = Bundle().apply { putInt(ARG_USER_ID, userId) }
        }
    }
}
