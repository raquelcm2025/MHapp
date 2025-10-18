package com.example.myhobbiesapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.sesion.SesionActiva
import com.example.myhobbiesapp.data.UsuarioDAO
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class PerfilFragment : Fragment(R.layout.fragment_perfil) {

    private lateinit var tvNombre: TextView
    private lateinit var tvCorreo: TextView
    private lateinit var tvCelular: TextView
    private lateinit var ivAvatar: ImageView

    private lateinit var btnEditarPerfil: MaterialButton
    private lateinit var btnEditarHobbies: MaterialButton
    private lateinit var btnAgregarFoto: MaterialButton
    private lateinit var btnCerrarSesion: MaterialButton

    private lateinit var chipsHobbies: ChipGroup   // opcional si aún no llenas
    private lateinit var rvGaleria: RecyclerView

    // desde Explora: id del usuario externo
    private var userIdArg: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userIdArg = arguments?.getInt(ARG_USER_ID)
    }

    override fun onViewCreated(v: View, s: Bundle?) {
        super.onViewCreated(v, s)

        // Match exacto con tus IDs
        tvNombre       = v.findViewById(R.id.tvNombreCompleto)
        tvCorreo       = v.findViewById(R.id.tvCorreo)
        tvCelular      = v.findViewById(R.id.tvCelular)
        ivAvatar       = v.findViewById(R.id.ivAvatar)

        btnEditarPerfil   = v.findViewById(R.id.btnEditarPerfil)
        btnEditarHobbies  = v.findViewById(R.id.btnEditarHobbies)
        btnAgregarFoto    = v.findViewById(R.id.btnAgregarFoto)
        btnCerrarSesion   = v.findViewById(R.id.btnCerrarSesion)

        chipsHobbies   = v.findViewById(R.id.chipsHobbies)
        rvGaleria      = v.findViewById(R.id.rvGaleria)


        btnEditarPerfil.setOnClickListener {
            val idSesion = SesionActiva.usuarioActual?.id
            val idPantalla = usuarioIdParaMostrar()
            if (idSesion != null && idSesion == idPantalla) {
                startActivity(Intent(requireContext(), com.example.myhobbiesapp.EditarPerfilActivity::class.java))
            } else {
                Toast.makeText(requireContext(), "Solo puedes editar tu propio perfil", Toast.LENGTH_SHORT).show()
            }
        }

        // Editar Hobbies (falta mejorar)
        btnEditarHobbies.setOnClickListener {
            val idSesion = SesionActiva.usuarioActual?.id ?: -1
            if (idSesion == -1) {
                Toast.makeText(requireContext(), "Inicia sesión", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val it = Intent(requireContext(), com.example.myhobbiesapp.ListaHobbiesActivity::class.java)
            it.putExtra("idUsuario", idSesion)
            startActivity(it)
        }

        // Agregar foto (próximamente)
        btnAgregarFoto.setOnClickListener {
            // Aquí luego puedes abrir tu GaleriaFotosActivity o un picker
            Toast.makeText(requireContext(), "Abrir selector de fotos (pendiente)", Toast.LENGTH_SHORT).show()
        }

        btnCerrarSesion.setOnClickListener {
            SesionActiva.usuarioActual = null
            val it = Intent(requireContext(), com.example.myhobbiesapp.AccesoActivity::class.java)
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(it)
            requireActivity().finish()
        }

        poblar()
    }

    override fun onResume() {
        super.onResume()
        poblar()
    }


    private fun usuarioIdParaMostrar(): Int =
        userIdArg ?: (SesionActiva.usuarioActual?.id ?: 0)

    private fun poblar() {
        val idMostrar = usuarioIdParaMostrar()

        if (idMostrar == 0) {
            // Sin sesión
            tvNombre.text = ""
            tvCorreo.text = ""
            tvCelular.text = ""
            ivAvatar.setImageResource(R.drawable.ic_person)
            btnEditarPerfil.isEnabled = false
            btnEditarHobbies.isEnabled = false
            Toast.makeText(requireContext(), "No hay usuario activo", Toast.LENGTH_SHORT).show()
            return
        }

        val u = try { UsuarioDAO(requireContext()).getById(idMostrar) } catch (_: Exception) { null }
        if (u == null) {
            tvNombre.text = ""
            tvCorreo.text = ""
            tvCelular.text = ""
            ivAvatar.setImageResource(R.drawable.ic_person)
            btnEditarPerfil.isEnabled = false
            btnEditarHobbies.isEnabled = false
            Toast.makeText(requireContext(), "No se encontró el usuario", Toast.LENGTH_SHORT).show()
            return
        }

        tvNombre.text  = "${u.nombre} ${u.apellido}".trim()
        tvCorreo.text  = u.correo
        tvCelular.text = u.celular
        ivAvatar.setImageResource(if (u.foto != 0) u.foto else R.drawable.ic_person)

        // Habilitar/inhabilitar edición de tu perfil
        val esMiPerfil = (SesionActiva.usuarioActual?.id == u.id)
        btnEditarPerfil.isEnabled  = esMiPerfil
        btnEditarHobbies.isEnabled = esMiPerfil
        btnAgregarFoto.isEnabled   = esMiPerfil

    }

    private fun fillHobbiesChips(hobbies: List<String>) {
        chipsHobbies.removeAllViews()
        hobbies.forEach { h ->
            val chip = Chip(requireContext()).apply {
                text = h
                isClickable = false
                isCheckable = false
            }
            chipsHobbies.addView(chip)
        }
    }

    companion object {
        private const val ARG_USER_ID = "arg_user_id"
        fun newInstance(userId: Int) = PerfilFragment().apply {
            arguments = Bundle().apply { putInt(ARG_USER_ID, userId) }
        }
    }
}
