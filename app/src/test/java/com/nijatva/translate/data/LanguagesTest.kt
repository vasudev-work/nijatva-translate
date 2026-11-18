package com.nijatva.translate.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LanguagesTest {

    @Test
    fun indicCatalogueCoversEveryScheduledLanguage() {
        assertEquals(22, Languages.indic.size)
    }

    @Test
    fun languageCodesAreUnique() {
        val codes = Languages.all.map { it.code }
        assertEquals(codes.size, codes.distinct().size)
    }

    @Test
    fun sourcePickerStartsWithAutoDetect() {
        assertEquals(Languages.AUTO, Languages.sourceOptions.first().code)
    }

    @Test
    fun searchMatchesEnglishNamesNativeNamesAndCodes() {
        assertTrue(Languages.byCode("hi").matches("hind"))
        assertTrue(Languages.byCode("ta").matches("தமிழ்"))
        assertTrue(Languages.byCode("mr").matches("MR"))
        assertTrue(Languages.byCode("bn").matches(""))
    }

    @Test
    fun unknownCodesFallBackToAutoDetect() {
        assertEquals(Languages.AUTO, Languages.byCode("xx").code)
    }
}
