package com.appdev.tobaccoleafdiseaseai

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.appdev.tobaccoleafdiseaseai.databinding.ActivitySettingsBinding

class Settings : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySettingsBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)

        val preferences: SharedPreferences =
            this.getSharedPreferences("LANGUAGE", Context.MODE_PRIVATE)
        val language = preferences.getString("SAVED_LANGUAGE", "eng")
        val localeHelper = LocaleHelper()
        val context = localeHelper.setLocale(this, language.toString())
        val resources = context.resources

        binding.changeLang.text = resources.getString(R.string.change_app_lang)
        binding.aboutApp.text = resources.getString(R.string.about_the_app)

        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);

        val aboutCard: CardView = findViewById(R.id.aboutCard)
        val changeAppLanguage: CardView = findViewById(R.id.translateCard)

        aboutCard.setOnClickListener{
            startActivity(Intent(this, About::class.java))
        }
        changeAppLanguage.setOnClickListener{
            startActivity(Intent(this, ChangeAppLanguage::class.java))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home
        ) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}