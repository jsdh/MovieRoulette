package com.jdhdev.mm8.movieDetails

import android.content.Intent
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.jdhdev.mm8.R
import com.jdhdev.mm8.data.FakeDataManager
import com.jdhdev.mm8.data.Model
import com.jdhdev.mm8.data.Movie
import com.jdhdev.mm8.data.source.remote.imdbapi.Model.ImdbMovieInfo
import com.jdhdev.mm8.movies.MoviesContract
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class MovieDetailsScreenTest {
    private val fakeModelSource = FakeDataManager.testSource

    @get:Rule
    val detailsActivityTestRule = ActivityTestRule<MovieDetailsActivity>(
            MovieDetailsActivity::class.java, true, false)

    private fun verifyIsDisplayed(viewId: Int) =
            onView(withId(viewId)).check(matches(isDisplayed()))


    private fun startActivityWithId(id: Int) {
        val intent = Intent()
        intent.putExtra(MovieDetailsActivity.EXTRA_MOVIE_ID, id)
        detailsActivityTestRule.launchActivity(intent)
    }

    private fun getModelWithAMovie() =
            Movie(42 ,"Peanut m&ms", "Sep 9th 2107", "", "")
                .updateWithImdbInfo(ImdbMovieInfo("4242", "4.2", "24", null))
                    .let {
                        Model(it, Movie.EMPTY, Movie.EMPTY,
                                false, false, false,
                                false, false, false)
                    }

    @Test
    fun allViews_DisplayedInUi() {
        startActivityWithId(MoviesContract.View.MOVIE_SLOT_1)

        fakeModelSource.onNext(getModelWithAMovie())

        verifyIsDisplayed(R.id.header_image)
        verifyIsDisplayed(R.id.toolbar)
        verifyIsDisplayed(R.id.release_date)
        verifyIsDisplayed(R.id.esrb_rating)
        verifyIsDisplayed(R.id.runtime)
        verifyIsDisplayed(R.id.languages)
        verifyIsDisplayed(R.id.genres)
        verifyIsDisplayed(R.id.poster)
        verifyIsDisplayed(R.id.summary)
        verifyIsDisplayed(R.id.imdb_logo)
        verifyIsDisplayed(R.id.imdb_rating)
        verifyIsDisplayed(R.id.metascore)
        verifyIsDisplayed(R.id.metascore_title)
        verifyIsDisplayed(R.id.share_movie)
//        verifyIsDisplayed(R.id.fab) TODO -- fails b/c of transition callback isn't triggered
    }

    // TODO
}