package com.andrewvora.apps.lensemblem.boundspicker

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.andrewvora.apps.lensemblem.R
import com.andrewvora.apps.lensemblem.dagger.component
import com.andrewvora.apps.lensemblem.models.Bounds
import com.andrewvora.apps.lensemblem.models.BoundsType
import kotlinx.android.synthetic.main.fragment_bounds_picker.*
import javax.inject.Inject

/**
 * Created on 4/2/2018.
 * @author Andrew Vorakrajangthiti
 */
class BoundsPickerFragment : Fragment() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var boundsPickerViewModel: BoundsPickerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.application?.component()?.inject(this)
        boundsPickerViewModel = ViewModelProviders.of(
                this, viewModelFactory)[BoundsPickerViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bounds_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // set up views and listeners
        initViews()

        // set up observers
        initObservers()

        // kick off streams
        startSelectingBounds()
    }

    private fun initViews() {
        back_button.setOnClickListener {
            val prev = boundsPickerViewModel.getPreviousStep()
            if (prev == BoundsType.UNSPECIFIED) {
                activity?.onBackPressed()
            } else {
                boundsPickerViewModel.goToStep(prev)
            }
        }

        save_button.setOnClickListener {
            boundsPickerViewModel.getBoundsMap().value?.let { boundsMap ->
                val currentStep = boundsPickerViewModel.currentStep
                val updatedBounds = calculateBounds()
                boundsMap.getOrPut(currentStep, {
                    Bounds(
                            xMod = updatedBounds.xMod,
                            yMod = updatedBounds.yMod,
                            widthMod = updatedBounds.widthMod,
                            heightMod = updatedBounds.heightMod,
                            type = currentStep)
                }).let {
                    boundsMap[currentStep] = it
                }
            }

            val next = boundsPickerViewModel.getNextStep()
            if (next == BoundsType.UNSPECIFIED) {
                boundsPickerViewModel.saveBounds()
            } else {
                boundsPickerViewModel.goToStep(next)
            }
        }
    }

    private fun initObservers() {
        boundsPickerViewModel.getError().observe(this, Observer {
            // TODO: figure out better error handling
            Toast.makeText(activity, it.toString(), Toast.LENGTH_SHORT).show()
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
                // TODO: show better finishing state
                activity?.onBackPressed()
            }
        })
    }

    private fun startSelectingBounds() {
        boundsPickerViewModel.loadExistingScreenshot()
    }

    private fun displayLatestScreenshot(bitmap: Bitmap) {
        bounds_picker_view.visibility = View.VISIBLE
        bounds_picker_view.setImageBitmap(bitmap)
        save_button.isEnabled = true
    }

    private fun displayInstructions() {
        bounds_picker_view.visibility = View.GONE
        instructions_view.visibility = View.VISIBLE
        save_button.isEnabled = false
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
        // TODO these calculations are not 1:1 to the actual bounds
        val width = resources.displayMetrics.widthPixels
        val height = resources.displayMetrics.heightPixels

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
}