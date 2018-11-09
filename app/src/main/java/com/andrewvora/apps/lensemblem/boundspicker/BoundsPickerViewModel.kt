package com.andrewvora.apps.lensemblem.boundspicker

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.graphics.Bitmap
import com.andrewvora.apps.lensemblem.capturehistory.LatestScreenshot
import com.andrewvora.apps.lensemblem.models.Bounds
import com.andrewvora.apps.lensemblem.models.BoundsType
import com.andrewvora.apps.lensemblem.repos.BoundsRepo
import com.andrewvora.apps.lensemblem.rxjava.useStandardObserveSubscribe
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Created on 4/13/2018.
 * @author Andrew Vorakrajangthiti
 */
class BoundsPickerViewModel
@Inject
constructor(private val latestScreenshot: LatestScreenshot,
            private val boundsRepo: BoundsRepo) : ViewModel() {

    private val boundsMap: MutableLiveData<MutableMap<BoundsType, Bounds>> = MutableLiveData()
    private val screenshot: MutableLiveData<Bitmap> = MutableLiveData()
    private val error: MutableLiveData<Throwable> = MutableLiveData()
    private val status: MutableLiveData<Status> = MutableLiveData()
    var currentStep: BoundsType = BoundsType.PROFILE_TITLE
        set(value) {
            field = value
            boundsMap.value = boundsMap.value
        }

    private val steps = arrayOf(
            BoundsType.PROFILE_TITLE,
            BoundsType.PROFILE_NAME,
            BoundsType.PROFILE_LEVEL,
            BoundsType.PROFILE_STATS)

    private val disposables = CompositeDisposable()

    fun getBoundsMap(): LiveData<MutableMap<BoundsType, Bounds>> {
        return boundsMap
    }

    fun getError(): LiveData<Throwable> {
        return error
    }

    fun getScreenshot(): LiveData<Bitmap> {
        return screenshot
    }

    fun getStatus(): LiveData<Status> {
        return status
    }

    fun loadExistingScreenshot() {
        screenshot.value = latestScreenshot.lastScreenshot
    }

    fun getBounds() {
        disposables.add(boundsRepo
                .getBounds()
                .useStandardObserveSubscribe()
                .subscribe({ map ->
                    boundsMap.value = if (map.isEmpty()) {
                        getDefaultBounds()
                    } else {
                        map.toMutableMap()
                    }
                }, {
                    error.value = it
                }))
    }

    private fun getDefaultBounds(): MutableMap<BoundsType, Bounds> {
        val boundingConfig = boundsRepo.getDefaultBounds()
        return mutableMapOf(
                BoundsType.PROFILE_TITLE to boundingConfig.characterTitle(),
                BoundsType.PROFILE_NAME to boundingConfig.characterName(),
                BoundsType.PROFILE_LEVEL to boundingConfig.characterLevel(),
                BoundsType.PROFILE_STATS to boundingConfig.characterStats()
        )
    }

    override fun onCleared() {
        if (disposables.isDisposed.not()) {
            disposables.dispose()
        }
    }

    fun saveBounds() {
        boundsMap.value?.let { map ->
            boundsRepo.saveBounds(*map.values.toTypedArray())
                    .useStandardObserveSubscribe()
                    .doOnError {
                        error.value = it
                    }
                    .subscribe {
                        status.value = Status.FINISHED
                    }
        }
    }

    fun getPreviousStep(): BoundsType {
        val currentIndex = steps.indexOf(currentStep)
        return steps.getOrElse(currentIndex - 1, { BoundsType.UNSPECIFIED })
    }

    fun getNextStep(): BoundsType {
        val currentIndex = steps.indexOf(currentStep)
        return steps.getOrElse(currentIndex + 1, { BoundsType.UNSPECIFIED })
    }

    fun goToStep(boundsType: BoundsType) {
        currentStep = boundsType
    }

    enum class Status {
        IDLE, FINISHED
    }
}