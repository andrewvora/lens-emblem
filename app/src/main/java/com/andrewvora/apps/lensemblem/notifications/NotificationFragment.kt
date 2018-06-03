package com.andrewvora.apps.lensemblem.notifications

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.andrewvora.apps.lensemblem.R
import com.andrewvora.apps.lensemblem.dagger.component
import kotlinx.android.synthetic.main.fragment_notifications.*
import kotlinx.android.synthetic.main.toolbar.*
import javax.inject.Inject

/**
 * Created on 4/2/2018.
 * @author Andrew Vorakrajangthiti
 */
class NotificationFragment: Fragment() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var notificationViewModel: NotificationsViewModel
    private lateinit var notificationAdapter: NotificationsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        activity?.application?.component()?.inject(this)
        notificationViewModel = ViewModelProviders.of(this,
                viewModelFactory)[NotificationsViewModel::class.java]
    }

    private fun initViews() {
        val parent = activity
        if (parent is AppCompatActivity) {
            parent.setSupportActionBar(toolbar)
            parent.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            parent.supportActionBar?.setTitle(R.string.notifications)
        }

        notificationAdapter = NotificationsAdapter(emptyList())
        notifications_recycler_view.adapter = notificationAdapter
        notifications_recycler_view.layoutManager = LinearLayoutManager(activity)

        notification_swipe_refresh.setOnRefreshListener {
            notification_swipe_refresh.isRefreshing = true
            notificationViewModel.refreshNotifications()
        }
    }

    private fun initObservers() {
        notificationViewModel.getNotifications().observe(this, Observer {
            empty_state.visibility = View.GONE
            notifications_recycler_view.animate().alpha(1.0f).setDuration(100).start()
            notification_swipe_refresh.isRefreshing = false
            notificationAdapter.updateNotifications(it ?: emptyList())
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_notifications, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews()
        initObservers()

        notificationViewModel.loadNotifications()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> activity?.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}