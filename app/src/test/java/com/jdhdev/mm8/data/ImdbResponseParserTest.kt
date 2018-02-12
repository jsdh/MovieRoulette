package com.jdhdev.mm8.data

import com.jdhdev.mm8.data.source.remote.imdbapi.ImdbResponseParser
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Before
import org.junit.Test
import java.io.File
import kotlin.test.assertTrue


class ImdbResponseParserTest {
    private lateinit var testHtmlString: String

    private val parser = ImdbResponseParser()

    @Before
    fun setUp() {
        val data = javaClass.classLoader.getResource("imdb_test_data.html")

        assertTrue { data != null }

        val inputStream = File(data.toURI()).inputStream()
        testHtmlString = inputStream.bufferedReader().use { it.readText() }

        assertTrue { testHtmlString.isNotEmpty() }
    }

    @Test
    fun parseResponse_correctlyParsesImdbInfo() {
        val info = parser.parseResponse(testHtmlString)

        assertThat(info.imdbId, equalTo("tt1074638"))
        assertThat(info.rating, equalTo("7.8"))
        assertThat(info.metaScore, equalTo("81"))
    }

    // TODO unit test for parsing the cast
}