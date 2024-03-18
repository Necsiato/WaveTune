package com.example.wavetuneplayer2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.wavetuneplayer2.databinding.ActivityPlaylistBinding

class PlaylistActivity : AppCompatActivity() {

    //Creating Binding Object
    private lateinit var binding: ActivityPlaylistBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.kocmocTheme_exp)
        binding = ActivityPlaylistBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backBtnPLA.setOnClickListener{finish()}
    }
}