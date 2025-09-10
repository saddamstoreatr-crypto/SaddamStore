package com.sdstore.viewmodels

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView // Import onView
import androidx.test.espresso.assertion.ViewAssertions.matches // Import matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed // Import isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId // Import withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sdstore.R
import com.sdstore.auth.AuthActivity
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthActivityTest {

    @Test
    fun test_isStartFragmentVisible_onAppLaunch() {
        ActivityScenario.launch(AuthActivity::class.java)

        // --- TABDEELI: Ghalat ID 'upNextButton' ko durust ID 'btn_login_with_phone' se tabdeel kiya gaya hai ---
        onView(withId(R.id.btn_login_with_phone)).check(matches(isDisplayed()))
    }
}
