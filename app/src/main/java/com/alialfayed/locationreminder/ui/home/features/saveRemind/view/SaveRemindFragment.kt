package com.alialfayed.locationreminder.ui.home.features.saveRemind.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.alfayedoficial.kotlinutils.*
import com.alialfayed.locationreminder.R
import com.alialfayed.locationreminder.core.geofence.GeofenceBroadcastReceiver
import com.alialfayed.locationreminder.databinding.FragmentSaveRemindBinding
import com.alialfayed.locationreminder.ui.home.features.saveRemind.viewModel.SaveReminderViewModel
import com.alialfayed.locationreminder.ui.home.view.OneSingleActivity
import com.alialfayed.locationreminder.ui.home.view.setupLocationServiceWithPermissionCheck
import com.alialfayed.locationreminder.utils.AppConstant.ACTION_GEOFENCE_EVENT
import com.alialfayed.locationreminder.utils.AppConstant.GEOFENCE_RADIUS
import com.alialfayed.locationreminder.utils.AppConstant.LOCATION_KEY
import com.alialfayed.locationreminder.utils.setDisplayHomeAsUpEnabled
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.android.ext.android.inject
import permissions.dispatcher.*
import java.io.IOException
import java.util.*

@RuntimePermissions
class SaveRemindFragment : Fragment() {

    private var _binding: FragmentSaveRemindBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val mViewModel : SaveReminderViewModel by inject()

    private val setupLocationServiceResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        setupLocationServiceWithPermissionCheck()
    }

    private val geofencingClient by lazy {
        LocationServices.getGeofencingClient(requireActivity())
    }

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(activity, GeofenceBroadcastReceiver::class.java)
        intent.action = ACTION_GEOFENCE_EVENT
        PendingIntent.getBroadcast(activity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSaveRemindBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = mViewModel
            fragment = this@SaveRemindFragment
        }
        setDisplayHomeAsUpEnabled(true)
        setUpViewModelStateObservers()

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<LatLng>(LOCATION_KEY)
            ?.observe(viewLifecycleOwner) {
                mViewModel.address.value = getAddress(it)
                mViewModel.location.value = it
            }
        setupLocationServiceWithPermissionCheck()

    }

    private fun setUpViewModelStateObservers() {

        mViewModel.loadingState.observe(viewLifecycleOwner){
            if (it == true) {
                binding.lyContainerIsLoading.root.kuShow()
            } else if (it == false) {
                binding.lyContainerIsLoading.root.kuHide()
                kuToast(getString(R.string.reminder_saved))
                findNavController().popBackStack()
            }
        }

        mViewModel.errorState.observe(viewLifecycleOwner){
            kuSnackBarError(getString(it) , kuRes.getColor(R.color.white, kuRes.newTheme()) , kuRes.getColor(R.color.TemplateRed, kuRes.newTheme()))
        }
    }

    fun onPickLocationClick() {
        findNavController().navigate(R.id.action_saveRemindFragment_to_PickAddress)
    }

    private fun getAddress(latLng: LatLng): String {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())

        return try {
            val addresses = geocoder.getFromLocation(
                latLng.latitude,
                latLng.longitude,
                1
            ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            val ad  = StringBuffer("")
            val adModel = addresses[0]
            if (!adModel.thoroughfare.isNullOrBlank() && !adModel.thoroughfare.isNullOrEmpty()){
                ad.append(adModel.thoroughfare)
                ad.append(" , ")
            }
            if (!adModel.locality.isNullOrBlank() && !adModel.locality.isNullOrEmpty() ){
                ad.append(adModel.locality)
                ad.append(" , ")
            }
            if (!adModel.subAdminArea.isNullOrBlank() && !adModel.subAdminArea.isNullOrEmpty() ){
                ad.append(adModel.subAdminArea)
                ad.append(" , ")
            }
            if (!adModel.countryName.isNullOrBlank() && !adModel.countryName.isNullOrEmpty() ){
                ad.append(adModel.countryName)
            }

            ad.toString()
        } catch (e: IOException) {
            e.printStackTrace()
            "No Address Found"
        }
    }


    fun onSaveRemindClick(){

        if(mViewModel.validateEnteredData()){
            mViewModel.saveReminderEntity()
        }else{
            return
        }

        if (checkLocationPermissions()){
            val locationRequest = LocationRequest.create().apply {
                priority = LocationRequest.PRIORITY_LOW_POWER
            }
            val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
            LocationServices.getSettingsClient(requireActivity()).checkLocationSettings(builder.build()).apply {
                addOnSuccessListener {
                    createGeoFence()
                }
                addOnFailureListener {
                    kuSnackBar(getString(R.string.Please_Enable))!!.setAction(getString(R.string.try_again)) {
                        onSaveRemindClick()
                    }.show()
                }
            }

        }else{
            setupLocationServiceWithPermissionCheck()
            (activity as OneSingleActivity).setupLocationServiceWithPermissionCheck()
        }
    }

    private fun createGeoFence() {
        val geofence = Geofence.Builder()
            .setRequestId(mViewModel.reminderEntity.value?.id.toString())
            .setCircularRegion(
                mViewModel.reminderEntity.value?.location?.latitude?:0.0,
                mViewModel.reminderEntity.value?.location?.longitude?:0.0,
                GEOFENCE_RADIUS)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
            .build()

        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        if (!checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) || !checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)){
            return
        }
        geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent).apply {
            addOnSuccessListener {
                mViewModel.saveReminderToDatabase()
            }
            addOnFailureListener {
                kuSnackBar(it.message.toString())!!.setAction(getString(R.string.try_again)) {
                    onSaveRemindClick()
                }.show()
            }
        }
    }

    private fun checkLocationPermissions(): Boolean {
        return checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) && checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { checkPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION) } else  true
    }

    private fun checkPermission(permission: String) = PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(requireContext(), permission)

    // Mark -*- handle Permissions
    // NeedsPermission method is called when the user has not granted the permission
    @RequiresApi(Build.VERSION_CODES.Q)
    @NeedsPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
    fun setupLocationService(){
        if (checkPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
            // Do the task needing access to the location
            return
        }else{
            // Show dialog to enable location
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.enable_location))
                .setMessage(getString(R.string.message_location))
                .setPositiveButton(getString(R.string.locationSettings)) { dialog, _ ->
                    // Open location settings
                    onSettingScreen()
                    dialog.dismiss()
                }
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                    // Do nothing
                    dialog.dismiss()
                }
                .show()
        }

    }

    private fun onSettingScreen() {
        setupLocationServiceResult.launch(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }

    // OnShowRationale method is called if the user has denied the permission before
    @RequiresApi(Build.VERSION_CODES.Q)
    @OnShowRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
    fun onRationaleAskLocation(request : PermissionRequest) {
        // Show the rationale
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.location_permission))
            .setMessage(getString(R.string.location_permission_message))
            .setPositiveButton(getString(R.string._ok)) { dialog, _ ->
                request.proceed()
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                // Do nothing
                request.cancel()
                dialog.dismiss()
            }
            .show()
    }

    // OnPermissionDenied method is called if the user has denied the permission
    @RequiresApi(Build.VERSION_CODES.Q)
    @OnPermissionDenied(Manifest.permission.ACCESS_BACKGROUND_LOCATION )
    fun onDeniedAskLocation() {
        kuToast(getString(R.string.location_permission_denied))
    }

    // OnNeverAskAgain method is called if the user has denied the permission and checked "Never ask again"
    @RequiresApi(Build.VERSION_CODES.Q)
    @OnNeverAskAgain(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
    fun onNeverAskLocation() {
        val onApplicationSettings = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        onApplicationSettings.data = Uri.parse("package:${requireActivity().packageName}")
        setupLocationServiceResult.launch(onApplicationSettings)
    }

    @SuppressLint("NeedOnRequestPermissionsResult")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // NOTE: delegate the permission handling to generated function
        onRequestPermissionsResult(requestCode, grantResults)
    }
    // Mark -*- handle Permissions

    override fun onDestroy() {
        mViewModel.cleanData()
        super.onDestroy()
    }
}