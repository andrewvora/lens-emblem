package com.andrewvora.apps.lensemblem.notifications

import android.content.pm.ActivityInfo
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import com.andrewvora.apps.lensemblem.R
import com.andrewvora.apps.lensemblem.main.MainActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * [NotificationFragment]
 * Created on 5/28/2018.
 * @author Andrew Vorakrajangthiti
 */
class NotificationFragmentTest {
    @Rule
    @JvmField
    val activityTestRule = ActivityTestRule<MainActivity>(MainActivity::class.java)

    @Before
    fun beforeEveryTest() {
        onView(withId(R.id.menu_notifications)).perform(click())
    }

    @Test
    fun handlesConfigChanges() {
        onView(withId(R.id.notifications_recycler_view)).check(matches(isDisplayed()))
        onView(ViewMatchers.withText(R.string.notifications)).check(matches(isDisplayed()))

        activityTestRule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        activityTestRule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        onView(withId(R.id.notifications_recycler_view)).check(matches(isDisplayed()))
        onView(ViewMatchers.withText(R.string.notifications)).check(matches(isDisplayed()))
    }
}