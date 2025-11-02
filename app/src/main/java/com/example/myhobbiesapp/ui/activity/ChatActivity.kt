package com.example.myhobbiesapp.ui.activity

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myhobbiesapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

data class ChatMsg(
    val fromUid: String = "",
    val text: String = "",
    val ts: Long = System.currentTimeMillis()
)

class ChatActivity : AppCompatActivity() {

    private lateinit var rv: RecyclerView
    private lateinit var et: EditText
    private lateinit var btn: ImageButton

    private val auth by lazy { FirebaseAuth.getInstance() }
    private lateinit var chatId: String
    private lateinit var refMsgs: DatabaseReference

    private val data = mutableListOf<ChatMsg>()
    private lateinit var adapter: SimpleChatAdapter
    private var listener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        chatId = intent.getStringExtra("chatId") ?: run {
            finish(); return
        }

        rv = findViewById(R.id.rvChat)
        et = findViewById(R.id.etMensaje)
        btn = findViewById(R.id.btnEnviar)

        adapter = SimpleChatAdapter(data, me = auth.currentUser?.uid.orEmpty())
        rv.layoutManager = LinearLayoutManager(this).apply { stackFromEnd = true }
        rv.adapter = adapter

        refMsgs = FirebaseDatabase.getInstance()
            .getReference("chats")
            .child(chatId)
            .child("messages")

        listenMessages()

        btn.setOnClickListener {
            val txt = et.text.toString().trim()
            val uid = auth.currentUser?.uid
            if (txt.isEmpty() || uid == null) return@setOnClickListener
            sendMessage(uid, txt)
        }
    }

    private fun listenMessages() {
        listener?.let { refMsgs.removeEventListener(it) }
        listener = refMsgs.orderByChild("ts").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(s: DataSnapshot) {
                data.clear()
                for (c in s.children) {
                    c.getValue(ChatMsg::class.java)?.let { data.add(it) }
                }
                adapter.notifyDataSetChanged()
                rv.scrollToPosition((data.size - 1).coerceAtLeast(0))
            }
            override fun onCancelled(error: DatabaseError) { }
        })
    }

    private fun sendMessage(fromUid: String, text: String) {
        val msg = ChatMsg(fromUid = fromUid, text = text, ts = System.currentTimeMillis())
        refMsgs.push().setValue(msg).addOnCompleteListener {
            if (it.isSuccessful) et.setText("")
            else Toast.makeText(this, "No se pudo enviar", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        listener?.let { refMsgs.removeEventListener(it) }
        super.onDestroy()
    }
}

class SimpleChatAdapter(
    private val items: List<ChatMsg>,
    private val me: String
) : RecyclerView.Adapter<SimpleChatVH>() {
    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): SimpleChatVH {
        val v = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_msg, parent, false)
        return SimpleChatVH(v)
    }
    override fun onBindViewHolder(h: SimpleChatVH, pos: Int) = h.bind(items[pos], me)
    override fun getItemCount() = items.size
}

class SimpleChatVH(v: android.view.View) : RecyclerView.ViewHolder(v) {
    private val tvMine: android.widget.TextView = v.findViewById(R.id.tvMine)
    private val tvOther: android.widget.TextView = v.findViewById(R.id.tvOther)
    fun bind(m: ChatMsg, me: String) {
        val mine = m.fromUid == me
        tvMine.text = if (mine) m.text else ""
        tvMine.visibility = if (mine) android.view.View.VISIBLE else android.view.View.GONE
        tvOther.text = if (!mine) m.text else ""
        tvOther.visibility = if (!mine) android.view.View.VISIBLE else android.view.View.GONE
    }
}
