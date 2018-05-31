package com.andrewvora.apps.lensemblem.models

import android.support.annotation.DrawableRes
import android.support.annotation.StringRes

/**
 * Created on 5/29/2018.
 * @author Andrew Vorakrajangthiti
 */
data class FilterOption(@DrawableRes val drawableId: Int,
                        @StringRes val titleId: Int)