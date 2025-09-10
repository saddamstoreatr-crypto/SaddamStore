package com.sdstore.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import com.sdstore.R
import com.sdstore.auth.AuthActivity
import com.sdstore.cart.viewmodels.CartViewModel
import com.sdstore.databinding.ActivityMainBinding
import com.sdstore.orders.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private val userViewModel: UserViewModel by viewModels()
    private val cartViewModel: CartViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                Snackbar.make(
                    binding.root,
                    getString(R.string.notification_permission_denied),
                    Snackbar.LENGTH_LONG
                ).setAction(getString(R.string.action_settings)) {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }.show()
            } else {
                Snackbar.make(
                    binding.root,
                    getString(R.string.notification_permission_warning),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupApp()
        askNotificationPermission()
    }

    private fun setupApp() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_main) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavView.setupWithNavController(navController)
        setupStateObservers()
    }

    private fun navigateToAuth() {
        val intent = Intent(this, AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun setupStateObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    userViewModel.loggedOutEvent.collect { hasLoggedOut ->
                        if (hasLoggedOut) {
                            navigateToAuth()
                            userViewModel.onLogoutEventHandled()
                        }
                    }
                }

                launch {
                    cartViewModel.cartItems.collect { cartItems ->
                        val badge = binding.bottomNavView.getOrCreateBadge(R.id.cartFragment)
                        badge.isVisible = !cartItems.isNullOrEmpty()
                        if (badge.isVisible) {
                            badge.number = cartItems.size
                        }
                    }
                }
            }
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                    Snackbar.make(
                        binding.root,
                        getString(R.string.notification_permission_rationale),
                        Snackbar.LENGTH_LONG
                    ).setAction(getString(R.string.action_allow)) {
                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }.show()
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }
}