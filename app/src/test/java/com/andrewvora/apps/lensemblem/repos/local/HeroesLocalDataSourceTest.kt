package com.andrewvora.apps.lensemblem.repos.local

import android.app.Application
import android.content.res.Resources
import com.andrewvora.apps.lensemblem.R
import com.andrewvora.apps.lensemblem.data.StringCleaner
import com.andrewvora.apps.lensemblem.database.AliasDao
import com.andrewvora.apps.lensemblem.database.HeroDao
import com.andrewvora.apps.lensemblem.database.LensEmblemDatabase
import com.andrewvora.apps.lensemblem.database.StatsDao
import com.andrewvora.apps.lensemblem.models.Hero
import com.andrewvora.apps.lensemblem.models.Stats
import com.google.gson.Gson
import com.nhaarman.mockito_kotlin.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayInputStream
import kotlin.math.exp

/**
 * [HeroesLocalDataSource]
 * Created on 5/28/2018.
 * @author Andrew Vorakrajangthiti
 */
class HeroesLocalDataSourceTest {

    private lateinit var localDataSource: HeroesLocalDataSource

    private val app: Application = mock()
    private val resources: Resources = mock()
    private val database: LensEmblemDatabase = mock()
    private val stringCleaner: StringCleaner = mock()
    private val aliasDao: AliasDao = mock()
    private val heroDao: HeroDao = mock()
    private val statsDao: StatsDao = mock()
    private val gson = Gson()

    @Before
    fun beforeEveryTest() {
        whenever(database.aliasDao()).thenReturn(aliasDao)
        whenever(database.heroDao()).thenReturn(heroDao)
        whenever(database.statsDao()).thenReturn(statsDao)

        localDataSource = HeroesLocalDataSource(
                app = app,
                database = database,
                gson = gson,
                stringCleaner = stringCleaner)
    }

    @Test
    fun `save a list of heroes to the database`() {
        // given
        val heroes = listOf(Hero())

        // when
        localDataSource.saveHeroesToDatabase(heroes)

        // then
        verify(database).heroDao()
        verify(heroDao).insert(*heroes.toTypedArray())
    }

    @Test
    fun `saving stats to the database`() {
        // given
        val hero = Hero(id = 1)
        val stat1 = Stats(hp = 0, atk= 0, def = 0, spd = 0, res = 0)
        val stat2 = Stats(hp = 0, atk= 0, def = 0, spd = 0, res = 0)
        val stats = listOf(stat1, stat2)

        // when
        localDataSource.saveStatsToDatabase(hero, stats)

        // then
        assertEquals(hero.id, stats[0].heroId)
        assertEquals(hero.id, stats[1].heroId)
        verify(statsDao).insert(any())
    }

    @Test
    fun `getting a hero from the database`() {
        // given
        val title = "Obsessive Bride"
        val name = "Tharja"
        val hero = Hero(id = 1)
        val stats = Stats(hp = 0, atk= 0, def = 0, spd = 0, res = 0, heroId = hero.id)
        whenever(statsDao.getStats(hero.id)).thenReturn(listOf(stats))

        whenever(stringCleaner.clean(title)).thenReturn(title)
        whenever(stringCleaner.clean(name)).thenReturn(name)
        whenever(heroDao.getHeroes("%$title%", "%$name%")).thenReturn(listOf(hero))

        // when
        val result = localDataSource.getHeroFromDatabase(title, name)

        // then
        assertEquals(stats, result.blockingGet()!!.stats!!.first())
        verify(heroDao).getHeroes("%$title%", "%$name%")
        verify(statsDao).getStats(hero.id)
    }

    @Test
    fun `getting a hero from the database failure states`() {
        // given
        val title = "Thicc"
        val name = "Tharja"
        whenever(stringCleaner.clean(title)).thenReturn(title)
        whenever(stringCleaner.clean(name)).thenReturn(name)
        whenever(heroDao.getHeroes("%$title%", "%$name%")).thenReturn(emptyList())
        whenever(heroDao.getHeroes("%T%c%", name)).thenReturn(emptyList())

        // when
        val result = localDataSource.getHeroFromDatabase(title, name)

        // then
        try {
            result.blockingGet()
            assertTrue("An exception should have been thrown", false)
        } catch (e: Exception) {
            assertTrue(true)
        }

        verify(statsDao, never()).getStats(any())
    }

    @Test
    fun `getting a hero from the database using the first last strategy`() {
        // given
        val title = "Chad"
        val name = "Xander"
        val expectedTitle = "%C%d%"
        val expectedName = "%X%r%"
        val hero = Hero(id = 1)
        val hero2 = Hero(id = 2)
        val stats = Stats(hp = 0, atk= 0, def = 0, spd = 0, res = 0, heroId = hero.id)
        whenever(statsDao.getStats(hero.id)).thenReturn(listOf(stats))

        whenever(heroDao.getHeroes(title, expectedName)).thenReturn(emptyList())
        whenever(heroDao.getHeroes(title, expectedName)).thenReturn(listOf(hero2, hero2))
        whenever(heroDao.getHeroes(expectedTitle, expectedName)).thenReturn(listOf(hero))

        // when
        localDataSource.getHeroFromDatabaseWithFirstLastStrategy(title, name)

        // then
        verify(heroDao).getHeroes(expectedTitle, name)
        verify(heroDao).getHeroes(title, expectedName)
        verify(heroDao).getHeroes(expectedTitle, expectedName)

        verify(statsDao).getStats(hero.id)
    }

    @Test
    fun `getting a hero from the database using the middle token strategy`() {
        // given
        val hero = Hero(id = 1)
        val stats = Stats(hp = 0, atk= 0, def = 0, spd = 0, res = 0, heroId = hero.id)
        whenever(statsDao.getStats(hero.id)).thenReturn(listOf(stats))
        whenever(heroDao.getHeroes(any(), any())).thenReturn(listOf(hero))

        // single token case
        var title = "Adorable"
        var name = "Fae"

        localDataSource.getHeroFromDatabaseWithMiddleTokenStrategy(title, name)

        verify(heroDao).getHeroes("%$title%", "%$name%")
        verify(statsDao).getStats(hero.id)
        assertEquals(stats, hero.stats!!.first())

        // two token case
        title = "Real\t Waifu"
        name = "Bridal\n Ninian"

        localDataSource.getHeroFromDatabaseWithMiddleTokenStrategy(title, name)

        verify(heroDao).getHeroes("%Waifu%", "%Ninian%")
        verify(statsDao, times(2)).getStats(hero.id)
        assertEquals(stats, hero.stats!!.first())

        // multiple token case
        title = "Run Away  From"
        name = "Plus Ten\t Reinhardt"

        localDataSource.getHeroFromDatabaseWithMiddleTokenStrategy(title, name)

        verify(heroDao).getHeroes("%Away%", "%Ten%")
        verify(statsDao, times(3)).getStats(hero.id)
        assertEquals(stats, hero.stats!!.first())

        // null case
        whenever(heroDao.getHeroes(any(), any())).thenReturn(emptyList())
        localDataSource.getHeroFromDatabaseWithMiddleTokenStrategy(title, name)

        verify(statsDao, times(3)).getStats(any())
    }

    @Test
    fun `getting all heroes from the database`() {
        // given
        whenever(heroDao.getHeroes()).thenReturn(emptyList())

        // when
        val heroes = localDataSource.getHeroDataFromDatabase()

        // then
        assertEquals(emptyList<Hero>(), heroes.blockingGet())
        verify(database).heroDao()
        verify(heroDao).getHeroes()
    }

    @Test
    fun `getting heroes from the local resources`() {
        // given
        val inputStream = ByteArrayInputStream("[]".toByteArray())
        whenever(app.resources).thenReturn(resources)
        whenever(resources.openRawResource(R.raw.hero_stats_v1)).thenReturn(inputStream)

        // when
        val result = localDataSource.getHeroDataFromResources()

        // then
        assertTrue(result.blockingGet().isEmpty())

        verify(app).resources
        verify(resources).openRawResource(R.raw.hero_stats_v1)
    }

    @Test
    fun `deleting heroes from the database`() {
        // when
        localDataSource.deleteAllHeroesFromDatabase()

        // then
        verify(database).heroDao()
        verify(heroDao).deleteAll()
    }
}