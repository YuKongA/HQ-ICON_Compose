package top.yukonga.hqicon.data

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

    data class CutStateData(val cutStateName: String, val cutStateCode: String)

    private val cutStateList = listOf(
        CutStateData("Original", "0"),
        CutStateData("Rounded", "1")
    )
    val cutStateNames = cutStateList.map { it.cutStateName }

    fun cutStateCode(cutStateName: String): String {
        val cutState = cutStateList.find { it.cutStateName == cutStateName } ?: return ""
        return cutState.cutStateCode
    }
}