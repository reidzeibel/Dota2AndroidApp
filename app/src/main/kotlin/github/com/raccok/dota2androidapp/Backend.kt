package github.com.raccok.dota2androidapp

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.util.Log
import android.widget.Toast
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

class Backend {
  private lateinit var mFrontend: MainActivity
  private lateinit var mSharedPreferences: SharedPreferences
  private var mHeroNames: MutableList<String> = mutableListOf()

  fun init(frontend: MainActivity) : Boolean {
    mFrontend = frontend

    if (mFrontend.resources.getString(R.string.api_key).isEmpty()) {
      Toast.makeText(mFrontend.applicationContext,
                     "Need to provide a valid Steam Web API key in res/values/strings.xml!",
                     Toast.LENGTH_LONG).show()
      return false
    }

    if (appIsMissingPermissions(mFrontend.applicationContext))
      return false

    // Get a handle to the device's key-value storage
    mSharedPreferences = mFrontend.getSharedPreferences(PREFS_GENERAL, Context.MODE_PRIVATE)

    return true
  }

  fun loadFavoriteHero() : String? = mSharedPreferences.getString(PREF_FAV_HERO, "")

  fun saveFavoriteHero(userInput: String) {
    if (mHeroNames.isEmpty())
      // If the available hero names are not yet loaded from the Dota 2 API, load them first
      // and then validate the user input
      queryHeroNames(userInput)
    else
      // If the available hero names are loaded, jump straight to validating the user input
      validateUserInput(userInput)
  }

  private fun queryHeroNames(userInput: String) {
    // Check if the device is connected to the internet
    if (!deviceIsOnline()) {
      Toast.makeText(mFrontend.applicationContext,
                     "Querying heroes from the Dota 2 API failed (no internet connection)",
                     Toast.LENGTH_LONG).show()
      return
    }

    // Create a client to perform networking
    val client = AsyncHttpClient()

    // Additional parameters needed to query the Dota 2 API for currently available heroes
    // (TODO: The Steam Web API key must be manually provided in res/values/strings.xml temporarily,
    // until we have Steam user authentication)
    val params = "IEconDOTA2_570/GetHeroes/v1?key=" +
                 mFrontend.resources.getString(R.string.api_key) + "&language=en_us"

    // Have the client get a JSONObject of data and define how to respond
    client.get(STEAM_API_URL + params, object : JsonHttpResponseHandler() {
      override fun onSuccess(status: Int, headers: Array<out Header>?, response: JSONObject?) {
        val resultData = response?.optJSONObject("result")
        if (resultData == null) {
          Toast.makeText(mFrontend.applicationContext,
                         "Querying heroes from the Dota 2 API failed (no 'result' data)",
                         Toast.LENGTH_LONG).show()
          return
        }

        val heroesData = resultData.optJSONArray("heroes")
        if (heroesData == null) {
          Toast.makeText(mFrontend.applicationContext,
                         "Querying heroes from the Dota 2 API failed (no 'heroes' data)",
                         Toast.LENGTH_LONG).show()
          return
        }

        // Parse and save names of currently available Dota 2 heroes to memory
        var i = 0
        while (i < heroesData.length()) {
          val heroEntry = heroesData.getJSONObject(i)
          if (heroEntry.has("localized_name"))
            mHeroNames.add(heroEntry.optString("localized_name"))
          ++i
        }

        // Now validate the user input
        validateUserInput(userInput)
      }

      override fun onFailure(status: Int, headers: Array<out Header>?,
        throwable: Throwable, error: JSONObject) {
        Toast.makeText(mFrontend.applicationContext, "Error: " + status + " " + throwable.message,
                       Toast.LENGTH_LONG).show()
        Log.e("Dota 2 Android App", status.toString() + " " + throwable.message)
      }
    })
  }

  private fun deviceIsOnline(): Boolean {
    val cm = mFrontend.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    val netInfo = cm?.activeNetworkInfo
    return netInfo != null && netInfo.isConnectedOrConnecting
  }

  private fun validateUserInput(userInput: String) {
    // Check if user input is a valid (currently available) Dota 2 hero
    if (mHeroNames.contains(userInput)) {
      // Put it into memory (don't forget to commit!)
      val e = mSharedPreferences.edit()
      e?.putString(PREF_FAV_HERO, userInput)
      e?.commit()

      Toast.makeText(mFrontend.applicationContext,
                     "Saved your favorite hero '$userInput' to device storage",
                     Toast.LENGTH_LONG).show()
      mFrontend.setFavoriteHeroText(userInput)
    } else {
      Toast.makeText(mFrontend.applicationContext, "'$userInput' is not a valid Dota 2 hero!",
                     Toast.LENGTH_LONG).show()
      mFrontend.displayWelcome()
    }
  }

  companion object {
    private val PREFS_GENERAL = "general"
    private val PREF_FAV_HERO = "fav_hero"
    private val STEAM_API_URL = "http://api.steampowered.com/"
  }
}