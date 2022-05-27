package ru.hse.equeue.ui.search.queue

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.squareup.picasso.Picasso
import ru.hse.equeue.R
import ru.hse.equeue.databinding.FragmentSearchQueueBinding
import ru.hse.equeue.ui.profile.ProfileViewModel
import ru.hse.equeue.ui.queues.ActiveQueueViewModel

class QueueInfoFragment : Fragment() {

    private val queueViewModel: ActiveQueueViewModel by activityViewModels()
    private val profileModel:ProfileViewModel by activityViewModels()

    private var _binding: FragmentSearchQueueBinding? = null

    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSearchQueueBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun standToQueueEvent() {
        queueViewModel.standToQueueResult.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let {
                it.onSuccess { result ->
                    findNavController().navigate(R.id.action_queueInfoFragment_to_navigation_queue)
                }.onFailure {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        standToQueueEvent()
        val currentQueue = queueViewModel.selectedQueue.value!!
        binding.addressQueueTextViewItem.text = "Адрес: " + currentQueue.address
        binding.queueNameTextViewItem.text = currentQueue.name
        binding.waitingTimeQueueTextViewItem.text = "Среднее время ожидания: " + String.format(
            "%.1f",
            currentQueue.status.currentUsersCount * currentQueue.status.serviceTime
        ) + " мин."
        binding.countOfPeopleQueueTextViewItem.text =
            "Людей в очереди: " + currentQueue.usersQueue.size
        Picasso.get().load(currentQueue.photoUrl).into(binding.photoQueueImageViewItem)

        binding.setQueueButton.setOnClickListener {
            queueViewModel.standToQueue(currentQueue.id)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
