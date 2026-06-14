# ImmersiAds — Network Troubleshooting

This guide covers common network-related issues when building ImmersiAds and how to resolve them.

---

## Table of Contents

- [Prerequisite Connectivity](#prerequisite-connectivity)
- [Quick Diagnostics](#quick-diagnostics)
- [Corporate Proxy](#corporate-proxy)
- [DNS Failures](#dns-failures)
- [GitHub Connectivity Issues](#github-connectivity-issues)
- [Offline Development](#offline-development)
- [Gradle Cache Issues](#gradle-cache-issues)
- [Gradle Distribution Download Failures](#gradle-distribution-download-failures)
- [Maven Repository Access](#maven-repository-access)
- [Android Studio Configuration](#android-studio-configuration)
- [Mirrors and Alternatives](#mirrors-and-alternatives)

---

## Prerequisite Connectivity

Building ImmersiAds requires network access to these services:

| Service | URL | Purpose |
|---|---|---|
| Gradle | `services.gradle.org` | Download Gradle distribution (8.11.1) |
| GitHub | `github.com` | Source code, release assets |
| Google | `dl.google.com` | Android SDK, Google Maven repository |
| Maven Central | `repo1.maven.org` | Kotlin, Coroutines, and other dependencies |
| Gradle Plugin Portal | `plugins.gradle.org` | Android, Kotlin, KSP plugins |
| GitHub assets | `release-assets.githubusercontent.com` | Gradle wrapper downloads |

---

## Quick Diagnostics

Run the network diagnostics script to check all connectivity at once:

**Windows:**
```powershell
.\scripts\diagnose-network.ps1
```

**macOS / Linux:**
```bash
bash scripts/diagnose-network.sh
```

The script tests:
- DNS resolution for each service
- HTTP HEAD requests to each URL
- Proxy configuration detection
- Gradle wrapper configuration (timeout, retries)
- Android SDK license status

---

## Corporate Proxy

### Setting Proxy Environment Variables

**Windows (PowerShell):**
```powershell
$env:HTTP_PROXY = "http://proxy.yourcompany.com:8080"
$env:HTTPS_PROXY = "http://proxy.yourcompany.com:8080"
$env:NO_PROXY = "localhost,127.0.0.1,.local"
```

**macOS / Linux:**
```bash
export HTTP_PROXY=http://proxy.yourcompany.com:8080
export HTTPS_PROXY=http://proxy.yourcompany.com:8080
export NO_PROXY=localhost,127.0.0.1,.local
```

### Gradle-Specific Proxy Configuration

Add to `gradle.properties` (project root):

```properties
systemProp.http.proxyHost=proxy.yourcompany.com
systemProp.http.proxyPort=8080
systemProp.https.proxyHost=proxy.yourcompany.com
systemProp.https.proxyPort=8080
systemProp.http.proxyUser=username
systemProp.http.proxyPassword=password
```

Or pass via command line:

```bash
./gradlew assembleDebug -Dhttp.proxyHost=proxy.yourcompany.com -Dhttp.proxyPort=8080
```

### Android Studio Proxy

File → Settings → Appearance & Behavior → System Settings → HTTP Proxy:
- Select "Manual proxy configuration"
- Enter your proxy host and port
- Check "Proxy authentication" if needed

---

## DNS Failures

### Symptoms

- `UnknownHostException` during Gradle builds
- Long hangs followed by connection timeouts

### Diagnosis

**Windows (PowerShell):**
```powershell
Resolve-DnsName services.gradle.org
```

**macOS / Linux:**
```bash
nslookup services.gradle.org
```

### Solutions

1. **Flush DNS cache:**
   ```bash
   # Windows
   ipconfig /flushdns

   # macOS
   sudo dscacheutil -flushcache; sudo killall -HUP mDNSResponder

   # Linux
   sudo systemd-resolve --flush-caches
   ```

2. **Use alternative DNS servers** (Google: `8.8.8.8`, Cloudflare: `1.1.1.1`)

3. **Add hosts file entries** (temporary workaround):
   ```
   # Windows: C:\Windows\System32\drivers\etc\hosts
   # macOS/Linux: /etc/hosts
   140.82.121.3  github.com
   ```

---

## GitHub Connectivity Issues

### Symptoms

- `git clone` hangs or fails
- Build fails trying to download from `github.com` or `release-assets.githubusercontent.com`
- Gradle wrapper download fails

### Solutions

1. **Use SSH instead of HTTPS:**
   ```bash
   git clone git@github.com:anomalyco/immersi-ads-android.git
   ```

2. **Use a personal access token:**
   ```bash
   git clone https://<username>:<token>@github.com/anomalyco/immersi-ads-android.git
   ```

3. **Check if GitHub is blocked** by your network — try visiting https://github.com in a browser

4. **Use a VPN** if GitHub is unavailable in your region

---

## Offline Development

Once you have completed an initial successful build, you can work offline.

### Gradle Offline Mode

```bash
# All scripts support --offline flag
bash scripts/build.sh --offline
bash scripts/test.sh --offline

# Or directly with gradlew
./gradlew assembleDebug --offline
```

### Pre-cache Dependencies

Run this on a connected network to populate the local cache:

```bash
# Cache Gradle distribution
./gradlew --no-daemon

# Cache all project dependencies
./gradlew assembleDebug --no-daemon

# Cache for offline use
./gradlew assembleDebug --no-daemon --build-cache
```

After this, `--offline` mode will work without internet access.

### What Gradle Offline Mode Covers

- Project dependencies (Maven Central, Google Maven, etc.)
- Gradle plugins (Android, Kotlin, KSP)
- Gradle wrapper distribution

### What Offline Mode Does NOT Cover

- First-time Gradle wrapper download (the wrapper jar must already exist)
- Android SDK downloads (system images, platform tools)
- Git operations

---

## Gradle Cache Issues

### Clearing the Cache

```bash
# Clear project build artifacts
bash scripts/clean.sh

# Clear Gradle global cache
rm -rf ~/.gradle/caches/

# Clear only Gradle distributions
rm -rf ~/.gradle/wrapper/dists/

# Clear specific version
rm -rf ~/.gradle/wrapper/dists/gradle-8.11.1-*
```

### Corrupted Cache Symptoms

- `Checksum mismatch` errors
- `CRC checksum failed` errors
- Build hangs at `Downloading Gradle distribution...`

### Fixing Cache Corruption

1. Stop any running Gradle processes:
   ```bash
   ./gradlew --stop
   ```

2. Clear the distribution cache:
   ```bash
   rm -rf ~/.gradle/wrapper/dists/gradle-8.11.1-*
   ```

3. Re-run:
   ```bash
   ./gradlew assembleDebug
   ```

### Gradle Daemon Issues

```bash
# Stop all daemons
./gradlew --stop

# Disable daemon (useful for debugging)
./gradlew assembleDebug --no-daemon
```

---

## Gradle Distribution Download Failures

### Understanding the Download

The Gradle wrapper downloads the distribution from:
```
https://services.gradle.org/distributions/gradle-8.11.1-bin.zip
```

### Common Failures

| Error | Likely Cause | Solution |
|---|---|---|
| `UnknownHostException: services.gradle.org` | DNS failure | Check DNS or use a mirror |
| `Read timed out` | Slow network | Increase `networkTimeout` in `gradle-wrapper.properties` |
| `Connection refused` | Proxy/firewall blocking | Configure proxy |
| `SSLHandshakeException` | Corporate SSL inspection | Use HTTP URL (less secure) or add cert to trust store |
| `checksum mismatch` | Corrupted download | Clear cache and retry |

### Manual Distribution Download

If the automatic download fails, you can manually download and place the distribution:

1. Download `gradle-8.11.1-bin.zip` from:
   - Primary: https://services.gradle.org/distributions/gradle-8.11.1-bin.zip
   - Mirror: https://mirrors.aliyun.com/gradle/gradle-8.11.1-bin.zip

2. Place it in the Gradle wrapper cache:
   ```
   ~/.gradle/wrapper/dists/gradle-8.11.1-bin/<hash>/
   ```
   (The hash is computed from the URL — check the error message for the exact path)

### Invalid Distribution URL

If `services.gradle.org` is blocked, edit `gradle/wrapper/gradle-wrapper.properties` and use an alternative mirror:

```properties
# Primary (default)
distributionUrl=https\://services.gradle.org/distributions/gradle-8.11.1-bin.zip

# Alternative mirrors — uncomment one:
# distributionUrl=https\://mirrors.aliyun.com/gradle/gradle-8.11.1-bin.zip
# distributionUrl=https\://downloads.gradle-dn.com/distributions/gradle-8.11.1-bin.zip
```

---

## Maven Repository Access

### Symptoms

- Build fails with `Could not resolve all files for configuration`
- Errors downloading from `repo1.maven.org`, `dl.google.com`, or `plugins.gradle.org`

### Solutions

1. **Verify Maven Central is accessible:**
   ```bash
   curl -I https://repo1.maven.org/maven2/
   ```

2. **Check Google Maven:**
   ```bash
   curl -I https://dl.google.com/dl/android/maven2/
   ```

3. **Add alternative repositories** in `settings.gradle.kts` (behind a mirror):

   ```kotlin
   dependencyResolutionManagement {
       repositories {
           // Try mirrors first, fall back to originals
           maven { url = uri("https://maven.aliyun.com/repository/public") }
           maven { url = uri("https://maven.aliyun.com/repository/google") }
           mavenCentral()
           google()
       }
   }
   ```

---

## Android Studio Configuration

### Proxy in Android Studio

File → Settings → Appearance & Behavior → System Settings → HTTP Proxy:
- **Auto-detect** — Use system proxy settings
- **Manual** — Enter host and port
- **No proxy** — Direct connection

### Gradle JDK Configuration

File → Settings → Build, Execution, Deployment → Build Tools → Gradle:
- **Gradle JDK** — Ensure it points to JDK 17 or later
- **Offline work** — Check this box to work in offline mode

### Gradle Settings

File → Settings → Build, Execution, Deployment → Build Tools → Gradle:
- **Maven repositories** — Can add mirror URLs here

---

## Mirrors and Alternatives

### Gradle Distribution Mirrors

| URL | Host |
|---|---|
| `https://services.gradle.org/distributions/` | Official (default) |
| `https://downloads.gradle-dn.com/distributions/` | CDN |
| `https://mirrors.aliyun.com/gradle/` | China mirror |
| `https://mirrors.cloud.tencent.com/gradle/` | Tencent Cloud |
| `https://mirrors.huaweicloud.com/gradle/` | Huawei Cloud |

### Maven Repository Mirrors

| URL | Maps To | Region |
|---|---|---|
| `https://maven.aliyun.com/repository/public` | Maven Central | China |
| `https://maven.aliyun.com/repository/google` | Google Maven | China |
| `https://repo.huaweicloud.com/repository/maven/` | Maven Central | China |
| `https://mirrors.tencent.com/nexus/repository/maven-public/` | Maven Central | China |

---

## Gradle Configuration Cache

If you encounter strange build failures after network issues, the Gradle configuration cache may be stale:

```bash
# Clean build with fresh configuration cache
./gradlew clean assembleDebug --no-configuration-cache

# Re-enable after clean build
./gradlew assembleDebug
```

Or disable temporarily in `gradle.properties`:
```properties
org.gradle.configuration-cache=false
```
