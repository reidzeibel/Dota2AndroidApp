package github.com.raccok.dota2androidapp

import android.app.AlertDialog
import android.content.Context
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class MainFrontend {
  private lateinit var mMainBackend: MainBackend
  private lateinit var mAppContext: Context
  private lateinit var mMainTextView: TextView
  private lateinit var mMainAlert: AlertDialog.Builder

  fun init(mainBackend: MainBackend, appContext: Context, mainTextView: TextView,
           mainAlert: AlertDialog.Builder) {
    mMainBackend = mainBackend
    mAppContext = appContext
    mMainTextView = mainTextView
    mMainAlert = mainAlert
  }

  fun displayWelcome() {
    // Initially displayed text
    mMainTextView.text = "Your favorite Dota 2 hero has not been defined yet."

    // Read the user's favorite Dota 2 hero, or an empty string if not found
    val favoriteHero = mMainBackend.loadFavoriteHero()

    if (favoriteHero != null && favoriteHero.isNotEmpty()) {
      Toast.makeText(mAppContext,
                     "Loaded your favorite Dota 2 hero '$favoriteHero' from device storage",
                     Toast.LENGTH_LONG).show()
      setFavoriteHeroText(favoriteHero)
    } else {
      // Show a dialog to ask for the user's favorite hero
      mMainAlert.setTitle("Hello!")
      mMainAlert.setMessage("What is your favorite Dota 2 hero?")

      // Create EditText for entry
      val input = EditText(mAppContext)
      mMainAlert.setView(input)

      // Make an "OK" button to save the favorite hero (if it's a valid Dota 2 hero)
      mMainAlert.setPositiveButton("OK") { _, _ ->
        // Grab the EditText's input and pass it on to the back-end
        mMainBackend.saveFavoriteHero(input.text.toString())
      }

      // Make a "Cancel" button that simply dismisses the alert
      mMainAlert.setNegativeButton("Cancel") { _, _ -> }

      mMainAlert.show()
    }
  }

  fun setFavoriteHeroText(name: String) {
    mMainTextView.text = "Your favorite Dota 2 hero is:\n\n$name\n\nWhat a fine choice!"
  }
}