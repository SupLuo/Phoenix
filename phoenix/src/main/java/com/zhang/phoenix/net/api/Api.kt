package com.zhang.phoenix.net.api

import bas.lib.core.converter.toJson
import com.zhang.phoenix.net.ApiClient
import com.zhang.phoenix.net.apiService
import org.json.JSONObject
import retrofit2.http.Path

object Api {

    private val service: ApiService by apiService(ApiClient)

    suspend fun getChannel(code: String): JSONObject {

        val any = service.getChannel(mapOf("code" to code))
        return JSONObject(any.toJson())
    }

    suspend fun getLivePlayUrl(@Path("id") id: String): String {
        return service.getLivePlayUrl(id)
    }

}