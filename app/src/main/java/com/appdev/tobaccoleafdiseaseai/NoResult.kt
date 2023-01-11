package com.appdev.tobaccoleafdiseaseai

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.appdev.tobaccoleafdiseaseai.databinding.ActivityNoResultBinding

class NoResult : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityNoResultBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)


        val preferences: SharedPreferences =
            this.getSharedPreferences("LANGUAGE", Context.MODE_PRIVATE)
        val language = preferences.getString("SAVED_LANGUAGE", "eng")
        val localeHelper = LocaleHelper()
        val context = localeHelper.setLocale(this, language.toString())
        val resources = context.resources

        binding.txtInfo.text = resources.getString(R.string.no_result)
        binding.goBack.text = resources.getString(R.string.go_back)

        binding.goBackCard.setOnClickListener {
            finish()
        }
    }
}