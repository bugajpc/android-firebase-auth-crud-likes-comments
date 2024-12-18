package com.example.registerapp

import android.content.ContentValues.TAG
import android.content.Intent
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
        val followImage: ImageView = holder.itemView.findViewById(R.id.item_follow_imageView)
        var doesExist = false
        val docRef = db.collection("follows").document(auth.currentUser!!.uid+":"+posts[position].id)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    followImage.setImageResource(R.drawable.baseline_favorite_24)
                    doesExist = true
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }

        title.text = posts[position].title
        content.text = posts[position].content

        if(auth.currentUser!!.uid != posts[position].uid) {
            removeImage.isVisible = false
        }
        followImage.setOnClickListener {

            if(!doesExist) {
                val follow = hashMapOf(
                    "uid" to auth.currentUser!!.uid,
                    "pid" to posts[position].id
                )

                db.collection("follows").document(auth.currentUser!!.uid+":"+posts[position].id)
                    .set(follow)
                    .addOnSuccessListener { documentReference ->
                        Log.d("TAG", "DocumentSnapshot written with ID:")
                        followImage.setImageResource(R.drawable.baseline_favorite_24)
                        doesExist = true
                    }
                    .addOnFailureListener { e ->
                        Log.w("TAG", "Error adding document", e)
                    }
            }
            else {
                db.collection("follows").document(auth.currentUser!!.uid+":"+posts[position].id)
                    .delete()
                    .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!")
                        followImage.setImageResource(R.drawable.baseline_favorite_border_24)
                        doesExist = false
                    }
                    .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
            }

        }
        removeImage.setOnClickListener {
            db.collection("posts").document(posts[position].id)
                .delete()
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!")
                    notifyDataSetChanged()
                }
                .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
        }
        holder.itemView.setOnClickListener {
            val singlePostIntent = Intent(it.context, SinglePostActivity::class.java)
            singlePostIntent.putExtra("title", posts[position].title)
            singlePostIntent.putExtra("content", posts[position].content)
            singlePostIntent.putExtra("pid", posts[position].id)
            singlePostIntent.putExtra("uid", posts[position].uid)
            it.context.startActivity(singlePostIntent)
        }
    }

    override fun getItemCount(): Int {
        return posts.size
    }
}