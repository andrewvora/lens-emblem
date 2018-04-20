package com.andrewvora.apps.lensemblem.ocr

import android.app.Application
import android.graphics.Bitmap
import com.googlecode.tesseract.android.TessBaseAPI
import dagger.Reusable
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created on 2/28/2018.
 * @author Andrew Vorakrajangthiti
 */
@Reusable
class OCRHelper
@Inject
constructor(private val app: Application) {

    private val tessBaseApi: TessBaseAPI

    init {
        val engDataFile = File("${getLanguageDataPath()}/tessdata")
        loadLanguageDataIfNecessary(engDataFile)

        tessBaseApi = TessBaseAPI()
        tessBaseApi.init(getLanguageDataPath(), ENG_LANGUAGE)
    }

    fun readAllText(bitmap: Bitmap): String {
        tessBaseApi.setImage(bitmap)
        return tessBaseApi.utF8Text
    }

    private fun loadLanguageDataIfNecessary(fileDir: File) {
        val directoryJustCreated = fileDir.exists().not() && fileDir.mkdirs()
        if (directoryJustCreated || fileDir.exists()) {
            loadLanguageData(engLanguageDataTargetFile())
        }
    }

    private fun loadLanguageData(targetFile: File) {
        val assetManager = app.assets
        val inputStream = assetManager.open("tessdata/eng.traineddata")
        val outputStream = FileOutputStream(targetFile.absoluteFile)

        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
    }

    private fun getLanguageDataPath(): String {
        return "${app.filesDir.absolutePath}/tesseract"
    }

    private fun engLanguageDataTargetFile(): File {
        val dataDir = getLanguageDataPath()
        val targetFilePath = "$dataDir/tessdata/eng.traineddata"

        return File(targetFilePath)
    }

    companion object {
        private const val ENG_LANGUAGE = "eng"
    }
}