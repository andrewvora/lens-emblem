package com.andrewvora.apps.lensemblem.boundspicker

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.andrewvora.apps.lensemblem.R
import com.andrewvora.apps.lensemblem.capturehistory.LatestScreenshot
import com.andrewvora.apps.lensemblem.dagger.component
import kotlinx.android.synthetic.main.fragment_bounds_picker.*
import javax.inject.Inject

/**
 * Created on 4/2/2018.
 * @author Andrew Vorakrajangthiti
 */
class BoundsPickerFragment : Fragment() {

    @Inject lateinit var latestScreenshot: LatestScreenshot

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.application?.component()?.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bounds_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bounds_picker_view.setImageBitmap(latestScreenshot.lastScreenshot)
    }
}