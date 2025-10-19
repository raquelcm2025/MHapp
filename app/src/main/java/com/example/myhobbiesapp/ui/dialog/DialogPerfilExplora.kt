package com.example.myhobbiesapp.ui.dialog

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.data.dao.UsuarioDAO
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton

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

        val usuario = try { UsuarioDAO(requireContext()).getById(userId) } catch (_:Exception){ null }
        if (usuario == null) {
            Toast.makeText(requireContext(), "Usuario no encontrado", Toast.LENGTH_SHORT).show()
            dismiss(); return
        }

        tvNombre.text = "${usuario.nombre} ${usuario.apellido}".trim()
        tvCorreo.text = usuario.correo
        ivFoto.setImageResource(if (usuario.foto != 0) usuario.foto else R.drawable.ic_person)
        tvHobby.text = "Hobby favorito: Natación"

        // === Galería horizontal ===
        rvMini.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvMini.clipToPadding = false
        rvMini.setPadding(0, 0, dpToPx(8), 0) // pequeño padding al final

        val fotos = listOf(R.mipmap.natacion1, R.mipmap.natacion2, R.mipmap.natacion3)
        val miniAdapter = MiniGaleriaAdapter(fotos)
        rvMini.adapter = miniAdapter

        btnCon.setOnClickListener {
            Toast.makeText(requireContext(), "Se envió su solicitud de amistad ✨", Toast.LENGTH_SHORT).show()
            dismiss()
        }
        btnCan.setOnClickListener { dismiss() }
    }

    private fun dpToPx(dp: Int): Int =
        (dp * resources.displayMetrics.density).toInt()

    companion object {
        private const val ARG_USER_ID = "arg_user_id"
        fun newInstance(userId: Int) = DialogPerfilExplora().apply {
            arguments = Bundle().apply { putInt(ARG_USER_ID, userId) }
        }
    }
}

/* Adapter interno: sin archivos extra  */

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
