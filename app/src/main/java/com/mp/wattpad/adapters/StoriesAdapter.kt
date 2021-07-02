package com.mp.wattpad.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mp.wattpad.R
import com.mp.wattpad.data.model.SQLModel
import kotlinx.android.synthetic.main.stories_card.view.*

class StoriesAdapter(val context: Context, var storiesList: MutableList<SQLModel>): RecyclerView.Adapter<StoriesAdapter.ViewHolder>() {

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val storyTitle: TextView = itemView.storyTitleTV
        val storyAuthor: TextView = itemView.storyAuthorTV
        val storyImage: ImageView = itemView.storyImageIV
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.stories_card, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = storiesList[position]
        holder.storyTitle.text = story.storyTitle
        holder.storyAuthor.text = story.storyAuthor
        var url = story.storyImage
        var uri = Uri.parse(url)
        Glide.with(context)
            .load(uri)
            .into(holder.storyImage)
    }
    override fun getItemCount() = storiesList.size
}