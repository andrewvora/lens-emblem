package com.andrewvora.apps.lensemblem.notifications

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.andrewvora.apps.lensemblem.models.AppMessage
import com.andrewvora.apps.lensemblem.repos.NotificationsRepo
import com.andrewvora.apps.lensemblem.rxjava.useStandardObserveSubscribe
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Created on 4/22/2018.
 * @author Andrew Vorakrajangthiti
 */
class NotificationsViewModel
@Inject constructor(private val notificationsRepo: NotificationsRepo) : ViewModel() {

    private val notifications = MutableLiveData<List<AppMessage>>()
    private val disposables = CompositeDisposable()

    fun getNotifications(): LiveData<List<AppMessage>> {
        return notifications
    }

    fun refreshNotifications() {
        disposables.add(notificationsRepo.fetchNotifications()
                .useStandardObserveSubscribe()
                .doOnError {
                    TODO("Implement error handling")
                }
                .doOnSuccess {
                    notifications.value = it
                }
                .subscribe())
    }

    fun loadNotifications() {
        disposables.add(notificationsRepo.getNotifications()
                .useStandardObserveSubscribe()
                .doOnError {
                    TODO("Implement error handling")
                }
                .doOnSuccess {
                    notifications.value = it
                }
                .subscribe())
    }

    override fun onCleared() {
        if (disposables.isDisposed.not()) {
            disposables.dispose()
        }
    }
}