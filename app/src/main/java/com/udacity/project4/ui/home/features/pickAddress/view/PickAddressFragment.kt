package com.udacity.project4.ui.home.features.pickAddress.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.os.Looper
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.alfayedoficial.kotlinutils.kuRes
import com.alfayedoficial.kotlinutils.kuSnackBarError
import com.udacity.project4.R
import com.udacity.project4.databinding.FragmentPickAddressBinding
import com.udacity.project4.utils.AppConstant.LOCATION_KEY
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
    private lateinit var pointOfInterest: PointOfInterest

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

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->
        mGoogleMap = googleMap

        //Create a LatLngBounds object. --> start to egypt map
        val egyptBounds = LatLngBounds.builder()
            .include(LatLng(31.4021, 25.0534))
            .include(LatLng( 21.8623, 36.7628))
            .build()

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(egyptBounds , 20))

        googleMap.isMyLocationEnabled = true

        moveCameraToLocation()


        try {
            // Customize the styling of the base map using a JSON object defined
            // in a raw resource file.
            val success = mGoogleMap.setMapStyle(context?.let { MapStyleOptions.loadRawResourceStyle(it, R.raw.map_style) })
            if (!success) {
                Toast.makeText(context,"Style parsing failed.", Toast.LENGTH_LONG).show()
            }
        } catch (e: Resources.NotFoundException) {
            Toast.makeText(context, "error $e", Toast.LENGTH_LONG).show()
        }

        googleMap.setOnPoiClickListener {
            pointOfInterest = it
            userLocation = it.latLng
            addMarkOnMap()
        }

        googleMap.setOnMapClickListener {
            userLocation = it
            pointOfInterest = PointOfInterest(userLocation!!, "", getString(R.string.dropped_pin))
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
                    pointOfInterest = PointOfInterest(location, "", getString(R.string.dropped_pin))
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
            .title(pointOfInterest.name)
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

        setMenu()
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

    private fun setMenu() {
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.map_options, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when(menuItem.itemId){
                        R.id.normal_map -> {
                            mGoogleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                            return true
                        }
                        R.id.hybrid_map -> {
                            mGoogleMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                            return true
                        }
                        R.id.satellite_map -> {
                            mGoogleMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                            return true
                        }
                        R.id.terrain_map -> {
                            mGoogleMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                            return true
                        }
                    else -> false
                }
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

}