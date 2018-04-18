package com.andrewvora.apps.lensemblem.boundspicker

import com.andrewvora.apps.lensemblem.models.Bounds


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
}