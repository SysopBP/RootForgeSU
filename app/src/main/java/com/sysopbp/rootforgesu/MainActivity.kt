package com.sysopbp.rootforgesu

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import java.io.File
import android.content.Intent
import android.widget.Toast
import java.util.concurrent.TimeUnit

class MainActivity : Activity() {
    private val background = Color.rgb(5, 5, 8)
    private val surface = Color.rgb(24, 24, 30)
    private val accent = Color.rgb(177, 66, 93)
    private lateinit var content: LinearLayout
    private var rootResult = "Not verified"
    private var currentSection = "Overview"

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
    private fun shape(color: Int): GradientDrawable = GradientDrawable().apply {
        setColor(color)
        cornerRadius = dp(22).toFloat()
        setStroke(dp(1), Color.rgb(52, 52, 62))
    }
    private fun text(value: String, size: Float, color: Int = Color.WHITE, bold: Boolean = false) =
        TextView(this).apply {
            text = value
            textSize = size
            setTextColor(color)
            if (bold) setTypeface(null, Typeface.BOLD)
        }
    private fun card(title: String, detail: String) {
        val panel = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = shape(surface)
            setPadding(dp(18), dp(16), dp(18), dp(16))
        }
        panel.addView(text(title, 13f, Color.rgb(194, 163, 173), true))
        panel.addView(text(detail, 16f).apply { setPadding(0, dp(7), 0, 0) })
        content.addView(panel, LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = dp(12) })
    }
    private fun kernel(): String = runCatching {
        File("/proc/sys/kernel/osrelease").readText().trim()
    }.getOrDefault("Unavailable")
    private fun rootSignals(): String {
        val known = listOf("/system/bin/su", "/system/xbin/su", "/debug_ramdisk/su",
            "/system/bin/ksud", "/data/adb/ksu")
        val visible = known.filter { File(it).exists() }
        return if (visible.isEmpty()) "No known root paths visible • Root may still be present"
        else "Possible root environment detected: ${visible.joinToString()}\nNot a verified root grant"
    }

    private fun managers(): String {
        val ids = listOf("me.weishu.kernelsu" to "KernelSU", "com.rifsxd.ksunext" to "KernelSU Next", "com.topjohnwu.magisk" to "Magisk")
        val found = ids.mapNotNull { (id, label) ->
            try { val info = packageManager.getPackageInfo(id, 0); "$label: ${info.versionName}" }
            catch (_: Exception) { null }
        }
        return if (found.isEmpty()) "No queried managers visible; forks may use other package IDs" else found.joinToString(", ")
    }
    private fun selinux(): String = try {
        when (File("/sys/fs/selinux/enforce").readText().trim()) {
            "1" -> "Enforcing"
            "0" -> "Permissive"
            else -> "Unknown"
        }
    } catch (_: Exception) { "Unknown (not readable)" }
    private fun verifyRoot() {
        rootResult = "Checking…"
        screen(currentSection)
        Thread {
            val result = try {
                val p = ProcessBuilder("su", "-c", "id").redirectErrorStream(true).start()
                if (!p.waitFor(8, TimeUnit.SECONDS)) { p.destroyForcibly(); "Timed out" }
                else {
                    val output = p.inputStream.bufferedReader().readText()
                    if (p.exitValue() == 0 && output.contains("uid=0(")) "Verified UID 0"
                    else "Not verified: ${output.take(120)}"
                }
            } catch (e: Exception) { "Not verified: ${e.javaClass.simpleName}" }
            runOnUiThread { rootResult = result; screen(currentSection) }
        }.start()
    }
    private fun report(): String = listOf(
        "RootForgeSU 0.3.0-alpha",
        "Device: ${Build.MANUFACTURER} ${Build.MODEL}",
        "Android: ${Build.VERSION.RELEASE} API ${Build.VERSION.SDK_INT}",
        "Kernel: ${kernel()}",
        "KMI: ${Regex("android\\d+-\\d+\\.\\d+").find(kernel())?.value ?: "Unknown"}",
        "Architecture: ${Build.SUPPORTED_ABIS.joinToString()}",
        "Firmware: ${Build.DISPLAY}",
        "Security patch: ${Build.VERSION.SECURITY_PATCH}",
        "Bootloader: ${Build.BOOTLOADER}",
        "SELinux: ${selinux()}",
        "Managers: ${managers()}",
        "Root paths: ${rootSignals()}",
        "Root authorization: $rootResult",
        "No identifiers or raw logs included."
    ).joinToString("\n")
    private fun exportReport() {
        startActivityForResult(Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/plain"
            putExtra(Intent.EXTRA_TITLE, "RootForgeSU_Diagnostics.txt")
        }, 41)
    }
    @Deprecated("Document picker callback")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 41 && resultCode == RESULT_OK) {
            try {
                val uri = data?.data ?: return
                contentResolver.openOutputStream(uri)?.use { it.write(report().toByteArray()) }
                    ?: error("Destination unavailable")
                Toast.makeText(this, "Report saved", Toast.LENGTH_LONG).show()
            } catch (e: Exception) { Toast.makeText(this, "Export failed", Toast.LENGTH_LONG).show() }
        }
    }
    private fun action(label: String, onClick: () -> Unit) {
        content.addView(Button(this).apply {
            text = label
            isAllCaps = false
            setOnClickListener { onClick() }
        })
    }
    private fun screen(section: String) {
        currentSection = section;
        content.removeAllViews()
        content.addView(text(section, 27f, Color.WHITE, true).apply {
            setPadding(dp(2), dp(4), 0, dp(18))
        })
        when (section) {
            "Overview" -> {
                card("DEVICE", "${Build.MANUFACTURER} ${Build.MODEL}")
                card("SYSTEM", "Android ${Build.VERSION.RELEASE} • API ${Build.VERSION.SDK_INT}\n${Build.DISPLAY}")
                card("ROOT STATUS", rootSignals())
                card("ROOT AUTHORIZATION", rootResult)
                card("SELINUX", selinux())
                card("SAFETY", "Read-only diagnostics • No exploit or privileged operations")
            }
            "Kernel" -> {
                card("KERNEL RELEASE", kernel())
                card("ARCHITECTURE", Build.SUPPORTED_ABIS.joinToString())
                val kmi = Regex("android\\d+-\\d+\\.\\d+").find(kernel())?.value
                card("KMI INDICATOR", kmi ?: "Not detected from kernel release")
                card("FIRMWARE", "${Build.DISPLAY}\nSecurity patch: ${Build.VERSION.SECURITY_PATCH}\nBootloader: ${Build.BOOTLOADER}")
            }
            "Root" -> {
                card("ENVIRONMENT", rootSignals())
                card("MANAGER DETECTION", managers())
                card("PRIVILEGES", rootResult)
                action("Verify root access") { verifyRoot() }
            }
            else -> {
                card("DIAGNOSTICS", "Device information is read locally. No telemetry or network permission.")
                card("SECURITY", "Locked-bootloader rooting is not implemented. Experimental backends are disabled.")
                card("SELINUX", selinux())
                action("Export diagnostics to file") { exportReport() }
                card("VERSION", "RootForgeSU 0.3.0-alpha")
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = background
        window.navigationBarColor = background
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(background)
            setPadding(dp(16), dp(12), dp(16), dp(12))
        }
        root.addView(text("ROOTFORGE  /  SU", 13f, accent, true).apply {
            setPadding(dp(4), dp(6), 0, dp(12))
        })
        content = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        root.addView(ScrollView(this).apply {
            fillViewport = true
            addView(content)
        }, LinearLayout.LayoutParams(-1, 0, 1f))
        val nav = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            background = shape(surface)
            setPadding(dp(4), dp(8), dp(4), dp(8))
        }
        listOf("Overview", "Kernel", "Root", "Safety").forEach { section ->
            val button = Button(this).apply {
                text = section
                textSize = 11f
                isAllCaps = false
                setTextColor(Color.WHITE)
                setBackgroundColor(Color.TRANSPARENT)
                minHeight = dp(44)
                minWidth = 0
                setPadding(0, 0, 0, 0)
                setOnClickListener { screen(section) }
            }
            nav.addView(button, LinearLayout.LayoutParams(0, dp(48), 1f))
        }
        root.addView(nav)
        setContentView(root)
        screen("Overview")
    }
}
