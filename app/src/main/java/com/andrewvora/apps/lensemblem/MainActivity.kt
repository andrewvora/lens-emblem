package com.andrewvora.apps.lensemblem

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.andrewvora.apps.lensemblem.boundspicker.BoundsPickerActivity
import com.andrewvora.apps.lensemblem.permissions.PermissionListener
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created on 4/2/2018.
 * @author Andrew Vorakrajangthiti
 */
class MainActivity : AppCompatActivity(), PermissionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        title = null

        start_service_button.setOnClickListener {
            application.startService(LensEmblemService.start(baseContext))
            bound_picker_button.isEnabled = true
        }
        bound_picker_button.setOnClickListener {
            startActivity(BoundsPickerActivity.start(baseContext))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_acknowledgements -> {

            }
            R.id.menu_notifications -> {

            }
            R.id.menu_sync_data -> {

            }
        }

        return true
    }

    override fun onScreenCapturePermissionGranted() {
        start_service_button.isEnabled = true
    }

    override fun onScreenCapturePermissionDenied() {
        start_service_button.isEnabled = false
        bound_picker_button.isEnabled = false
    }
}