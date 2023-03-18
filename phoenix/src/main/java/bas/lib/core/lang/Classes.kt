@file:JvmName("ClassesKt")
package bas.lib.core.lang


inline fun isClassExists(className: String): Boolean {
    return try {
        Class.forName(className)
        true
    } catch (e: ClassNotFoundException) {
        false
    }
}

