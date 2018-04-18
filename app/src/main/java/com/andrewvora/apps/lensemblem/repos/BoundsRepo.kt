package com.andrewvora.apps.lensemblem.repos

import android.app.Application
import com.andrewvora.apps.lensemblem.boundspicker.BoundingConfig
import com.andrewvora.apps.lensemblem.database.LensEmblemDatabase
import com.andrewvora.apps.lensemblem.models.Bounds
import com.andrewvora.apps.lensemblem.models.BoundsType
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created on 4/15/2018.
 * @author Andrew Vorakrajangthiti
 */
@Singleton
class BoundsRepo
@Inject constructor(
        private val app: Application,
        private val lensEmblemDatabase: LensEmblemDatabase) {

    fun getBounds(): Single<Map<BoundsType, Bounds>> {
        return Single.defer {
            return@defer Single.just(lensEmblemDatabase.boundsDao()
                    .getBounds()
                    .filterNot {
                        it.type == BoundsType.UNSPECIFIED
                    }
                    .associateBy {
                        it.type
                    })
        }
    }

    fun saveBounds(vararg bounds: Bounds): Completable {
        return Completable.defer {
            val newBounds = bounds.filter { it.id == 0 && it.type != BoundsType.UNSPECIFIED }
            lensEmblemDatabase.boundsDao().insert(*newBounds.toTypedArray())
            val existingBounds = bounds.filter { it.id > 0 && it.type != BoundsType.UNSPECIFIED }
            lensEmblemDatabase.boundsDao().update(*existingBounds.toTypedArray())

            return@defer Completable.complete()
        }
    }

    fun getDefaultBounds(): BoundingConfig {
        // TODO("Figure out best way to determine what bounds to use")
        return BoundingConfig.Nexus5
    }
}