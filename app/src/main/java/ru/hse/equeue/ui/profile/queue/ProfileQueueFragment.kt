package ru.hse.equeue.ui.profile.queue

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.squareup.picasso.Picasso
import ru.hse.equeue.databinding.FragmentProfileQueueBinding
import ru.hse.equeue.ui.profile.ProfileScannerActivity
import ru.hse.equeue.ui.profile.ProfileViewModel
import ru.hse.equeue.ui.util.ScannerActivity

class ProfileQueueFragment : Fragment() {

    private var _binding: FragmentProfileQueueBinding? = null
    private val profileModel: ProfileViewModel by activityViewModels()

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
        binding.scanQrCode.setOnClickListener {
            val intent = Intent(activity, ProfileScannerActivity::class.java)
            startActivity(intent)
        }
        binding.skipPerson.setOnClickListener {
            Toast.makeText(requireContext(),"Person set in queue", Toast.LENGTH_SHORT).show()
        }
        observeQueue()
    }

    private fun observeQueue() {
        profileModel.user.observe(viewLifecycleOwner) {
            val queue = it.queue
            binding.queueNameTextViewItem.text = queue.name
            binding.addressQueueTextViewItem.text = "Адрес: " + queue.address
            binding.countOfPeopleQueueTextViewItem.text =
                "Место в очереди: " + queue.countOfPeople.toString()
            binding.descriptionQueueTextViewItem.text = queue.name
            binding.waitingTimeQueueTextViewItem.text =
                "Среднее время ожидания: " + String.format(
                    "%.1f",
                    ((queue.averageWaitingTime * queue.countOfPeople) / 60)
                ) + " мин."
            Picasso.get().load(queue.photoUrl).into(binding.photoQueueImageViewItem)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}