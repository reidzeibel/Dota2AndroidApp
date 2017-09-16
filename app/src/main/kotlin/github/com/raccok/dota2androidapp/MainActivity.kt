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
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity() {
  private var mBackend: Backend = Backend()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    if (!mBackend.init(this))
      return

    setContentView(R.layout.activity_main)

    // Initially displayed text
    textView.text = "Your favorite Dota 2 hero has not been defined yet."

    // Display loaded favorite Dota 2 hero or ask for it if not defined yet
    displayWelcome()
  }

  fun displayWelcome() {
    // Read the user's favorite Dota 2 hero, or an empty string if not found
    val favoriteHero = mBackend.loadFavoriteHero()

    if (favoriteHero != null && favoriteHero.isNotEmpty()) {
      Toast.makeText(applicationContext,
                     "Loaded your favorite Dota 2 hero '$favoriteHero' from device storage",
                     Toast.LENGTH_LONG).show()
      setFavoriteHeroText(favoriteHero)
    } else {
      // Show a dialog to ask for the user's favorite hero
      val alert = AlertDialog.Builder(this)
      alert.setTitle("Hello!")
      alert.setMessage("What is your favorite Dota 2 hero?")

      // Create EditText for entry
      val input = EditText(this)
      alert.setView(input)

      // Make an "OK" button to save the favorite hero (if it's a valid Dota 2 hero)
      alert.setPositiveButton("OK") { _, _ ->
        // Grab the EditText's input and pass it on to the back-end
        mBackend.saveFavoriteHero(input.text.toString())
      }

      // Make a "Cancel" button that simply dismisses the alert
      alert.setNegativeButton("Cancel") { _, _ -> }

      alert.show()
    }
  }

  fun setFavoriteHeroText(name: String) {
    textView.text = "Your favorite Dota 2 hero is:\n\n$name\n\nWhat a fine choice!"
  }
}
