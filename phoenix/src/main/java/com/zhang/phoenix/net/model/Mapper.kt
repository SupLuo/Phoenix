package com.zhang.phoenix.net.model

import org.json.JSONArray
import org.json.JSONObject
import java.util.*


fun JSONObject.readMediaModel(key: String): MediaModel? {
    val array = getJSONObject(key).getJSONArray("mergeArray")
    if (array.length() == 0)
        return null

    val obj = array.getJSONObject(0).getJSONObject("media")
    return MediaModel(obj)
}

fun JSONObject.readMediaModels(key: String): List<MediaModel>? {
    val array = getJSONObject(key).getJSONArray("mergeArray")
    if (array.length() == 0)
        return null

    return array.map<JSONObject,MediaModel> {
        MediaModel(it.getJSONObject("media"))
    }
}

fun JSONObject.readFirstPosters(): String? {
    val array = getJSONArray("posters")
    if (array.length() <= 0)
        return null
    return array.getString(0)
}

fun JSONObject.readLongDate(key: String): Date? {
    val time = readLong(key)
    if (time > 0) {
        return Date(time)
    }
    return null
}

@JvmOverloads
fun JSONObject.readLongOrThrow(key: String, def: Long = 0): Long {
    if (!this.has(key))
        return def
    return getLong(key)
}

@JvmOverloads
fun JSONObject.readLong(key: String, def: Long = 0): Long {
    return try {
        readLongOrThrow(key, def)
    } catch (e: Throwable) {
        def
    }
}

@JvmOverloads
fun JSONObject.readBooleanOrThrow(key: String, def: Boolean = false): Boolean {
    if (!this.has(key))
        return def
    return getBoolean(key)
}

@JvmOverloads
fun JSONObject.readBoolean(key: String, def: Boolean = false): Boolean {
    return try {
        readBooleanOrThrow(key, def)
    } catch (e: Throwable) {
        def
    }
}

@JvmOverloads
fun JSONObject.readString(key: String, def: String? = null): String? {
    if (!this.has(key))
        return def
    return getString(key)
}

inline fun <T> JSONArray.forEach(block: (T) -> Unit) {
    val size = this.length()
    for (i in 0 until size) {
        val item = this.get(i) as T
        block.invoke(item)
    }
}

inline fun <T, R> JSONArray.map(mapper: (T) -> R): List<R> {
    val result = mutableListOf<R>()
    val size = this.length()
    for (i in 0 until size) {
        result.add(mapper.invoke(this.get(i) as T))
    }
    return result
}
