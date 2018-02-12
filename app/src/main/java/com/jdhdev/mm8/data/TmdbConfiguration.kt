package com.jdhdev.mm8.data


data class TmdbConfiguration(
        val startYear: Int = 1850,
        val endYear: Int = 2020,
        val rating: ESBRating = ESBRating.ANY,
        var genres: Set<Genres> = emptySet<Genres>(),
        var includeAdult: Boolean = false) {

    val genreValues = genres
            .map { it.value }
            .joinToString()

    companion object {
        enum class ESBRating(val stringValue: String) {
            ANY(""),
            NR("NR"),
            G("G"),
            PG("PG"),
            PG_13("PG-13"),
            R("R"),
            NC_17("NC-17")
        }

        enum class Genres(val value: Int) {
            ACTION(28),
            ADVENTURE(12),
            ANIMATION(16),
            COMEDY(35),
            CRIME(80),
            DOCUMENTARY(99),
            DRAMA(18),
            FAMILY(10751),
            FANTASY(14),
            HISTORY(36),
            HORROR(27),
            MUSIC(10402),
            MYSTERY(9648),
            ROMANCE(10749),
            SIFY(878),
            TVMOVIE(10770),
            THRILLER(53),
            WAR(10752),
            WESTERN(37)
        }
    }
}