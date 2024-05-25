package com.example.moviechooser

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class LanguageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_language)

        val btnHome = findViewById<ImageButton>(R.id.button_home)
        val btnWatchlistPage = findViewById<Button>(R.id.button_watchlist_page)
        val btnWatchedPage = findViewById<Button>(R.id.button_watched_page)

        btnHome.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnWatchedPage.setOnClickListener {
            val intent = Intent(this, WatchedActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnWatchlistPage.setOnClickListener {
            val intent = Intent(this, WatchlistActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}