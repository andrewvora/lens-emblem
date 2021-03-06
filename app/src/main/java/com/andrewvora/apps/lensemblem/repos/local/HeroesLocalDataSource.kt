package com.andrewvora.apps.lensemblem.repos.local

import android.app.Application
import com.andrewvora.apps.lensemblem.R
import com.andrewvora.apps.lensemblem.data.StringCleaner
import com.andrewvora.apps.lensemblem.database.LensEmblemDatabase
import com.andrewvora.apps.lensemblem.models.Hero
import com.andrewvora.apps.lensemblem.models.NameAlias
import com.andrewvora.apps.lensemblem.models.Stats
import com.andrewvora.apps.lensemblem.models.TitleAlias
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created on 3/4/2018.
 * @author Andrew Vorakrajangthiti
 */
@Singleton
open class HeroesLocalDataSource
@Inject
constructor(private val app: Application,
            private val database: LensEmblemDatabase,
            private val gson: Gson,
            private val stringCleaner: StringCleaner) {

    fun saveHeroesToDatabase(heroes: List<Hero>) {
        database.heroDao().insert(*heroes.toTypedArray())
    }

    fun saveStatsToDatabase(hero: Hero, stats: List<Stats>) {
        // associate each stats object with the hero
        // toList to dereference original list
        stats.toList().forEach { heroStats ->
            hero.id.let {
                heroStats.heroId = it
            }
        }

        // ignore failed results
        try {
            database.statsDao().insert(*stats.toTypedArray())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun saveNameAliasesToDatabase(nameAliases: List<NameAlias>) {
        database.aliasDao().insert(*nameAliases.toTypedArray())
    }

    fun saveTitleAliasesToDatabase(titleAliases: List<TitleAlias>) {
        database.aliasDao().insert(*titleAliases.toTypedArray())
    }

    fun getHeroFromDatabase(title: String, name: String): Single<Hero?> {
        val cleanedTitle = stringCleaner.clean(title)
        val cleanedName = stringCleaner.clean(name)
        val flexibleTitle = "%$cleanedTitle%"
        val flexibleName = "%$cleanedName%"
        database.heroDao().getHeroes(flexibleTitle, flexibleName).firstOrNull()?.let { hero ->
            val stats = database.statsDao().getStats(hero.id)
            hero.stats = stats

            return Single.just(hero)
        }

        val heroFromFirstLastMatch = getHeroFromDatabaseWithFirstLastStrategy(cleanedTitle, cleanedName)
        if (heroFromFirstLastMatch != null) {
            return Single.just(heroFromFirstLastMatch)
        }

        val heroFromMiddleTokenMatch = getHeroFromDatabaseWithMiddleTokenStrategy(cleanedTitle, cleanedName)
        if (heroFromMiddleTokenMatch != null) {
            return Single.just(heroFromMiddleTokenMatch)
        }

        val heroFromHalfMatch = getHeroFromDatabaseWithHalvesStrategy(cleanedTitle, cleanedName)
        if (heroFromHalfMatch != null) {
            return Single.just(heroFromHalfMatch)
        }

        return Single.error(RuntimeException("Could not find $title - $name"))
    }

    fun getHeroFromDatabaseWithFirstLastStrategy(title: String, name: String): Hero? {
        val firstLastTitle = "%${title.first()}%${title.last()}%"
        val firstLastName = "%${name.first()}%${name.last()}%"

        val result1 = database.heroDao().getHeroes(title, firstLastName)
        val result2 = database.heroDao().getHeroes(firstLastTitle, name)
        val result3 = database.heroDao().getHeroes(firstLastTitle, firstLastName)

        // return the first hero in a list with least number of matches
        // excludes empty lists
        return listOf(result1, result2, result3)
                .filter { it.isNotEmpty() }
                .minBy { it.size }
                ?.firstOrNull()
                ?.apply {
                    stats = database.statsDao().getStats(id)
                }
    }

    fun getHeroFromDatabaseWithMiddleTokenStrategy(title: String, name: String): Hero? {
        val nameTokens = name.split(Regex("\\s+"))
        val titleTokens = title.split(Regex("\\s+"))

        val middleTitleToken = titleTokens.getOrNull(titleTokens.size / 2)
        val middleNameToken = nameTokens.getOrNull(nameTokens.size / 2)
        return database.heroDao()
                .getHeroes(
                        "%${middleTitleToken ?: title}%",
                        "%${middleNameToken ?: name}%"
                )
                .firstOrNull()
                ?.apply {
                    stats = database.statsDao().getStats(id)
                }
    }

    fun getHeroFromDatabaseWithHalvesStrategy(title: String, name: String): Hero? {
        val firstHalfTitle = "%${title.substring(0, title.length / 2)}%"
        val lastHalfTitle = "%${title.substring(title.length / 2)}%"
        val firstHalfName = "%${name.substring(0, name.length / 2)}%"
        val lastHalfName = "%${name.substring(name.length / 2)}%"

        val firstResult = database.heroDao().getHeroes(firstHalfTitle, firstHalfName)
        val secondResult = database.heroDao().getHeroes(lastHalfTitle, lastHalfName)

        return listOf(firstResult, secondResult)
                .filter { it.isNotEmpty() }
                .minBy { it.size }
                ?.firstOrNull()
                ?.apply {
                    stats = database.statsDao().getStats(this.id)
                }
    }

    fun getHeroesFromDatabase(query: String): List<Hero> {
        return database.heroDao().getHeroes("%${query.toLowerCase()}%")
    }

    fun getHeroDataFromDatabase(): Single<List<Hero>> {
        return Single.just(database.heroDao().getHeroes())
    }

    fun getHeroDataFromDatabase(id: Long): Single<Hero?> {
        val hero = database.heroDao().getHero(id)?.apply {
            stats = database.statsDao().getStats(this.id)
        }

        return if (hero == null) {
            Single.error(Exception("No hero found for id=$id"))
        } else {
            Single.just(hero)
        }
    }

    fun getHeroTitleAliasesFromDatabase(): Single<List<TitleAlias>> {
        return Single.just(database.aliasDao().getTitleAliases())
    }

    fun getHeroNameAliasesFromDatabase(): Single<List<NameAlias>> {
        return Single.just(database.aliasDao().getNameAliases())
    }

    fun getHeroDataFromResources(): Single<List<Hero>> {
        val inputStream = app.resources.openRawResource(R.raw.hero_stats_v1)
        val json = inputStream.bufferedReader().use { it.readText() }
        val heroes = gson.fromJson<List<Hero>>(json, object: TypeToken<List<Hero>>() {}.type)

        return Single.just(heroes)
    }

    fun getHeroAliasesFromResources(): Single<Pair<List<NameAlias>, List<TitleAlias>>> {
        // get names aliases
        val namesInputStream = app.resources.openRawResource(R.raw.hero_name_aliases_v1)
        val nameJson = namesInputStream.bufferedReader().use { it.readText() }
        val nameAliasesMap = gson.fromJson<Map<String, List<String>>>(
                nameJson,
                object: TypeToken<Map<String, List<String>>>(){}.type
        )

        val nameAliases = nameAliasesMap.flatMap { entry ->
            entry.value.map { NameAlias(id = 0, heroName = entry.key, capturedText = it) }
        }

        // get title aliases
        val titlesInputStream = app.resources.openRawResource(R.raw.hero_title_aliases_v1)
        val titlesJson = titlesInputStream.bufferedReader().use { it.readText() }
        val titlesAliasesMap = gson.fromJson<Map<String, List<String>>>(
                titlesJson,
                object: TypeToken<Map<String, List<String>>>(){}.type
        )
        val titleAliases = titlesAliasesMap.flatMap { entry ->
            entry.value.map { TitleAlias(id = 0, heroTitle = entry.key, capturedText = it) }
        }

        return Single.just(Pair(nameAliases, titleAliases))
    }

    fun deleteAllHeroesFromDatabase(): Completable {
        database.heroDao().deleteAll()

        return Completable.complete()
    }

    fun deleteAllHeroAliasesFromDatabase(): Completable {
        database.aliasDao().deleteAllTitles()
        database.aliasDao().deleteAllNames()

        return Completable.complete()
    }
}