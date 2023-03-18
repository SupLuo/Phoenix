package com.zhang.phoenix.ui.home

import bas.droid.core.lifecycle.launch
import bas.droid.core.util.getDrawableId
import bas.lib.core.coroutines.ioInvoke
import bas.lib.core.exception.tryCatching
import com.zhang.phoenix.Phoenix
import com.zhang.phoenix.base.BaseViewModel
import com.zhang.phoenix.net.model.HomeRow
import com.zhang.phoenix.net.repository.HomeRepository
import com.zhang.phoenix.ui.home.model.TabInfo
import kotlinx.coroutines.flow.MutableStateFlow

class HomeViewModel : BaseViewModel() {

    val homeUiState: MutableStateFlow<List<Any>> = MutableStateFlow(emptyList())

    fun tabs(): List<TabInfo> {
        return (0 until 7).map {
            TabInfo(
                it,
                Phoenix.context.getDrawableId("pnx_home_tab_${it + 1}"),
                Phoenix.context.getDrawableId("pnx_home_tab_${it + 1}${it + 1}")
            )
        }
    }

    fun fetchHome() {
        launch {
            tryCatching {
                val data = ioInvoke {
                    val data = HomeRepository().getHomeChannel()
                    HomeRow.read(data)
                }
                homeUiState.value = data
            }
        }
    }


}