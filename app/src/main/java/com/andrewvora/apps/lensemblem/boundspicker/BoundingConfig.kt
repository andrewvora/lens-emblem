package com.andrewvora.apps.lensemblem.boundspicker

import com.andrewvora.apps.lensemblem.models.Bounds
import com.andrewvora.apps.lensemblem.models.BoundsType


/**
 * Defines the possible [Bounds] needed for breaking up a
 * @link Bitmap
 */
sealed class BoundingConfig {

    abstract fun characterTitle(): Bounds
    abstract fun characterName(): Bounds
    abstract fun characterLevel(): Bounds
    abstract fun characterStats(): Bounds

    object Nexus5 : BoundingConfig() {
        override fun characterTitle(): Bounds {
            return Bounds(xMod = 0.05, yMod = 0.43, widthMod = 0.43, heightMod = 0.05)
        }

        override fun characterName(): Bounds {
            return Bounds(xMod = 0.1, yMod = 0.5, widthMod = 0.43, heightMod = 0.04)
        }

        override fun characterLevel(): Bounds {
            return Bounds(xMod = 0.1, yMod = 0.57, widthMod = 0.3, heightMod = 0.05)
        }

        override fun characterStats(): Bounds {
            return Bounds(xMod = 0.1, yMod = 0.62, widthMod = 0.35, heightMod = 0.275)
        }
    }

    class CustomBoundingConfig(private val boundsMap: Map<BoundsType, Bounds>) : BoundingConfig() {
        override fun characterTitle(): Bounds {
            val bounds = boundsMap[BoundsType.PROFILE_TITLE] ?: Nexus5.characterTitle()
            return Bounds(
                    xMod = bounds.xMod,
                    yMod = bounds.yMod,
                    widthMod = bounds.widthMod,
                    heightMod = bounds.heightMod)
        }

        override fun characterName(): Bounds {
            val bounds = boundsMap[BoundsType.PROFILE_NAME] ?: Nexus5.characterName()
            return Bounds(
                    xMod = bounds.xMod,
                    yMod = bounds.yMod,
                    widthMod = bounds.widthMod,
                    heightMod = bounds.heightMod)
        }

        override fun characterLevel(): Bounds {
            val bounds = boundsMap[BoundsType.PROFILE_LEVEL] ?: Nexus5.characterLevel()
            return Bounds(
                    xMod = bounds.xMod,
                    yMod = bounds.yMod,
                    widthMod = bounds.widthMod,
                    heightMod = bounds.heightMod)
        }

        override fun characterStats(): Bounds {
            val bounds = boundsMap[BoundsType.PROFILE_STATS] ?: Nexus5.characterStats()
            return Bounds(
                    xMod = bounds.xMod,
                    yMod = bounds.yMod,
                    widthMod = bounds.widthMod,
                    heightMod = bounds.heightMod)
        }
    }
}