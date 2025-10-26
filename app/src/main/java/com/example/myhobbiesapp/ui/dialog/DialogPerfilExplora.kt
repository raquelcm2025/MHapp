package com.example.myhobbiesapp.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.data.dao.UsuarioDAO

class DialogPerfilExplora : BottomSheetDialogFragment() {

    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userId = requireArguments().getInt(ARG_USER_ID)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, s: Bundle?): View {
        return inflater.inflate(R.layout.dialog_perfil_explora, container, false)
    }

    override fun onViewCreated(v: View, s: Bundle?) {
        val ivFoto   = v.findViewById<ImageView>(R.id.ivFoto)
        val tvNombre = v.findViewById<TextView>(R.id.tvNombre)
        val tvCorreo = v.findViewById<TextView>(R.id.tvCorreo)
        val tvHobby  = v.findViewById<TextView>(R.id.tvHobbyFav)
        val rvMini   = v.findViewById<RecyclerView>(R.id.rvMiniGaleria)
        val btnCon   = v.findViewById<MaterialButton>(R.id.btnConectar)
        val btnCan   = v.findViewById<MaterialButton>(R.id.btnCancelar)

        val usuario = try { UsuarioDAO(requireContext()).getById(userId) } catch (_: Exception) { null }
        if (usuario == null) {
            Toast.makeText(requireContext(), "Usuario no encontrado", Toast.LENGTH_SHORT).show()
            dismiss()
            return
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

        tvHobby.text = "Hobby favorito: Natación" // deja tu valor real si lo tienes en BD

        rvMini.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvMini.clipToPadding = false
        rvMini.setPadding(0, 0, dpToPx(8), 0)

        val fotos = listOf(R.mipmap.natacion1, R.mipmap.natacion2, R.mipmap.natacion3)
        rvMini.adapter = MiniGaleriaAdapter(fotos)

        btnCon.setOnClickListener {
            Toast.makeText(requireContext(), "Se envió su solicitud de amistad ✨", Toast.LENGTH_SHORT).show()
            dismiss()
        }
        btnCan.setOnClickListener { dismiss() }
    }

    private fun dpToPx(dp: Int): Int = (dp * resources.displayMetrics.density).toInt()

    companion object {
        private const val ARG_USER_ID = "arg_user_id"
        fun newInstance(userId: Int) = DialogPerfilExplora().apply {
            arguments = Bundle().apply { putInt(ARG_USER_ID, userId) }
        }
    }
}

/* Adapter interno */
private class MiniGaleriaAdapter(
    private val data: List<Int>
) : RecyclerView.Adapter<MiniVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MiniVH {
        val context = parent.context
        val density = context.resources.displayMetrics.density
        val sizePx   = (180 * density).toInt()
        val marginPx = (12 * density).toInt()

        val iv = ImageView(context).apply {
            layoutParams = ViewGroup.MarginLayoutParams(sizePx, sizePx).apply {
                rightMargin = marginPx
            }
            adjustViewBounds = true
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
        return MiniVH(iv)
    }

    override fun onBindViewHolder(holder: MiniVH, position: Int) {
        (holder.itemView as ImageView).setImageResource(data[position])
    }

    override fun getItemCount(): Int = data.size
}

private class MiniVH(v: View) : RecyclerView.ViewHolder(v)
