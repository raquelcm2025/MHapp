package com.example.myhobbiesapp.ui.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.adapter.GaleriaAdapter
import com.example.myhobbiesapp.data.dao.FotoLocalDAO
import com.example.myhobbiesapp.data.entity.FotoLocal
import com.example.myhobbiesapp.databinding.FragmentPerfilBinding
import com.example.myhobbiesapp.firebase.FirebaseDb
import com.example.myhobbiesapp.firebase.model.UserProfile
import com.example.myhobbiesapp.ui.activity.AccesoActivity
import com.example.myhobbiesapp.ui.dialog.DialogAgregarHobby
// --- ¡IMPORTACIONES AÑADIDAS! ---
import com.example.myhobbiesapp.ui.dialog.DialogEditarPerfil
import com.example.myhobbiesapp.ui.dialog.DialogRenombrarHobby
// ---------------------------------
import com.example.myhobbiesapp.util.SessionManager
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PerfilFragment : Fragment() {

    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val galeriaAdapter by lazy { GaleriaAdapter() }

    private var currentUserProfile: UserProfile? = null
    private var currentHobbiesMap = mutableMapOf<String, Boolean>()
    private var currentUid: String? = null

    private val selectorDeFotos = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data
            if (uri != null) {
                guardarFotoEnDb(uri)
            } else {
                toast("No se pudo obtener la imagen")
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

        binding.btnEditarPerfil.setOnClickListener {
            DialogEditarPerfil.newInstance().show(parentFragmentManager, "editar_perfil")
        }

        currentUid = auth.currentUser?.uid
        if (currentUid == null) {
            mostrarUIComoDesconectado()
        } else {
            cargarPerfilDesdeFirebase(currentUid!!)
        }

        parentFragmentManager.setFragmentResultListener(
            DialogAgregarHobby.RESULT_HOBBIES_CHANGED,
            viewLifecycleOwner
        ) { _, bundle ->
            val nuevoHobby = bundle.getString("nuevoHobby") ?: return@setFragmentResultListener
            if (currentUid != null) {
                agregarHobby(nuevoHobby)
            }
        }

        parentFragmentManager.setFragmentResultListener(
            DialogRenombrarHobby.RESULT_HOBBY_RENAMED,
            viewLifecycleOwner
        ) { _, bundle ->
            val nombreViejo = bundle.getString("hobbyRenombradoViejo")
            val nombreNuevo = bundle.getString("hobbyRenombradoNuevo")
            if (currentUid != null && nombreViejo != null && nombreNuevo != null) {
                renombrarHobby(nombreViejo, nombreNuevo)
            }
        }


        binding.btnEditarHobbies.setOnClickListener {
            DialogAgregarHobby.newInstance().show(parentFragmentManager, "add_hobby")
        }

        binding.btnAgregarFoto.setOnClickListener {
            abrirGaleriaParaSeleccionarFoto()
        }

        binding.btnCerrarSesion.setOnClickListener {
            mostrarDialogoCerrarSesion()
        }
    }

    private fun cargarPerfilDesdeFirebase(uid: String) {
        binding.tvNombreCompleto.text = "Cargando perfil..."
        binding.tvCorreo.text = "..."
        binding.tvCelular.text = "..."

        FirebaseDb.getUserProfile(uid) { profile ->
            if (!isAdded || _binding == null) return@getUserProfile

            if (profile != null) {
                currentUserProfile = profile

                val nombreCompleto = "${profile.nombre} ${profile.apellidoPaterno} ${profile.apellidoMaterno}".trim()
                binding.tvNombreCompleto.text = nombreCompleto
                binding.tvCorreo.text = profile.correo
                binding.tvCelular.text = profile.celular

                val fotoRes = when (profile.genero.lowercase()) {
                    "femenino", "mujer" -> R.drawable.ic_mujer
                    "masculino", "hombre" -> R.drawable.ic_hombre
                    else -> R.drawable.ic_person
                }
                binding.ivAvatar.setImageResource(fotoRes)

                renderHobbies(profile.hobbies)
                cargarGaleriaLocal(uid)
            } else {
                toast("Error: No se pudo cargar tu perfil.")
                mostrarUIComoDesconectado()
            }
        }
    }

    private fun renderHobbies(hobbies: Map<String, Boolean>) {
        if (!isAdded || _binding == null) return

        currentHobbiesMap = hobbies.toMutableMap()

        binding.chipsHobbies.removeAllViews()
        if (hobbies.isEmpty()) {
            addChipVisual("Sin hobbies aún", null)
            return
        }

        hobbies.keys.sorted().forEach { hobbyNombre ->
            addChipVisual(hobbyNombre) {
                mostrarPopupDeHobby(it, hobbyNombre)
            }
        }
    }

    private fun cargarGaleriaLocal(uid: String) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val fotos = FotoLocalDAO(requireContext())
                .listByUserLimit(uid, 3)
                .map { it.uri }

            withContext(Dispatchers.Main) {
                if (!isAdded || _binding == null) return@withContext
                if (fotos.isEmpty()) {
                    binding.tvEmptyGaleria.visibility = View.VISIBLE
                    binding.rvGaleria.visibility = View.GONE
                } else {
                    binding.tvEmptyGaleria.visibility = View.GONE
                    binding.rvGaleria.visibility = View.VISIBLE
                }
                galeriaAdapter.submitList(fotos)
            }
        }
    }

    // --- Lógica de Hobbies (Con Renombrar) ---

    private fun agregarHobby(nombre: String) {
        val uid = currentUid ?: return
        currentHobbiesMap[nombre] = true

        FirebaseDb.saveUserHobbies(uid, currentHobbiesMap) { ok ->
            if (ok) {
                toast("Hobby '$nombre' añadido")
                renderHobbies(currentHobbiesMap)
            } else {
                toast("Error al guardar hobby")
                currentHobbiesMap.remove(nombre)
            }
        }
    }

    private fun eliminarHobby(nombre: String) {
        val uid = currentUid ?: return
        currentHobbiesMap.remove(nombre)

        FirebaseDb.saveUserHobbies(uid, currentHobbiesMap) { ok ->
            if (ok) {
                toast("Hobby '$nombre' eliminado")
                renderHobbies(currentHobbiesMap)
            } else {
                toast("Error al eliminar hobby")
                currentHobbiesMap[nombre] = true
            }
        }
    }

    private fun renombrarHobby(nombreViejo: String, nombreNuevo: String) {
        val uid = currentUid ?: return

        currentHobbiesMap.remove(nombreViejo)
        currentHobbiesMap[nombreNuevo] = true

        FirebaseDb.saveUserHobbies(uid, currentHobbiesMap) { ok ->
            if (ok) {
                toast("Hobby renombrado")
                renderHobbies(currentHobbiesMap) // Re-renderizar la UI
            } else {
                toast("Error al renombrar")
                // Revertimos si falla
                currentHobbiesMap.remove(nombreNuevo)
                currentHobbiesMap[nombreViejo] = true
            }
        }
    }


    private fun abrirGaleriaParaSeleccionarFoto() {
        val uid = currentUid ?: return

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val count = FotoLocalDAO(requireContext()).countByUser(uid)
            withContext(Dispatchers.Main) {
                if (count >= 3) {
                    toast("¡Ya tienes el máximo de 3 fotos!")
                } else {
                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    selectorDeFotos.launch(intent)
                }
            }
        }
    }

    private fun guardarFotoEnDb(uri: Uri) {
        val uid = currentUid ?: return

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val foto = FotoLocal(
                userId = uid,
                uri = uri.toString()
            )
            FotoLocalDAO(requireContext()).insert(foto)

            withContext(Dispatchers.Main) {
                cargarGaleriaLocal(uid)
            }
        }
    }


    private fun mostrarPopupDeHobby(viewChip: View, hobbyNombre: String) {
        PopupMenu(requireContext(), viewChip).apply {
            menu.add("Editar")
            menu.add("Eliminar")

            setOnMenuItemClickListener { item ->
                when (item.title?.toString()) {
                    // ¡ARREGLO! Añadimos la lógica de Editar
                    "Editar" -> {
                        DialogRenombrarHobby.newInstance(hobbyNombre)
                            .show(parentFragmentManager, "rename_hobby")
                    }
                    "Eliminar" -> {
                        eliminarHobby(hobbyNombre)
                    }
                }
                true
            }
        }.show()
    }

    private fun addChipVisual(text: String, onClick: ((View) -> Unit)?) {
        val chip = Chip(requireContext()).apply {
            this.text = text
            isCheckable = false
            isClickable = (onClick != null)
        }
        if (onClick != null) {
            chip.setOnClickListener(onClick)
        }
        binding.chipsHobbies.addView(chip)
    }

    private fun mostrarUIComoDesconectado() {
        binding.tvNombreCompleto.text = getString(R.string.sesion_no_iniciada)
        binding.tvCorreo.text = ""
        binding.tvCelular.text = ""
        binding.ivAvatar.setImageResource(R.drawable.ic_person)
        binding.chipsHobbies.removeAllViews()
        addChipVisual("Inicia sesión para ver tus hobbies", null)
        galeriaAdapter.submitList(emptyList())
        binding.tvEmptyGaleria.visibility = View.VISIBLE
        binding.rvGaleria.visibility = View.GONE
        binding.btnEditarHobbies.isEnabled = false
        binding.btnAgregarFoto.isEnabled = false
    }

    private fun mostrarDialogoCerrarSesion() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Cerrar sesión")
            .setMessage("¿Deseas cerrar tu sesión?")
            .setPositiveButton("Confirmar") { _, _ ->
                auth.signOut()
                SessionManager.clear(requireContext())
                val intent = Intent(requireContext(), AccesoActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                activity?.finish()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun toast(msg: String) {
        if (isAdded) {
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}