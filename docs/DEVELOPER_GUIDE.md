# ImmersiAds — Developer Guide

Everything you need to set up, build, test, and contribute to the ImmersiAds Android app.

---

## Prerequisites

| Tool | Version | Notes |
|---|---|---|
| Android Studio | Ladybug (2024.2.1) or newer | Download from [developer.android.com/studio](https://developer.android.com/studio) |
| JDK | 17 | Bundled with Android Studio or installed separately |
| Android SDK | 35 (API level 35) | Install via SDK Manager in Android Studio |
| Gradle | 8.11.1 | Wrapper included (`gradlew`); no manual install needed |

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

### Debug APK

```bash
./gradlew assembleDebug
```

Output: `app/build/outputs/apk/debug/app-debug.apk`

### Release APK

```bash
./gradlew assembleRelease
```

Output: `app/build/outputs/apk/release/app-release-unsigned.apk`

> The release build requires a signing configuration. See [Admin Guide](ADMIN_GUIDE.md#signing) for setup.

### Install on Device / Emulator

```bash
./gradlew installDebug
```

---

## Running Tests

### Unit Tests

```bash
./gradlew testDebugUnitTest
```

Tests use **JUnit 4**, **MockK**, and **kotlinx-coroutines-test** with `StandardTestDispatcher`.

Results: `app/build/reports/tests/testDebugUnitTest/index.html`

### Instrumented Tests

Requires an emulator or connected device:

```bash
./gradlew connectedDebugAndroidTest
```

### Lint

```bash
./gradlew lint
```

Results: `app/build/reports/lint-results-debug.html`

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

| File | Purpose |
|---|---|
| `MainActivity.kt` | Single-activity entry point |
| `ImmersiAdsApp.kt` | Application class & service locator |
| `AppNavigation.kt` | Navigation graph with all routes |
| `AppDatabase.kt` | Room database definition |
| `SampleData.kt` | Hardcoded sample ads for development |
| `build.gradle.kts` | App-level build configuration |
| `gradle/libs.versions.toml` | Version catalog for dependencies |

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

## Contributing

1. Fork the repository.
2. Create a feature branch (`git checkout -b feat/my-feature`).
3. Make your changes and ensure tests pass.
4. Run `./gradlew lint` and fix any warnings.
5. Commit with a descriptive message.
6. Push and open a Pull Request against `main`.

### Pull Request Checklist

- [ ] Code compiles and tests pass
- [ ] Lint reports no new warnings
- [ ] New features include tests
- [ ] UI changes follow Material3 guidelines
- [ ] Documentation updated if needed
