package com.appdev.tobaccoleafdiseaseai

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.appdev.tobaccoleafdiseaseai.databinding.ActivityChangeAppLanguageBinding
import com.jakewharton.processphoenix.ProcessPhoenix

class ChangeAppLanguage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityChangeAppLanguageBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)

        val preferences: SharedPreferences =
            this.getSharedPreferences("LANGUAGE", Context.MODE_PRIVATE)
        val savedLanguage = preferences.getString("SAVED_LANGUAGE", "en")

        val language = preferences.getString("SAVED_LANGUAGE", "en")
        val localeHelper = LocaleHelper()
        val context = localeHelper.setLocale(this, language.toString())
        val resources = context.resources

        binding.txt1.text = resources.getString(R.string.change_app_lang)

        var lang = savedLanguage!!

        when (lang) {
            "en" -> {
                binding.cardENG.setCardBackgroundColor(Color.parseColor("#59E168"))
            }
            "tl" -> {
                binding.cardTL.setCardBackgroundColor(Color.parseColor("#59E168"))
            }
            "ilo" -> {
                binding.cardILO.setCardBackgroundColor(Color.parseColor("#59E168"))
            }
        }

        binding.cardENG.setOnClickListener {
            binding.cardENG.setCardBackgroundColor(Color.parseColor("#59E168"))
            binding.cardILO.setCardBackgroundColor(Color.parseColor("#ECECEC"))
            binding.cardTL.setCardBackgroundColor(Color.parseColor("#ECECEC"))
            lang = "en"
        }

        binding.cardTL.setOnClickListener {
            binding.cardENG.setCardBackgroundColor(Color.parseColor("#ECECEC"))
            binding.cardILO.setCardBackgroundColor(Color.parseColor("#ECECEC"))
            binding.cardTL.setCardBackgroundColor(Color.parseColor("#59E168"))
            lang = "tl"
        }

        binding.cardILO.setOnClickListener {
            binding.cardENG.setCardBackgroundColor(Color.parseColor("#ECECEC"))
            binding.cardILO.setCardBackgroundColor(Color.parseColor("#59E168"))
            binding.cardTL.setCardBackgroundColor(Color.parseColor("#ECECEC"))
            lang = "ilo"
        }

        binding.saveBtn.setOnClickListener {
            if (lang == savedLanguage) {
                finish()
            } else {
                val localeHelper = LocaleHelper()
                localeHelper.changeLocale(applicationContext, lang)
                showPromptRestartApp()
            }
        }


    }

    private fun showPromptRestartApp() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage("Restart app for language change to take effect.")
        alertDialogBuilder.setNegativeButton("Restart later"
        ) { dialog, _ ->
            finish()

        }
        alertDialogBuilder.setPositiveButton("Restart now"
        ) { _, _ ->
            ProcessPhoenix.triggerRebirth(applicationContext)
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}