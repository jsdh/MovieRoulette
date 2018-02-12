package com.jdhdev.mm8.movies

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.view.View
import com.jdhdev.mm8.R
import com.jdhdev.mm8.data.FakeDataManager
import com.jdhdev.mm8.data.Model
import com.jdhdev.mm8.data.Movie
import com.jdhdev.mm8.data.source.remote.imdbapi.Model.ImdbMovieInfo
import org.hamcrest.Matcher
import org.hamcrest.Matchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// NOTE: https://stackoverflow.com/a/30172064/7411230

@RunWith(AndroidJUnit4::class)
@LargeTest
class MoviesScreenTest {
    private val modelIn = FakeDataManager.testSource
    // TODO.. listOf(...) throws a method not found runtime error.. ??
    private val tileIds = java.util.Arrays.asList(R.id.movie_tile_1, R.id.movie_tile_2, R.id.movie_tile_3)

    @get:Rule
    val moviesActivityTestRule = ActivityTestRule<MoviesActivity>(
            MoviesActivity::class.java, true, true)

    private fun verifyIsDisplayed(parent: Int, child: Int = -1) =
            verify(parent, child, isDisplayed())

    private fun verifyNotDisplayed(parent: Int, child: Int = -1) =
            verify(parent, child, not(isDisplayed()))

    private fun verify(parent: Int, child: Int, test: Matcher<View>) =
            if (child != -1)
                onView(allOf(isDescendantOfA(withId(parent)),
                        withId(child)))
                        .check(matches(test))
            else
                onView(withId(parent)).check(matches(test))

    @Test
    fun emptyState_DisplayedInUi() {
        modelIn.onNext(Model())

        verifyIsDisplayed(R.id.refresh_movies)
        tileIds.forEach {
            verifyIsDisplayed(it)
        }
    }

    @Test
    fun clickFab_requestsNewRandomMovies() {
        val count = FakeDataManager.findNewRandomMoviesClicks
        onView(withId(R.id.refresh_movies)).perform(click())

        assertThat(FakeDataManager.findNewRandomMoviesClicks, equalTo(count + 1))

    }

    @Test
    fun loadingAllMovies_DisplayedInUi() {
        var m = Model(Movie.EMPTY, Movie.EMPTY, Movie.EMPTY,
                true, true, true,
                true, true, true)
        modelIn.onNext(m)

        tileIds.forEach {
            verifyIsDisplayed(it, R.id.loadingMovie)
            verifyIsDisplayed(it, R.id.loadingScores)
        }

        m = Model(Movie.EMPTY, Movie.EMPTY, Movie.EMPTY,
                false, false, false,
                false, false, false)
        modelIn.onNext(m)

        tileIds.forEach {
            verifyNotDisplayed(it, R.id.loadingMovie)
            verifyNotDisplayed(it, R.id.loadingScores)
        }
    }

    @Test
    fun movieData_DisplayedInUi() {
        val movie = Movie(42 ,"Peanut m&ms", "Sep 9th 2107", "", "")
                .updateWithImdbInfo(ImdbMovieInfo("4242", "4.2", "24", null))

        val model = Model(movie, Movie.EMPTY, Movie.EMPTY,
                false, false, false,
                false, false, false)
        modelIn.onNext(model)

        tileIds.forEach {
            verifyNotDisplayed(it, R.id.loadingMovie)
            verifyNotDisplayed(it, R.id.loadingScores)
        }

        onView(allOf(isDescendantOfA(withId(tileIds[0])),
                withId(R.id.movie)))
                .check(matches(withText(movie.title)))

        onView(allOf(isDescendantOfA(withId(tileIds[0])),
                withId(R.id.movie_year)))
                .check(matches(withText("2107")))

        onView(allOf(isDescendantOfA(withId(tileIds[0])),
                withId(R.id.movie_meta_score)))
                .check(matches(withText(movie.metaScore)))

        onView(allOf(isDescendantOfA(withId(tileIds[0])),
                withId(R.id.movie_imdb_score)))
                .check(matches(withText(movie.imdbScore)))


    }
}