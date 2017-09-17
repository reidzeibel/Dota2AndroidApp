package github.com.raccok.dota2androidapp.api.model

/**
 * Created by RidwanAditama on 17/09/2017.
 *
 * This class defines the Heroes model result from getHeroes API
 */

data class Heroes(val id: Long,
                  val name: String,
                  val localized_name: String)