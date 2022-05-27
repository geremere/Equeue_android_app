package ru.hse.equeue.ui.queues

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.squareup.picasso.Picasso
import ru.hse.equeue.R
import ru.hse.equeue.databinding.FragmentActiveQueueRootBinding
import ru.hse.equeue.ui.profile.ProfileViewModel

class ActiveQueueRootFragment : Fragment() {

    private var _binding: FragmentActiveQueueRootBinding? = null
    private val activeQueueViewModel: ActiveQueueViewModel by activityViewModels()
    private val profileViewModel: ProfileViewModel by activityViewModels();

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentActiveQueueRootBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activeQueueViewModel.getActiveQueue(profileViewModel.user.value?.id!!)
        queueLoadedEvent()
        initInactiveBinding()
        outFromQueueEvent()
        standToQueueEvent()
    }

    private fun outFromQueueEvent() {
        activeQueueViewModel.outFromQueueResult.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { result ->
                result.onSuccess {
                    binding.activeQueue.activeQueue.visibility = View.GONE
                    binding.notActiveQueue.notActiveQueue.visibility = View.VISIBLE

                }.onFailure { ex ->
                    Toast.makeText(requireContext(), ex.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initActiveBinding() {
        val queue = activeQueueViewModel.queue.value!!
        qrCode(profileViewModel.user.value!!.id)
        val placeInQueue = getPlaceInQueue() + 1
        binding.activeQueue.activeQueueNameTextViewItem.text = queue.name
        binding.activeQueue.addressActiveQueueTextViewItem.text = "Адрес: " + queue.address
        binding.activeQueue.countOfPeopleActiveQueueTextViewItem.text =
            "Место в очереди: " + placeInQueue
        binding.activeQueue.waitingTimeActiveQueueTextViewItem.text =
            "Среднее время ожидания: " + String.format(
                "%.1f",
                ((queue.status.serviceTime * placeInQueue) / 60)
            ) + " мин."
        Picasso.get().load(queue.photoUrl).into(binding.activeQueue.photoActiveQueueImageViewItem)
        binding.activeQueue.outFromQueueButton.setOnClickListener {
            activeQueueViewModel.outFromQueue()
        }
        binding.activeQueue.gotoMap.setOnClickListener {
            val gmmIntentUri: Uri =
                Uri.parse("geo:" + queue.x + "," + queue.y + "?q=" + queue.address)
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            if (mapIntent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(mapIntent)
            }
        }
    }

    private fun getPlaceInQueue(): Int {
        for (i in activeQueueViewModel.queue.value!!.usersQueue.indices) {
            if (activeQueueViewModel.queue.value!!.usersQueue[i].id.equals(profileViewModel.user.value!!.id)) {
                return i
            }
        }
        return -1
    }

    private val multiFormatWriter: MultiFormatWriter = MultiFormatWriter()

    private fun qrCode(id: String) {
        val bitMatrix: BitMatrix =
            multiFormatWriter.encode(id, BarcodeFormat.QR_CODE, 520, 520)
        val barcodeEncoder: BarcodeEncoder = BarcodeEncoder()
        val bitmap: Bitmap = barcodeEncoder.createBitmap(bitMatrix)
        binding.activeQueue.userQrCode.setImageBitmap(bitmap)
    }

    private fun initInactiveBinding() {
        binding.notActiveQueue.activeQueueScanQr.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_queue_to_activeQueueScannerFragment)
        }
        binding.notActiveQueue.goToSearchScreen.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_queue_to_navigation_search)
        }
    }

    private fun queueLoadedEvent() {
        activeQueueViewModel.queueResult.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { result ->
                result
                    .onSuccess { content ->
                        activeQueueViewModel.queue.value = content
                        binding.loading.loading.visibility = View.GONE
                        binding.activeQueue.activeQueue.visibility = View.VISIBLE
                        binding.notActiveQueue.notActiveQueue.visibility = View.GONE
                        initActiveBinding()
                    }
                    .onFailure {
                        binding.loading.loading.visibility = View.GONE
                        binding.activeQueue.activeQueue.visibility = View.GONE
                        binding.notActiveQueue.notActiveQueue.visibility = View.VISIBLE
                    }
            }
        }
    }

    private fun standToQueueEvent() {
        activeQueueViewModel.standToQueueResult.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { result ->
                result.onSuccess { content ->
                    activeQueueViewModel.queue.value = content
                    binding.loading.loading.visibility = View.GONE
                    binding.activeQueue.activeQueue.visibility = View.VISIBLE
                    binding.notActiveQueue.notActiveQueue.visibility = View.GONE
                    initActiveBinding()
                }.onFailure { ex ->
                    binding.loading.loading.visibility = View.GONE
                    binding.activeQueue.activeQueue.visibility = View.GONE
                    binding.notActiveQueue.notActiveQueue.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), ex.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}