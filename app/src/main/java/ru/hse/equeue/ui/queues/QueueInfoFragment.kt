package ru.hse.equeue.ui.queues


import android.os.Bundle
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

class QueueInfoFragment : Fragment() {

    private val queueViewModel: ActiveQueueViewModel by activityViewModels()

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
            if (queueViewModel.queue.value == null) {
                queueViewModel.standToQueue(currentQueue.id)
                findNavController().navigate(R.id.action_queueFragment_to_navigation_queue)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Вы уже находитесь в другой очереди",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

}
