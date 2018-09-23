package com.andrewvora.apps.lensemblem

import android.content.pm.ActivityInfo
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.andrewvora.apps.lensemblem.main.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * [MainFragment]
 * Created on 3/14/2018.
 * @author Andrew Vorakrajangthiti
 */
@RunWith(AndroidJUnit4::class)
class MainFragment {

    @Rule
    @JvmField
    val activityTestRule = ActivityTestRule<MainActivity>(MainActivity::class.java)

    @Test
    fun checkBasicNavigation() {
        onView(withId(R.id.start_service_button))
                .check(matches(isDisplayed()))
                .check(matches(isEnabled()))

        onView(withId(R.id.menu_view_heroes)).perform(click())
        onView(withId(R.id.hero_list_recycler_view)).check(matches(isDisplayed()))

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())
        onView(withId(R.id.start_service_button))
                .check(matches(isDisplayed()))
                .check(matches(isEnabled()))
    }

    @Test
    fun checkOverflowMenuVisibility() {
        onView(withId(R.id.menu_view_heroes)).check(matches(isDisplayed()))
    }

    @Test
    fun handlesConfigChanges() {
        activityTestRule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        activityTestRule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
}