package github.com.raccok.dota2androidapp.api

import github.com.raccok.dota2androidapp.api.response.GenericResponse
import github.com.raccok.dota2androidapp.api.response.GetHeroesResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by RidwanAditama on 17/09/2017.
 *
 * This class is used to define the API interfaces.
 * 1. Add new endpoints to the companion object block
 * 2. Define the API call (GET/POST, Query parameters, etc) and don't forget to specify its type
 * 3. Add new models as needed
 *
 *
 */

interface RestApi {

    companion object {
        const val GET_HEROES = "IEconDOTA2_570/GetHeroes/v1?language=en_us"
    }

    @GET(GET_HEROES)
    fun getHeroes(@Query("key") key: String) : Call<GenericResponse<GetHeroesResponse>>
}