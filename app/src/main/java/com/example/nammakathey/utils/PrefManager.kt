package com.nammakathey.utils

import android.content.Context

object PrefManager {
    private const val PREF_NAME = "namma_kathey_prefs"
    private const val KEY_LANG = "language"

    fun getLanguage(context: Context): String {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_LANG, "en") ?: "en"
    }

    fun setLanguage(context: Context, lang: String) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit().putString(KEY_LANG, lang).apply()
    }

    fun isKannada(context: Context) = getLanguage(context) == "kn"
    fun toggleLanguage(context: Context) {
        val current = getLanguage(context)
        setLanguage(context, if (current == "en") "kn" else "en")
    }
}