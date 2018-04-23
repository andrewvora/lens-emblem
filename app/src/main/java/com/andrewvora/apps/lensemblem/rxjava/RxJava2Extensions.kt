package com.andrewvora.apps.lensemblem.rxjava

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created on 4/22/2018.
 * @author Andrew Vorakrajangthiti
 */

fun <T> Single<T>.useStandardObserveSubscribe(): Single<T> {
    return this
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}

fun Completable.useStandardObserveSubscribe(): Completable {
    return this
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}