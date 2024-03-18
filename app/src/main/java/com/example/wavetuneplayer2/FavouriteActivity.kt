package com.example.wavetuneplayer2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.wavetuneplayer2.databinding.ActivityFavouriteBinding

class FavouriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavouriteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.kocmocTheme_exp)
        binding = ActivityFavouriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backBtnFA.setOnClickListener{finish()}
    }
}