package com.example.myhobbiesapp.firebase

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class SimpleValueListener(private val block: (DataSnapshot) -> Unit) : ValueEventListener {
    override fun onDataChange(snapshot: DataSnapshot) = block(snapshot)
    override fun onCancelled(error: DatabaseError) { }
}
