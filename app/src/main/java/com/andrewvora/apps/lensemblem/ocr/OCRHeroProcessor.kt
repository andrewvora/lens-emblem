package com.andrewvora.apps.lensemblem.ocr

import android.graphics.Bitmap
import com.andrewvora.apps.lensemblem.imageprocessing.BitmapHelper
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


    fun processHeroProfile(profileBitmap: Bitmap): Hero {
        val titleBitmap = bitmapHelper.getCharacterTitleBitmap(profileBitmap)
        val nameBitmap = bitmapHelper.getCharacterNameBitmap(profileBitmap)
        val levelBitmap = bitmapHelper.getCharacterLevelBitmap(profileBitmap)
        val statsBitmap = bitmapHelper.getCharacterStatsBitmap(profileBitmap)

        val heroTitle = processHeroTitle(titleBitmap)
        val heroName = processHeroName(nameBitmap)
        val heroLevel = processHeroLevel(levelBitmap)
        val heroStats = processHeroStats(statsBitmap).copy(level = heroLevel)

        return Hero(title = heroTitle, name = heroName, stats = listOf(heroStats))
    }

    fun processHeroTitle(titleBitmap: Bitmap): String {
        val titleText = ocrHelper.readAllText(titleBitmap)
        val titleAliases = heroRepo.getTitleAliases().blockingGet()
        return titleAliases.find {alias ->
            titleText.contains(alias.capturedText)
        }?.heroTitle ?: DEFAULT_TITLE
    }

    fun processHeroName(nameBitmap: Bitmap): String {
        val nameText = ocrHelper.readAllText(nameBitmap)
        val nameAliases = heroRepo.getNameAliases().blockingGet()
        return nameAliases.find { alias ->
            nameText.contains(alias.capturedText)
        }?.heroName ?: DEFAULT_NAME
    }

    fun processHeroLevel(levelBitmap: Bitmap): Int {
        val levelText = ocrHelper.readAllText(levelBitmap)
        val trimmedLevelText = Regex("(LV.*\\d{2})").find(levelText)?.value ?: ""
        return parseNumber(trimmedLevelText)
    }

    fun processHeroStats(statsBitmap: Bitmap): Stats {
        val statsText = ocrHelper.readAllText(statsBitmap)
        val hpText = Regex("HP.*\\d{1,2}").find(statsText)?.value ?: ""
        val atkText = Regex("Atk.*\\d{1,2}").find(statsText)?.value ?: ""
        val spdText = Regex("Spd.*\\d{1,2}").find(statsText)?.value ?: ""
        val defText = Regex("Def.*\\d{1,2}").find(statsText)?.value ?: ""
        val resText = Regex("Res.*\\d{1,2}").find(statsText)?.value ?: ""

        val hp = parseNumber(hpText)
        val atk = parseNumber(atkText)
        val spd = parseNumber(spdText)
        val def = parseNumber(defText)
        val res = parseNumber(resText)

        return Stats(hp = hp, atk = atk, spd = spd, def = def, res = res)
    }

    private fun parseNumber(str: String): Int {
        return Regex("\\D+").replace(str, "").toInt()
    }

    companion object {
        private const val DEFAULT_NAME = "unknown"
        private const val DEFAULT_TITLE = "unknown"
    }
}