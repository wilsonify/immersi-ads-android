# ImmersiAds — Android

> **Learn a new language through authentic advertisements.**

ImmersiAds helps language learners build real-world listening comprehension and vocabulary through authentic advertising content from native speakers.

---

## Quick Start

```bash
# 1. Clone the repository
git clone https://github.com/anomalyco/immersi-ads-android.git
cd immersi-ads-android

# 2. Check your environment
bash scripts/setup.sh              # macOS / Linux
# .\scripts\setup.ps1              # Windows PowerShell

# 3. Build the app
bash scripts/build.sh              # macOS / Linux
# .\scripts\build.ps1              # Windows PowerShell

# 4. Run tests
bash scripts/test.sh               # macOS / Linux
# .\scripts\test.ps1               # Windows PowerShell
```

---

## Prerequisites

| Requirement | Version | Notes |
|---|---|---|
| **Java** | 17 or later | [Adoptium](https://adoptium.net/) or Android Studio bundled JDK |
| **Android SDK** | 35 (API 35) | Install via Android Studio SDK Manager |
| **Android Studio** | Ladybug (2024.2.1+) | [Download](https://developer.android.com/studio) |
| **Git** | Any recent version | [Download](https://git-scm.com/) |

### Checking Your Setup

Run the setup script to validate everything:

```bash
bash scripts/setup.sh
```

The script checks:
- Java version (must be 17+)
- Android SDK installation (must include platform 35 and build-tools 35.0.0)
- Android SDK license acceptance
- Gradle wrapper integrity
- Network connectivity to Gradle, Maven, and GitHub

### Installing Dependencies

**Java:**
- Download from [Adoptium](https://adoptium.net/) (Temurin JDK 17 LTS recommended)
- Set `JAVA_HOME` environment variable to the installation path
- Add `$JAVA_HOME/bin` to your `PATH`

**Android SDK:**
- Android Studio installs the SDK automatically
- Alternatively, install [command-line tools](https://developer.android.com/studio#command-line-tools-only)
- Set `ANDROID_HOME` to your SDK path:
  - macOS: `~/Library/Android/Sdk`
  - Linux: `~/Android/Sdk`
  - Windows: `C:\Users\<user>\AppData\Local\Android\Sdk`

---

## First Build

```bash
# Validate environment
bash scripts/setup.sh

# Build debug APK
bash scripts/build.sh

# The APK will be at:
#   app/build/outputs/apk/debug/app-debug.apk

# Install on connected device/emulator
./gradlew installDebug
```

> On the first build, Gradle will download its distribution and all project dependencies. This can take 5–15 minutes depending on your connection.

---

## Development Workflow

Scripts are provided for common tasks:

```bash
scripts/setup.sh      # Validate prerequisites and environment
scripts/build.sh       # Build debug APK
scripts/test.sh        # Run unit tests
scripts/lint.sh        # Run lint checks
scripts/verify.sh      # Run lint + tests + build (full check)
scripts/clean.sh       # Remove build artifacts
scripts/diagnose-network.sh  # Check connectivity to all required services
```

All scripts accept extra Gradle flags. For example:

```bash
bash scripts/build.sh --offline            # Build without network
bash scripts/test.sh --no-daemon           # Tests without Gradle daemon
bash scripts/verify.sh --offline           # Full verification offline
```

---

## Network Troubleshooting

If you are behind a corporate firewall, proxy, or have intermittent internet:

1. **Run diagnostics:**
   ```bash
   bash scripts/diagnose-network.sh
   ```

2. **Configure proxy:**
   ```bash
   export HTTP_PROXY=http://proxy:8080
   export HTTPS_PROXY=http://proxy:8080
   ```

3. **Add Gradle proxy** in `gradle.properties`:
   ```properties
   systemProp.http.proxyHost=proxy
   systemProp.http.proxyPort=8080
   systemProp.https.proxyHost=proxy
   systemProp.https.proxyPort=8080
   ```

4. **Use offline mode** after an initial successful build:
   ```bash
   bash scripts/build.sh --offline
   ```

5. **Use a Gradle mirror** if `services.gradle.org` is blocked — edit `gradle/wrapper/gradle-wrapper.properties`:
   ```properties
   distributionUrl=https\://mirrors.aliyun.com/gradle/gradle-8.11.1-bin.zip
   ```

See [docs/NETWORK.md](docs/NETWORK.md) for comprehensive troubleshooting.

---

## Common Problems

| Problem | Solution |
|---|---|
| `Java not found` | Install JDK 17+ and set `JAVA_HOME` |
| `Android SDK not found` | Install Android Studio and set `ANDROID_HOME` |
| `Gradle distribution download failed` | Check network; try a mirror; see [NETWORK.md](docs/NETWORK.md) |
| `Could not resolve dependencies` | Check Maven/Google repository access; try `--offline` after successful build |
| `Lint: abortOnError` | Lint issues are informational — review `app/build/reports/lint-results-debug.html` |
| `Tests: No tests found` | Ensure test files are in `src/test/` directory |
| `Configuration cache issue` | Run with `--no-configuration-cache` to refresh |

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

---

## CI/CD Workflow

GitHub Actions workflows run on every push and pull request to `main`:

```
Push / PR
  └── Lint (./gradlew lintDebug)
        └── Unit Tests (./gradlew testDebugUnitTest)
              └── Build APK (./gradlew assembleDebug)
                    └── Upload APK as artifact
```

- Workflow file: [.github/workflows/ci.yml](.github/workflows/ci.yml)
- Security analysis: [.github/workflows/codeql.yml](.github/workflows/codeql.yml)
- Local equivalent: `bash scripts/verify.sh`

---

## Documentation

| Guide | Audience | Description |
|---|---|---|
| [User Guide](docs/USER_GUIDE.md) | Users | Installation, features walkthrough, tips, FAQs |
| [Developer Guide](docs/DEVELOPER_GUIDE.md) | Developers | Setup, build, test, code style, contribution guide |
| [Admin Guide](docs/ADMIN_GUIDE.md) | Admins | CI/CD, release process, signing, configuration |
| [Architecture](docs/ARCHITECTURE.md) | Developers | Clean Architecture layers, data flow, dependency graph |
| [Network Guide](docs/NETWORK.md) | Developers | Proxy, DNS, offline mode, Gradle mirror troubleshooting |

---

## Sample Data

The app ships with sample advertisements in Spanish, French, and German with pre-loaded subtitles and translations for local development. No network connection is required to explore all features.

---

## Roadmap

See [docs/ROADMAP.md](docs/ROADMAP.md) for planned features and future direction.

---

## License

GNU General Public License v3.0 — see [LICENSE](LICENSE).
