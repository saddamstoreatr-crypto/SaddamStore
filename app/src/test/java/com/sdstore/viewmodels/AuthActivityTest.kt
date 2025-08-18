package com.sdstore.viewmodels

import com.sdstore.R
import com.sdstore.auth.AuthActivity

/**
 * AuthActivity کے لیے UI ٹیسٹ۔
 * یہ ٹیسٹ ایک ایمولیٹر یا حقیقی ڈیوائس پر چلتا ہے۔
 */
@RunWith(AndroidJUnit4::class)
class AuthActivityTest {

    /**
     * یہ ٹیسٹ اس بات کی تصدیق کرتا ہے کہ جب AuthActivity لانچ ہوتی ہے،
     * تو StartFragment نظر آتا ہے اور اس میں موجود 'Create New Account' کا بٹن (upNextButton)
     * اسکرین پر موجود ہے۔
     */
    @Test
    fun test_isStartFragmentVisible_onAppLaunch() {
        // AuthActivity کو لانچ کریں
        ActivityScenario.launch(AuthActivity::class.java)

        // چیک کریں کہ R.id.upNextButton والا ویو اسکرین پر نظر آ رہا ہے
        onView(withId(R.id.upNextButton)).check(matches(isDisplayed()))
    }
}