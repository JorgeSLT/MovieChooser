package com.example.moviechooser

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class LanguageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_language)

        val btnHome = findViewById<ImageButton>(R.id.button_home)
        val btnWatchlistPage = findViewById<Button>(R.id.button_watchlist_page)
        val btnWatchedPage = findViewById<Button>(R.id.button_watched_page)
        val btnEnglish = findViewById<ImageButton>(R.id.button_english)
        val btnSpanish = findViewById<ImageButton>(R.id.button_spanish)

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

        btnEnglish.setOnClickListener {
            setLocale("en")
        }

        btnSpanish.setOnClickListener {
            setLocale("es")
        }
    }

    private fun setLocale(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
        recreate()
    }
}