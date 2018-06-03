package com.andrewvora.apps.lensemblem.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.Toast
import androidx.navigation.Navigation.findNavController
import com.andrewvora.apps.lensemblem.BuildConfig
import com.andrewvora.apps.lensemblem.LensEmblemService
import com.andrewvora.apps.lensemblem.R
import com.andrewvora.apps.lensemblem.dagger.component
import com.andrewvora.apps.lensemblem.permissions.PermissionListener
import com.andrewvora.apps.lensemblem.settings.LensEmblemSettings
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*
import javax.inject.Inject
import android.content.Intent
import android.content.Intent.getIntent



/**
 * Created on 5/13/2018.
 * @author Andrew Vorakrajangthiti
 */
class MainFragment : Fragment(), PermissionListener {
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject lateinit var lensEmblemSettings: LensEmblemSettings

    private lateinit var mainViewModel: MainViewModel
    private var notificationMenuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        activity?.application?.component()?.inject(this)
        mainViewModel = ViewModelProviders.of(this, viewModelFactory)[MainViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews()
        initObservers()
        loadHeroesIfNecessary()
        mainViewModel.loadNotifications()
        mainViewModel.loadHeroSyncTimestamp()
    }

    private fun initViews() {
        applyViewState(mainViewModel.currentState)

        (activity as? AppCompatActivity)?.apply {
            setSupportActionBar(toolbar)
            title = null
        }

        start_service_button.setOnClickListener {
            activity?.let {
                it.startService(LensEmblemService.start(it.applicationContext))
                mainViewModel.currentState = MainViewModel.State.SERVICE_STARTED
            }
        }
        bound_picker_button.setOnClickListener { v ->
            activity?.let {
                findNavController(v).navigate(R.id.open_bounds_picker)
            }
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

        mainViewModel.getError().observe(this, Observer { throwable ->
            activity?.let { context ->
                if (BuildConfig.DEBUG) {
                    Toast.makeText(context, throwable.toString(), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, getString(R.string.network_error), Toast.LENGTH_SHORT).show()
                }
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

        mainViewModel.getLastHeroSyncTimestamp().observe(this, Observer {
            last_hero_sync_timestamp.text = getString(R.string.heroes_last_updated, it)
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

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_main, menu)
        notificationMenuItem = menu?.findItem(R.id.menu_notifications)
        menu?.findItem(R.id.menu_use_light_theme)?.isChecked = lensEmblemSettings.useDarkTheme()
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_acknowledgements -> {

            }
            R.id.menu_notifications -> {
                activity?.let {
                    // use normal icon after click to show "read" status
                    notificationMenuItem?.setIcon(R.drawable.ic_notifications_24dp)

                    findNavController(toolbar).navigate(R.id.open_notifications)
                }
            }
            R.id.menu_sync_data -> {
                mainViewModel.syncHeroData()
            }
            R.id.menu_view_heroes -> {
                activity?.let {
                    findNavController(toolbar).navigate(R.id.open_heroes_list)
                }
            }
            R.id.menu_use_light_theme -> {
                item.isChecked = item.isChecked.not()
                lensEmblemSettings.setDarkThemePreference(item.isChecked)

                val intent = Intent(activity, MainActivity::class.java)
                startActivity(intent)
            }
        }

        return false
    }

    override fun onScreenCapturePermissionGranted() {
        mainViewModel.currentState = MainViewModel.State.PERMISSION_GRANTED
    }

    override fun onScreenCapturePermissionDenied() {
        mainViewModel.currentState = MainViewModel.State.DEFAULT
    }
}