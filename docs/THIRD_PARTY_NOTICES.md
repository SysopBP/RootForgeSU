# Third-party licensing review (v0.5)

RootForgeSU application source and its independently written JNI bridge are published under Apache License 2.0.

The current app Gradle file declares Android application and Kotlin Android plugins; it does not declare external runtime libraries. The build uses the Android SDK, Kotlin, Gradle, CMake and Android NDK toolchain, which have their own applicable terms.

No DirtySuki, DFRoot, KernelSU, Shizuku, or third-party exploit source, binaries, or assets are included in RootForgeSU's v0.5 implementation.

The repository license has been updated to include the complete Apache License 2.0 text. This is a source-tree review, not a full dependency/SBOM or legal certification. Recheck dependencies and binary APK contents before a public release.
