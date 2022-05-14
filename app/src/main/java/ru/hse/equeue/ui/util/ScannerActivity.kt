package ru.hse.equeue.ui.util

import android.Manifest.permission.CAMERA
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import me.dm7.barcodescanner.zbar.ZBarScannerView

abstract class ScannerActivity : AppCompatActivity(), ZBarScannerView.ResultHandler {

    private lateinit var scanner: ZBarScannerView
    private val cameraPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        when {
            granted -> {
                initBinding()
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scanner = ZBarScannerView(this)
        cameraPermission.launch(CAMERA)
    }

    private fun initBinding(){
        setContentView(scanner)
    }
//
//    fun checkPermissions() {
//        if (ContextCompat.checkSelfPermission(
//                applicationContext.applicationContext,
//                CAMERA
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            this.let {
//                ActivityCompat.requestPermissions(
//                    it,
//                    arrayOf(
//                        Manifest.permission.CAMERA
//                    ),
//                    1234
//                )
//            }
//        }
//    }

    override fun onResume() {
        super.onResume()
        scanner.setResultHandler(this)
        scanner.startCamera()
    }

    override fun onPause() {
        super.onPause()
        scanner.stopCamera()
    }

}