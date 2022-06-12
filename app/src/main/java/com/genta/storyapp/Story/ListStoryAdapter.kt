package com.genta.storyapp.Story


import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.genta.storyapp.Data.Response.ResponseGetStory
import com.genta.storyapp.databinding.ItemBinding

class ListStoryAdapter(private val listStory : ArrayList<ResponseGetStory>):RecyclerView.Adapter<ListStoryAdapter.ListViewHolder>() {

    override fun onCreateViewHolder(view: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemBinding.inflate(LayoutInflater.from(view.context), view, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(listStory[position])
    }

    override fun getItemCount(): Int {
        return listStory.size
    }

    inner class ListViewHolder(private var binding: ItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(StoryItem: ResponseGetStory) {
            val dateList = StoryItem.createdAt
            binding.apply {
                tvItemName.setText(StoryItem.name)
                Glide.with(itemView.context)
                    .load(StoryItem.photoUrl)
                    .into(imgItemPhoto)
                date.setText("created at : " + dateList)
                ResponseGetStory(
                    StoryItem.id,
                    StoryItem.name,
                    StoryItem.description,
                    StoryItem.photoUrl,
                    StoryItem.createdAt,
                    StoryItem.lat,
                    StoryItem.lon
                )
                itemView.setOnClickListener {
                    val intent = Intent(itemView.context, DetailActivity::class.java)
                    intent.putExtra(DetailActivity.data, StoryItem)
                    itemView.context.startActivity(intent)
                }


            }
        }
    }
}