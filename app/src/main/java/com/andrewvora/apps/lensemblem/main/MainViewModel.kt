package com.andrewvora.apps.lensemblem.main

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.andrewvora.apps.lensemblem.data.SystemConfig
import com.andrewvora.apps.lensemblem.data.TimestampFormatter
import com.andrewvora.apps.lensemblem.models.AppMessage
import com.andrewvora.apps.lensemblem.preferences.LensEmblemPreferences
import com.andrewvora.apps.lensemblem.repos.HeroesRepo
import com.andrewvora.apps.lensemblem.repos.NotificationsRepo
import com.andrewvora.apps.lensemblem.rxjava.useStandardObserveSubscribe
import io.reactivex.disposables.CompositeDisposable
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created on 4/19/2018.
 * @author Andrew Vorakrajangthiti
 */
class MainViewModel
@Inject
constructor(private val heroesRepo: HeroesRepo,
            private val notificationsRepo: NotificationsRepo,
            private val lensEmblemPrefs: LensEmblemPreferences,
            private val timestampFormatter: TimestampFormatter,
            private val systemConfig: SystemConfig) : ViewModel() {

    var currentState: State = State.DEFAULT
        set(value) {
            field = value
            state.value = value
        }

    private val state = MutableLiveData<State>()
    private val heroesLoaded = MutableLiveData<Boolean>()
    private val showProgress = MutableLiveData<Boolean>()
    private val notifications = MutableLiveData<List<AppMessage>>()
    private val error = MutableLiveData<Throwable>()
    private val disposables = CompositeDisposable()
    private val lastHeroSyncTimestamp = MutableLiveData<String>()
    private var isLoadingHeroes = false

    fun getHeroesLoaded(): LiveData<Boolean> {
        return heroesLoaded
    }

    fun getNotifications(): LiveData<List<AppMessage>> {
        return notifications
    }

    fun getError(): LiveData<Throwable> {
        return error
    }

    fun getState(): LiveData<State> {
        return state
    }

    fun getShowProgress(): LiveData<Boolean> {
        return showProgress
    }

    fun getLastHeroSyncTimestamp(): LiveData<String> {
        return lastHeroSyncTimestamp
    }

    fun heroesLoaded(): Boolean {
        return lensEmblemPrefs.hasLoadedDefaultData()
    }

    fun loadHeroes() {
        if (isLoadingHeroes.not()) {
            isLoadingHeroes = true
            showProgress.value = true

            disposables.add(heroesRepo.loadDefaultData()
                    .useStandardObserveSubscribe()
                    .doOnError {
                        error.value = it
                        isLoadingHeroes = false
                        showProgress.value = false
                    }
                    .doOnComplete {
                        lensEmblemPrefs.setLoadedDefaultData(true)
                        showProgress.value = false
                        heroesLoaded.value = true
                    }
                    .subscribe())
        }
    }

    fun loadHeroSyncTimestamp() {
        val lastSyncMillis = lensEmblemPrefs.lastHeroSync()
        if (lastSyncMillis > 0) {
            updateLastHeroSyncTimestamp(Date(lastSyncMillis))
        }
    }

    fun syncHeroData() {
        if (isLoadingHeroes.not()) {
            isLoadingHeroes = true
            showProgress.value = true

            disposables.add(heroesRepo.fetchHeroes()
                    .useStandardObserveSubscribe()
                    .doOnError {
                        error.value = it
                        isLoadingHeroes = false
                        showProgress.value = false
                    }
                    .doOnComplete {
                        isLoadingHeroes = false
                        showProgress.value = false
                        heroesLoaded.value = true

                        updateLastHeroSyncTimestamp()
                    }
                    .subscribe())
        }
    }

    private fun updateLastHeroSyncTimestamp(timestamp: Date = Calendar.getInstance().time) {
        val use12H = systemConfig.use24HourTime().not()
        lastHeroSyncTimestamp.value = timestampFormatter.format(timestamp, use12H)

        lensEmblemPrefs.setLastHeroSync(timestamp.time)
    }

    fun loadNotifications() {
        if (shouldLoadNotificationsFromNetwork()) {
            val networkNotificationStream = notificationsRepo.fetchNotifications()
            val latestNotificationStream = notificationsRepo.getLatestNotification()
            disposables.add(networkNotificationStream
                    .map { messages ->
                        val latestNotification = latestNotificationStream.blockingGet()
                        val hasNewNotifications =
                                messages.isNotEmpty() &&
                                messages.first().posted?.after(latestNotification.posted) ?: false

                        if (hasNewNotifications) {
                            messages
                        } else {
                            emptyList()
                        }
                    }
                    .useStandardObserveSubscribe()
                    .doOnSuccess {
                        if (it.isNotEmpty()) {
                            notifications.value = it
                        }
                    }
                    .subscribe())
        }
    }

    private fun shouldLoadNotificationsFromNetwork(): Boolean {
        val lastCheckedTime = lensEmblemPrefs.lastCheckedNotificationTime()
        return System.currentTimeMillis() - lastCheckedTime > NOTIFICATION_STALENESS_THRESHOLD
    }

    override fun onCleared() {
        if (disposables.isDisposed.not()) {
            disposables.clear()
        }
    }

    enum class State {
        DEFAULT, HEROES_LOADED, PERMISSION_GRANTED, SERVICE_STARTED
    }

    companion object {
        private val NOTIFICATION_STALENESS_THRESHOLD = TimeUnit.MINUTES.toMillis(5L)
    }
}