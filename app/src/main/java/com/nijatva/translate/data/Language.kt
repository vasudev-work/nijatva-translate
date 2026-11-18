package com.nijatva.translate.data

/**
 * A language Nijatva Translate can offer in its pickers.
 *
 * [code] is the ISO 639-1 (or ISO 639-3 where needed) tag the translation engine expects.
 */
data class Language(
    val code: String,
    val englishName: String,
    val nativeName: String,
    val script: String = "",
) {
    /** Native name when we have one, otherwise the English name. */
    val label: String
        get() = if (nativeName.isBlank() || nativeName.equals(englishName, ignoreCase = true)) {
            englishName
        } else {
            nativeName
        }

    fun matches(query: String): Boolean {
        if (query.isBlank()) return true
        val q = query.trim()
        return englishName.contains(q, ignoreCase = true) ||
            nativeName.contains(q, ignoreCase = true) ||
            code.equals(q, ignoreCase = true)
    }
}

object Languages {

    const val AUTO = "auto"

    val auto = Language(AUTO, "Detect language", "Detect language")

    val english = Language("en", "English", "English")

    /** The 22 scheduled languages of India, in English alphabetical order. */
    val indic: List<Language> = listOf(
        Language("as", "Assamese", "অসমীয়া", "Bengali-Assamese"),
        Language("bn", "Bengali", "বাংলা", "Bengali-Assamese"),
        Language("brx", "Bodo", "बर'", "Devanagari"),
        Language("doi", "Dogri", "डोगरी", "Devanagari"),
        Language("gu", "Gujarati", "ગુજરાતી", "Gujarati"),
        Language("hi", "Hindi", "हिन्दी", "Devanagari"),
        Language("kn", "Kannada", "ಕನ್ನಡ", "Kannada"),
        Language("kok", "Konkani", "कोंकणी", "Devanagari"),
        Language("ks", "Kashmiri", "کٲشُر", "Perso-Arabic"),
        Language("mai", "Maithili", "मैथिली", "Devanagari"),
        Language("ml", "Malayalam", "മലയാളം", "Malayalam"),
        Language("mni", "Manipuri", "মৈতৈলোন্", "Meitei Mayek"),
        Language("mr", "Marathi", "मराठी", "Devanagari"),
        Language("ne", "Nepali", "नेपाली", "Devanagari"),
        Language("or", "Odia", "ଓଡ଼ିଆ", "Odia"),
        Language("pa", "Punjabi", "ਪੰਜਾਬੀ", "Gurmukhi"),
        Language("sa", "Sanskrit", "संस्कृतम्", "Devanagari"),
        Language("sat", "Santali", "ᱥᱟᱱᱛᱟᱲᱤ", "Ol Chiki"),
        Language("sd", "Sindhi", "سنڌي", "Perso-Arabic"),
        Language("ta", "Tamil", "தமிழ்", "Tamil"),
        Language("te", "Telugu", "తెలుగు", "Telugu"),
        Language("ur", "Urdu", "اردو", "Perso-Arabic"),
    )

    /** A short set of widely used non-Indian languages. */
    val world: List<Language> = listOf(
        Language("ar", "Arabic", "العربية", "Arabic"),
        Language("de", "German", "Deutsch", "Latin"),
        Language("es", "Spanish", "Español", "Latin"),
        Language("fa", "Persian", "فارسی", "Perso-Arabic"),
        Language("fr", "French", "Français", "Latin"),
        Language("id", "Indonesian", "Bahasa Indonesia", "Latin"),
        Language("it", "Italian", "Italiano", "Latin"),
        Language("ja", "Japanese", "日本語", "Japanese"),
        Language("ko", "Korean", "한국어", "Hangul"),
        Language("pt", "Portuguese", "Português", "Latin"),
        Language("ru", "Russian", "Русский", "Cyrillic"),
        Language("sw", "Swahili", "Kiswahili", "Latin"),
        Language("th", "Thai", "ไทย", "Thai"),
        Language("tr", "Turkish", "Türkçe", "Latin"),
        Language("vi", "Vietnamese", "Tiếng Việt", "Latin"),
        Language("zh", "Chinese", "中文", "Han"),
    )

    val all: List<Language> = (listOf(english) + indic + world).distinctBy { it.code }

    /** Source pickers also allow automatic language detection. */
    val sourceOptions: List<Language> = listOf(auto) + all

    fun byCode(code: String): Language =
        all.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: auto
}
