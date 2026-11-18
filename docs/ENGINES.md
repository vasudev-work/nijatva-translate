# Engine notes

Nijatva Translate ships one engine implementation and a narrow interface so other
backends can be added without touching the UI.

```kotlin
interface TranslationEngine {
    suspend fun translate(text: String, source: String, target: String): String
}
```

`LibreTranslateEngine` is the reference implementation. It speaks the
`POST /translate` JSON contract used by LibreTranslate and by most self-hosted
gateways that wrap Indic models, so pointing the app at a different backend is a
settings change, not a code change.

## Backends worth knowing

| Backend | What it gives you | How to use it here |
| --- | --- | --- |
| [LibreTranslate](https://github.com/LibreTranslate/LibreTranslate) | Self-hosted REST API on Argos Translate models, Docker friendly, no cloud dependency | Run locally, set the base URL to `http://10.0.2.2:5000` from the emulator |
| [AI4Bharat IndicTrans2](https://github.com/AI4Bharat/IndicTrans2) | The NMT models behind Bhashini, all 22 scheduled Indian languages, CTranslate2 quantisation for CPU | Wrap in a small FastAPI service exposing `/translate` |
| [Bhashini](https://bhashini.gov.in) | Government APIs for ASR, MT and TTS across 22 languages | Needs an API key; front it with a gateway that maps its payload to this contract |
| [bharatvoiceai](https://github.com/DuttaSam/bharatvoiceai) | Unified FastAPI wrapper over Bhashini, AI4Bharat and Sarvam AI | Point the base URL at it once it exposes a `/translate` route |
| [lekhaanuvaad](https://github.com/bhashini-dibd/lekhaanuvaad) | Document translation that preserves layout, OCR included | Server side only; not an app dependency |
| [RTranslator](https://github.com/niedev/RTranslator) | Fully offline on-device speech and text translation via ONNX | Reference for an offline mode later |

## Adding an engine

1. Implement `TranslationEngine` in `app/src/main/java/com/nijatva/translate/data/`.
2. Choose it in `TranslationRepository.engineFor()`.
3. Keep the `source`/`target` values as ISO codes; the language picker already
   emits them.

An engine that streams audio or returns partial results needs a wider interface
than this one — add it alongside rather than widening `TranslationEngine` for
every caller.

## Privacy

Text leaves the device only when you press Translate, and only to the endpoint you
configure in Engine settings. There is no analytics, no bundled key and no default
account. If that matters to you, run LibreTranslate on your own machine and point
the app at it over your LAN.
