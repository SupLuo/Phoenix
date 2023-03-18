package com.zhang.phoenix.net.repository

import com.zhang.phoenix.net.api.Api
import org.json.JSONObject

class HomeRepository {

    suspend fun getHomeChannel(): JSONObject {
        return Api.getChannel("home")
    }

}