package com.example.wavetuneplayer2

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wavetuneplayer2.databinding.ActivityMainBinding
import java.io.File
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    //Creating Binding Object
    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var musicAdapter: MusicAdapter

    companion object{
        lateinit var MusicListMA : ArrayList<Music>
    }
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //requestRuntimePermission()

        setTheme(R.style.kocmoc_Nav_exp)

        //Initializing Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //for navigation drawer

        toggle = ActionBarDrawerToggle(this, binding.root, R.string.open, R.string.close)
        binding.root.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if(requestRuntimePermission()){
            initializeLayout()
        }

        initializeLayout()

        binding.shuffleBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, PlayerActivity::class.java)
            intent.putExtra("index",0)
            intent.putExtra("class","MainActivity")
            startActivity(intent)
        }

        binding.featuresBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, FavouriteActivity::class.java)
            startActivity(intent)
        }
        binding.playlistBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, PlaylistActivity::class.java)
            startActivity(intent)
        }

        /*
        Когда происходит нажатие на какой-либо элемент ActionBar,
        код проверяет ID элемента.
        Затем, в зависимости от ID, код выполняет
        определенное действие
        */
        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navFeedBack -> Toast.makeText(baseContext, "Feedback", Toast.LENGTH_SHORT)
                    .show()

                R.id.navSettings -> Toast.makeText(baseContext, "Settings", Toast.LENGTH_SHORT)
                    .show()

                R.id.navAbout -> Toast.makeText(baseContext, "About", Toast.LENGTH_SHORT).show()
                R.id.navExit -> exitProcess(1)
            }
            true
        }

    }

    //For requesting permission
    private fun requestRuntimePermission() :Boolean{
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU){
            if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 13)
                return false
            }
        }
        //android 13 permission request
        else if(Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU){
            if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_MEDIA_AUDIO), 13)
                return false
            }
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 13) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                initializeLayout()
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                13
            )
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item))
            return true

        return super.onOptionsItemSelected(item)
    }
    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("SetTextI18n")
    private fun initializeLayout() {


        MusicListMA = getAllAudio()
        binding.musicRV.setHasFixedSize(true)
        binding.musicRV.setItemViewCacheSize(13)
        binding.musicRV.layoutManager = LinearLayoutManager(this@MainActivity)
        musicAdapter = MusicAdapter(this@MainActivity, MusicListMA)
        binding.musicRV.adapter = musicAdapter
        binding.totalSongs.text = "Total Songs: " + musicAdapter.itemCount

    }
    @SuppressLint("Recycle", "Range", "SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.R)

    private fun getAllAudio(): ArrayList<Music> {
        val tempList = ArrayList<Music>()
        val selection = MediaStore.Audio.Media.IS_MUSIC + " !=0 "
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID
        )
        val cursor = this.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null,
            MediaStore.Audio.Media.DATE_ADDED + " DESC", null
        )

        if(cursor != null){
            if(cursor.moveToFirst())
                do {
                    val titleC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))?:"Unknown"
                    val idC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))?:"Unknown"
                    val albumC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))?:"Unknown"
                    val artistC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))?:"Unknown"
                    val pathC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val durationC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val albumIdC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)).toString()
                    val uri = Uri.parse("content://media/external/audio/albumart")
                    val artUriC = Uri.withAppendedPath(uri, albumIdC).toString()
                    val music = Music(id = idC, title = titleC, album = albumC, artist = artistC, path = pathC, duration = durationC,
                        artUri = artUriC)
                    val file = File(music.path)
                    if(file.exists())
                        tempList.add(music)
                }while (cursor.moveToNext())
                cursor.close()
            }
        return tempList
    }

    override fun onDestroy() {
        super.onDestroy()
        if(!PlayerActivity.isPlaying && PlayerActivity.musicService != null){
            PlayerActivity.musicService!!.stopForeground(true)
            PlayerActivity.musicService!!.mediaPlayer!!.release()
            PlayerActivity.musicService= null
            exitProcess(1)
        }
    }
}