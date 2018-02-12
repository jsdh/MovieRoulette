package com.jdhdev.mm8.data

import com.jdhdev.mm8.data.source.ScoresDataSource
import com.jdhdev.mm8.data.source.ScoresRepository
import com.jdhdev.mm8.data.source.remote.imdbapi.Model.ImdbMovieInfo
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test


class ScoresRepositoryTest {
    private lateinit var scoresRepo: ScoresRepository

    private val remoteSource: ScoresDataSource = mock()
    private val localSource: ScoresDataSource = mock()

    @Before
    fun setUp() {
        scoresRepo = ScoresRepository(remoteSource, localSource)
    }

    @Test
    fun getScore_retrievesDataFromRemoteSource() {
        val testInfo = ImdbMovieInfo("42", "4.2", "2.4", mutableListOf())
        whenever(remoteSource.getScore("42")).doReturn(Observable.just(testInfo))
        whenever(localSource.getScore(any())).doReturn(Observable.empty())

        scoresRepo.getScore("42").test().apply {
            assertNoErrors()
            assertValueCount(1)
            assertValueAt(0, { it == testInfo })
        }
    }
}