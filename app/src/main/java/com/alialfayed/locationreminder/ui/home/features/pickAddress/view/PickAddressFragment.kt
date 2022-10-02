package com.alialfayed.locationreminder.ui.home.features.pickAddress.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.alfayedoficial.kotlinutils.kuRes
import com.alfayedoficial.kotlinutils.kuSnackBarError
import com.alialfayed.locationreminder.R
import com.alialfayed.locationreminder.databinding.FragmentPickAddressBinding
import com.alialfayed.locationreminder.utils.AppConstant.LOCATION_KEY
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class PickAddressFragment : Fragment() {

    private var _binding: FragmentPickAddressBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var userLocation: LatLng? = null
    private lateinit var mGoogleMap: GoogleMap

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            setUserLocationMarker()
        }
    }

    private val locationRequest: LocationRequest by lazy {
        LocationRequest.create()
        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        .setInterval(10 * 1000)
        .setFastestInterval(2000)
    }

    private val fusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    private val callback = OnMapReadyCallback { googleMap ->
        mGoogleMap = googleMap

        //Create a LatLngBounds object. --> start to egypt map
        val egyptBounds = LatLngBounds.builder()
            .include(LatLng(31.4021, 25.0534))
            .include(LatLng( 21.8623, 36.7628))
            .build()

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(egyptBounds , 20))


        moveCameraToLocation()

        googleMap.setOnMapClickListener {
            userLocation = it
            addMarkOnMap()
        }
    }

    fun moveCameraToLocation(){
        mGoogleMap.clear()
        setUserLocationMarker()
    }

    private fun setUserLocationMarker(){
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, object: LocationCallback(){
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)

                    val location = LatLng(locationResult.lastLocation!!.latitude,locationResult.lastLocation!!.longitude)
                    userLocation = location
                    fusedLocationProviderClient.removeLocationUpdates(this)

                    addMarkOnMap()

                }
            }, Looper.getMainLooper())
        }else{
            requestPermissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }
    }

    private fun addMarkOnMap(){
        mGoogleMap.clear() // to remove all marker
        val marker = MarkerOptions()
            .position(userLocation!!)
            .icon(getBitmapFromVector(requireContext(),R.drawable.ic_marker_user))

        mGoogleMap.addMarker(marker)
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation!!,15.0f))

    }

    private fun getBitmapFromVector(context: Context, resId: Int): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(context,resId)
        vectorDrawable?.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
        val bitmap = Bitmap.createBitmap(
            vectorDrawable!!.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPickAddressBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fragment = this
        binding.lifecycleOwner = this

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(callback)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun submitAddress(){
        if (userLocation != null){
            navigateBack(userLocation)
        }else{
            kuSnackBarError(getString(R.string.requrid_location) , kuRes.getColor(R.color.white , kuRes.newTheme()), kuRes.getColor(R.color.TemplateRed , kuRes.newTheme()))
        }

    }

    private fun navigateBack(location: LatLng?) = with(findNavController()) {
        previousBackStackEntry?.savedStateHandle?.set(LOCATION_KEY, location)
        popBackStack()
    }

}