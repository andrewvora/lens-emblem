package com.andrewvora.apps.lensemblem.statprocessing

import com.andrewvora.apps.lensemblem.models.Stats
import javax.inject.Inject

/**
 * Created on 3/13/2018.
 * @author Andrew Vorakrajangthiti
 */
class IVProcessor
@Inject constructor() {
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

    /**
     * Since we can't process the rarity easily,
     * we run based on "adequate" match, as in,
     * most of the stats match, where the mismatches
     * are likely the IVs
     */
    fun statsAdequatelyMatch(sourceStats: Stats, stats: Stats): Boolean {
        val hpMatched = sourceStats.hp == stats.hp
        val atkMatched = sourceStats.atk == stats.atk
        val spdMatched = sourceStats.spd == stats.spd
        val defMatched = sourceStats.def == stats.def
        val resMatched = sourceStats.res == stats.res

        val mismatches = arrayOf(hpMatched, atkMatched, spdMatched, defMatched, resMatched)
                .count { !it }

        if (mismatches == 0 || mismatches == 2) {
            val ivs = calculateIVs(sourceStats, stats)

            val baneBoonMatch = ivs.first == ivs.second
            val doesNotViolateNoneConstraint = ivs.first != ivs.second &&
                    ivs.first != Stat.NONE &&
                    ivs.second != Stat.NONE
            return baneBoonMatch || doesNotViolateNoneConstraint
        } else {
            return false
        }
    }
}