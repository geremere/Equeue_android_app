package ru.hse.equeue.ui.search.map

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment

data class MapCameraState(
    var x: Double,
    var y: Double,
    var zoom: Float

)

class MapViewModel(application: Application) : AndroidViewModel(application) {
    var gMap = MutableLiveData<GoogleMap>()
    var mapManager = MutableLiveData<SupportMapFragment>()
    var cameraState = MutableLiveData<MapCameraState>()


}