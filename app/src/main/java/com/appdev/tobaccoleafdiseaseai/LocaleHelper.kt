package com.appdev.tobaccoleafdiseaseai

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import java.util.*

class LocaleHelper {

    //method to set the language at runtime
    fun setLocale(context: Context, language: String): Context {
        return updateResources(context, language)
    }

    // method to change languae in settings
    fun changeLocale(context: Context, language: String) {
        persist(context, language)
    }

    // persist the change language in SharedPref
    private fun persist(context: Context, language: String) {
        val preferences: SharedPreferences =
            context.getSharedPreferences("LANGUAGE", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putString("SAVED_LANGUAGE", language)
        editor.apply()
    }

    // method to update the language of app by creating object of inbuilt Locale class and
    // passing language argument to it.

    private fun updateResources(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val configuration: Configuration = context.resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)

        return context.createConfigurationContext(configuration)
    }

}