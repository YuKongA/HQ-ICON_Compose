package top.yukonga.hq_icon.data

import kotlinx.serialization.Serializable

object Response {
    @Serializable
    data class Root(
        val resultCount: Int, // 请求数量
        val results: List<Result>  // 结果列表
    )

    @Serializable
    data class Result(
        val trackName: String, // 应用名称
        val primaryGenreName: String, // 类别
        val artistName: String, // 发行商
        val artworkUrl512: String, // 图标链接
    )
}