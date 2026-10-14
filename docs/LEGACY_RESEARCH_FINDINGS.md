# Legacy research findings — compatibility only

Source reviewed: archived SysopBP/-DirtySuki README (2026-10-14). This document records non-operational observations, not implementation instructions.

## Grounded observations
- Device under test: Samsung SM-S948U1, Android 17 (API 37).
- Recorded kernel: 6.12.69, Android-based KMI android16-6.12.
- SELinux: enforcing.
- Rooted Termux and ordinary application contexts had different visibility of protected vendor files. UID 0 tests did not establish ordinary app permissions.
- A previous test failed after a capacity mismatch between an experimental payload and a selected target file. Subsequent capacity checks were safeguards, not proof of exploit feasibility.
- Manager package labels and embedded assets alone did not demonstrate that a kernel module or late-loading feature worked on Samsung firmware.
- A loader reporting completion did not prove that usable manager-controlled root had been established.
- Automatic startup, soft reboot, and recovery features presented additional reliability concerns.

## RootForgeSU implications
1. Keep unprivileged and pre-existing-root diagnostic results clearly separated.
2. Require explicit verification before claiming UID 0 or a functional privileged backend.
3. Show kernel KMI as an indicator, never as proof of exploit compatibility.
4. Keep native experiments opt-in, nonpersistent, and isolated from the standard diagnostics application.
5. Do not import DirtySuki, DFRoot, or related native binaries, source, artwork, or signing material.
6. Track upstream authorship and licenses separately if third-party implementation is ever evaluated.

## Limits
The reviewed README is a project-authored report, not independently reproduced test evidence. No current exploit viability or reliable locked-bootloader rooting capability was verified. This summary intentionally excludes operational exploitation steps and sensitive implementation details.
