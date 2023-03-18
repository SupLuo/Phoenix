@file:JvmName("ConvertersKt")

package bas.lib.core.converter

import java.lang.reflect.Type

/**
 * Created by Lucio on 2021/7/22.
 */
inline fun <reified T> T?.toJson(): String? {
    return Converters.toJson(this, T::class.java)
}

inline fun <reified T> T?.toJson(type: Type): String? {
    return Converters.toJson(this, type)
}


inline fun <reified T> String?.toObject(): T? {
    return Converters.toObject(this, T::class.java)
}

inline fun <reified T> String?.toObject(type: Type): T? {
    return Converters.toObject(this, type)
}
