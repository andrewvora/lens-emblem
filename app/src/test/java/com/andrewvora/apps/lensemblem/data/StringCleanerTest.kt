package com.andrewvora.apps.lensemblem.data

import org.junit.Assert.*
import org.junit.Test

/**
 * [StringCleaner]
 * Created on 5/28/2018.
 * @author Andrew Vorakrajangthiti
 */
class StringCleanerTest {

    private val stringCleaner = StringCleaner()

    @Test
    fun `standalone letters are removed`() {
        val case1 = "a "
        var result = stringCleaner.clean(case1)
        assertEquals("", result)

        val case2 = "y co B"
        result = stringCleaner.clean(case2)
        assertEquals("co", result)

        val case3 = "p Blushing t Bride a"
        result = stringCleaner.clean(case3)
        assertEquals("Blushing Bride", result)

        val case4 = "Arthur"
        result = stringCleaner.clean(case4)
        assertEquals("Arthur", result)
    }

    @Test
    fun `unicode left-right apostrophes are removed`() {
        val case1 = "‘"
        var result = stringCleaner.clean(case1)
        assertEquals("'", result)

        val case2 = "’"
        result = stringCleaner.clean(case2)
        assertEquals("'", result)

        val case3 = "’ Caeda ‘"
        result = stringCleaner.clean(case3)
        assertEquals("' Caeda '", result)

        val case4 = "Lon‘qu"
        result = stringCleaner.clean(case4)
        assertEquals("Lon'qu", result)
    }

    @Test
    fun `periods are removed`() {
        val case1 = "."
        var result = stringCleaner.clean(case1)
        assertEquals("", result)

        val case2 = "...Lukas..."
        result = stringCleaner.clean(case2)
        assertEquals("Lukas", result)

        val case3 = "Sharp . Soldier"
        result = stringCleaner.clean(case3)
        assertEquals("Sharp Soldier", result)
    }

    @Test
    fun `extra whitespace is removed`() {
        val case1 = " \t\n\r"
        var result = stringCleaner.clean(case1)
        assertEquals("", result)

        val case2 = "Spring \t Princess"
        result = stringCleaner.clean(case2)
        assertEquals("Spring Princess", result)

        val case3 = " Steven \t"
        result = stringCleaner.clean(case3)
        assertEquals("Steven", result)
    }

    @Test
    fun `does not trim proper names`() {
        val case1 = "Obsessive Bride"
        assertEquals(case1, stringCleaner.clean(case1))

        val case2 = "Tharja"
        assertEquals(case2, stringCleaner.clean(case2))

        val case3 = "Young Mercenary"
        assertEquals(case3, stringCleaner.clean(case3))

        val case4 = "Reinhardt"
        assertEquals(case4, stringCleaner.clean(case4))

        val case5 = "An Unusual Title"
        assertEquals(case5, stringCleaner.clean(case5))

    }
}