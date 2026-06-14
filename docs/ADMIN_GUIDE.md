# ImmersiAds — Admin Guide

Build pipeline, release management, signing, and operational configuration for administrators.

---

## Table of Contents

- [CI/CD Pipeline](#cicd-pipeline)
- [Release Process](#release-process)
- [Signing Configuration](#signing)
- [Versioning](#versioning)
- [ProGuard & R8](#proguard--r8)
- [GitHub Actions Configuration](#github-actions-configuration)
- [Environment Configuration](#environment-configuration)
- [Monitoring & Alerts](#monitoring--alerts)

---

## CI/CD Pipeline

The project uses **GitHub Actions** for continuous integration and delivery. Workflows are defined in `.github/workflows/`.

### Workflows

| Workflow | File         | Trigger                          | Actions                                |
|----------|--------------|----------------------------------|----------------------------------------|
| CI       | `ci.yml`     | Push to any branch, PR to `main` | Lint → Unit Tests → Assemble Debug APK |
| CodeQL   | `codeql.yml` | Push to `main`, weekly schedule  | Static security analysis               |

### ci.yml Pipeline Stages

```
Trigger (push / PR)
   └── Lint (./gradlew lintDebug)
         └── Unit Tests (./gradlew testDebugUnitTest)
               └── Build Debug APK (./gradlew assembleDebug)
                     └── Upload APK as workflow artifact
```

All three stages must pass before a PR can be merged. The debug APK is available as a downloadable artifact from the workflow run page.

> **Note:** Instrumented tests are not run in CI as they require a hardware device or emulator.

---

## Release Process

### 1. Prepare the Release

```bash
git checkout main
git pull --ff-only origin main
```

### 2. Update Version

Edit `app/build.gradle.kts`:

```kotlin
versionCode = 2   // increment by 1 for each release
versionName = "1.0.1"  // follow semantic versioning
```

> See [docs/ROADMAP.md](ROADMAP.md) for the planned version schedule.

### 3. Create a Signed Release APK

```bash
./gradlew assembleRelease
```

### 4. Sign the APK

Using `apksigner` from the Android SDK:

```bash
apksigner sign --ks release-keystore.jks \
               --ks-key-alias immersiads \
               --ks-pass pass:<keystore-password> \
               --key-pass pass:<key-password> \
               --out app/build/outputs/apk/release/app-release.apk \
               app/build/outputs/apk/release/app-release-unsigned.apk
```

Output: `app/build/outputs/apk/release/app-release.apk`

### 5. Verify

```bash
apksigner verify app/build/outputs/apk/release/app-release.apk
```

### 6. Create a GitHub Release

```bash
gh release create v1.0.1 \
  --title "v1.0.1" \
  --notes "Release notes here" \
  app/build/outputs/apk/release/app-release.apk
```

### 7. Tag the Release

```bash
git tag -a v1.0.1 -m "v1.0.1"
git push origin v1.0.1
```

---

## Signing

### Generating a Keystore

```bash
keytool -genkey -v -keystore release-keystore.jks \
        -alias immersiads \
        -keyalg RSA \
        -keysize 2048 \
        -validity 10000
```

### Configuring Signing in Gradle

Create `app/keystore.properties` (do **not** commit):

```properties
storeFile=../release-keystore.jks
storePassword=<your-store-password>
keyAlias=immersiads
keyPassword=<your-key-password>
```

In `app/build.gradle.kts`, the signing config reads this file:

```kotlin
val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(keystorePropertiesFile.inputStream())
}

android {
    signingConfigs {
        create("release") {
            storeFile = keystoreProperties["storeFile"]?.let { file(it) }
            storePassword = keystoreProperties["storePassword"] as? String
            keyAlias = keystoreProperties["keyAlias"] as? String
            keyPassword = keystoreProperties["keyPassword"] as? String
        }
    }
}
```

> **Security:** Add `keystore.properties` and `*.jks` to `.gitignore`. In CI, inject signing credentials via repository secrets.

---

## Versioning

The app follows **semantic versioning** (`major.minor.patch`):

| Component     | Location               | Example   |
|---------------|------------------------|-----------|
| `versionCode` | `app/build.gradle.kts` | `2`       |
| `versionName` | `app/build.gradle.kts` | `"1.0.0"` |
| Git tag       | `v1.0.0`               | `v1.0.0`  |

- `versionCode` — Integer incremented for each release (used by Android for upgrade detection)
- `versionName` — Human-readable version displayed to users

---

## ProGuard & R8

ProGuard/R8 rules are configured in `app/proguard-rules.pro`.

### Current Rules

```proguard
# Keep application class
-keep class com.immersiads.app.ImmersiAdsApp { *; }

# Keep Room entities
-keep class com.immersiads.app.data.local.entities.** { *; }
```

### Testing Release Build

```bash
./gradlew assembleRelease --stacktrace
```

If `minifyEnabled = true` causes crashes, run:

```bash
./gradlew assembleRelease -Pminify=false
```

Then inspect the mapping file at `app/build/outputs/mapping/release/mapping.txt` to debug obfuscation issues.

---

## GitHub Actions Configuration

### Required Secrets

If CI signing is configured, add these to the repository under **Settings → Secrets and variables → Actions**:

| Secret              | Purpose                      |
|---------------------|------------------------------|
| `KEYSTORE_BASE64`   | Base64-encoded keystore file |
| `KEYSTORE_PASSWORD` | Keystore password            |
| `KEY_ALIAS`         | Key alias                    |
| `KEY_PASSWORD`      | Key password                 |

### CI Self-Hosted Runner Notes

- The CI workflow runs on `ubuntu-latest` (GitHub-hosted).
- Android SDK is installed via `actions/setup-java` and the Gradle wrapper handles the rest.
- If using self-hosted runners, ensure Android SDK 35 and JDK 17 are pre-installed.

---

## Environment Configuration

### Build Variants

| Variant   | `applicationId`            | `minifyEnabled` | Debuggable |
|-----------|----------------------------|-----------------|------------|
| `debug`   | `com.immersiads.app.debug` | `false`         | Yes        |
| `release` | `com.immersiads.app`       | `true`          | No         |

### Switching Between Sample and Real Data

Sample data is hardcoded in `SampleData.kt`. To integrate a real API:

1. Create a remote data source (e.g., Retrofit-based).
2. Update `AdRepository` to fetch from the remote source.
3. Keep `SampleData` as a fallback for development.

No feature flags or build flavors are configured yet — these can be added in `build.gradle.kts`:

```kotlin
flavorDimensions("data_source")
productFlavors {
    create("sample") { dimension = "data_source" }
    create("production") { dimension = "data_source" }
}
```

---

## Monitoring & Alerts

### CI Monitoring

- Workflow failures send notifications via GitHub's default email alerts.
- Add a Slack/Discord webhook in GitHub Settings → Notifications for real-time CI status.

### Crash Reporting

The app does not currently include a crash reporting SDK. To add one:

1. Add `firebase-crashlytics` and `firebase-analytics` to `gradle/libs.versions.toml`.
2. Add the Google Services plugin to the root and app `build.gradle.kts`.
3. Place `google-services.json` (download from Firebase Console) in `app/`.

### App Health

Track these metrics after release:

- **Crash-free rate** — Target > 99.5%
- **DAU/MAU** — Daily/Monthly active users
- **Average session length** — Measures engagement with video content
- **Vocabulary items saved per user** — Core action metric

---

## Maintenance Tasks

| Frequency   | Task                                                                 |
|-------------|----------------------------------------------------------------------|
| Weekly      | Review CI workflow runs for failures                                 |
| Monthly     | Check for dependency updates (Gradle, AGP, Compose, Room, ExoPlayer) |
| Per release | Verify ProGuard mapping file is saved with the release               |
| Per release | Test release build on a physical device before publishing            |
| Quarterly   | Rotate keystore passwords if used in shared environments             |
