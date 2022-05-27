package ru.hse.equeue.ui.search.map

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
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
import com.google.android.gms.maps.model.VisibleRegion
import ru.hse.equeue.R
import ru.hse.equeue.databinding.FragmentSearchMapBinding
import ru.hse.equeue.model.ListQueueRequest
import ru.hse.equeue.model.Queue
import ru.hse.equeue.ui.queues.ActiveQueueViewModel
import ru.hse.equeue.ui.search.MapPosition
import ru.hse.equeue.ui.search.SearchViewModel
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class MapSearchFragment() : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
    GoogleMap.OnCameraMoveListener {

    private val searchViewModel: SearchViewModel by activityViewModels()
    private val queueViewModel: ActiveQueueViewModel by activityViewModels()

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


    private var gMap: GoogleMap? = null

    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap
        gMap?.setOnMarkerClickListener(this)
        gMap?.setOnCameraMoveListener(this)
        if (searchViewModel.currentLocation.value == null) {
            getDeviceLocation()
        } else {
            searchViewModel.getQueues()
            gMap!!.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(searchViewModel.mapPosition.value!!.lat, searchViewModel.mapPosition.value!!.lng),
                    searchViewModel.mapPosition.value!!.zoom
                )
            )
        }
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            gMap?.isMyLocationEnabled = true
//                searchViewModel.gMap.value!!.uiSettings.isMyLocationButtonEnabled = false

        }
    }

    fun addMarker(markerOptions: MarkerOptions, queue: Queue) {
        val marker: Marker? = gMap?.addMarker(markerOptions)
        markersMap.put(marker, queue)
    }

    private var _binding: FragmentSearchMapBinding? = null


    private val binding get() = _binding!!
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient

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
            findNavController().navigate(R.id.action_mapSearchFragment_to_listSearchFragment)
        }
        binding.editSearch.setOnClickListener {
            findNavController().navigate(R.id.action_mapSearchFragment_to_listSearchFragment2)
        }
        getQueuesEvent()
    }

    private fun initMap() {
        if (gMap == null) {
            var mapFragment: SupportMapFragment =
                childFragmentManager.findFragmentById(R.id.gMap) as SupportMapFragment
            mapFragment.getMapAsync(this)
        }
    }

    private fun getDeviceLocation() {
        mFusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity().application)
        try {
            val location = mFusedLocationProviderClient.lastLocation
            location.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var currentLocation: Location = task.result
                    gMap!!.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(currentLocation.latitude, currentLocation.longitude),
                            15f
                        )
                    )
                    searchViewModel.currentLocation.value = ListQueueRequest(
                        x = currentLocation.latitude,
                        y = currentLocation.longitude,
                        r = getRadius()
                    )

                    searchViewModel.mapPosition.value = MapPosition(
                        lat = currentLocation.latitude,
                        lng = currentLocation.longitude,
                        zoom = 15f
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

    private fun getQueuesEvent() {
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
        queueViewModel.selectedQueue.value = markersMap.get(marker)!!
        findNavController().navigate(R.id.action_mapSearchFragment_to_queueInfoFragment)
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun getRadius(): Double {
        val visibleRegion = gMap!!.getProjection().getVisibleRegion()
        val lng = visibleRegion.farRight.longitude - visibleRegion.farLeft.longitude
        val lat = visibleRegion.farRight.latitude - visibleRegion.nearRight.latitude
        return  sqrt(
            lat.pow(2.0) + lng.pow(2.0)
        ) / 2 + 0.01
    }

    override fun onCameraMove() {
        val radius = getRadius()

        if (abs(searchViewModel.currentLocation.value!!.r - radius) > 0.1
            || (abs(searchViewModel.currentLocation.value!!.x - gMap!!.cameraPosition.target.latitude) > 0.01)
            || (abs(searchViewModel.currentLocation.value!!.y - gMap!!.cameraPosition.target.longitude) > 0.01)
        ) {
            searchViewModel.currentLocation.value = ListQueueRequest(
                radius,
                gMap!!.cameraPosition.target.latitude,
                gMap!!.cameraPosition.target.longitude
            )

            searchViewModel.getQueues()
        }
        searchViewModel.mapPosition.value = MapPosition(
            gMap!!.cameraPosition.zoom,
            gMap!!.cameraPosition.target.latitude,
            gMap!!.cameraPosition.target.longitude
        )
    }

}

