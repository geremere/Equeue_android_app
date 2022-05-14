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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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
import ru.hse.equeue.model.ListQueueRequest
import ru.hse.equeue.model.Queue
import ru.hse.equeue.ui.search.SearchViewModel
import ru.hse.equeue.ui.search.queue.QueueViewModel
import kotlin.math.abs
import kotlin.math.pow

class MapSearchFragment() : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
    GoogleMap.OnCameraMoveListener {

    private val searchViewModel: SearchViewModel by activityViewModels()
    private val queueViewModel: QueueViewModel by activityViewModels()

    private val markersMap = mutableMapOf<Marker?, Queue>()
    private val mapPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { granted ->
            var flag = true
            granted.keys.forEach { key ->
                if (!granted.get(key)!!) {
                    flag = false
                }
            }
            if (flag) {
                initMap()
            }
        }


    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap
        gMap.setOnMarkerClickListener(this)
        gMap.setOnCameraMoveListener(this)
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

    fun addMarker(markerOptions: MarkerOptions, queue: Queue) {
        val marker: Marker? = gMap.addMarker(markerOptions)
        markersMap.put(marker, queue)
    }

    private var _binding: FragmentSearchMapBinding? = null

    lateinit var gMap: GoogleMap


    private val binding get() = _binding!!
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private val DEFAULT_ZOOM = 15.0
    private val TWO_NUM: Double = 2.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSearchMapBinding.inflate(inflater, container, false)
        val root: View = binding.root
        mapPermissions.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
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
        getQueuesEvent()
    }

    private fun initMap() {
        var mapFragment: SupportMapFragment = SupportMapFragment.newInstance()
        childFragmentManager.beginTransaction()
            .replace(R.id.gMap, mapFragment)
            .commit()

        mapFragment.getMapAsync(this)
    }

    private fun getDeviceLocation() {
        mFusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity().application)
        try {
            val location = mFusedLocationProviderClient.lastLocation
            location.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var currentLocation: Location = task.result
                    gMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(currentLocation.latitude, currentLocation.longitude),
                            DEFAULT_ZOOM.toFloat()
                        )
                    )
                    searchViewModel.currentLocation.value = ListQueueRequest(
                        x = currentLocation.latitude,
                        y = currentLocation.longitude,
                        r = 0.015060 * (40000 / TWO_NUM.pow(DEFAULT_ZOOM)) * 2
                    )
                    searchViewModel.getQueues()
                } else {
                    Toast.makeText(requireContext(), "some problems", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: SecurityException) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
    }

    fun getQueuesEvent() {
        searchViewModel.queuesResult.observe(viewLifecycleOwner) {
            it.onSuccess { result ->
                searchViewModel.setQueues(result)
                result.forEach {
                    addMarker(
                        MarkerOptions()
                            .position(LatLng(it.x, it.y))
                            .title(it.name),
                        it
                    )
                }

            }.onFailure {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            }

        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        queueViewModel.setQueue(markersMap.get(marker)!!)
        findNavController().navigate(
            R.id.action_mapSearchFragment_to_queueFragment,
            null
        )
        return false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCameraMove() {
        if (gMap.cameraPosition.zoom > 7 &&
            abs(0.015060 * (40000 / TWO_NUM.pow(gMap.cameraPosition.zoom.toDouble())) * 2 - searchViewModel.currentLocation.value?.r!!) > 0.2
        ) {
            searchViewModel.currentLocation.value?.r =
                0.015060 * (40000 / TWO_NUM.pow(gMap.cameraPosition.zoom.toDouble())) * 2
            searchViewModel.getQueues()
        }
    }
}
