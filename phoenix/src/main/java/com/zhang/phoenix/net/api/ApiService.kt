package com.zhang.phoenix.net.api

import retrofit2.http.*

interface ApiService {

    @POST("mzboss/c/home/channel")
    suspend fun getChannel(@Body body: Map<String, String>): Any

    @GET("http://ott1.ccnks.com:8009/index.php/Apkinstall/LiveSource/{id}")
    suspend fun getLivePlayUrl(@Path("id")id:String):String

}