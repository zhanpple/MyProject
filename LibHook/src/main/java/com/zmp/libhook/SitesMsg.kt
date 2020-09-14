package com.zmp.libhook

/**
 * @author
 * @date 2020/8/19.
 */
data class SitesMsg(val type: Int = 0, val content: String) {
    companion object {
        const val HEART_TYPE = 0
        const val URL_TYPE = 1
        const val OPEN_TYPE = 2
        const val CLOSE_TYPE = 3
    }
}