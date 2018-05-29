package com.andrewvora.apps.lensemblem.heroeslist

import android.content.pm.ActivityInfo
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import com.andrewvora.apps.lensemblem.R
import com.andrewvora.apps.lensemblem.main.MainActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * [HeroesListFragment]
 * Created on 5/28/2018.
 * @author Andrew Vorakrajangthiti
 */
class HeroesListFragmentTest {

    @Rule
    @JvmField
    val activityTestRule = ActivityTestRule<MainActivity>(MainActivity::class.java)

    @Before
    fun beforeEveryTest() {
        onView(withId(R.id.menu_view_heroes)).perform(click())
    }

    @Test
    fun handlesConfigChanges() {
        onView(withId(R.id.hero_list_recycler_view)).check(matches(isDisplayed()))
        onView(withText(R.string.heroes)).check(matches(isDisplayed()))

        activityTestRule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        activityTestRule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        onView(withId(R.id.hero_list_recycler_view)).check(matches(isDisplayed()))
        onView(withText(R.string.heroes)).check(matches(isDisplayed()))
    }
}