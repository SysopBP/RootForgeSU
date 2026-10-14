package com.sysopbp.rootforgesu

import android.os.Build
import java.io.File

object DeviceSecurity {
    private fun property(name: String): String = runCatching {
        val process = ProcessBuilder("getprop", name).redirectErrorStream(true).start()
        val value = process.inputStream.bufferedReader().use { it.readText().trim() }
        if (process.waitFor() == 0 && value.isNotBlank()) value else "Unknown"
    }.getOrDefault("Unknown")

    private fun field(name: String, value: String) = "$name: $value"
    private fun verifiedBoot(raw: String): String = when (raw.lowercase()) {
        "green" -> "Reported green (verified)"
        "yellow" -> "Reported yellow (custom trust anchor)"
        "orange" -> "Reported orange (unlocked)"
        "red" -> "Reported red (verification failure)"
        else -> "Unknown (property: $raw)"
    }
    private fun bootloader(raw: String): String = when (raw.lowercase()) {
        "1", "true", "yes" -> "Reported locked"
        "0", "false", "no" -> "Reported unlocked"
        else -> "Unknown (property: $raw)"
    }
    private fun kmi(): String {
        val release = runCatching { File("/proc/sys/kernel/osrelease").readText().trim() }
            .getOrDefault("")
        return Regex("android\\d+-\\d+\\.\\d+").find(release)?.value ?: "Unknown"
    }
    fun summary(): String = listOf(
        field("Device", "${Build.MANUFACTURER} ${Build.MODEL}"),
        field("Firmware", Build.DISPLAY),
        field("Bootloader version", Build.BOOTLOADER),
        field("Security patch", Build.VERSION.SECURITY_PATCH),
        field("Kernel KMI indicator", kmi()),
        field("Verified boot", verifiedBoot(property("ro.boot.verifiedbootstate"))),
        field("Bootloader lock", bootloader(property("ro.boot.flash.locked"))),
        field("VBMeta state", property("ro.boot.vbmeta.device_state")),
        field("Verified boot digest", "Not exported (device identifier)"),
        "Property reports are not attestation and do not establish root feasibility."
    ).joinToString("\n")
}
