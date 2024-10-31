package com.example.registerapp

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Visibility
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class PostsAdapter(val posts: MutableList<Post>) : RecyclerView.Adapter<PostsAdapter.PostsViewHolder>() {
    inner class PostsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    private var auth = Firebase.auth
    val db = Firebase.firestore
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_item, parent, false)
        return PostsViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostsViewHolder, position: Int) {
        val title: TextView = holder.itemView.findViewById(R.id.post_title_textView)
        val content: TextView = holder.itemView.findViewById(R.id.post_content_textView)
        val removeImage: ImageView = holder.itemView.findViewById(R.id.remove_imageView)

        title.text = posts[position].title
        content.text = posts[position].content

        if(auth.currentUser!!.uid != posts[position].uid) {
            removeImage.isVisible = false
        }
        removeImage.setOnClickListener {
            db.collection("posts").document(posts[position].id)
                .delete()
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!")
                    notifyDataSetChanged()
                }
                .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
        }
    }

    override fun getItemCount(): Int {
        return posts.size
    }
}