package com.example.registerapp

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.squareup.picasso.Picasso

class CommentsAdapter(val comments: MutableList<Comment>) : RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder>()  {
    inner class CommentsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    val db = Firebase.firestore
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.comment_item, parent, false)
        return CommentsViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentsViewHolder, position: Int) {
        val imageView: ImageView = holder.itemView.findViewById(R.id.comment_imageView)
        val timestamp: TextView = holder.itemView.findViewById(R.id.comment_timestamp_textView)
        val username: TextView = holder.itemView.findViewById(R.id.comment_username_textView)
        val content: TextView = holder.itemView.findViewById(R.id.comment_content_textView)

        val docRef = db.collection("users").document(comments[position].uid)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    username.text = document.data!!["email"].toString()
                    Picasso.get().load(document.data!!["avatar"].toString()).into(imageView)
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }

        timestamp.text = comments[position].timestamp.toDate().toLocaleString()
        content.text = comments[position].content
    }

    override fun getItemCount(): Int {
        return comments.size
    }
}