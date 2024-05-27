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

        // Setup the home button to navigate back to the MainActivity
        val btnHome = findViewById<ImageButton>(R.id.button_home)
        btnHome.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Setup the movies button to navigate to the WatchedActivity
        val btnMovies = findViewById<ImageButton>(R.id.button_movies)
        btnMovies.setOnClickListener {
            val intent = Intent(this, WatchedActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Setup the button for selecting English language
        val btnEnglish = findViewById<ImageButton>(R.id.button_english)
        btnEnglish.setOnClickListener {
            setLocale("en")
        }

        // Setup the button for selecting Spanish language
        val btnSpanish = findViewById<ImageButton>(R.id.button_spanish)
        btnSpanish.setOnClickListener {
            setLocale("es")
        }
    }

    // Set the locale of the app based on the given language code
    private fun setLocale(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
        recreate()
    }
}