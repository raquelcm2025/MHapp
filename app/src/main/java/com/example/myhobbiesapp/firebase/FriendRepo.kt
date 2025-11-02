package com.example.myhobbiesapp.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

object  FriendRepo {
    private val db by lazy { FirebaseDatabase.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }

    /** Acepta UID o email: si viene email, resuelve a UID con el índice */
    fun sendRequest(toKey: String, cb: (Boolean) -> Unit) {
        val fromUid = auth.currentUser?.uid ?: return cb(false)

        fun doSend(toUid: String) {
            if (toUid == fromUid) return cb(false)
            val reqRef = db.getReference("friendRequests").child(toUid).push()
            val data = mapOf(
                "fromUid" to fromUid,
                "toUid" to toUid,
                "status" to "pending",
                "ts" to System.currentTimeMillis()
            )
            reqRef.setValue(data).addOnCompleteListener { cb(it.isSuccessful) }
        }

        if (toKey.contains("@")) {
            EmailIndex.resolve(toKey) { uid -> if (uid != null) doSend(uid) else cb(false) }
        } else {
            doSend(toKey)
        }
    }

    /** Aceptar solicitud (toUid = yo, reqId = id del request que recibí) */
    fun acceptRequest(toUid: String, reqId: String, fromUid: String, cb: (Boolean) -> Unit) {
        val me = FirebaseAuth.getInstance().currentUser?.uid ?: return cb(false)
        if (me != toUid) return cb(false)

        // 1. Multi-update: marcar accepted + crear vínculo en ambos sentidos
        val updates = mapOf(
            "/friendRequests/$toUid/$reqId/status" to "accepted",
            "/friends/$toUid/$fromUid" to true,
            "/friends/$fromUid/$toUid" to true
        )
        FirebaseDatabase.getInstance().reference.updateChildren(updates).addOnCompleteListener { ok ->
            if (ok.isSuccessful) {
                // 3. Si la amistad se crea, AHORA aseguramos que se cree el chat
                ChatRepo.ensureChat(toUid, fromUid) { _ -> cb(true) }
            } else cb(false)
        }
    }
    /** Rechazar solicitud */
    fun declineRequest(toUid: String, reqId: String, cb: (Boolean) -> Unit) {
        val me = auth.currentUser?.uid ?: return cb(false)
        if (me != toUid) return cb(false)
        db.getReference("friendRequests/$toUid/$reqId/status")
            .setValue("declined").addOnCompleteListener { cb(it.isSuccessful) }
    }
}
