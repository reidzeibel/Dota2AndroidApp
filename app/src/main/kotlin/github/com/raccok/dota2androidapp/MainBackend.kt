package github.com.raccok.dota2androidapp

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import android.widget.Toast
import github.com.raccok.dota2androidapp.api.RetrofitInstance
import github.com.raccok.dota2androidapp.api.response.GenericResponse
import github.com.raccok.dota2androidapp.api.response.GetHeroesResponse
import github.com.raccok.dota2androidapp.utils.SharedPreferenceHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainBackend (context: Context, mainActivityInterface: MainActivityInterface) {

    private val mAppContext = context
    private val mInterface = mainActivityInterface
    private val mAppConnectivityMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val mHeroNames = mutableListOf<String>()

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
            Toast.makeText(mAppContext,
                    "Querying heroes from the Dota 2 API failed (no internet connection)",
                    Toast.LENGTH_LONG).show()
            return
        }

        RetrofitInstance().rest.getHeroes(mAppContext.getString(R.string.api_key)).enqueue(object : Callback<GenericResponse<GetHeroesResponse>> {
            override fun onResponse(call: Call<GenericResponse<GetHeroesResponse>>?, response: Response<GenericResponse<GetHeroesResponse>>) {
                if (response.isSuccessful) {
//                    Log.d("####", response.raw().toString())
                    mHeroNames.addAll(response.body()!!.result.heroes.map{it.localized_name})
                    validateUserInput(userInput)
                }
            }

            override fun onFailure(call: Call<GenericResponse<GetHeroesResponse>>?, throwable: Throwable?) {
                Toast.makeText(mAppContext, "Error: " + throwable?.message, Toast.LENGTH_LONG).show()
                Log.e("Dota 2 Android App", "Error: "+ throwable?.message)
            }
        })
    }

    private fun deviceIsOnline(): Boolean {
        val netInfo = mAppConnectivityMgr.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }

    private fun validateUserInput(userInput: String) {
        // Check if user input is a valid (currently available) Dota 2 hero
        if (mHeroNames.contains(userInput)) {
            // Put it into memory (don't forget to commit!)
            SharedPreferenceHelper(mAppContext).setFavoriteHero(userInput)

            Toast.makeText(mAppContext,
                    "Saved your favorite hero '$userInput' to device storage",
                    Toast.LENGTH_LONG).show()

            //Set display text
            mInterface.onValidInput(userInput)
        } else {
            Toast.makeText(mAppContext, "'$userInput' is not a valid Dota 2 hero!",
                    Toast.LENGTH_LONG).show()

            //Display Welcome
            mInterface.onInvalidInput()
        }
    }
}