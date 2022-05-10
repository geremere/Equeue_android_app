package ru.hse.equeue.ui.profile.queue

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.hse.equeue.R
import ru.hse.equeue.databinding.FragmentProfileCreateQueueBinding
import ru.hse.equeue.model.QueueStatus
import ru.hse.equeue.ui.profile.ProfileViewModel
import java.io.IOException
import java.util.*


class ProfileCreateQueueFragment : Fragment() {

    private var _binding: FragmentProfileCreateQueueBinding? = null
    private val createQueueModel: CreateQueueModel by activityViewModels()
    private val profileModel: ProfileViewModel by activityViewModels()

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileCreateQueueBinding.inflate(inflater, container, false)
        checkPermissions()
        return binding.root
    }

    private fun checkPermissions() {
        if (
            ContextCompat.checkSelfPermission(
                requireActivity().applicationContext,
                Manifest.permission.MANAGE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                requireActivity().applicationContext,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                requireActivity().applicationContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            activity?.let {
                ActivityCompat.requestPermissions(
                    it,
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.MANAGE_EXTERNAL_STORAGE
                    ),
                    1234
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        queueCreateEvent()
        binding.queueAddressInput.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH
                || actionId == EditorInfo.IME_ACTION_DONE
                || keyEvent.action == KeyEvent.ACTION_DOWN
                || keyEvent.action == KeyEvent.KEYCODE_ENTER
            ) {
                getAddress(textView.text.toString())
                true
            } else {
                false
            }
        }
        binding.queueNameInput.addTextChangedListener {
            createQueueModel.queue.value?.name = it.toString()
        }
        binding.queueServiceTimeInput.addTextChangedListener {
            createQueueModel.queue.value?.status = QueueStatus(
                currentUsersCount = 0,
                serviceTime = it.toString().toDouble(),
                totalUsersCount = 1,
                status = "OnPause"
            )
        }
        binding.createQueueImage.setOnClickListener {
            val intent: Intent = Intent()
            intent.setType("image/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(intent, 1)
        }
        binding.createQueueButton.setOnClickListener {
            if (binding.createQueueImage.tag != null) {
                createQueueModel.queue.value?.owner = profileModel.user.value!!
                createQueueModel.createQueue(binding.createQueueImage.tag.toString())
            }
        }

    }

    private fun queueCreateEvent() {
        createQueueModel.queueCreated.observe(viewLifecycleOwner) {
            profileModel.getUser()
            findNavController().navigate(R.id.action_profileCreateQueueFragment_to_navigation_profile)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && data != null && data.data != null) {
            if (resultCode == RESULT_OK) {
                binding.createQueueImage.setImageURI(data.data)
                binding.createQueueImage.setTag(ImageFilePath.getPath(requireContext(), data.data))
            }
        }
        if (requestCode == 1234) {
            return
        }
    }

    private fun getAddress(searchString: String) {
        val geocoder: Geocoder = Geocoder(requireContext(), Locale.getDefault())
        var addresses: List<Address> = listOf()
        try {
            addresses = geocoder.getFromLocationName(searchString, 5)
            binding.queueAddressInput.setText(addresses.get(0).getAddressLine(0))
            createQueueModel.queue.value?.address = binding.queueAddressInput.text.toString()
            createQueueModel.queue.value?.x = addresses.get(0).latitude
            createQueueModel.queue.value?.y = addresses.get(0).longitude
        } catch (e: IOException) {
            Toast.makeText(requireContext(), "something went wrong", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}