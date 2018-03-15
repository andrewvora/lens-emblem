package com.andrewvora.apps.lensemblem.statprocessing

import com.andrewvora.apps.lensemblem.models.Hero
import com.andrewvora.apps.lensemblem.models.Stats
import com.andrewvora.apps.lensemblem.repos.HeroesRepo
import javax.inject.Inject

/**
 * Created on 3/13/2018.
 * @author Andrew Vorakrajangthiti
 */
class IVProcessor
@Inject constructor(private val heroesRepo: HeroesRepo) {
    enum class Stat {
        HP,
        ATK,
        SPD,
        DEF,
        RES,
        NONE
    }
    fun calculateIVs(sourceStats: Stats, stats: Stats): Pair<Stat, Stat> {
        val bane = when {
            stats.hp < sourceStats.hp -> Stat.HP
            stats.atk < sourceStats.atk -> Stat.ATK
            stats.spd < sourceStats.spd -> Stat.SPD
            stats.def < sourceStats.def -> Stat.DEF
            stats.res < sourceStats.res -> Stat.RES
            else -> Stat.NONE
        }
        val boon = when {
            stats.hp > sourceStats.hp -> Stat.HP
            stats.atk > sourceStats.atk -> Stat.ATK
            stats.spd > sourceStats.spd -> Stat.SPD
            stats.def > sourceStats.def -> Stat.DEF
            stats.res > sourceStats.res -> Stat.RES
            else -> Stat.NONE
        }

        return Pair(boon, bane)
    }
}