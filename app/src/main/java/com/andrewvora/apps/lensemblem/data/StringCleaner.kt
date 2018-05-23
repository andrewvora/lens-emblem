package com.andrewvora.apps.lensemblem.data

import javax.inject.Inject

/**
 * Created on 5/23/2018.
 * @author Andrew Vorakrajangthiti
 */
open class StringCleaner
@Inject
constructor() {
    /**
     * Removes special characters and replaces them with basic equivalents
     */
    fun clean(dirtyString: String): String {
        return dirtyString
                .replace(Regex("\u2018"), "'")
                .replace(Regex("\u2019"), "'")
    }
}