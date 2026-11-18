<div align="center">

<img src="docs/icon.png" alt="Nijatva Translate" width="140" />

# Nijatva Translate

**A calm, offline-friendly translator for India's languages — bring your own engine.**

Kotlin · Jetpack Compose · Material 3 · `com.nijatva.translate`

</div>

---

## What it is

Nijatva Translate is an Android client for translation engines you control. The UI
covers all 22 scheduled languages of India plus a set of world languages, and it
talks to any LibreTranslate-compatible endpoint — a local Docker container, a
self-hosted IndicTrans2 service, or your own Bhashini gateway.

Nothing is hard-wired to a vendor: the endpoint and API key live in **Engine
settings**, so the same APK works against a laptop on your LAN or a server you
run in production.

## Features

- **22 scheduled Indian languages** with native-script names, in a searchable picker.
- **Source auto-detection**, one-tap language swap (it swaps the text too).
- **Engine-agnostic**: any LibreTranslate-compatible `POST /translate` endpoint.
- **Speak the result** with the platform text-to-speech engine, using the target language tag.
- **Copy, share, and receive text** — the app registers for `ACTION_SEND` text shares,
  so "Translate" appears in other apps' share sheets.
- **Material 3** with a midnight-navy and ice-blue palette, light and dark themes.
- No analytics, no bundled keys, no account.

## Quick start

```bash
git clone https://github.com/vasudev-work/nijatva-translate.git
cd nijatva-translate
```

Open the folder in Android Studio (Ladybug or newer) and press **Run**, or from the
command line:

```bash
gradle wrapper --gradle-version 8.9   # once, if gradlew is missing
./gradlew assembleDebug
```

Requirements: JDK 17, Android SDK 35, `minSdk` 24. The Gradle wrapper JAR is not
committed, so run `gradle wrapper` once (or let Android Studio generate it) before
using `./gradlew`.

## Point it at an engine

The app starts with the public LibreTranslate endpoint. For anything serious, run
your own:

```bash
docker run -ti --rm -p 5000:5000 libretranslate/libretranslate
```

Then open **Engine settings** in the app:

| Field | Value |
| --- | --- |
| Base URL | `http://10.0.2.2:5000` from the emulator, `http://<your-lan-ip>:5000` from a device |
| API key | leave blank unless your server requires one |

Plain HTTP is refused on Android 9+ unless the host is allowlisted. For a LAN
server, add a `network_security_config.xml` that permits that one host — see
[`docs/ENGINES.md`](docs/ENGINES.md) for the wider list of backends
(IndicTrans2, Bhashini, bharatvoiceai) and how to wrap them in this contract.

## Project layout

```
app/src/main/java/com/nijatva/translate/
├── MainActivity.kt                 entry point, edge-to-edge, share-intent intake
├── data/
│   ├── Language.kt                 the 22 scheduled languages + world set, search
│   ├── TranslationEngine.kt        the interface + LibreTranslateEngine
│   ├── SettingsStore.kt            DataStore-backed engine URL and key
│   └── TranslationRepository.kt    single entry point for translation
└── ui/
    ├── NijatvaTranslateApp.kt      screen switch (translate ↔ settings)
    ├── TranslateScreen.kt          language bar, input, result card, picker sheet
    ├── SettingsScreen.kt           engine configuration
    ├── TranslateViewModel.kt       state, swap logic, request lifecycle
    └── theme/                      colours, typography, Material 3 scheme
```

Tests live in `app/src/test/` and cover the language catalogue and search.
`tools/generate_icons.py` regenerates every launcher mipmap from one drawing
routine — change the geometry there, not in ten PNGs.

## Icon

A white outline speech bubble on a midnight navy rounded tile, drawn as an adaptive
icon (vector background + foreground, monochrome layer for themed icons) with
downsampled PNGs for API 24–25.

## Status

Version 1.0.0 is a working text translator: language selection, translation,
copy/share/listen, and engine configuration. Speech input, camera OCR, offline
models and conversation mode are the natural next steps — see the issue tracker.

## Licence

MIT. See [LICENSE](LICENSE).
