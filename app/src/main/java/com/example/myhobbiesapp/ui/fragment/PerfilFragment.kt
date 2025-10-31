package com.example.myhobbiesapp.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.adapter.GaleriaAdapter
import com.example.myhobbiesapp.data.dao.FotoLocalDAO
import com.example.myhobbiesapp.data.dao.HobbyDAO
import com.example.myhobbiesapp.data.dao.UsuarioDAO
import com.example.myhobbiesapp.data.entity.FotoLocal
import com.example.myhobbiesapp.databinding.FragmentPerfilBinding
import com.example.myhobbiesapp.ui.activity.AccesoActivity
import com.example.myhobbiesapp.ui.dialog.DialogAgregarHobby
import com.example.myhobbiesapp.ui.dialog.DialogEditarPerfil
import com.example.myhobbiesapp.ui.dialog.SimpleRenameHobbySheet
import com.example.myhobbiesapp.util.SessionManager
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument
import androidx.lifecycle.lifecycleScope
import com.example.myhobbiesapp.data.entity.Hobby
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PerfilFragment : Fragment() {

    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!
    private val galeriaAdapter by lazy { GaleriaAdapter() }

    private val pickImage = registerForActivityResult(OpenDocument()) { uri ->
        val u = usuarioActual() ?: return@registerForActivityResult
        if (uri != null) {
            val flags = (Intent.FLAG_GRANT_READ_URI_PERMISSION)
            requireContext().contentResolver.takePersistableUriPermission(uri, flags)

            lifecycleScope.launch(Dispatchers.IO) {
                FotoLocalDAO(requireContext()).insert(FotoLocal(userId = u.id, uri = uri.toString()))
                withContext(Dispatchers.Main) { cargarGaleria(u.id) }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, s: Bundle?): View {
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(v: View, s: Bundle?) {
        super.onViewCreated(v, s)

        binding.rvGaleria.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvGaleria.adapter = galeriaAdapter

        cargarPerfilYHobbies()

        parentFragmentManager.setFragmentResultListener(
            DialogAgregarHobby.RESULT_HOBBIES_CHANGED, viewLifecycleOwner
        ) { _, _ -> cargarPerfilYHobbies() }

        binding.btnEditarHobbies.setOnClickListener {
            usuarioActual()?.let { u ->
                DialogAgregarHobby.newInstance(u.id)
                    .show(parentFragmentManager, "add_hobby")
            }
        }

        binding.btnEditarPerfil.setOnClickListener {
            DialogEditarPerfil.newInstance().show(parentFragmentManager, "editar_perfil")
        }

        parentFragmentManager.setFragmentResultListener("perfil_editado", viewLifecycleOwner) { _, _ ->
            cargarPerfilYHobbies()
        }

        binding.btnAgregarFoto.setOnClickListener {
            pickImage.launch(arrayOf("image/*"))
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
            cargarGaleria(u.id)
        } else {
            binding.tvNombreCompleto.text = "Sin sesión"
            binding.tvCorreo.text = ""
            binding.tvCelular.text = ""
            binding.ivAvatar.setImageResource(R.drawable.ic_person)
            binding.chipsHobbies.removeAllViews()
            addChip("Sin hobbies aún")
            galeriaAdapter.submit(emptyList())
        }
    }

    private fun cargarGaleria(userId: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            val fotos = FotoLocalDAO(requireContext()).listByUser(userId).map { it.uri }
            withContext(Dispatchers.Main) {
                if (fotos.isEmpty()) {
                    binding.tvEmptyGaleria.visibility = View.VISIBLE
                    binding.rvGaleria.visibility = View.GONE
                } else {
                    binding.tvEmptyGaleria.visibility = View.GONE
                    binding.rvGaleria.visibility = View.VISIBLE
                }
                galeriaAdapter.submit(fotos)
            }
        }
    }

    private fun renderHobbies(userId: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            val dao = HobbyDAO(requireContext())
            val hobbies = dao.listByUser(userId).distinctBy { it.nombre.lowercase() }

            withContext(Dispatchers.Main) {
                binding.chipsHobbies.removeAllViews()

                if (hobbies.isEmpty()) {
                    addChip("Sin hobbies aún")
                    return@withContext
                }

                hobbies.forEach { h ->
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
                                when (item.title.toString()) {
                                    "Editar" -> {
                                        SimpleRenameHobbySheet
                                            .newInstance(userId, h.id, h.nombre)
                                            .show(parentFragmentManager, "rename_hobby")
                                    }
                                    "Eliminar" -> {
                                        // En PerfilFragment, dentro del PopupMenu -> "Eliminar":
                                        lifecycleScope.launch(Dispatchers.IO) {
                                            // RECOMENDADO: si ya usas listUserHobbies() (IDs reales)
                                            HobbyDAO(requireContext()).unlinkUsuarioHobby(
                                                userId,
                                                h.id
                                            )

                                            withContext(Dispatchers.Main) { renderHobbies(userId) } // <-- refresca chips

                                        }
                                    }
                                }
                                true
                            }
                        }.show()
                    }
                    binding.chipsHobbies.addView(chip)
                }
            }
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
