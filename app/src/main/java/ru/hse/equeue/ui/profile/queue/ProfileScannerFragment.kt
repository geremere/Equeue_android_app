package ru.hse.equeue.ui.profile.queue

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import me.dm7.barcodescanner.zbar.Result
import me.dm7.barcodescanner.zbar.ZBarScannerView
import ru.hse.equeue.R
import ru.hse.equeue.ui.profile.ProfileViewModel

class ProfileScannerFragment : Fragment(), ZBarScannerView.ResultHandler {

    private val profileModel: ProfileViewModel by activityViewModels()
    private lateinit var scanner: ZBarScannerView


    override fun handleResult(res: Result?) {
        if (res != null) {
            profileModel.serve(res?.contents.toString())
            findNavController().navigate(R.id.action_profileScannerFragment_to_profileQueueFragment)
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