package com.andrewvora.apps.lensemblem.repos.local

import com.andrewvora.apps.lensemblem.models.Hero
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.nio.file.Paths

/**
 * Tests the "hero_stats_v1" JSON.
 *
 * Created on 7/7/2018.
 * @author Andrew Vorakrajangthiti
 */
class HeroesV1Json {

    private val heroJson = File(Paths
            .get("src", "main", "res", "raw", "hero_stats_v1.json")
            .toAbsolutePath()
            .toUri())
            .readText()

    @Test
    fun `heroes can be parsed into the model objects`() {
        val heroes = Gson().fromJson<List<Hero>>(heroJson, object: TypeToken<List<Hero>>() {}.type)

        assertNotNull(heroes)
        assertTrue(heroes.isNotEmpty())
    }

    @Test
    fun `heroes have stats`() {
        val heroes = Gson().fromJson<List<Hero>>(heroJson, object: TypeToken<List<Hero>>() {}.type)
        assertTrue(heroes.isNotEmpty())

        // make sure there is a hero with stats
        assertNotNull(heroes.find { it.stats!!.isNotEmpty() })

        // check the rest of the list
        heroes.forEach {  hero ->
            when (hero.name) {
                // use a sampling of heroes to validate stats were added
                "Abel", "Xander", "Celica" -> {
                    assertTrue(hero.stats!!.isNotEmpty())
                    assertNotNull(hero.stats!!.first())
                }
            }
        }
    }
}