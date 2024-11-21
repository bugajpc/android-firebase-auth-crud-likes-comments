package com.example.registerapp

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class SinglePostActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val comments = mutableListOf<Comment>()
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
        auth = Firebase.auth
        val title = intent.getStringExtra("title")?:"Title"
        val content = intent.getStringExtra("content")?:"Content"
        val pid = intent.getStringExtra("pid")?:"none"
        val uid = intent.getStringExtra("uid")?:"none"
        val titleTextView: TextView = findViewById(R.id.single_title_textView)
        val contentTextView: TextView = findViewById(R.id.single_content_textView)
        val emailTextView: TextView = findViewById(R.id.single_email_textView)
        val avatarImage: ImageView = findViewById(R.id.single_avatar_imageView)
        val commentContent: EditText = findViewById(R.id.singlePost_editText_comment)
        val commentButton: Button = findViewById(R.id.singlePost_button_comment)

        val commentsRecyclerView: RecyclerView = findViewById(R.id.comments_recyclerView)
        val commentsAdapter = CommentsAdapter(comments)
        commentsRecyclerView.adapter = commentsAdapter
        commentsRecyclerView.layoutManager = LinearLayoutManager(this)

        titleTextView.text = title
        contentTextView.text = content

        commentButton.setOnClickListener {
            val content2 = commentContent.text.toString()
            val userId = auth.currentUser!!.uid

            val singleComment = hashMapOf(
                "content" to content2,
                "uid" to userId,
                "pid" to pid,
                "timestamp" to Timestamp.now()
            )

            db.collection("comments")
                .add(singleComment)
                .addOnSuccessListener { documentReference ->
                    commentContent.onEditorAction(0)
                    commentContent.text.clear()
                    Log.d("SingleComment", "DocumentSnapshot written with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w("SingleComment", "Error adding document", e)
                }
        }

        db.collection("comments").whereEqualTo("pid", pid)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    comments.add(Comment(document.id,
                        document.data["uid"].toString(),
                        pid,
                        document.data["content"].toString(),
                        document.data["timestamp"] as Timestamp
                        ))
                }
                commentsAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }

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