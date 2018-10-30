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
import com.andrewvora.apps.lensemblem.acknowledgements.AcknowledgementsDialog
import com.andrewvora.apps.lensemblem.dagger.component
import com.andrewvora.apps.lensemblem.imageprocessing.ScreenshotHelper
import com.andrewvora.apps.lensemblem.permissions.PermissionListener
import com.andrewvora.apps.lensemblem.permissions.PermissionsFragment
import com.andrewvora.apps.lensemblem.preferences.LensEmblemPreferences
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*
import javax.inject.Inject


/**
 * Created on 5/13/2018.
 * @author Andrew Vorakrajangthiti
 */
class MainFragment : Fragment(), PermissionListener {
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject lateinit var lensEmblemPreferences: LensEmblemPreferences
    @Inject lateinit var screenshotHelper: ScreenshotHelper

    private lateinit var mainViewModel: MainViewModel

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
        mainViewModel.restoreViewState()
        mainViewModel.loadHeroSyncTimestamp()
    }

    private fun initViews() {
        applyViewState(mainViewModel.currentState)

        (activity as? AppCompatActivity)?.apply {
            setSupportActionBar(toolbar)
            title = null
        }

        start_service_button.setOnClickListener {
            if (screenshotHelper.hasPermission()) {
                startLensEmblemService()
            } else {
                getPermissionsFragment()?.getScreenCapturePermission()
            }
        }

        bound_picker_button.setOnClickListener { v ->
            activity?.let {
                findNavController(v).navigate(R.id.open_bounds_picker)
            }
        }

        start_tutorial_button.setOnClickListener { v ->
            activity?.let {
                findNavController(v).navigate(R.id.open_tutorial)
            }
        }
    }

    private fun getPermissionsFragment(): PermissionsFragment? {
        val permissionFragment = childFragmentManager.findFragmentByTag(PermissionsFragment.TAG)
        return if (permissionFragment == null) {
            val newInstance = PermissionsFragment()
            childFragmentManager
                    .beginTransaction()
                    .add(newInstance, PermissionsFragment.TAG)
                    .commitNow()

            newInstance
        } else {
            permissionFragment as? PermissionsFragment
        }
    }

    private fun startLensEmblemService() {
        activity?.let {
            it.startService(LensEmblemService.start(it.applicationContext))
            mainViewModel.currentState = MainViewModel.State.SERVICE_STARTED
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
            start_service_button.isEnabled = true
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
                start_service_button.isEnabled = mainViewModel.heroesLoaded()
                bound_picker_button.isEnabled = false
            }
            MainViewModel.State.SERVICE_STARTED -> {
                start_service_button.isEnabled = true
                bound_picker_button.isEnabled = true
            }
            MainViewModel.State.HEROES_LOADED -> {}
        }
    }

    private fun loadHeroesIfNecessary() {
        mainViewModel.loadHeroes()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_main, menu)
        menu?.findItem(R.id.menu_use_light_theme)?.isChecked = lensEmblemPreferences.useDarkTheme()
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_acknowledgements -> {
                activity?.fragmentManager?.let {
                    AcknowledgementsDialog().show(it, "acknowledgements")
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
                lensEmblemPreferences.setDarkThemePreference(item.isChecked)

                activity?.recreate()
            }
        }

        return false
    }

    override fun onScreenCapturePermissionGranted() {
        // automatically run service if
        // - app has permission intent
        val canTurnOnService = screenshotHelper.hasPermission()

        if (canTurnOnService) {
            startLensEmblemService()
        }
    }

    override fun onScreenCapturePermissionDenied() {
        mainViewModel.currentState = MainViewModel.State.DEFAULT
    }
}