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
    open fun clean(dirtyString: String): String {
        return dirtyString
                // replace left single quote
                .replace(Regex("\u2018"), "'")
                // replace right single quote
                .replace(Regex("\u2019"), "'")
                // replace periods
                .replace(Regex("\\."), "")
                // replace single-letter words
                .replace(Regex("(\\s[a-zA-Z]\\s)|(\\s[a-zA-Z]$)|(^[a-zA-Z]\\s)"), " ")
                // replace extra whitespace w/ single whitespace
                .replace(Regex("\\s+"), " ")
                .trim()
    }
}