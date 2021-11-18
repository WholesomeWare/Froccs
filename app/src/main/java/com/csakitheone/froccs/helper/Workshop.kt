package com.csakitheone.froccs.helper

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Workshop {
    companion object {
        private val db = FirebaseDatabase.getInstance()

        // APP SPECIFIC
        val WORKSHOP_APP_ID = "froccs"
        val WORKSHOP_CATEGORY_RECIPE = "recipes"

        // HELPER
        fun getAllStrings(category: String, callback: (List<String>) -> Unit) {
            db.getReference("$WORKSHOP_APP_ID/$category").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    callback(snapshot.children.map { it.value.toString() })
                }
                override fun onCancelled(error: DatabaseError) { }
            })
        }

        fun addString(category: String, text: String, callback: ((Boolean) -> Unit)? = null) {
            getAllStrings(category) {
                if (!it.contains(text)) {
                    db.getReference("$WORKSHOP_APP_ID/$category").push().setValue(text).addOnCompleteListener { task ->
                        if (callback != null) callback(task.isSuccessful)
                    }
                }
            }
        }
    }
}