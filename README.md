# ARIO File Manager

A compact terminal-style Android file manager.

## Build on GitHub (phone-friendly)
1. Create a GitHub repository.
2. Upload this entire project.
3. Open **Actions**.
4. Run **Build ARIO Files APK**.
5. Download the `ArioFileManager-debug` artifact.

## Android Studio
Open the folder as a Gradle project and run:
`gradle assembleDebug`

### Important Android limitation
Modern Android versions restrict unrestricted access to all storage. This starter uses the app's external-storage view and media permissions. For true Android-wide file management on newer Android versions, add Android's Storage Access Framework and, where appropriate, the special `MANAGE_EXTERNAL_STORAGE` flow, subject to Play Store policy.


## If GitHub build fails
The workflow now uses Gradle 8.10 directly and `--stacktrace --info`, so the failing Gradle message will be visible in the Actions log.
