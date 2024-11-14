package com.example.registerapp

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class SinglePostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_single_post)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val db = Firebase.firestore
        val title = intent.getStringExtra("title")?:"Title"
        val content = intent.getStringExtra("content")?:"Content"
        val pid = intent.getStringExtra("pid")?:"none"
        val uid = intent.getStringExtra("uid")?:"none"
        val titleTextView: TextView = findViewById(R.id.single_title_textView)
        val contentTextView: TextView = findViewById(R.id.single_content_textView)
        val emailTextView: TextView = findViewById(R.id.single_email_textView)
        val avatarImage: ImageView = findViewById(R.id.single_avatar_imageView)

        titleTextView.text = title
        contentTextView.text = content

        val docRef = db.collection("users").document(uid)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    emailTextView.text = document.data!!["email"].toString()
                    Picasso.get().load(document.data!!["avatar"].toString()).into(avatarImage)
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }

    }
}