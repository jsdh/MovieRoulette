package com.jdhdev.mm8.movies

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import com.jdhdev.mm8.MmApplication
import com.jdhdev.mm8.R

class MoviesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movies)

        val fragment = supportFragmentManager.findFragmentById(R.id.container) as MoviesFragment?
                ?: getFragmentInstance()

        val app = application as MmApplication
        MoviesPresenter(app.objectGraph.schedulerProvider,
                app.objectGraph.dataManager,
                fragment)
    }

    private fun getFragmentInstance() =
            MoviesFragment.newInstance().also {
                supportFragmentManager.beginTransaction()
                        .add(R.id.container, it)
                        .commit()
            }

}
