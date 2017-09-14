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

package github.com.raccok.dota2androidapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

  TextView mainTextView;
  private static final String PREFS = "prefs";
  private static final String PREF_NAME = "name";
  SharedPreferences mSharedPreferences;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main);

    // 1. Access the TextView defined in res/layout/activity_main.xml and then set its text
    mainTextView = (TextView) findViewById(R.id.main_textview);

    // 2. Display loaded favorite Dota 2 hero, or ask for their favorite hero if not defined yet
    displayWelcome();
  }

  public void displayWelcome() {
    // Initially displayed text
    mainTextView.setText("Your favorite Dota 2 hero has not been defined yet.");

    // Access the device's key-value storage
    mSharedPreferences = getSharedPreferences(PREFS, MODE_PRIVATE);

    // Read the user's favorite hero, or an empty string if nothing found
    String name = mSharedPreferences.getString(PREF_NAME, "");

    if (name.length() > 0) {
      // If the name is valid, display it
      Toast.makeText(this,
                     "Loaded your favorite Dota 2 hero '" + name + "' from device storage",
                     Toast.LENGTH_LONG).show();
      setFavoriteHeroText(name);
    } else {
      // otherwise, show a dialog to ask for the hero's name
      AlertDialog.Builder alert = new AlertDialog.Builder(this);
      alert.setTitle("Hello!");
      alert.setMessage("What is your favorite Dota 2 hero?");

      // Create EditText for entry
      final EditText input = new EditText(this);
      alert.setView(input);

      // Make an "OK" button to save the name
      alert.setPositiveButton("OK",
          new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
              // Grab the EditText's input
              String inputName = input.getText().toString();

              // Put it into memory (don't forget to commit!)
              SharedPreferences.Editor e = mSharedPreferences.edit();
              e.putString(PREF_NAME, inputName);
              e.commit();

              // Let the user know the hero name was saved
              Toast.makeText(getApplicationContext(),
                             "Saved your favorite hero '" + inputName + "' to device storage",
                             Toast.LENGTH_LONG).show();

              setFavoriteHeroText(inputName);
            }
          });

      // Make a "Cancel" button that simply dismisses the alert
      alert.setNegativeButton("Cancel",
          new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {}
          });

      alert.show();
    }
  }

  private void setFavoriteHeroText(String name) {
    mainTextView.setText("Your favorite Dota 2 hero is:\n\n" + name + "\n\nWhat a fine choice!");
  }
}