package com.andrewvora.apps.lensemblem.imageprocessing


/**
 * Bounding box parameters. Used to create region for bitmap.
 */
data class Bounds(val xMod: Double, val yMod: Double, val widthMod: Double, val heightMod: Double)

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
            return Bounds(0.05, 0.43, 0.43, 0.05)
        }

        override fun characterName(): Bounds {
            return Bounds(0.1, 0.5, 0.43, 0.04)
        }

        override fun characterLevel(): Bounds {
            return Bounds(0.1, 0.57, 0.3, 0.05)
        }

        override fun characterStats(): Bounds {
            return Bounds(0.1, 0.62, 0.35, 0.275)
        }
    }

    object Espresso: BoundingConfig() {
        override fun characterTitle(): Bounds {
            return Bounds(0.05, 0.43, 0.425, 0.05)
        }

        override fun characterName(): Bounds {
            return Bounds(0.1, 0.5, 0.4, 0.04)
        }

        override fun characterLevel(): Bounds {
            throw NotImplementedError("Espresso script does not parse levels")
        }

        override fun characterStats(): Bounds {
            throw NotImplementedError("Espresso script does not parse stats")
        }
    }
}