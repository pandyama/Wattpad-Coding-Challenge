package com.mp.wattpad.api

import com.mp.wattpad.data.model.StoriesModel
import retrofit2.http.GET

/**
 * When using coroutines with Retrofit, you only return the model object not a Call object
 */

interface WattpadApi {
    @GET("")
    suspend fun getStories(): StoriesModel
}