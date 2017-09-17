package github.com.raccok.dota2androidapp.utils

import android.content.Context
import android.preference.PreferenceManager

/**
 * Created by RidwanAditama on 17/09/2017.
 *
 * Shared preference helper class.
 *
 * 1. add new constants to companion object block
 * 2. add getter and setter for the constant
 *
 * usage : SharedPreferenceHelper(context).<getter/setter>
 */

class SharedPreferenceHelper(context: Context) {

    companion object {
        const val PREF_FAV_HERO = "fav_hero"
    }

    private val sharedPreference = PreferenceManager.getDefaultSharedPreferences(context)

    fun getFavoriteHero() : String {
        return sharedPreference.getString(PREF_FAV_HERO, "")
    }

    fun setFavoriteHero(hero : String) {
        sharedPreference.edit().putString(PREF_FAV_HERO, hero).apply()
    }
}