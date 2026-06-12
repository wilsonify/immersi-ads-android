# ImmersiAds — Architecture

## Overview

ImmersiAds follows **Clean Architecture** principles with a clear separation of concerns across three layers:

1. **Data Layer** — Room database, DataStore preferences, sample/mock data sources
2. **Domain Layer** — Business logic, use cases expressed through repositories and UserPreferences
3. **Presentation Layer** — ViewModels + Compose UI

## Layers

### Data Layer (`data/`)

**Models** (`data/model/`)
- `Advertisement` — Video ad with subtitles and metadata
- `Language` — Supported language with code, name, and flag
- `Subtitle` — Timed subtitle with translation
- `VocabularyItem` — Saved vocabulary word
- `DifficultyLevel` — Enum for ad difficulty

**Local Storage** (`data/local/`)
- `AppDatabase` — Room database (single instance, singleton)
- `VocabularyDao` — CRUD operations for vocabulary
- `VocabularyEntity` — Room entity for vocabulary persistence

**Repositories** (`data/repository/`)
- `AdRepository` — Provides advertisements; currently backed by in-memory sample data. Replace backing source to integrate a real API.
- `VocabularyRepository` — Wraps `VocabularyDao` with mapping between entities and domain models.

**Sample Data** (`data/sample/`)
- `SampleData` — Hardcoded sample ads with subtitles in Spanish, French, and German for local development.

### Domain Layer (`domain/`)

- `UserPreferences` — DataStore-backed preferences: native language, target language, playback speed, subtitles toggle, streak, total ads watched, dark mode preference.

### Presentation Layer (`ui/`)

Each feature is self-contained with a `Screen.kt` (Compose UI) and a `ViewModel.kt`:

| Feature | ViewModel | Key State |
|---|---|---|
| Onboarding | `OnboardingViewModel` | Step, language selections, completion |
| Feed | `FeedViewModel` | Ad list, language filter, streak |
| Player | `PlayerViewModel` | Playback, subtitle sync, word selection |
| Vocabulary | `VocabularyViewModel` | Item list, search query |
| Progress | `ProgressViewModel` | Streak, ads watched, vocab count |
| Settings | `SettingsViewModel` | All user preferences |

**Navigation** (`ui/navigation/`)
- `AppNavigation` — `NavHost` with `Screen` sealed class for type-safe routing. Navigates to onboarding or feed based on `isOnboardingComplete`.

**Theme** (`ui/theme/`)
- `ImmersiAdsTheme` — Material3 dynamic color with light/dark support
- `Color.kt`, `Type.kt` — Brand colors and typography

## Dependency Graph

```
MainActivity
    └── AppNavigation
            ├── OnboardingScreen ← OnboardingViewModel ← UserPreferences
            ├── FeedScreen       ← FeedViewModel       ← AdRepository, UserPreferences
            ├── PlayerScreen     ← PlayerViewModel     ← AdRepository, VocabularyRepository, UserPreferences
            ├── VocabularyScreen ← VocabularyViewModel ← VocabularyRepository, UserPreferences
            ├── ProgressScreen   ← ProgressViewModel   ← UserPreferences, VocabularyRepository
            └── SettingsScreen   ← SettingsViewModel   ← UserPreferences
```

## Dependency Injection

The app uses a simple application-level service locator via `ImmersiAdsApp`. Each screen retrieves the `Application` context and casts it to `ImmersiAdsApp` to obtain shared instances. This keeps the codebase simple without requiring a DI framework like Hilt.

As the application scales, migration to Hilt is recommended (see ROADMAP).

## Data Flow

```
User Action → Screen (Compose) → ViewModel → Repository/Preferences
                                 ↓ (StateFlow)
                              Screen re-composition
```

All asynchronous operations use `viewModelScope` with Kotlin Coroutines. UI state is exposed as `StateFlow<UiState>` and collected in screens with `collectAsState()`.

## Testing Strategy

- **Unit tests** for ViewModels and Repositories using MockK and `kotlinx-coroutines-test`
- **Instrumented tests** for critical UI flows using Espresso and Compose Test
- Repository tests use MockK to mock DAOs and verify correct mapping
- ViewModel tests use `StandardTestDispatcher` for precise coroutine control
