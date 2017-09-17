package github.com.raccok.dota2androidapp.api.response

/**
 * Created by RidwanAditama on 17/09/2017.
 *
 * Wrapper class because the Dota 2 API appends a "result" key on their api call result.
 */

data class GenericResponse<T>(val result : T)