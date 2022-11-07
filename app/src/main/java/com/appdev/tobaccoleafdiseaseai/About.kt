package com.appdev.tobaccoleafdiseaseai

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.appdev.tobaccoleafdiseaseai.databinding.ActivityAboutBinding
import com.appdev.tobaccoleafdiseaseai.databinding.ActivitySettingsBinding

class About : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAboutBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)

        val preferences: SharedPreferences =
            this.getSharedPreferences("LANGUAGE", Context.MODE_PRIVATE)
        val language = preferences.getString("SAVED_LANGUAGE", "eng")
        val localeHelper = LocaleHelper()
        val context = localeHelper.setLocale(this, language.toString())
        val resources = context.resources

        binding.aboutInfo.text = resources.getString(R.string.about_app)
    }
}