package bas.lib.core.converter.moshi

import bas.lib.core.converter.JsonConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.io.IOException
import java.lang.reflect.Type

/**
 * Created by Lucio on 2022/1/12.
 */
class MoshiConverter @JvmOverloads constructor(private val moshi: Moshi = createPreferredMoshi()) :
    JsonConverter {

    override fun <T> toObject(json: String?, clazz: Class<T>): T? {
        return try {
            if (json.isNullOrEmpty()) return null
            moshi.adapter(clazz).fromJson(json)
        } catch (e: IOException) {
            e.printStackTrace()
            throw RuntimeException(e)
        }
    }

    override fun <T : Any?> toObject(json: String?, type: Type): T? {
        if (json.isNullOrEmpty()) return null
        return try {
            moshi.adapter<T>(type).fromJson(json)
        } catch (e: IOException) {
            e.printStackTrace()
            throw RuntimeException(e)
        }
    }

    override fun <T> toObjectList(json: String?, tClass: Class<T>): List<T>? {
        if (json.isNullOrEmpty()) return emptyList()
        return try {
            val type: Type = Types.newParameterizedType(MutableList::class.java, tClass)
            moshi.adapter<List<T>>(type).fromJson(json)
        } catch (e: IOException) {
            e.printStackTrace()
            throw RuntimeException(e)
        }
    }

    override fun <T : Any?> toJson(obj: T?, tClass: Class<T>): String? {
        return moshi.adapter(tClass).toJson(obj)
    }

    override fun <T : Any?> toJson(obj: T?, type: Type): String? {
        return moshi.adapter<T>(type).toJson(obj)
    }

    companion object {
        //    @Override
        //    public @Nullable String toJson(@Nullable Object obj) {
        //        return moshi.adapter(obj.getClass()).toJson(obj);
        //    }
        @JvmStatic
        fun createPreferredMoshi(): Moshi {
            return Moshi.Builder().build()
        }

        /**
         * @param isSupportTimestamp 是否支持时间戳
         */
        @JvmStatic
        fun applyUtcDate(
            builder: Moshi.Builder,
            isSupportTimestamp: Boolean = false
        ): Moshi.Builder {
            builder.add(UTCDateJsonAdapter(isSupportTimestamp))
            return builder
        }
    }
}

//
////
////    //反序列化
//inline fun <reified T> toJson2(t: T) = getAdapter<T>().toJson(t) ?: ""
//
//fun <T> getAdapter(type: Type): JsonAdapter<T> =
//    (Converters.getJsonConverter() as MoshiConverter).moshi.adapter(type)
//
//inline fun <reified T> getAdapter(): JsonAdapter<T> =
//    (Converters.getJsonConverter() as MoshiConverter).moshi.adapter(object : TypeToken<T>() {}.type)
//
//
//abstract class TypeToken<T> {
//    val type: Type
//        get() = run {
//            val superclass = javaClass.genericSuperclass
//            Util.canonicalize((superclass as ParameterizedType).actualTypeArguments[0])
//        }
//}