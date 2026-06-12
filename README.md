# ImmersiAds — Android

> **Learn a new language through authentic advertisements.**

ImmersiAds helps language learners build real-world listening comprehension and vocabulary through authentic advertising content from native speakers. Rather than studying isolated vocabulary lists, users learn language in context through short, engaging video advertisements.

---

## Features

| Feature | Status |
|---|---|
| User onboarding (native + target language selection) | ✅ |
| Advertisement feed with language filtering | ✅ |
| Video playback with ExoPlayer | ✅ |
| Variable playback speed (0.5× – 2.0×) | ✅ |
| Pause and replay controls | ✅ |
| Interactive subtitles with word tapping | ✅ |
| Word and phrase translation popup | ✅ |
| Save vocabulary to local database | ✅ |
| Personal vocabulary list with search | ✅ |
| Learning streaks | ✅ |
| Progress dashboard | ✅ |
| Settings (subtitles, speed, dark mode) | ✅ |

---

## Architecture

The app follows **Clean Architecture** with **MVVM** at the presentation layer:

```
app/
└── src/main/java/com/immersiads/app/
    ├── data/
    │   ├── local/         # Room database, DAO
    │   ├── model/         # Domain models
    │   ├── repository/    # Repository implementations
    │   └── sample/        # Sample data for local development
    ├── domain/
    │   └── UserPreferences.kt  # DataStore preferences
    └── ui/
        ├── navigation/    # Navigation graph
        ├── onboarding/    # Onboarding flow
        ├── feed/          # Ad feed
        ├── player/        # Video player
        ├── vocabulary/    # Saved vocabulary
        ├── progress/      # Learning dashboard
        ├── settings/      # App settings
        └── theme/         # Material3 theming
```

See [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) for detailed architecture documentation.

---

## Tech Stack

| Component | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material3 |
| Architecture | MVVM + Clean Architecture |
| Navigation | Navigation Compose |
| Video | Media3 ExoPlayer |
| Database | Room |
| Preferences | DataStore |
| Async | Kotlin Coroutines + Flow |
| Testing | JUnit4 + MockK + Coroutines Test |
| Build | Gradle (Kotlin DSL) + Version Catalog |
| CI | GitHub Actions |

---

## Getting Started

### Prerequisites

- Android Studio Ladybug (2024.2.1) or newer
- JDK 17
- Android SDK 35 (API level 35)

### Build

```bash
./gradlew assembleDebug
```

### Run Tests

```bash
./gradlew testDebugUnitTest
```

### Lint

```bash
./gradlew lint
```

---

## CI/CD

GitHub Actions workflows run on every push and pull request to `main`:

1. **Lint** — Android lint checks
2. **Unit Tests** — JVM unit tests
3. **Build** — Debug APK assembly (after lint + tests pass)

See [.github/workflows/ci.yml](.github/workflows/ci.yml).

---

## Sample Data

The app ships with sample advertisements in four languages (Spanish, French, German) with pre-loaded subtitles and translations for local development. No network connection is required to explore all features.

---

## Roadmap

See [docs/ROADMAP.md](docs/ROADMAP.md) for planned features and future direction.

---

## License

GNU General Public License v3.0 — see [LICENSE](LICENSE).
