package ru.hse.equeue.ui.profile.queue

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
import ru.hse.equeue.databinding.FragmentProfileQueueBinding
import ru.hse.equeue.ui.profile.ProfileViewModel
import java.io.ByteArrayOutputStream


class ProfileQueueFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private var _binding: FragmentProfileQueueBinding? = null
    private val profileModel: ProfileViewModel by activityViewModels()
    private val selectedMap = mapOf<String, Int>("ACTIVE" to 0, "ON_PAUSE" to 1, "CLOSED" to 2)

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileQueueBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBinding()
        queueChangeEvent()
        changeStatusEvent()
        serveEvent()
    }

    private fun changeStatusEvent() {
        profileModel.changeStatusResult.observe(viewLifecycleOwner) {
            it.onSuccess { result ->
                profileModel.getUser()
            }.onFailure { error ->
                Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun serveEvent() {
        profileModel.serveClientResult.observe(viewLifecycleOwner) {event->
            event.getContentIfNotHandled()?.let {result->
                result.onSuccess { content ->
                    profileModel.getUser()
                }.onFailure { error ->
                    Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    private fun initBinding() {
        binding.scanQrCode.setOnClickListener {
           findNavController().navigate(R.id.action_profileQueueFragment_to_profileScannerFragment)
        }
        binding.skipPerson.setOnClickListener {
            profileModel.serve(null)
        }
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.statusNames,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.changeStatus.adapter = adapter
        }
        binding.changeStatus.onItemSelectedListener = this
        binding.shareButton.setOnClickListener {
            val shareIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(
                    Intent.EXTRA_STREAM,
                    generateCode(
                        profileModel.user.value!!.queue.id,
                        profileModel.user.value!!.queue.name
                    )
                )
                type = "image/jpeg"
            }
            startActivity(Intent.createChooser(shareIntent, null))
        }
    }


    private fun generateCode(id: Long, name: String): Uri {
        val multiFormatWriter: MultiFormatWriter = MultiFormatWriter()
        val bitMatrix: BitMatrix =
            multiFormatWriter.encode(id.toString(), BarcodeFormat.QR_CODE, 480, 480)
        val barcodeEncoder: BarcodeEncoder = BarcodeEncoder()
        val bitmap: Bitmap = barcodeEncoder.createBitmap(bitMatrix)
        var bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            requireContext().contentResolver,
            bitmap,
            name,
            "Qr-code of ${name} queue"
        )
        return Uri.parse(path)
    }

    private fun queueChangeEvent() {
        profileModel.user.observe(viewLifecycleOwner) {
            val queue = it.queue
            binding.queueNameTextViewItem.text = queue.name
            binding.addressQueueTextViewItem.text = "Адрес: " + queue.address
            binding.countOfPeopleQueueTextViewItem.text =
                "Людей в очереди: " + queue.usersQueue.size
            binding.waitingTimeQueueTextViewItem.text =
                "Среднее время ожидания: " + String.format(
                    "%.1f",
                    queue.status.serviceTime / 60
                ) + " мин."
            Picasso.get().load(queue.photoUrl).into(binding.photoQueueImageViewItem)
            binding.changeStatus.setSelection(selectedMap[queue.status.status]!!)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemSelected(adapter: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        var selectedItem: String = adapter!!.adapter.getItem(pos).toString()
        if (profileModel.user.value != null &&
            !selectedItem.uppercase()
                .equals(profileModel.user.value!!.queue.status.status.uppercase())
        ) {
            if (pos == 1)
                selectedItem = "ON_PAUSE"
            profileModel.changeStatus(selectedItem)
        }
    }

    override fun onNothingSelected(adapter: AdapterView<*>?) {
        //Nothing To Do
    }

}