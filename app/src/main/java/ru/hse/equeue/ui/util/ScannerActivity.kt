package ru.hse.equeue.ui.util

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import me.dm7.barcodescanner.zbar.ZBarScannerView

abstract class ScannerActivity : AppCompatActivity(), ZBarScannerView.ResultHandler {

    private lateinit var scanner: ZBarScannerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scanner = ZBarScannerView(this)
        setContentView(scanner)
    }

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