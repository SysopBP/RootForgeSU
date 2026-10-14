# RootForgeSU

Independent Android root compatibility and diagnostic application for Samsung devices.

## Scope

- Kotlin Android application with AMOLED-friendly UI.
- Device, Android version, kernel, architecture and KMI diagnostics.
- Detection of existing root environments without attempting privilege escalation.
- Exportable local diagnostics with sensitive values redacted.
- Optional privileged integrations only after explicit review and user consent.

**Status:** early development. This app does not currently root locked-bootloader devices. No DirtySuki/DFRoot source, binaries, or assets are incorporated.

## Build

Open in Android Studio with JDK 17 and Android SDK 35, or use the GitHub Actions debug APK workflow.

## License

Apache License 2.0; see `LICENSE`. Third-party dependencies retain their respective licenses.
