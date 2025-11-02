package com.example.myhobbiesapp.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.firebase.FirebaseDb
import com.example.myhobbiesapp.firebase.FriendRepo
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.FirebaseAuth

class DialogOpcionesExplora : DialogFragment() {

    companion object {
        private const val ARG_UID = "arg_uid"
        fun newInstance(uid: String) = DialogOpcionesExplora().apply {
            arguments = Bundle().apply { putString(ARG_UID, uid) }
        }
    }

    private lateinit var ivFoto: ImageView
    private lateinit var tvNombre: TextView
    private lateinit var tvCorreo: TextView
    private lateinit var chipGroup: ChipGroup
    private lateinit var rvMini: RecyclerView
    private lateinit var tvEmptyMini: TextView
    private lateinit var btnConectar: MaterialButton
    private lateinit var btnCancelar: MaterialButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, s: Bundle?): View {
        return inflater.inflate(R.layout.dialog_opciones_explora, container, false)
    }

    override fun onViewCreated(v: View, s: Bundle?) {
        super.onViewCreated(v, s)

        val targetUid = arguments?.getString(ARG_UID).orEmpty()
        val myUid = FirebaseAuth.getInstance().currentUser?.uid

        if (targetUid.isBlank() || myUid == null) {
            Toast.makeText(context, "Error: UID de usuario no válido", Toast.LENGTH_SHORT).show()
            dismiss(); return
        }

        ivFoto = v.findViewById(R.id.ivFoto)
        tvNombre = v.findViewById(R.id.tvNombre)
        tvCorreo = v.findViewById(R.id.tvCorreo)
        chipGroup = v.findViewById(R.id.chipsHobbiesMini)
        tvEmptyMini = v.findViewById(R.id.tvEmptyMini)
        rvMini = v.findViewById(R.id.rvMiniGaleria)
        btnConectar = v.findViewById(R.id.btnConectar)
        btnCancelar = v.findViewById(R.id.btnCancelar)


        tvEmptyMini.visibility = View.GONE
        rvMini.visibility = View.GONE

        btnCancelar.setOnClickListener { dismiss() }

        btnConectar.setOnClickListener {
            FriendRepo.sendRequest(targetUid) { ok ->
                val msg = if (ok) "Solicitud enviada ✨" else "Error al enviar solicitud"
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                dismiss()
            }
        }


        tvNombre.text = "Cargando..."
        tvCorreo.text = "..."
        chipGroup.removeAllViews()
        btnConectar.isEnabled = false // Deshabilitar hasta que cargue

        FirebaseDb.getUserProfile(targetUid) { profile ->
            if (!isAdded) return@getUserProfile

            if (profile == null) {
                Toast.makeText(context, "No se pudo cargar el perfil.", Toast.LENGTH_SHORT).show()
                dismiss(); return@getUserProfile
            }

            tvNombre.text = "${profile.nombre} ${profile.apellidoPaterno}".trim()
            tvCorreo.text = profile.correo
            btnConectar.isEnabled = true

            // Ponemos el avatar según el género
            val fotoRes = when (profile.genero.lowercase()) {
                "femenino", "mujer" -> R.drawable.ic_mujer
                "masculino", "hombre" -> R.drawable.ic_hombre
                else -> R.drawable.ic_person
            }
            ivFoto.setImageResource(fotoRes)

            // 6. Rellenar Hobbies (¡LEIDO DESDE Firebase!)
            if (profile.hobbies.isEmpty()) {
                addChip("Sin hobbies")
            } else {
                profile.hobbies.keys.forEach { hobbyNombre ->
                    addChip(hobbyNombre)
                }
            }
        }

    }

    private fun addChip(texto: String) {
        if (!isAdded) return
        val chip = Chip(requireContext()).apply {
            text = texto
            isClickable = false
            isCheckable = false
        }
        chipGroup.addView(chip)
    }
    override fun onStart() {
        super.onStart()

        if (dialog != null) {
            val width = (resources.displayMetrics.widthPixels * 0.90).toInt()

            val height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT

            dialog?.window?.setLayout(width, height)
        }
    }
}