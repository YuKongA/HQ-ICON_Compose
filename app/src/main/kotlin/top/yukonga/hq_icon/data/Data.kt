package top.yukonga.hq_icon.data

class Data {
    data class PlatformData(val platformName: String, val platformCode: String)

    private val platformList = listOf(
        PlatformData("iOS", "software"),
        PlatformData("macOS", "macSoftware")
    )
    val platformNames = platformList.map { it.platformName }

    fun platformCode(platformName: String): String {
        val platform = platformList.find { it.platformName == platformName } ?: return ""
        return platform.platformCode
    }

    fun platformName(platformCode: String): String {
        val platform = platformList.find { it.platformCode == platformCode } ?: return ""
        return platform.platformName
    }

    data class ResolutionData(val resolutionName: String, val resolutionCode: String)

    private val resolutionList = listOf(
        ResolutionData("256px", "256"),
        ResolutionData("512px", "512"),
        ResolutionData("1024px", "1024")
    )
    val resolutionNames = resolutionList.map { it.resolutionName }

    fun resolutionCode(resolutionName: String): String {
        val resolution = resolutionList.find { it.resolutionName == resolutionName } ?: return ""
        return resolution.resolutionCode
    }

    fun resolutionName(cutCode: String): String {
        val cut = resolutionList.find { it.resolutionCode == cutCode } ?: return ""
        return cut.resolutionName
    }

    data class CornerData(val cornerName: String, val cornerCode: String)

    private val cornerList = listOf(
        CornerData("Rounded", "1"),
        CornerData("Original", "0")
    )
    val cornerNames = cornerList.map { it.cornerName }

    fun cornerCode(cornerName: String): String {
        val corner = cornerList.find { it.cornerName == cornerName } ?: return ""
        return corner.cornerCode
    }

    fun cornerName(cornerCode: String): String {
        val corner = cornerList.find { it.cornerCode == cornerCode } ?: return ""
        return corner.cornerName
    }

    val country = listOf("CN", "US", "JP", "KR")
}