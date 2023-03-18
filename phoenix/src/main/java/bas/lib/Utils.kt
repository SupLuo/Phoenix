package bas.lib


inline fun Int?.orDefault(def: Int = 0) = this ?: def
inline fun Float?.orDefault(def: Float = 0f) = this ?: def
inline fun Long?.orDefault(def: Long = 0) = this ?: def
inline fun Double?.orDefault(def: Double = 0.0) = this ?: def

/**
 * null或空字符串时使用默认值
 */
inline fun String?.orDefaultIfNullOrEmpty(def: String = ""): String = if(this.isNullOrEmpty()) def else this

/**
 * 判断两个列表是否相等（顺序和对应的item equal）
 *
 * @param source 源数据集合
 * @param other  其他数据源
 * @return 如果{@link source}与@{link other}数据集合中的元素相同，则返回true，其他情况返回false。
 */
fun <E> Collection<E>?.areItemsEqual(
    other: Collection<E>?
): Boolean {
    if (this == null) return other == null
    return if (other == null || other.size != this.size) false else this.containsAll(other)
}