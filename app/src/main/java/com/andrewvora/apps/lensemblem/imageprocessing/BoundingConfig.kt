package com.andrewvora.apps.lensemblem.imageprocessing

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
            return Bounds(0, 0.05, 0.43, 0.43, 0.05)
        }

        override fun characterName(): Bounds {
            return Bounds(0, 0.1, 0.5, 0.43, 0.04)
        }

        override fun characterLevel(): Bounds {
            return Bounds(0, 0.1, 0.57, 0.3, 0.05)
        }

        override fun characterStats(): Bounds {
            return Bounds(0, 0.1, 0.62, 0.35, 0.275)
        }
    }
}