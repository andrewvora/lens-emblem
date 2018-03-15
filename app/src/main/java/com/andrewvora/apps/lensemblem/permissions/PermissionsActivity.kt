package com.andrewvora.apps.lensemblem.permissions

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.andrewvora.apps.lensemblem.LensEmblemService
import com.andrewvora.apps.lensemblem.R
import com.andrewvora.apps.lensemblem.dagger.component
import com.andrewvora.apps.lensemblem.imageprocessing.ScreenshotHelper
import com.andrewvora.apps.lensemblem.imageprocessing.ScreenshotHelper.Companion.REQUEST_CODE_SCREENSHOT_PERMISSION
import com.andrewvora.apps.lensemblem.repos.HeroesRepo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


class PermissionsActivity : AppCompatActivity() {

    @Inject lateinit var screenshotHelper: ScreenshotHelper
    @Inject lateinit var heroesRepo: HeroesRepo

    private val disposables: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        application.component().inject(this)

        disposables.add(heroesRepo.deleteAllHeroes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError {
                    val msg = "Couldn't clear the database: $it"
                    Toast.makeText(application, msg, Toast.LENGTH_SHORT).show()
                }
                .subscribe {
                    val msg = "Loading hero data"
                    Toast.makeText(application, msg, Toast.LENGTH_SHORT).show()
                    loadLocalFiles()
                })

        getScreenCapturePermission()
    }

    private fun loadLocalFiles() {
        disposables.add(heroesRepo.loadDefaultData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError {
                    val msg = "Failed to load default data."
                    Toast.makeText(application, msg, Toast.LENGTH_SHORT).show()
                    progress_indicator.visibility = View.GONE
                }
                .subscribe {
                    val msg = "Default data loaded!"
                    Toast.makeText(application, msg, Toast.LENGTH_SHORT).show()
                    progress_indicator.visibility = View.GONE
                })
    }

    override fun onDestroy() {
        super.onDestroy()
        if (disposables.isDisposed.not()) {
            disposables.dispose()
        }
        stopService(Intent(this, LensEmblemService::class.java))
    }

    private fun getScreenCapturePermission() {
        val permissionIntent = screenshotHelper.getPermissionIntent()
        startActivityForResult(permissionIntent, REQUEST_CODE_SCREENSHOT_PERMISSION)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SCREENSHOT_PERMISSION && resultCode == Activity.RESULT_OK) {
            startService(Intent(application, LensEmblemService::class.java))
            screenshotHelper.setPermissionResult(resultCode, data)
        }
    }
}
