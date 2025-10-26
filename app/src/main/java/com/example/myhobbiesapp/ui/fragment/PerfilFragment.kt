package com.example.myhobbiesapp.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.data.dao.HobbyDAO
import com.example.myhobbiesapp.data.dao.UsuarioDAO
import com.example.myhobbiesapp.databinding.FragmentPerfilBinding
import com.example.myhobbiesapp.ui.activity.AccesoActivity
import com.example.myhobbiesapp.ui.dialog.DialogAgregarHobby
import com.example.myhobbiesapp.ui.dialog.DialogEditarPerfil
import com.example.myhobbiesapp.ui.dialog.SimpleRenameHobbySheet
import com.example.myhobbiesapp.util.SessionManager
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PerfilFragment : Fragment() {

    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        s: Bundle?
    ): View {
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(v: View, s: Bundle?) {
        super.onViewCreated(v, s)

        // Cargar datos y hobbies al entrar
        cargarPerfilYHobbies()

        // Refrescar cuando se agregue/edite/elimine un hobby
        parentFragmentManager.setFragmentResultListener(
            DialogAgregarHobby.RESULT_HOBBIES_CHANGED,
            viewLifecycleOwner
        ) { _, _ -> cargarPerfilYHobbies() }

        // Botón “Añadir / Editar hobbies”
        binding.btnEditarHobbies.setOnClickListener {
            usuarioActual()?.let { u ->
                DialogAgregarHobby.newInstance(u.id)
                    .show(parentFragmentManager, "add_hobby")
            }
        }

        // Botón “Editar perfil”
        binding.btnEditarPerfil.setOnClickListener {
            DialogEditarPerfil.newInstance()
                .show(parentFragmentManager, "editar_perfil")
        }

        // Si desde el diálogo de edición de perfil emites “perfil_editado”
        parentFragmentManager.setFragmentResultListener(
            "perfil_editado",
            viewLifecycleOwner
        ) { _, _ -> cargarPerfilYHobbies() }

        // Botón “Agregar foto”
        binding.btnAgregarFoto.setOnClickListener {
        }

        binding.btnCerrarSesion.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Cerrar sesión")
                .setMessage("¿Deseas cerrar tu sesión?")
                .setPositiveButton("Confirmar") { _, _ ->
                    SessionManager.clear(requireContext())
                    startActivity(Intent(requireContext(), AccesoActivity::class.java))
                    activity?.finish()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    /** Carga el usuario actual y pinta sus datos + hobbies (o “Sin sesión”) */
    private fun cargarPerfilYHobbies() {
        val u = usuarioActual()
        if (u != null) {
            val nombreCompleto = "${u.nombre} ${u.apellidoPaterno} ${u.apellidoMaterno}".trim()
            binding.tvNombreCompleto.text = nombreCompleto
            binding.tvCorreo.text = u.correo
            binding.tvCelular.text = u.celular

            val fotoRes = when (u.genero?.lowercase()) {
                "femenino", "mujer" -> R.drawable.ic_mujer
                "masculino", "hombre" -> R.drawable.ic_hombre
                else -> R.drawable.ic_person
            }
            binding.ivAvatar.setImageResource(fotoRes)

            renderHobbies(u.id)
        } else {
            binding.tvNombreCompleto.text = "Sin sesión"
            binding.tvCorreo.text = ""
            binding.tvCelular.text = ""
            binding.ivAvatar.setImageResource(R.drawable.ic_person)
            binding.chipsHobbies.removeAllViews()
            addChip("Sin hobbies aún")
        }
    }

    /** Pinta chips de hobbies del usuario y habilita popup Editar/Eliminar */
    private fun renderHobbies(userId: Int) {
        val dao = HobbyDAO(requireContext())
        val hobbies = dao.listByUser(userId)

        binding.chipsHobbies.removeAllViews()

        // Evita duplicados por nombre (defensa extra de UI)
        val unicos = hobbies.distinctBy { it.nombre.trim().lowercase() }

        if (unicos.isEmpty()) {
            addChip("Sin hobbies aún")
            return
        }

        unicos.forEach { h ->
            val chip = Chip(requireContext()).apply {
                text = h.nombre
                isCheckable = false
                isClickable = true
                isCloseIconVisible = false
            }

            chip.setOnClickListener {
                PopupMenu(requireContext(), chip).apply {
                    menu.add("Editar")
                    menu.add("Eliminar")
                    setOnMenuItemClickListener { item ->
                        when (item.title?.toString()) {
                            "Editar" -> {
                                SimpleRenameHobbySheet
                                    .newInstance(userId, h.id, h.nombre)
                                    .show(parentFragmentManager, "rename_hobby")
                            }
                            "Eliminar" -> {
                                dao.unlinkUsuarioHobby(userId, h.id)
                                // Notifica que cambió
                                parentFragmentManager.setFragmentResult(
                                    DialogAgregarHobby.RESULT_HOBBIES_CHANGED,
                                    Bundle()
                                )
                            }
                        }
                        true
                    }
                }.show()
            }

            binding.chipsHobbies.addView(chip)
        }
    }

    private fun addChip(text: String) {
        val chip = Chip(requireContext()).apply {
            this.text = text
            isCheckable = false
            isClickable = false
        }
        binding.chipsHobbies.addView(chip)
    }

    private fun usuarioActual() =
        SessionManager.getCurrentEmail(requireContext())?.let { correo ->
            UsuarioDAO(requireContext()).getByCorreo(correo)
        }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
