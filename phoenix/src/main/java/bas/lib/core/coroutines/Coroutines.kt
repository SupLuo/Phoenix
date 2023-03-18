/**
 * Created by Lucio on 2021/11/10.
 * 协程相关
 */

package bas.lib.core.coroutines

import kotlinx.coroutines.*

/**
 * 在io线程执行
 */
suspend inline fun <T> ioInvoke(
    crossinline block: suspend CoroutineScope.() -> T
): T {
    return withContext(Dispatchers.IO) {
        block()
    }
}