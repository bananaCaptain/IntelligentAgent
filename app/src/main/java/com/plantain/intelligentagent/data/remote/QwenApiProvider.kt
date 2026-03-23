package com.plantain.intelligentagent.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object QwenApiProvider {

    private const val BASE_URL = "https://dashscope.aliyuncs.com/api/"

    fun createService(): QwenService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(NetworkClient.okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(QwenService::class.java)
    }

    fun createDataSource(apiKey: String, model: String = "qwen-turbo"): QwenDataSource {
        return RetrofitQwenDataSource(
            service = createService(),
            apiKey = apiKey,
            model = model
        )
    }
}
