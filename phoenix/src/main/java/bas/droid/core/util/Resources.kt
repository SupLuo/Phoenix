package bas.droid.core.util

import android.content.Context

inline fun Context.getIdentifier(type: String, name: String): Int {
    return resources.getIdentifier(name, type, packageName)
}

/**
 * 获取 drawable资源ID
 * @param resName drawable 的名称
 */
inline fun Context.getDrawableId(resName: String): Int {
    return getIdentifier("drawable", resName)
}