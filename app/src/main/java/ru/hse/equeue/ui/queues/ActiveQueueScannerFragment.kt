package ru.hse.equeue.ui.queues

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import me.dm7.barcodescanner.zbar.Result
import me.dm7.barcodescanner.zbar.ZBarScannerView
import ru.hse.equeue.R

class ActiveQueueScannerFragment : Fragment(), ZBarScannerView.ResultHandler {

    private val queueViewModel: ActiveQueueViewModel by activityViewModels()
    private lateinit var scanner: ZBarScannerView


    override fun handleResult(result: Result?) {
        if (result != null) {
            queueViewModel.standToQueue(result.contents.toLong())
            findNavController().navigate(R.id.action_activeQueueScannerFragment_to_navigation_queue)
        } else {
            Toast.makeText(requireContext(), "Некорректный QR-сcode", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        scanner = ZBarScannerView(activity)

        return scanner
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