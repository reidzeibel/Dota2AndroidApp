package github.com.raccok.dota2androidapp.api

import com.google.gson.Gson
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by RidwanAditama on 17/09/2017.
 *
 * Class to instantiate the Retrofit Instance, which is used to call the RestApi
 *
 * usage : RetrofitInstance().rest.<insert API here>
 */

class RetrofitInstance {

    companion object {
        const val STEAM_API_URL = "http://api.steampowered.com/"
    }

    val okHttpClient = OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .build()

    val retrofit = Retrofit.Builder()
            .baseUrl(STEAM_API_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .build()

    val rest = retrofit.create(RestApi::class.java)


}