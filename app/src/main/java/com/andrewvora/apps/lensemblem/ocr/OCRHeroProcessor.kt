package com.andrewvora.apps.lensemblem.ocr

import android.graphics.Bitmap
import com.andrewvora.apps.lensemblem.imageprocessing.BitmapHelper
import com.andrewvora.apps.lensemblem.boundspicker.BoundingConfig
import com.andrewvora.apps.lensemblem.models.Hero
import com.andrewvora.apps.lensemblem.models.Stats
import com.andrewvora.apps.lensemblem.repos.HeroesRepo
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created on 3/11/2018.
 * @author Andrew Vorakrajangthiti
 */
@Singleton
class OCRHeroProcessor
@Inject constructor(private val ocrHelper: OCRHelper,
                    private val bitmapHelper: BitmapHelper,
                    private val heroRepo: HeroesRepo) {

    init {
        bitmapHelper.setBoundingConfig(BoundingConfig.Nexus5)
    }

    fun processHeroProfile(profileBitmap: Bitmap): Hero {
        val titleBitmap = bitmapHelper.getCharacterTitleBitmap(profileBitmap)
        val nameBitmap = bitmapHelper.getCharacterNameBitmap(profileBitmap)
        val levelBitmap = bitmapHelper.getCharacterLevelBitmap(profileBitmap)
        val statsBitmap = bitmapHelper.getCharacterStatsBitmap(profileBitmap)

        val heroTitle = processHeroTitle(bitmapHelper.makeHighContrast(titleBitmap))
        val heroName = processHeroName(bitmapHelper.makeHighContrast(nameBitmap))
        val heroLevel = processHeroLevel(bitmapHelper.makeHighContrast(levelBitmap))
        val heroStats = processHeroStats(bitmapHelper.makeHighContrast(statsBitmap)).copy(level = heroLevel)

        return Hero(title = heroTitle, name = heroName, stats = listOf(heroStats))
    }

    fun processHeroTitle(titleBitmap: Bitmap): String {
        val titleText = ocrHelper.readAllText(titleBitmap).toLowerCase()
        val titleAliases = heroRepo.getTitleAliases().blockingGet()
        return titleAliases.find {alias ->
            titleText.contains(alias.capturedText.toLowerCase())
        }?.heroTitle ?: titleText
    }

    fun processHeroName(nameBitmap: Bitmap): String {
        val nameText = ocrHelper.readAllText(nameBitmap).toLowerCase()
        val nameAliases = heroRepo.getNameAliases().blockingGet()
        return nameAliases.find { alias ->
            nameText.contains(alias.capturedText.toLowerCase())
        }?.heroName ?: nameText
    }

    fun processHeroLevel(levelBitmap: Bitmap): Int {
        val levelText = ocrHelper.readAllText(levelBitmap).toLowerCase()
        val trimmedLevelText = Regex("(lv|iv)\\.*\\s*\\d{1,2}").find(levelText)?.value ?: ""
        return parseNumber(trimmedLevelText)
    }

    fun processHeroStats(statsBitmap: Bitmap): Stats {
        val statsText = ocrHelper.readAllText(statsBitmap).toLowerCase()
        val hpText = Regex("hp.*\\d{1,2}").find(statsText)?.value ?: ""
        val atkText = Regex("atk.*\\d{1,2}").find(statsText)?.value ?: ""
        val spdText = Regex("spd.*\\d{1,2}").find(statsText)?.value ?: ""
        val defText = Regex("def.*\\d{1,2}").find(statsText)?.value ?: ""
        val resText = Regex("res.*\\d{1,2}").find(statsText)?.value ?: ""

        val hp = parseNumber(hpText)
        val atk = parseNumber(atkText)
        val spd = parseNumber(spdText)
        val def = parseNumber(defText)
        val res = parseNumber(resText)

        return Stats(hp = hp, atk = atk, spd = spd, def = def, res = res)
    }

    private fun parseNumber(str: String): Int {
        return try {
            Regex("\\D+").replace(str, "").toInt()
        } catch (e: Exception) {
            e.printStackTrace()
            DEFAULT_LEVEL
        }
    }

    companion object {
        private const val DEFAULT_LEVEL = 1
    }
}