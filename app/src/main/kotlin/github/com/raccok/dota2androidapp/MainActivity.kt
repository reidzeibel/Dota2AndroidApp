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
import github.com.raccok.dota2androidapp.utils.SharedPreferenceHelper
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity(), MainActivityInterface {
    private lateinit var mBackend: MainBackend

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mBackend = MainBackend(this, this)

        if (resources.getString(R.string.api_key).isEmpty()) {
            Toast.makeText(this,
                    "Need to provide a valid Steam Web API key in res/values/strings.xml!",
                    Toast.LENGTH_LONG).show()
        }

        if (!appIsMissingPermissions(this)) {
            displayWelcome()
        }


    }

    private fun displayWelcome() {
        // Initially displayed text
        textView.text = "Your favorite Dota 2 hero has not been defined yet."

        // Read the user's favorite Dota 2 hero, or an empty string if not found
        val favoriteHero = SharedPreferenceHelper(this).getFavoriteHero()

        if (favoriteHero.isNotEmpty()) {
            Toast.makeText(this,
                    "Loaded your favorite Dota 2 hero '$favoriteHero' from device storage",
                    Toast.LENGTH_LONG).show()
            textView.text = "Your favorite Dota 2 hero is: '$favoriteHero' What a fine choice!"
        } else {
            // EditText for entry
            val input = EditText(this)

            AlertDialog.Builder(this)                                                        // Create alert dialog
                    .setTitle("Hello!")                                                              // Title
                    .setMessage("What is your favorite Dota 2 hero?")                                // Message
                    .setView(input)                                                                  // EditText as view
                    .setPositiveButton("OK") { _,_ ->                                           // Add Positive button
                        mBackend.saveFavoriteHero(input.text.toString())  // Save to preference if ok
                    }
                    .setNegativeButton("Cancel") { _,_ ->                                       // Add negative button that only dismisses the dialog
                    }
                    .show()                                                                          // Show dialog
        }
    }

    override fun onValidInput(userInput: String) {
        textView.text = "Your favorite Dota 2 hero is: '$userInput' What a fine choice!"
    }

    override fun onInvalidInput() {
        displayWelcome()
    }
}
