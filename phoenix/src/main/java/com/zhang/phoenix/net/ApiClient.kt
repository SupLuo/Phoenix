package com.zhang.phoenix.net

import bas.lib.core.converter.moshi.MoshiConverter
import com.squareup.moshi.FromJson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.LoggingEventListener
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.zhang.phoenix.Phoenix.isDebuggable
import org.json.JSONObject
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

class MoshiJSONObjectAdapter {
    @FromJson
    fun fromJson(json: String?): JSONObject? {
        if (json.isNullOrEmpty())
            return null
        return JSONObject(json)
    }

    @ToJson
    fun toJson(value: JSONObject?): String? {
        return value?.toString()
    }
}

val moshiBuilder = Moshi.Builder().also {
    MoshiConverter.applyUtcDate(it, isSupportTimestamp = true)
    it.add(MoshiJSONObjectAdapter())
}
val apiMoshi: Moshi = moshiBuilder.build()

const val IMAGE_URL_PREFIX = "http://fmsj.ccnks.com:9002/static"
object ApiClient : ApiServiceFactory {

    private var _retrofit: Retrofit? = null
    private var _baseUrl: String = "http://fmsj.ccnks.com:9003/"

    /**
     * API超时时间
     */
    var timeout: Long = 30

    /**
     * API超时时间单位
     */
    var timeoutUnit: TimeUnit = TimeUnit.SECONDS

    val retrofit: Retrofit
        get() {
            if (_retrofit == null || _retrofit?.baseUrl()?.toString() != baseUrl) {
                _retrofit = createRetrofit()
            }
            return _retrofit!!
        }

    @JvmStatic
    val okHttpClient: OkHttpClient
        get() {
            if (_okHttpClient == null) {
                _okHttpClient = createOkHttpClient()
            }
            return _okHttpClient!!
        }

    override var baseUrl: String
        get() = _baseUrl.orEmpty()
        set(value) {
            if (_baseUrl != value) {
                setClientInvalidate()
                _baseUrl = value
            }
        }

    private var _okHttpClient: OkHttpClient? = null

    private fun createOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(timeout, timeoutUnit)
            .readTimeout(timeout, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)       //设置出现错误进行重新连接
            .addNetworkInterceptor(HeadersInterceptor())//添加header


//        //指定TLS
//        trustSpecificCertificate(App.instance,"ucuxin7434801.pem",builder)

        if (isDebuggable) {
            //logging 拦截器，okhttp.logging提供，主要是用于输出网络请求和结果的Log
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY//配置输出级别
            builder.addInterceptor(httpLoggingInterceptor)//配置日志拦截器
            builder.eventListenerFactory(LoggingEventListener.Factory())
        }
        return builder.build()
    }

    private fun createRetrofit(): Retrofit {

        return Retrofit.Builder()
            .baseUrl(_baseUrl)   //配置服务器路径
//            .addConverterFactory(JacksonConverterFactory.create(apiConvert.objectMapper))
            .addConverterFactory(ScalarsConverterFactory.create() )
            .addConverterFactory(MoshiConverterFactory.create(apiMoshi))
//            .addConverterFactory(scala)
            .client(okHttpClient)
            .build()
    }

    /**
     * 设置ApiClient不可用
     */
    fun setClientInvalidate() {
        _okHttpClient = null
        _retrofit = null
    }


    override fun <T : Any> createService(clz: KClass<T>): T {
        return retrofit.create(clz.java)
    }

}