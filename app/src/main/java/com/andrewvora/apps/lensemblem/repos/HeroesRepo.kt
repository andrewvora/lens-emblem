package com.andrewvora.apps.lensemblem.repos

import com.andrewvora.apps.lensemblem.models.Hero
import com.andrewvora.apps.lensemblem.models.NameAlias
import com.andrewvora.apps.lensemblem.models.TitleAlias
import com.andrewvora.apps.lensemblem.repos.local.HeroesLocalDataSource
import com.andrewvora.apps.lensemblem.repos.remote.HeroesRemoteDataSource
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created on 3/4/2018.
 * @author Andrew Vorakrajangthiti
 */
@Singleton
class HeroesRepo
@Inject constructor(private val localSource: HeroesLocalDataSource,
                    private val remoteSource: HeroesRemoteDataSource)
{
    fun loadDefaultData(): Completable {
        return Completable.defer {
            loadDefaultHeroes().blockingAwait()
            loadDefaultHeroAliases().blockingAwait()
            return@defer Completable.complete()
        }
    }

    /**
     * Load from the app resources
     */
    private fun loadDefaultHeroes(): Completable {
        return Completable.defer {
            val heroes = localSource.getHeroDataFromResources().blockingGet()
            saveHeroesLocally(heroes)

            return@defer Completable.complete()
        }
    }

    private fun saveHeroesLocally(heroes: List<Hero>) {
        if (heroes.isEmpty()) {
            return
        }

        val heroesMap = heroes.associateBy { it.title + it.name }

        localSource.deleteAllHeroesFromDatabase().blockingAwait()
        localSource.saveHeroesToDatabase(heroes)
        localSource.getHeroDataFromDatabase().blockingGet()
                .forEach { hero ->
                    val stats = heroesMap[hero.title + hero.name]?.stats
                    if (stats != null) {
                        localSource.saveStatsToDatabase(hero, stats)
                    }
                }
    }

    /**
     * Loads from the app resources.
     */
    private fun loadDefaultHeroAliases(): Completable {
        return Completable.defer {
            val aliases = localSource.getHeroAliasesFromResources().blockingGet()
            val nameAliases = aliases.first
            val titleAliases = aliases.second

            saveAliasesLocally(nameAliases, titleAliases)

            return@defer Completable.complete()
        }
    }

    private fun saveAliasesLocally(nameAliases: List<NameAlias>, titleAliases: List<TitleAlias>) {
        localSource.saveNameAliasesToDatabase(nameAliases)
        localSource.saveTitleAliasesToDatabase(titleAliases)
    }

    /**
     * Get the latest hero data from the network.
     */
    fun fetchHeroes(): Completable {
       return Completable.defer {
           val heroes = remoteSource.getHeroData().blockingGet()
           saveHeroesLocally(heroes)

           return@defer Completable.complete()
       }
    }

    /**
     * Gets the most readily available hero data
     * Normally, the cache or database
     */
    fun getHeroes(): Single<List<Hero>> {
        return Single.defer {
            localSource.getHeroDataFromDatabase()
        }
    }

    fun getHeroes(query: String): Single<List<Hero>> {
        return Single.defer {
            val heroes = localSource.getHeroesFromDatabase(query)
            return@defer Single.just(heroes)
        }
    }

    fun getHero(id: Long): Single<Hero> {
        return Single.defer {
            localSource.getHeroDataFromDatabase(id)
        }
    }

    /**
     * Gets the most readily available hero name alias data
     * Normally, the cache or database
     */
    fun getNameAliases(): Single<List<NameAlias>> {
        return Single.defer {
            localSource.getHeroNameAliasesFromDatabase()
        }
    }

    /**
     * Gets the most readily available hero title alias data
     * Normally, the cache or database
     */
    fun getTitleAliases(): Single<List<TitleAlias>> {
        return Single.defer {
            localSource.getHeroTitleAliasesFromDatabase()
        }
    }

    /**
     * Gets a hero matching the given parameters.
     * @param title
     * @param name
     */
    fun getHero(title: String, name: String): Single<Hero> {
        return Single.defer {
            localSource.getHeroFromDatabase(title, name)
        }
    }

    fun deleteAllHeroes(): Completable {
        return Completable.defer {
            val deleteHeroesCompletable = localSource.deleteAllHeroAliasesFromDatabase()
            val deleteAliasesCompletable = localSource.deleteAllHeroesFromDatabase()
            return@defer deleteHeroesCompletable.andThen(deleteAliasesCompletable)
        }
    }
}