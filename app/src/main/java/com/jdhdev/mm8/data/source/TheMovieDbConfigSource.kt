package com.jdhdev.mm8.data.source

import com.jdhdev.mm8.data.TmdbConfiguration
import com.jdhdev.mm8.data.TmdbConfiguration.Companion.ESBRating
import com.jdhdev.mm8.data.TmdbConfiguration.Companion.Genres
import com.jdhdev.mm8.data.source.local.database.AppDatabase
import com.jdhdev.mm8.data.source.local.database.TheMovieDbDao
import com.jdhdev.mm8.data.source.local.database.entity.TheMovieDbConfig
import io.reactivex.Completable
import javax.inject.Inject


class TheMovieDbConfigSource @Inject constructor(db: AppDatabase) {

    private val dao: TheMovieDbDao = db.getTMDbConfigDao()


    // TODO -- make this a property
    fun getConfig() = dao.getConfig().map { configEntityToPojo(it) }
    fun setConfig(config: TmdbConfiguration) =
            Completable.fromCallable { dao.updateConfig(configPojoToEntity(config)) }


    private fun configEntityToPojo(entity: TheMovieDbConfig) =
            TmdbConfiguration(
                    startYear = entity.startYear,
                    endYear = entity.endYear,
                    includeAdult = entity.includeAdult,
                    rating = ESBRating.valueOf(entity.rating),
                    genres = entity.genres
                            .split(",")
                            .map { it.trim() }
                            .filter { it.isNotEmpty() }
                            .map { Genres.valueOf(it) }
                            .toSet())


    private fun configPojoToEntity(pojo: TmdbConfiguration) =
            TheMovieDbConfig(
                    startYear = pojo.startYear,
                    endYear = pojo.endYear,
                    rating = pojo.rating.name,
                    genres = pojo.genres.joinToString(separator = ","),
                    includeAdult = pojo.includeAdult)
}