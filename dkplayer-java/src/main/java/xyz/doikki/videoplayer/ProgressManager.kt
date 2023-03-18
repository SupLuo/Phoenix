package xyz.doikki.videoplayer

import xyz.doikki.videoplayer.internal.DefaultProgressManager

/**
 * 播放进度管理器，继承此接口实现自己的进度管理器。
 */
interface ProgressManager {

    /**
     * 此方法用于实现保存进度的逻辑
     * @param url 播放地址
     * @param progress 播放进度
     */
    fun saveProgress(url: String, progress: Long)

    /**
     * 此方法用于实现获取保存的进度的逻辑
     * @param url 播放地址
     * @return 保存的播放进度
     */
    fun getSavedProgress(url: String): Long

    /**
     * 清除指定地址的进度缓存
     */
    fun clear(url: String)

    /**
     * 清空所有缓存的进度
     */
    fun clearAll()

    /**
     * Key生成器，用于关联播放地址和播放进度
     */
    interface KeyGenerator {

        /**
         * 默认的Key生成器采用url对应的哈希值
         */
        fun generateKey(url: String): Long = url.hashCode().toLong()

    }

    companion object {

        @JvmStatic
        fun default(): ProgressManager {
            return DefaultProgressManager(object : KeyGenerator {})
        }

        @JvmStatic
        fun default(keyGenerator: KeyGenerator): ProgressManager {
            return DefaultProgressManager(keyGenerator)
        }

    }

}