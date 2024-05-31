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

    data class CornerStateData(val cornerStateName: String, val cornerStateCode: String)

    private val cornerStateList = listOf(
        CornerStateData("Rounded", "1"),
        CornerStateData("Original", "0")
    )
    val cornerStateNames = cornerStateList.map { it.cornerStateName }

    fun cornerStateCode(cornerStateName: String): String {
        val cornerState = cornerStateList.find { it.cornerStateName == cornerStateName } ?: return ""
        return cornerState.cornerStateCode
    }

    fun cornerStateName(cornerStateCode: String): String {
        val cornerState = cornerStateList.find { it.cornerStateCode == cornerStateCode } ?: return ""
        return cornerState.cornerStateName
    }

    val country = listOf("CN", "US", "JP", "KR")
}