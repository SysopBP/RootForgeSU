# RootForge Native Core v0.4

This is an independently authored, read-only JNI/C++ diagnostic proof of concept.

## Implemented
- Native library loaded through Android's standard JNI mechanism.
- Kernel and CPU architecture information from uname.
- Current application UID and effective UID.
- SELinux enforcement flag, when readable.
- Graceful fallback if native loading fails.

## Safety boundaries
No privilege escalation, exploit payload, kernel write, boot partition change, persistence mechanism, or root daemon is included. UID 0 must not be inferred from the presence of a root manager or a su executable. Locked-bootloader rooting feasibility remains unproven.

## Testing
Build the debug APK in GitHub Actions. Open the Safety tab and compare native kernel/UID information against the Java-side diagnostics. On a normal unprivileged launch, UID/EUID should be the app UID, not 0. Never claim successful rooting based solely on the native bridge loading.
