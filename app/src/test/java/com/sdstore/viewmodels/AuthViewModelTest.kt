package com.sdstore.viewmodels

import android.app.Application

class AuthViewModelTest {

    // یہ اصول LiveData کو ٹیسٹ کرنے کے لیے ضروری ہے
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    // Application context کا ایک نقلی (mock) ورژن بناتا ہے
    @Mock
    private lateinit var application: Application

    private lateinit var viewModel: AuthViewModel

    // یہ فنکشن ہر ٹیسٹ سے پہلے چلتا ہے
    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = AuthViewModel(application)
    }

    @Test
    fun `login with valid credentials sets isLoggedIn to true`() {
        // ViewModel کا login فنکشن کال کریں
        viewModel.login("testuser", "password123")

        // تصدیق کریں کہ isLoggedIn کی ویلیو true ہو گئی ہے
        val isLoggedIn = viewModel.isLoggedIn.value
        assertEquals(true, isLoggedIn)
    }

    @Test
    fun `logout sets isLoggedIn to false`() {
        // پہلے لاگ ان کریں
        viewModel.login("testuser", "password123")

        // پھر لاگ آؤٹ کریں
        viewModel.logout()

        // تصدیق کریں کہ isLoggedIn کی ویلیو false ہو گئی ہے
        val isLoggedIn = viewModel.isLoggedIn.value
        assertEquals(false, isLoggedIn)
    }

    @Test
    fun `login with empty username does not change isLoggedIn`() {
        // خالی نام کے ساتھ لاگ ان کی کوشش کریں
        viewModel.login("", "password123")

        // تصدیق کریں کہ isLoggedIn کی ویلیو false ہی ہے
        val isLoggedIn = viewModel.isLoggedIn.value
        assertEquals(false, isLoggedIn)
    }
}