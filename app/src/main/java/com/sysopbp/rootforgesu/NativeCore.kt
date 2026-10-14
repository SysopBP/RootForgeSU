package com.sysopbp.rootforgesu

object NativeCore {
    private val loaded: Boolean = runCatching {
        System.loadLibrary("rootforge_native")
        true
    }.getOrDefault(false)

    private external fun inspect(): String

    fun diagnostic(): String {
        if (!loaded) return "Native backend unavailable (library not loaded)"
        return runCatching { inspect() }
            .getOrElse { "Native diagnostic failed: ${it.javaClass.simpleName}" }
    }
}
