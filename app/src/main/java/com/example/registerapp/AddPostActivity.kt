package com.example.registerapp

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class AddPostActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        val titleEditText: EditText = findViewById(R.id.addPost_titleEditText)
        val contentEditText: EditText = findViewById(R.id.addPost_contentEditText)
        val addPostButton: Button = findViewById(R.id.addPost_button)
        auth = Firebase.auth
        val db = Firebase.firestore

        addPostButton.setOnClickListener {
            if(titleEditText.text.isEmpty() || contentEditText.text.isEmpty()) {
                return@setOnClickListener
            }

            val title = titleEditText.text.toString()
            val content = contentEditText.text.toString()
            val currentUserId = auth.currentUser?.uid

            val post = hashMapOf(
                "uid" to currentUserId,
                "title" to title,
                "content" to content
            )

            db.collection("posts")
                .add(post)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
                    finish()
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }
        }
    }
}