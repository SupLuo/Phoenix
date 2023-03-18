@file:JvmName("RvaKt")
@file:JvmMultifileClass

package bas.droid.rva

import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.PowerfulAdapterProxy
import androidx.recyclerview.widget.RecyclerView
import com.zhang.phoenix.R
import bas.droid.rva.adapter.PowerfulDataAdapter
import bas.droid.rva.internal.PowerfulImpl
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers


/**
 * 使用普通Adapter作为数据适配器
 * @see RecyclerView.powerAdapter 获取对应的数据适配器
 */
@JvmOverloads
fun RecyclerView.setup(
    @LayoutRes layoutId: Int = -1,
    configs: Configs = Configs(),
    setup: PowerfulDataAdapter.(RecyclerView) -> Unit
): Power {
    val power = PowerfulImpl(configs = configs)
    val adapter = PowerfulDataAdapter(power = power, layoutId = layoutId)
    power.bindDataAdapter(adapter)
    adapter.setup(this)
    power.apply(this)
    return power
}

/**
 * @param oldDataAdapter 用于原来的适配器：比如对已经存在了adapter的界面进行改造，想要使其具有Power的能力的情况
 * @see RecyclerView.power 获取对应的扩展能力对象
 */
@JvmOverloads
fun <VH : RecyclerView.ViewHolder> RecyclerView.setupWithOld(
    oldDataAdapter: RecyclerView.Adapter<VH>,
    configs: Configs = Configs(),
    setup: Power.(RecyclerView) -> Unit
): Power {
    val power = PowerfulImpl(configs = configs)
    val adapter = PowerfulAdapterProxy(oldDataAdapter, power = power)
    power.bindDataAdapter(adapter)
    adapter.setup(this)
    power.apply(this)
    return power
}

/**
 * 获取拥有扩展能力的数据适配器（数据适配器除power提供的扩展能力外，还具有所有的与数据适配器相关的能力，比如数据添加修改、绑定item、设置事件等）
 *
 * 对于已存在用户自己的Adapter场景，不能使用本方法，只能使用power的能力
 */
val RecyclerView.powerAdapter: PowerfulDataAdapter? get() = power as PowerfulDataAdapter?

fun RecyclerView.requirePowerAdapter(): PowerfulDataAdapter {
    return requirePower() as PowerfulDataAdapter
}

/**
 * 获取adapter的扩展能力
 */
val RecyclerView.power: Power?
    get() {
        try {
            return powerOrThrow
        } catch (e: Throwable) {
            return null
        }
    }

fun RecyclerView.requirePower(): Power {
    val power = getTag(R.id.tag_powerful_adapter) as? Power
    if (power != null)
        return power
    val adapter =
        this.adapter ?: throw NullPointerException("the adapter of recycler view is null.")
    if (adapter is Power) {
        cachePower(adapter)
        return adapter
    }
    //开始查找
    adapter as ConcatAdapter?
        ?: throw  IllegalArgumentException("the recycler view's adapter is not a ConcatAdapter.")
    adapter.adapters.forEach {
        if (it is Power) {
            cachePower(it)
            return it
        }
    }
    throw IllegalArgumentException("Not find power in recycler view's adapter,please ensure use the method 'usePower' of the recycler view.")
}

@Deprecated("use requirePower")
val RecyclerView.powerOrThrow: Power
    get() = requirePower()

private inline fun RecyclerView.cachePower(power: Power) {
    setTag(R.id.tag_powerful_adapter, power)
}