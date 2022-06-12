package com.genta.storyapp.Story

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.genta.storyapp.Data.Response.ResponseGetStory
import com.genta.storyapp.databinding.ActivityDetailBinding
import com.genta.storyapp.databinding.ActivityLoginBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    companion object{
        const val data = "get data"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Detail Story"
        val getData = intent.getParcelableExtra<ResponseGetStory>(data)
        Glide.with(this).load(getData!!.photoUrl).into(binding.imageView4)

        binding.apply {
            tvName.setText("Nama : "+getData.name)
            tvDeskripsi.setText("Deskripsi : "+getData.description)
            tvDate.setText("Created at : "+getData.createdAt)
        }

    }
}