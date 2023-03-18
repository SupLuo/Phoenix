package com.zhang.phoenix.net



internal class HeadersInterceptor : AutomaticHeadersInterceptor() {

    override fun getHeaders(): Map<String, String>? {
        return _headers
    }


    private val _headers = mutableMapOf(
        "X-Token" to "1",
        "X-Card" to "6818810000000194",
        "X-Channel"  to "cqccn",
    )
}
