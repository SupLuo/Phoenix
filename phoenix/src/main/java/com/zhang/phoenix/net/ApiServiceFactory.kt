package com.zhang.phoenix.net

import kotlin.reflect.KClass

interface ApiServiceFactory {

    val baseUrl: String

    fun <T : Any> createService(clz: KClass<T>): T
}