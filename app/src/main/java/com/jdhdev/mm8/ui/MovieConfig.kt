package com.jdhdev.mm8.ui

import android.content.Context
import android.os.Handler
import android.support.annotation.AttrRes
import android.support.annotation.StyleRes
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import belka.us.androidtoggleswitch.widgets.MultipleToggleSwitch

import com.jdhdev.mm8.R
import com.jdhdev.mm8.data.TmdbConfiguration
import com.jdhdev.mm8.data.TmdbConfiguration.Companion.ESBRating
import com.jdhdev.mm8.data.TmdbConfiguration.Companion.Genres


import java.util.HashSet

import kotlinx.android.synthetic.main.movie_config_view.view.*


class MovieConfig : LinearLayout {
    private var endYear         = 2050
    private var startYear       = 1899
    private var includeAdult    = false
    private var rating          = ESBRating.ANY
    private val selectedGenres  = HashSet<Genres>()

    private var notifyEnabled   = true
    private val uiMaping        = HashMap<Int, ToggleSwitch>()
    private var currentState    = TmdbConfiguration(startYear, endYear, rating, selectedGenres, includeAdult)
    private var delayedNotification : Runnable? = null
    private var listener: ConfigChangeListener? = null
    private lateinit var uiHandler: Handler

    inner class ToggleSwitch(val ui: MultipleToggleSwitch, val id: Int)


    interface ConfigChangeListener {
        fun onConfigChanged(config: TmdbConfiguration)
    }


    constructor(context: Context) : super(context) {
        initializeViews(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initializeViews(context)
    }

    constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initializeViews(context)
    }

    constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int, @StyleRes defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        initializeViews(context)
    }

    private fun initializeViews(context: Context) {
        val inflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.movie_config_view, this)

        uiHandler = Handler()
    }

    // TODO this can be a property
    fun setListener(ccListener: ConfigChangeListener) {
        listener = ccListener
    }

    fun bind(config: TmdbConfiguration) {
        currentState  = config
        notifyEnabled = false

        includeAdult = config.includeAdult
        toggle_adult.isChecked = !includeAdult

        release_date_range.setRangePinsByValue(config.startYear.toFloat(), config.endYear.toFloat())

        resetGenreSwitches()
        selectedGenres.clear()
        config.genres.forEach({ g -> initGenres(mapGenreToUi(g), true)})

        rating_selector.setSelection(config.rating.ordinal)

        notifyEnabled = true
    }

    private fun resetGenreSwitches() {
        for (i in 0..2) {
            genres_1.setUncheckedTogglePosition(i)
        }
    }

    private fun initGenres(position: Int, checked: Boolean) {
        val genre = mapUiToGenre(position)
        val ui = uiMaping[position]!!.ui
        val id = uiMaping[position]!!.id

        if (checked) {
            selectedGenres.add(genre)
            ui.setCheckedTogglePosition(id)
        } else {
            selectedGenres.remove(genre)
            ui.setUncheckedTogglePosition(id)
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        val adapter = ArrayAdapter.createFromResource(context,
                R.array.ratings_array,
                android.R.layout.simple_spinner_dropdown_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        rating_selector.adapter = adapter
        rating_selector.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, id: Long) {
                toggleRating(when (pos) {
                    0 -> ESBRating.ANY
                    1 -> ESBRating.NR
                    2 -> ESBRating.G
                    3 -> ESBRating.PG
                    4 -> ESBRating.PG_13
                    5 -> ESBRating.R
                    6 -> ESBRating.NC_17
                    else -> throw IllegalArgumentException("$pos not a valid rating index")})
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        toggle_adult.setOnCheckedChangeListener({ _, isChecked -> toggleAdult(isChecked) })

        release_date_range.setOnRangeBarChangeListener {
            _, _, _, leftPinValue, rightPinValue -> updateReleaseDateRange(leftPinValue, rightPinValue)
        }

        genres_1.setOnToggleSwitchChangeListener { p, checked -> toggleGenre(checked, mapUiToGenre(p)) }
        genres_2.setOnToggleSwitchChangeListener { p, checked -> toggleGenre(checked, mapUiToGenre(3 + p)) }
        genres_3.setOnToggleSwitchChangeListener { p, checked -> toggleGenre(checked, mapUiToGenre(5 + p)) }
        genres_4.setOnToggleSwitchChangeListener { p, checked -> toggleGenre(checked, mapUiToGenre(7 + p)) }
        genres_5.setOnToggleSwitchChangeListener { p, checked -> toggleGenre(checked, mapUiToGenre(10 + p)) }
        genres_6.setOnToggleSwitchChangeListener { p, checked -> toggleGenre(checked, mapUiToGenre(13 + p)) }
        genres_7.setOnToggleSwitchChangeListener { p, checked -> toggleGenre(checked, mapUiToGenre(15 + p)) }
        genres_8.setOnToggleSwitchChangeListener { p, checked -> toggleGenre(checked, mapUiToGenre(17 + p)) }

        // row 1
        uiMaping[0] = ToggleSwitch(genres_1, 0)
        uiMaping[1] = ToggleSwitch(genres_1, 1)
        uiMaping[2] = ToggleSwitch(genres_1, 2)

        // row 2
        uiMaping[3] = ToggleSwitch(genres_2, 0)
        uiMaping[4] = ToggleSwitch(genres_2, 1)

        // row 3
        uiMaping[5] = ToggleSwitch(genres_3, 0)
        uiMaping[6] = ToggleSwitch(genres_3, 1)

        // row 4
        uiMaping[7] = ToggleSwitch(genres_4, 0)
        uiMaping[8] = ToggleSwitch(genres_4, 1)
        uiMaping[9] = ToggleSwitch(genres_4, 2)

        // row 5
        uiMaping[10] = ToggleSwitch(genres_5, 0)
        uiMaping[11] = ToggleSwitch(genres_5, 1)
        uiMaping[12] = ToggleSwitch(genres_5, 2)

        // row 6
        uiMaping[13] = ToggleSwitch(genres_6, 0)
        uiMaping[14] = ToggleSwitch(genres_6, 1)

        // row 7
        uiMaping[15] = ToggleSwitch(genres_7, 0)
        uiMaping[16] = ToggleSwitch(genres_7, 1)

        // row 8
        uiMaping[17] = ToggleSwitch(genres_8, 0)
        uiMaping[18] = ToggleSwitch(genres_8, 1)

    }

    private fun updateReleaseDateRange(start: String, end: String) {
        startYear = start.toInt()
        endYear = end.toInt()

        text_start_year.text = startYear.toString()
        text_end_year.text = endYear.toString()

        delayedNotification?.let {  uiHandler.removeCallbacks(it) }
        delayedNotification = object : Runnable {
            override fun run() = notifyListener()
        }
        uiHandler.postDelayed(delayedNotification, 750)
    }

    private fun toggleRating(selection: ESBRating) {
        rating = selection
        notifyListener()
    }

    private fun toggleGenre(add: Boolean, g: Genres) {
        if (add) selectedGenres.add(g)
        else selectedGenres.remove(g)
        notifyListener()
    }

    private fun toggleAdult(checked: Boolean) {
        includeAdult = !checked
        notifyListener()
    }

    private fun notifyListener() {
        val config = TmdbConfiguration(
                startYear, endYear,
                rating,
                HashSet<Genres>(selectedGenres),
                includeAdult)

        if (notifyEnabled && config != currentState) {
            currentState = config
            listener?.onConfigChanged(currentState)
        }
    }

    private fun mapGenreToUi(genre: Genres) = genre.ordinal

    private fun mapUiToGenre(position: Int) = when (position) {
        0 ->  Genres.ACTION
        1 ->  Genres.ADVENTURE
        2 ->  Genres.ANIMATION
        3 ->  Genres.COMEDY
        4 ->  Genres.CRIME
        5 ->  Genres.DOCUMENTARY
        6 ->  Genres.DRAMA
        7 ->  Genres.FAMILY
        8 ->  Genres.FANTASY
        9 ->  Genres.HISTORY
        10 ->  Genres.HORROR
        11 ->  Genres.MUSIC
        12 ->  Genres.MYSTERY
        13 ->  Genres.ROMANCE
        14 ->  Genres.SIFY
        15 ->  Genres.TVMOVIE
        16 ->  Genres.THRILLER
        17 ->  Genres.WAR
        18 ->  Genres.WESTERN
        else -> throw IllegalArgumentException("invalid genre key: " + position)
    }
}
