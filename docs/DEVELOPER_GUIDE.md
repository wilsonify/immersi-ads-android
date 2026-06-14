# ImmersiAds — Developer Guide

Everything you need to set up, build, test, and contribute to the ImmersiAds Android app.

---

## Prerequisites

| Tool           | Version                     | Notes                                                                    |
|----------------|-----------------------------|--------------------------------------------------------------------------|
| Android Studio | Ladybug (2024.2.1) or newer | [Download](https://developer.android.com/studio)                         |
| JDK            | 17                          | Bundled with Android Studio or install [Adoptium](https://adoptium.net/) |
| Android SDK    | 35 (API level 35)           | Install via SDK Manager in Android Studio                                |
| Gradle         | 8.11.1                      | Wrapper included (`gradlew` / `gradlew.bat`); no manual install needed   |

> **Quick check:** Run `bash scripts/setup.sh` (macOS/Linux) or `.\scripts\setup.ps1` (Windows) to validate your environment.

---

## Project Setup

### 1. Clone the Repository

```bash
git clone https://github.com/anomalyco/immersi-ads-android.git
cd immersi-ads-android
```

### 2. Open in Android Studio

- Launch Android Studio and select **Open an existing project**
- Navigate to the cloned directory and click **Open**
- Android Studio will sync the Gradle configuration and download dependencies automatically

### 3. Verify Setup

```bash
./gradlew --version
```

---

## Building

> **Tip:** Use the convenience scripts instead of raw `gradlew` — they handle output formatting and error reporting.  
> `bash scripts/build.sh` (macOS/Linux) or `.\scripts\build.ps1` (Windows)

### Debug APK

```bash
./gradlew assembleDebug          # macOS / Linux
# gradlew.bat assembleDebug       # Windows
```

Output: `app/build/outputs/apk/debug/app-debug.apk`

### Release APK

```bash
./gradlew assembleRelease        # macOS / Linux
# gradlew.bat assembleRelease     # Windows
```

Output: `app/build/outputs/apk/release/app-release-unsigned.apk`

> The release build requires a signing configuration. See [Admin Guide](ADMIN_GUIDE.md#signing) for setup.

### Run on Physical Device

#### 1. Enable Developer Options on the Device

1. Open **Settings** → **About phone** → tap **Build number** 7 times
2. Go back to **Settings** → **System** → **Developer options**
3. Enable **USB debugging**

#### 2. Connect and Verify

```bash
adb devices
```

Expected output (device serial will vary):
```
List of devices attached
41130DLJH001RS  device
```

If the device shows as `unauthorized`, check the prompt on the device and tap **Allow**.

#### 3. Install the APK

```bash
# Using Gradle (builds + installs in one step):
./gradlew installDebug            # macOS / Linux
# gradlew.bat installDebug        # Windows

# Or install a pre-built APK via ADB:
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

If multiple devices are connected, specify the target:
```bash
adb -s <device-serial> install -r app/build/outputs/apk/debug/app-debug.apk
```

#### 4. Launch the App

```bash
adb shell am start -n "com.immersiads.app.debug/com.immersiads.app.MainActivity"
```

#### 5. View Logs

```bash
adb logcat -v brief | findstr immersi            # Windows
adb logcat -v brief | grep -i immersi            # macOS / Linux
```

### Run on Emulator

#### 1. Create an AVD (first time only)

```bash
# Using the Android CLI (Windows):
android emulator create medium_phone

# Or using avdmanager (cross-platform):
avdmanager create avd -n medium_phone -k "system-images;android-35;google_apis;x86_64"
```

#### 2. Start the Emulator

```bash
# Background process (keeps running after terminal closes):
emulator -avd medium_phone -no-boot-anim -gpu auto -memory 2048 &

# Or via Android CLI (blocks until boot completes):
android emulator start medium_phone
```

#### 3. Install the APK

```bash
# Using Gradle (builds + installs in one step):
./gradlew installDebug            # macOS / Linux
# gradlew.bat installDebug        # Windows

# Or install a pre-built APK via ADB:
adb -s emulator-5554 install -r app/build/outputs/apk/debug/app-debug.apk
```

#### 4. Launch the App

```bash
adb shell am start -n "com.immersiads.app.debug/com.immersiads.app.MainActivity"
```

> **Tip:** Use `android emulator list` to view created AVDs, and `android emulator stop medium_phone` to shut down.

---

## Running Tests

> **Tip:** Use `bash scripts/test.sh` (macOS/Linux) or `.\scripts\test.ps1` (Windows) for a streamlined test run.

### Unit Tests

```bash
./gradlew testDebugUnitTest       # macOS / Linux
# gradlew.bat testDebugUnitTest   # Windows
```

Tests use **JUnit 4**, **MockK**, and **kotlinx-coroutines-test** with `StandardTestDispatcher`.

Results: `app/build/reports/tests/testDebugUnitTest/index.html`

To run a specific test class:

```bash
./gradlew testDebugUnitTest --tests *VocabularyViewModelTest
```

### Instrumented Tests

Requires an emulator or connected device:

```bash
./gradlew connectedDebugAndroidTest    # macOS / Linux
# gradlew.bat connectedDebugAndroidTest # Windows
```

### Lint

```bash
./gradlew lintDebug                    # macOS / Linux  (matches CI)
# gradlew.bat lintDebug                # Windows
```

> Use `lintDebug` (not `lint`) to match the CI pipeline. The `lint.sh` / `lint.ps1` scripts use `lintDebug`.

Results: `app/build/reports/lint-results-debug.html`

---

## Build Configuration

### `gradle.properties` (Project-Level)

Located at the project root, this file sets JVM and Gradle options:

```properties
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8   # Give Gradle 2 GB of heap
org.gradle.configuration-cache=true                    # Speed up subsequent builds
org.gradle.parallel=true                               # Build modules in parallel
android.useAndroidX=true
kotlin.code.style=official
android.nonTransitiveRClass=true
```

### `gradle/libs.versions.toml` (Version Catalog)

All dependency versions are centralized here. To add a new dependency:

1. **Declare the version** under `[versions]`:
   ```toml
   retrofit = "2.9.0"
   ```

2. **Declare the library** under `[libraries]`:
   ```toml
   androidx-retrofit = { group = "com.squareup.retrofit2", name = "retrofit", version.ref = "retrofit" }
   ```

3. **Use in `app/build.gradle.kts`**:
   ```kotlin
   implementation(libs.androidx.retrofit)
   ```

### `app/build.gradle.kts` (Module-Level)

Key configuration in the app module:

| Setting         | Value                                                   | Notes                          |
|-----------------|---------------------------------------------------------|--------------------------------|
| `compileSdk`    | 35                                                      | Target the latest Android APIs |
| `minSdk`        | 24                                                      | Android 7.0 minimum            |
| `targetSdk`     | 35                                                      | Target SDK for Google Play     |
| `jvmTarget`     | 17                                                      | Must match JDK version         |
| `applicationId` | `com.immersiads.app` (`.debug` suffix for debug builds) |

---

## Project Structure

```
app/
└── src/main/java/com/immersiads/app/
    ├── data/
    │   ├── local/              # Room database, DAO, entities
    │   ├── model/              # Domain models
    │   ├── repository/         # Repository implementations
    │   └── sample/             # Sample data for local dev
    ├── domain/
    │   └── UserPreferences.kt  # DataStore-backed preferences
    └── ui/
        ├── navigation/         # NavHost + type-safe routes
        ├── onboarding/         # First-run setup flow
        ├── feed/               # Ad browsing feed
        ├── player/             # Video player with subtitles
        ├── vocabulary/         # Saved words list
        ├── progress/           # Learning dashboard
        ├── settings/           # App configuration
        └── theme/              # Material3 theming
```

### Key Files

| File                        | Purpose                              |
|-----------------------------|--------------------------------------|
| `MainActivity.kt`           | Single-activity entry point          |
| `ImmersiAdsApp.kt`          | Application class & service locator  |
| `AppNavigation.kt`          | Navigation graph with all routes     |
| `AppDatabase.kt`            | Room database definition             |
| `SampleData.kt`             | Hardcoded sample ads for development |
| `build.gradle.kts`          | App-level build configuration        |
| `gradle/libs.versions.toml` | Version catalog for dependencies     |

---

## Architecture

The app follows **Clean Architecture** with **MVVM** at the presentation layer:

```
User Action → Composable Screen → ViewModel → Repository → Data Source
                                    ↓ (StateFlow)
                              Screen re-composition
```

### Layers

- **Data layer** (`data/`) — Room database, DataStore, repository implementations, sample data
- **Domain layer** (`domain/`) — Business logic via `UserPreferences`
- **Presentation layer** (`ui/`) — Compose screens + ViewModels

Each screen is backed by a ViewModel that exposes state as `StateFlow`. Screens collect state with `collectAsState()`.

### Dependency Injection

The app uses a lightweight service locator pattern via `ImmersiAdsApp`:

```kotlin
val app = context.applicationContext as ImmersiAdsApp
val repository = app.adRepository
```

No DI framework is used. Migration to Hilt is planned for a future release.

### Navigation

Routing uses Jetpack Navigation Compose with a sealed class:

```kotlin
sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Feed : Screen("feed")
    object Player : Screen("player/{adId}")
    object Vocabulary : Screen("vocabulary")
    object Progress : Screen("progress")
    object Settings : Screen("settings")
}
```

---

## Adding a New Feature

1. **Create the screen** — Add a new package under `ui/` with `FeatureScreen.kt` and `FeatureViewModel.kt`.
2. **Define the route** — Add a new `Screen` entry in `AppNavigation.kt` and wire the composable.
3. **Implement data layer** — Add models, DAO methods, or repository functions as needed.
4. **Add sample data** — Update `SampleData.kt` if the feature needs demo content.
5. **Write tests** — Add unit tests for the ViewModel and repository logic.

---

## Code Style

- **Kotlin** — Follow the [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- **Compose** — Prefer state hoisting; keep composables stateless where possible
- **Naming** — Screens: `*Screen.kt`, ViewModels: `*ViewModel.kt`
- **Formatting** — Use Android Studio's built-in Kotlin formatter (Ctrl+Alt+L)

---

## Troubleshooting

| Problem                                               | Likely Cause                          | Solution                                                                                                           |
|-------------------------------------------------------|---------------------------------------|--------------------------------------------------------------------------------------------------------------------|
| `JAVA_HOME is not set`                                | JDK not installed or not configured   | Install JDK 17+ and set `JAVA_HOME`, or open in Android Studio which bundles a JDK                                 |
| `Android SDK not found`                               | `ANDROID_HOME` not set                | Set `ANDROID_HOME` to your SDK path (see [Prerequisites](#prerequisites))                                          |
| `Gradle distribution download failed`                 | Network issue or blocked URL          | Run `bash scripts/diagnose-network.sh`; try a Gradle mirror (see [docs/NETWORK.md](NETWORK.md))                    |
| `Could not resolve all files for configuration`       | Missing dependency or network issue   | Try `--offline` if you've built before, or check Maven Central / Google Maven access                               |
| `Lint found issues`                                   | Code style / correctness warnings     | Review `app/build/reports/lint-results-debug.html`; run `./gradlew lintDebug` to see details                       |
| `Tests: No tests found`                               | Wrong test source directory           | Ensure test files are in `src/test/` (unit) or `src/androidTest/` (instrumented)                                   |
| `BUILD FAILED in 9s`                                  | Java version mismatch                 | AGP 8.7.0 requires JDK 17–21. If you have Java 25+, install JDK 17 and update `JAVA_HOME`                          |
| `Configuration cache stale`                           | Build config changed                  | Run with `--no-configuration-cache` to rebuild the cache                                                           |
| `Cannot resolve symbol` in IDE                        | IDE cache out of sync                 | File → Invalidate Caches → Invalidate and Restart                                                                  |
| `Emulator shuts down immediately after starting`      | Process tied to parent shell          | Launch with `emulator -avd <name> &` or `Start-Process` to detach                                                  |
| `adb: device not found`                               | Emulator not running or still booting | Run `adb devices` to confirm; wait for boot with `adb wait-for-device`                                             |
| `adb: device unauthorized`                            | USB debugging not authorized          | Check the prompt on the device and tap **Allow**; run `adb kill-server && adb start-server` to retry                 |
| `adb: failed to install: device is still booting`     | Emulator not fully started            | Wait for `sys.boot_completed=1`: `adb shell getprop sys.boot_completed`                                            |
| `INSTALL_FAILED_UPDATE_INCOMPATIBLE`                  | App signature mismatch                | Uninstall the existing app first: `adb uninstall com.immersiads.app.debug`                                          |
| `Emulator: Could not initialize DirectSoundCapture`   | No audio driver (headless)            | Safe to ignore; add `-no-audio` to suppress                                                                        |
| `Build fails with: 25.0.3 / IllegalArgumentException` | JDK 25 used instead of JDK 17         | Set `JAVA_HOME` to JDK 17 install path; the Kotlin compiler used by AGP 8.7.0 cannot parse JDK 25's version string |
| `SDK XML version 4 warning during build`              | Newer SDK tools than AGP expects      | Harmless warning; update AGP to latest or ignore                                                                   |

---

## Contributing

1. Fork the repository.
2. Create a feature branch (`git checkout -b feat/my-feature`).
3. Make your changes and ensure tests pass.
4. Run `./gradlew lintDebug` (or `bash scripts/lint.sh`) and fix any warnings.
5. Commit with a descriptive message.
6. Push and open a Pull Request against `main`.

### Pull Request Checklist

- [ ] Code compiles and tests pass
- [ ] Lint reports no new warnings
- [ ] New features include tests
- [ ] UI changes follow Material3 guidelines
- [ ] Documentation updated if needed
