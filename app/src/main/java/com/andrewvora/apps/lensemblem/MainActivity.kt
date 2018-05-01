package com.andrewvora.apps.lensemblem

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.andrewvora.apps.lensemblem.boundspicker.BoundsPickerActivity
import com.andrewvora.apps.lensemblem.dagger.component
import com.andrewvora.apps.lensemblem.heroeslist.HeroesListActivity
import com.andrewvora.apps.lensemblem.main.MainViewModel
import com.andrewvora.apps.lensemblem.notifications.NotificationsActivity
import com.andrewvora.apps.lensemblem.permissions.PermissionListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import javax.inject.Inject

/**
 * Created on 4/2/2018.
 * @author Andrew Vorakrajangthiti
 */
class MainActivity : AppCompatActivity(), PermissionListener {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var mainViewModel: MainViewModel
    private var notificationMenuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        application.component().inject(this)
        mainViewModel = ViewModelProviders.of(this, viewModelFactory)[MainViewModel::class.java]

        initViews()
        initObservers()
        loadHeroesIfNecessary()
        mainViewModel.loadNotifications()
    }

    private fun initViews() {
        applyViewState(mainViewModel.currentState)

        setSupportActionBar(toolbar)
        title = null

        start_service_button.setOnClickListener {
            application.startService(LensEmblemService.start(baseContext))
            mainViewModel.currentState = MainViewModel.State.SERVICE_STARTED
        }
        bound_picker_button.setOnClickListener {
            startActivity(BoundsPickerActivity.start(baseContext))
        }
    }

    private fun initObservers() {
        mainViewModel.getShowProgress().observe(this, Observer {
            if (it == true) {
                progress_indicator.progress_indicator_message.text = getString(R.string.loading_heroes)
                progress_indicator.animate().alpha(1.0f).setDuration(200).start()
            } else {
                progress_indicator.animate().alpha(0f).setDuration(100).start()
            }
        })

        mainViewModel.getHeroesLoaded().observe(this, Observer {
            if (mainViewModel.currentState == MainViewModel.State.PERMISSION_GRANTED) {
                start_service_button.isEnabled = true
            }
        })

        mainViewModel.getError().observe(this, Observer {
            if (BuildConfig.DEBUG) {
                Toast.makeText(baseContext, it.toString(), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(baseContext, getString(R.string.network_error), Toast.LENGTH_SHORT).show()
            }
        })

        mainViewModel.getNotifications().observe(this, Observer {
            notificationMenuItem?.setIcon(R.drawable.ic_notifications_active_24dp)
        })

        mainViewModel.getState().observe(this, Observer {
            if (it != null) {
                applyViewState(it)
            }
        })
    }

    private fun applyViewState(state: MainViewModel.State) {
        when(state) {
            MainViewModel.State.DEFAULT -> {
                start_service_button.isEnabled = false
                bound_picker_button.isEnabled = false
            }
            MainViewModel.State.PERMISSION_GRANTED -> {
                if (mainViewModel.heroesLoaded()) {
                    start_service_button.isEnabled = true
                }
            }
            MainViewModel.State.SERVICE_STARTED -> {
                start_service_button.isEnabled = true
                bound_picker_button.isEnabled = true
            }
            MainViewModel.State.HEROES_LOADED -> {}
        }
    }

    private fun loadHeroesIfNecessary() {
        if (mainViewModel.heroesLoaded().not()) {
            mainViewModel.loadHeroes()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        notificationMenuItem = menu?.findItem(R.id.menu_notifications)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_acknowledgements -> {

            }
            R.id.menu_notifications -> {
                notificationMenuItem?.setIcon(R.drawable.ic_notifications_24dp)
                startActivity(NotificationsActivity.start(this))
            }
            R.id.menu_sync_data -> {
                mainViewModel.syncHeroData()
            }
            R.id.menu_view_heroes -> {
                startActivity(HeroesListActivity.start(this))
            }
        }

        return true
    }

    override fun onScreenCapturePermissionGranted() {
        mainViewModel.currentState = MainViewModel.State.PERMISSION_GRANTED
    }

    override fun onScreenCapturePermissionDenied() {
        mainViewModel.currentState = MainViewModel.State.DEFAULT
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) {
            stopService(LensEmblemService.start(application))
        }
    }
}