package com.andrewvora.apps.lensemblem.runners

import android.accessibilityservice.AccessibilityService
import android.app.Application
import android.graphics.BitmapFactory
import android.os.Environment
import android.support.test.InstrumentationRegistry
import android.support.test.rule.ActivityTestRule
import android.support.test.rule.GrantPermissionRule
import android.support.test.runner.AndroidJUnit4
import com.andrewvora.apps.lensemblem.imageprocessing.BitmapHelper
import com.andrewvora.apps.lensemblem.imageprocessing.BoundingConfig
import com.andrewvora.apps.lensemblem.ocr.OCRHelper
import com.andrewvora.apps.lensemblem.permissions.PermissionsActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.PrintWriter

/**
 * Only runs when explicitly targeted.
 *
 * Created on 3/14/2018.
 * @author Andrew Vorakrajangthiti
 */
@RunWith(AndroidJUnit4::class)
@Ignore
class AliasJobRunner {
    private lateinit var ocrHelper: OCRHelper
    private lateinit var bitmapHelper: BitmapHelper
    private val gson = Gson()

    @Rule @JvmField
    val activityRule = ActivityTestRule<PermissionsActivity>(PermissionsActivity::class.java)
    @Rule @JvmField
    val permissionsRule: GrantPermissionRule = GrantPermissionRule.grant(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

    @Before
    fun setUp() {
        ocrHelper = OCRHelper(activityRule.activity.application as Application)
        bitmapHelper = BitmapHelper().apply {
            setBoundingConfig(BoundingConfig.Espresso)
        }
    }

    @Test
    fun getAliases() {
        // process aliases
        val nameAliases = mutableMapOf<String, Set<String>>()
        val titleAliases = mutableMapOf<String, Set<String>>()

        FILE_NAMES.forEach { filename ->
            val fileDir = "${getDownloadDirectory().absolutePath}/$filename"
            val bitmap = BitmapFactory.decodeFile(fileDir)

            val name = filename.toLowerCase().replace(
                    Regex("\\.(jpeg|jpg|png)"),
                    "")
            val characterTitle = name
                    .replace("_", " ")
                    .split(Regex("-"))
                    .first()
            val characterName = name
                    .replace("_", " ")
                    .split(Regex("-"))
                    .last()

            val titleBitmap = bitmapHelper.makeHighContrast(bitmapHelper.getCharacterTitleBitmap(bitmap))
            val nameBitmap = bitmapHelper.makeHighContrast(bitmapHelper.getCharacterNameBitmap(bitmap))

            // run multiple passes to capture variations
            for (i in 0 until 2) {
                val titleAlias = ocrHelper.readAllText(titleBitmap)
                val nameAlias = ocrHelper.readAllText(nameBitmap)

                val titleSet = titleAliases.getOrPut(characterTitle, { mutableSetOf() })
                titleAliases[characterTitle] = titleSet.plus(titleAlias)

                val nameSet = nameAliases.getOrPut(characterName, { mutableSetOf() })
                nameAliases[characterName] = nameSet.plus(nameAlias)
            }
        }

        // export maps
        writeToJsonFile(nameAliases, NAMES_EXPORT_FILE)
        writeToJsonFile(titleAliases, TITLES_EXPORT_FILE)

        // close the permission prompt
        InstrumentationRegistry.getInstrumentation().uiAutomation
                .performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
    }

    private fun getDownloadDirectory(): File {
        return File("${Environment.getExternalStorageDirectory()}/$DOWNLOAD_DIR")
    }

    private fun writeToJsonFile(data: Map<String, Set<String>>, filename: String): Boolean {
        val outputPath = "${Environment.getExternalStorageDirectory()}/$DOWNLOAD_DIR/$CONTENT_DIR"
        val outputDir = File(outputPath)
        val directoryExists = if (outputDir.exists().not()) {
            outputDir.mkdirs()
        } else {
            true
        }

        val outputFile = File("$outputPath/$filename")
        return if (directoryExists) {
            val jsonMap = gson.toJson(data, object: TypeToken<Map<String, Set<String>>>(){}.type)
            PrintWriter(outputFile).use { out -> out.println(jsonMap) }
            true
        } else {
            throw RuntimeException("Could not create directory!")
        }
    }
}

private val FILE_NAMES = arrayOf(
        "empty_vessel-takumi.jpg"
)
private const val DOWNLOAD_DIR = "Download"
private const val CONTENT_DIR = "LensEmblem"
private const val NAMES_EXPORT_FILE = "hero_name_aliases.json"
private const val TITLES_EXPORT_FILE = "hero_title_aliases.json"