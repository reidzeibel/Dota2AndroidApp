package github.com.raccok.dota2androidapp.api.response

import github.com.raccok.dota2androidapp.api.model.Heroes

/**
 * Created by RidwanAditama on 17/09/2017.
 *
 * This class maps the getHeroes() API result
 */

data class GetHeroesResponse(val count: Int, val status: Int, val heroes: List<Heroes>)