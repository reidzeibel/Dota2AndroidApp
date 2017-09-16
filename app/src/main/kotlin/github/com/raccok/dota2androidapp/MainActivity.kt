/*
 * Copyright (c) 2016 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package github.com.raccok.dota2androidapp

import android.app.Activity
import android.app.AlertDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONObject
import android.net.ConnectivityManager

class MainActivity : Activity() {
  private var mUserInput = ""
  private var mHeroNames: MutableList<String> = mutableListOf()
  private var mMainTextView: TextView? = null
  private var mSharedPreferences: SharedPreferences? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    if (appIsMissingPermissions(applicationContext))
      return

    setContentView(R.layout.activity_main)

    // Get a handle to the TextView defined in res/layout/activity_main.xml
    mMainTextView = findViewById(R.id.main_textview)

    // Get a handle to the device's key-value storage
    mSharedPreferences = getSharedPreferences(PREFS, MODE_PRIVATE)

    // Display loaded favorite Dota 2 hero or ask for the user's favorite hero if not defined yet
    displayWelcome()
  }

  private fun displayWelcome() {
    // Initially displayed text
    mMainTextView?.text = "Your favorite Dota 2 hero has not been defined yet."

    // Read the user's favorite hero, or an empty string if nothing found
    val name = mSharedPreferences?.getString(PREF_NAME, "")

    if (name != null && name.isNotEmpty()) {
      // If the name is valid, display it
      Toast.makeText(applicationContext,
                     "Loaded your favorite Dota 2 hero '$name' from device storage",
                     Toast.LENGTH_LONG).show()
      setFavoriteHeroText(name)
    } else {
      // otherwise, show a dialog to ask for the hero's name
      val alert = AlertDialog.Builder(this)
      alert.setTitle("Hello!")
      alert.setMessage("What is your favorite Dota 2 hero?")

      // Create EditText for entry
      val input = EditText(this)
      alert.setView(input)

      // Make an "OK" button to save the name
      alert.setPositiveButton("OK") { _, _ ->
        // Grab the EditText's input
        mUserInput = input.text.toString()

        if (mHeroNames.isEmpty())
          // If the available hero names are not yet loaded from the Dota 2 API, load them first
          // and then validate the user input
          queryHeroNames()
        else
          // If the available hero names are loaded, jump straight to validating the user input
          validateUserInput()
      }

      // Make a "Cancel" button that simply dismisses the alert
      alert.setNegativeButton("Cancel") { _, _ -> }

      alert.show()
    }
  }

  private fun queryHeroNames() {
    // Check if the device is connected to the internet
    if (!deviceIsOnline()) {
      Toast.makeText(applicationContext,
                     "Querying heroes from the Dota 2 API failed (no internet connection)",
                     Toast.LENGTH_LONG).show()
      return
    }

    // Create a client to perform networking
    val client = AsyncHttpClient()

    // Additional parameters needed to query the Dota 2 API for currently available heroes
    // (TODO: the API key is hard-coded temporarily, until we have Steam user authentication)
    val params = "IEconDOTA2_570/GetHeroes/v1?key=F9FEEEDF9C7762EA5D76F2EEDB7A08BC&language=en_us"

    // Have the client get a JSONObject of data and define how to respond
    client.get(STEAM_API_URL + params, object : JsonHttpResponseHandler() {
      override fun onSuccess(status: Int, headers: Array<out Header>?, response: JSONObject?) {
        val resultData = response?.optJSONObject("result")
        if (resultData == null) {
          Toast.makeText(applicationContext,
                         "Querying heroes from the Dota 2 API failed (no 'result' data)",
                         Toast.LENGTH_LONG).show()
          return
        }

        val heroesData = resultData.optJSONArray("heroes")
        if (heroesData == null) {
          Toast.makeText(applicationContext,
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
        validateUserInput()
      }

      override fun onFailure(status: Int, headers: Array<out Header>?,
                             throwable: Throwable, error: JSONObject) {
        Toast.makeText(applicationContext, "Error: " + status + " " + throwable.message,
                       Toast.LENGTH_LONG).show()
        Log.e("Dota 2 Android App", status.toString() + " " + throwable.message)
      }
    })
  }

  private fun deviceIsOnline(): Boolean {
    val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager?
    val netInfo = cm?.activeNetworkInfo
    return netInfo != null && netInfo.isConnectedOrConnecting
  }

  private fun validateUserInput() {
    // Check if user input is a valid (currently available) Dota 2 hero
    if (mHeroNames.contains(mUserInput)) {
      // Put it into memory (don't forget to commit!)
      val e = mSharedPreferences?.edit()
      e?.putString(PREF_NAME, mUserInput)
      e?.commit()

      Toast.makeText(applicationContext, "Saved your favorite hero '$mUserInput' to device storage",
                     Toast.LENGTH_LONG).show()
      setFavoriteHeroText(mUserInput)
    } else {
      Toast.makeText(applicationContext, "'$mUserInput' is not a valid Dota 2 hero!",
                     Toast.LENGTH_LONG).show()
      displayWelcome()
    }
  }

  private fun setFavoriteHeroText(name: String) {
    mMainTextView?.text = "Your favorite Dota 2 hero is:\n\n$name\n\nWhat a fine choice!"
  }

  companion object {
    private val PREFS = "prefs"
    private val PREF_NAME = "name"
    private val STEAM_API_URL = "http://api.steampowered.com/"
  }
}
