package com.andrewvora.apps.lensemblem.boundspicker

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.andrewvora.apps.lensemblem.R
import com.andrewvora.apps.lensemblem.dagger.component
import com.andrewvora.apps.lensemblem.models.Bounds
import com.andrewvora.apps.lensemblem.models.BoundsType
import kotlinx.android.synthetic.main.activity_bounds_picker.*
import javax.inject.Inject
import kotlin.math.min


/**
 * Created on 4/6/2018.
 * @author Andrew Vorakrajangthiti
 */
class BoundsPickerActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var boundsPickerViewModel: BoundsPickerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        window.decorView.systemUiVisibility = getFullscreenFlags()
        setContentView(R.layout.activity_bounds_picker)

        application?.component()?.inject(this)

        boundsPickerViewModel = ViewModelProviders.of(this,
                viewModelFactory)[BoundsPickerViewModel::class.java]

        // set up views and listeners
        initViews()

        // set up observers
        initObservers()

        // kick off streams
        startSelectingBounds()
    }

    private fun initViews() {
        val screenSize = getScreenSize()
        val maxAllowableHeight = screenSize.widthPixels / 10.0 * 18
        val currentHeight = screenSize.heightPixels
        bounds_picker_view.layoutParams.height = min(maxAllowableHeight.toInt(), currentHeight)

        back_button.setOnClickListener {
            val prev = boundsPickerViewModel.getPreviousStep()
            if (prev == BoundsType.UNSPECIFIED) {
                onBackPressed()
            } else {
                boundsPickerViewModel.goToStep(prev)
            }
        }

        save_button.setOnClickListener {
            boundsPickerViewModel.getBoundsMap().value?.let { boundsMap ->
                val currentStep = boundsPickerViewModel.currentStep
                val updatedBounds = calculateBounds()
                boundsMap.put(currentStep,
                        Bounds(
                                xMod = updatedBounds.xMod,
                                yMod = updatedBounds.yMod,
                                widthMod = updatedBounds.widthMod,
                                heightMod = updatedBounds.heightMod,
                                type = currentStep))
            }

            val next = boundsPickerViewModel.getNextStep()
            if (next == BoundsType.UNSPECIFIED) {
                boundsPickerViewModel.saveBounds()
            } else {
                boundsPickerViewModel.goToStep(next)
            }
        }
    }

    private fun getScreenSize(): DisplayMetrics {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getRealMetrics(displayMetrics)
        return displayMetrics
    }

    private fun initObservers() {
        boundsPickerViewModel.getError().observe(this, Observer {
            Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
        })

        boundsPickerViewModel.getBoundsMap().observe(this, Observer { boundsMap ->
            if (boundsMap != null) {
                boundsMap[boundsPickerViewModel.currentStep]?.let {
                    applyBounds(it)
                }
            }
        })

        boundsPickerViewModel.getScreenshot().observe(this, Observer { bitmap ->
            if (bitmap != null) {
                displayLatestScreenshot(bitmap)
                boundsPickerViewModel.getBounds()
            } else {
                displayInstructions()
            }
        })

        boundsPickerViewModel.getStatus().observe(this, Observer {
            if (it == BoundsPickerViewModel.Status.FINISHED) {
                onBackPressed()
            }
        })
    }

    private fun getFullscreenFlags(): Int {
        return (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        // Set the content to appear under the system bars so that the
        // content doesn't resize when the system bars hide and show.
        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        // Hide the nav bar and status bar
        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    private fun startSelectingBounds() {
        boundsPickerViewModel.loadExistingScreenshot()
    }

    private fun displayLatestScreenshot(bitmap: Bitmap) {
        bounds_picker_view.visibility = View.VISIBLE
        bounds_picker_view.setImageBitmap(bitmap)
        save_button.isEnabled = true
        save_button.show()
    }

    private fun displayInstructions() {
        bounds_picker_view.visibility = View.GONE
        instructions_view.visibility = View.VISIBLE
        save_button.isEnabled = false
        save_button.hide()
    }

    private fun applyBounds(bounds: Bounds) {
        val left = bounds.xMod * bounds_picker_view.width
        val top = bounds.yMod * bounds_picker_view.height
        val right = left + bounds.widthMod * bounds_picker_view.width
        val bottom = top + bounds.heightMod * bounds_picker_view.height

        bounds_picker_view.setCoordinates(
                left = left.toFloat(),
                top = top.toFloat(),
                right = right.toFloat(),
                bottom = bottom.toFloat()
        )
    }

    private fun calculateBounds(): Bounds {
        val width = bounds_picker_view.width
        val height = bounds_picker_view.height

        val left = bounds_picker_view.getLeftEdge().toDouble()
        val top = bounds_picker_view.getTopEdge().toDouble()
        val right = bounds_picker_view.getRightEdge().toDouble()
        val bottom = bounds_picker_view.getBottomEdge().toDouble()

        return Bounds(
                xMod = left / width,
                yMod = top / height,
                widthMod = (right - left) / width,
                heightMod = (bottom - top) / height
        )
    }

    companion object {
        fun start(context: Context): Intent {
            return Intent(context, BoundsPickerActivity::class.java)
        }
    }
}