package ru.hse.equeue.ui.queues

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.squareup.picasso.Picasso
import ru.hse.equeue.databinding.FragmentActiveQueueBinding

class ActiveQueueFragment : Fragment() {

    private var _binding: FragmentActiveQueueBinding? = null
    private val activeQueueViewModel: ActiveQueueViewModel by activityViewModels()

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentActiveQueueBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBinding()
    }

    private fun initBinding() {
        if(activeQueueViewModel.queue.value != null){
            val queue = activeQueueViewModel.queue.value!!

            binding.activeQueueNameTextViewItem.text = queue.name
            binding.addressActiveQueueTextViewItem.text = "Адрес: " + queue.address
//            binding.countOfPeopleActiveQueueTextViewItem.text =
//                "Место в очереди: " + queue.countOfPeople.toString()
//            binding.waitingTimeActiveQueueTextViewItem.text =
//                "Среднее время ожидания: " + String.format(
//                    "%.1f",
//                    ((queue.averageWaitingTime * queue.countOfPeople) / 60)
//                ) + " мин."
            Picasso.get().load(queue.photoUrl).into(binding.photoActiveQueueImageViewItem)
            qrCode(queue.id.toLong())
        }
    }


    private val multiFormatWriter: MultiFormatWriter = MultiFormatWriter()

    private fun qrCode(id: Long) {
        val bitMatrix: BitMatrix =
            multiFormatWriter.encode(id.toString(), BarcodeFormat.QR_CODE, 720, 720)
        val barcodeEncoder: BarcodeEncoder = BarcodeEncoder()
        val bitmap: Bitmap = barcodeEncoder.createBitmap(bitMatrix)
        binding.userQrCode.setImageBitmap(bitmap)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}