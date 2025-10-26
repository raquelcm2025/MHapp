package com.example.myhobbiesapp.ui.dialog

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.data.dao.FotoLocalDAO
import com.example.myhobbiesapp.data.dao.HobbyDAO
import com.example.myhobbiesapp.data.dao.UsuarioDAO
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip

class DialogPerfilExplora : BottomSheetDialogFragment() {

    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userId = requireArguments().getInt(ARG_USER_ID, -1)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, s: Bundle?): View {
        return inflater.inflate(R.layout.dialog_perfil_explora, container, false)
    }

    override fun onViewCreated(v: View, s: Bundle?) {
        super.onViewCreated(v, s)

        val ivFoto   = v.findViewById<ImageView>(R.id.ivFoto)
        val tvNombre = v.findViewById<TextView>(R.id.tvNombre)
        val tvCorreo = v.findViewById<TextView>(R.id.tvCorreo)
        val rvMini   = v.findViewById<RecyclerView>(R.id.rvMiniGaleria)
        val chipGroup = v.findViewById<com.google.android.material.chip.ChipGroup>(R.id.chipsHobbiesMini)
        val btnCon   = v.findViewById<MaterialButton>(R.id.btnConectar)
        val btnCan   = v.findViewById<MaterialButton>(R.id.btnCancelar)
        val tvEmptyMini = v.findViewById<TextView>(R.id.tvEmptyMini)



        if (userId <= 0) { dismiss(); return }

        val usuario = try { UsuarioDAO(requireContext()).getById(userId) } catch (_: Exception) { null }
        if (usuario == null) {
            Toast.makeText(requireContext(), "Usuario no encontrado", Toast.LENGTH_SHORT).show()
            dismiss(); return
        }

        val nombreCompleto = buildString {
            append(usuario.nombre)
            if (usuario.apellidoPaterno.isNotBlank()) append(" ").append(usuario.apellidoPaterno)
            if (usuario.apellidoMaterno.isNotBlank()) append(" ").append(usuario.apellidoMaterno)
        }.trim()

        tvNombre.text = nombreCompleto
        tvCorreo.text = usuario.correo

        val generoIcon = when (usuario.genero?.lowercase()) {
            "femenino", "mujer" -> R.drawable.ic_mujer
            "masculino", "hombre" -> R.drawable.ic_hombre
            else -> R.drawable.ic_person
        }
        ivFoto.setImageResource(if (usuario.foto != 0) usuario.foto else generoIcon)

        // Mini-galería real (máx 3)
        val fotos = try { FotoLocalDAO(requireContext()).topNByUser(userId, 3) } catch (_: Exception) { emptyList() }
        rvMini.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvMini.adapter = object : RecyclerView.Adapter<MiniVH>() {
            override fun onCreateViewHolder(p: ViewGroup, vt: Int): MiniVH {
                val d = p.resources.displayMetrics.density
                val size = (120 * d).toInt()
                val margin = (8 * d).toInt()
                val iv = ImageView(p.context).apply {
                    layoutParams = ViewGroup.MarginLayoutParams(size, size).apply { rightMargin = margin }
                    scaleType = ImageView.ScaleType.CENTER_CROP
                }
                return MiniVH(iv)
            }
            override fun onBindViewHolder(h: MiniVH, i: Int) {
                val uri = try { Uri.parse(fotos[i].uri) } catch (_: Exception) { null }
                if (uri != null) (h.itemView as ImageView).setImageURI(uri)
            }
            override fun getItemCount(): Int = fotos.size
        }

        // Hobbies (si existe el ChipGroup)
        chipGroup?.removeAllViews()
        val hobbiesUser = try { HobbyDAO(requireContext()).listByUser(userId) } catch (_: Exception) { emptyList() }
        if (chipGroup != null) {
            if (hobbiesUser.isEmpty()) {
                val t = TextView(requireContext()).apply { text = "Sin hobbies"; setTextColor(Color.GRAY) }
                chipGroup.addView(t)
            } else {
                hobbiesUser.take(4).forEach { h ->
                    val chip = Chip(requireContext()).apply {
                        text = h.nombre
                        isClickable = false
                        isCheckable = false
                    }
                    chipGroup.addView(chip)
                }
            }
        }

        if (fotos.isEmpty()) {
            tvEmptyMini.visibility = View.VISIBLE
            rvMini.visibility = View.GONE
        } else {
            tvEmptyMini.visibility = View.GONE
            rvMini.visibility = View.VISIBLE
        }



        btnCon.setOnClickListener {
            Toast.makeText(requireContext(), "Se envió su solicitud de amistad ✨", Toast.LENGTH_SHORT).show()
            dismiss()
        }
        btnCan.setOnClickListener { dismiss() }
    }

    private class MiniVH(v: View) : RecyclerView.ViewHolder(v)

    companion object {
        private const val ARG_USER_ID = "arg_user_id"
        fun newInstance(userId: Int) = DialogPerfilExplora().apply {
            arguments = Bundle().apply { putInt(ARG_USER_ID, userId) }
        }
    }
}
