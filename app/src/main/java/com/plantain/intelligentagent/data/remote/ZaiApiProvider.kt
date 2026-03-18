package com.plantain.intelligentagent.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ZaiApiProvider {

    private const val BASE_URL = "https://open.bigmodel.cn"

    fun createService(): ZaiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ZaiService::class.java)
    }

    fun createDataSource(apiKey: String, model: String = "glm-4-flash"): ZaiDataSource {
        return RetrofitZaiDataSource(
            service = createService(),
            apiKey = apiKey,
            model = model
        )
    }
}
