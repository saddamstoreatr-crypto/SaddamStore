package com.sdstore.auth.register

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.*
import com.sdstore.feature_auth.databinding.FragmentOutletLocationBinding
import com.sdstore.auth.viewmodels.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OutletLocationFragment : Fragment() {
    private var _binding: FragmentOutletLocationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RegisterViewModel by activityViewModels()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                checkLocationServiceAndProceed()
            }
            else -> {
                Toast.makeText(context, getString(com.sdstore.R.string.location_permission_needed), Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOutletLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        checkLocationServiceAndProceed(checkPermissionOnly = true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        setupClickListeners()
        setupObservers()
    }

    private fun setupClickListeners() {
        binding.btnGetLocation.setOnClickListener {
            checkLocationServiceAndProceed()
        }
        binding.registerLocationButton.setOnClickListener {
            if (viewModel.location?.isNotEmpty() == true) {
                viewModel.saveRegistrationData()
            } else {
                Toast.makeText(context, getString(com.sdstore.R.string.get_location_first), Toast.LENGTH_SHORT).show()
            }
        }
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun checkLocationServiceAndProceed(checkPermissionOnly: Boolean = false) {
        if (isLocationServiceEnabled()) {
            binding.tvLocationStatus.text = "Location is ON"
            if (!checkPermissionOnly) {
                requestLocationPermission()
            }
        } else {
            binding.tvLocationStatus.text = "Location is OFF"
            if (!checkPermissionOnly) {
                showEnableLocationDialog()
            }
        }
    }

    private fun isLocationServiceEnabled(): Boolean {
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun showEnableLocationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(com.sdstore.R.string.location_service_off_title))
            .setMessage(getString(com.sdstore.R.string.location_service_off_message))
            .setPositiveButton(getString(com.sdstore.R.string.turn_on)) { dialog, _ ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
                dialog.dismiss()
            }
            .setNegativeButton(getString(com.sdstore.R.string.cancel)) { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.registrationState.collect { state ->
                    val isLoading = state is RegisterViewModel.RegistrationState.Loading
                    binding.registerLocationButton.isEnabled = !isLoading
                    binding.registerLocationButton.text = if (isLoading) "" else getString(com.sdstore.R.string.register_location)

                    when (state) {
                        is RegisterViewModel.RegistrationState.Success -> {
                            findNavController().navigate(com.sdstore.feature_auth.R.id.action_outletLocationFragment_to_registerSuccessFragment)
                            viewModel.resetRegistrationState()
                        }
                        is RegisterViewModel.RegistrationState.Error -> {
                            Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                            viewModel.resetRegistrationState()
                        }
                        else -> { /* Idle or other states */ }
                    }
                }
            }
        }
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation()
        } else {
            locationPermissionRequest.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
        }
    }

    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        binding.tvLocationStatus.text = getString(com.sdstore.R.string.location_fetching)

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
            .setMinUpdateIntervalMillis(5000)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    if (location != null) {
                        val locationString = "${location.latitude}, ${location.longitude}"
                        viewModel.saveLocation(locationString)
                        binding.tvLocationStatus.text = getString(com.sdstore.R.string.location_saved)
                        fusedLocationClient.removeLocationUpdates(this)
                        return
                    }
                }
            }
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (::locationCallback.isInitialized) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
        _binding = null
    }
}