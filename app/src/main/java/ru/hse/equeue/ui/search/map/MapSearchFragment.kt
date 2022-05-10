package ru.hse.equeue.ui.search.map

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import ru.hse.equeue.R
import ru.hse.equeue.databinding.FragmentSearchMapBinding
import ru.hse.equeue.model.Queue
import ru.hse.equeue.ui.search.SearchViewModel
import ru.hse.equeue.ui.search.queue.QueueViewModel

class MapSearchFragment() : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private val searchViewModel: SearchViewModel by activityViewModels()
    private val queueViewModel: QueueViewModel by activityViewModels()

    private val markersMap = mutableMapOf<Marker?, Queue>()


    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap
        searchViewModel.getQueues().forEach {
            val marker: Marker? = gMap.addMarker(
                MarkerOptions()
                    .position(LatLng(it.x, it.y))
                    .title(it.name)
            )
            markersMap.put(marker, it)
        }
        gMap.setOnMarkerClickListener(this)
        if (mLocationPermissionResult) {
            getDeviceLocation()
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                gMap.isMyLocationEnabled = true
//                gMap.uiSettings.isMyLocationButtonEnabled = false
            }

        }
    }

    private var _binding: FragmentSearchMapBinding? = null

    lateinit var gMap: GoogleMap


    private val binding get() = _binding!!
    private var mLocationPermissionResult: Boolean = false
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private val DEFAULT_ZOOM = 15f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSearchMapBinding.inflate(inflater, container, false)
        val root: View = binding.root
        getLocationPermission()
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toListButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_navigation_search_to_navigation_search_list2,
                null
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getLocationPermission() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (ContextCompat.checkSelfPermission(
                requireActivity().applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            &&
            ContextCompat.checkSelfPermission(
                requireActivity().applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mLocationPermissionResult = true
            initMap()

        } else {
            activity?.let {
                ActivityCompat.requestPermissions(
                    it,
                    permissions,
                    1234
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        mLocationPermissionResult = false
        when (requestCode) {
            1234 -> {
                if (grantResults.isNotEmpty()) {
                    grantResults
                        .forEach {
                            if (it != PackageManager.PERMISSION_GRANTED) {
                                mLocationPermissionResult = false
                                return
                            }
                        }
                    mLocationPermissionResult = true
                    initMap()
                }
            }
        }

    }

    private fun initMap() {
        var mapFragment: SupportMapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment = SupportMapFragment.newInstance()
        fragmentManager?.beginTransaction()?.replace(R.id.map, mapFragment)
            ?.commit()

        mapFragment.getMapAsync(this)
    }

    private fun getDeviceLocation() {
        mFusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity().application)
        try {
            if (mLocationPermissionResult) {
                val location = mFusedLocationProviderClient.lastLocation
                location.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        var currentLocation: Location = task.result
                        moveCamera(
                            LatLng(currentLocation.latitude, currentLocation.longitude),
                            DEFAULT_ZOOM
                        )
                    } else {
                        Toast.makeText(requireContext(), "some problems", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("MapActivity", "getDeviceLocation: SecurityException: " + e.message)
        }
    }

    private fun moveCamera(latLng: LatLng, zoom: Float) {
        Log.d(
            "MapActivity",
            "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude
        )
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        queueViewModel.setQueue(markersMap.get(marker)!!)
        findNavController().navigate(
            R.id.action_mapSearchFragment_to_queueFragment,
            null
        )
        return false
    }
}