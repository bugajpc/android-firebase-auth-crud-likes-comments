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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class PostsActivity : AppCompatActivity() {
    private val posts = mutableListOf<Post>()
    val postsAdapter = PostsAdapter(posts)
    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posts)

        val titleEdit: EditText = findViewById(R.id.posts_search_editText)
        val searchButton: Button = findViewById(R.id.posts_search_button)

        val postsRecyclerView: RecyclerView = findViewById(R.id.postsRecyclerView)
        postsRecyclerView.adapter = postsAdapter
        postsRecyclerView.layoutManager = LinearLayoutManager(this)

        fetchPosts("")

        searchButton.setOnClickListener {
            posts.clear()
            fetchPosts(titleEdit.text.toString())
        }

    }
    fun fetchPosts(title: String) {
        db.collection("posts").whereGreaterThanOrEqualTo("title", title)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    posts.add(Post(document.id,
                        document.data["uid"].toString(),
                        document.data["title"].toString(),
                        document.data["content"].toString()))
                }
                postsAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }
    }
}