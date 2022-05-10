package ru.hse.equeue.ui.search.queue

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.squareup.picasso.Picasso
import ru.hse.equeue.R
import ru.hse.equeue.databinding.FragmentSearchQueueBinding
import ru.hse.equeue.ui.queues.ActiveQueueViewModel


class QueueFragment : Fragment() {

    private val queueViewModel: QueueViewModel by activityViewModels()
    private val activeQueueModel:ActiveQueueViewModel by activityViewModels()

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
        val currentQueue = queueViewModel.getQueue()
        binding.addressQueueTextViewItem.text = "Адрес: " + currentQueue.address
        binding.queueNameTextViewItem.text = currentQueue.name
        binding.waitingTimeQueueTextViewItem.text = "Среднее время ожидания: " + String.format(
            "%.1f",
            ((currentQueue.averageWaitingTime * currentQueue.countOfPeople) / 60)
        ) + " мин."
        binding.countOfPeopleQueueTextViewItem.text =
            "Людей в очереди: " + currentQueue.countOfPeople.toString()
        binding.descriptionQueueTextViewItem.text =
            "someTextsomeTextsomeTextsomeTextsomeTextsomeTextsomeTextsomeTextsomeTextsomeTextsomeTextsomeTextsomeTextsomeTextsomeTextsomeTextsomeTextsomeTextsomeTextsomeTextsomeTextsomeTextsomeTextsomeTextsomeTextsomeTextsomeTextsomeTextsomeTextsomeTextsomeTextsomeTextsomeTextsomeTextsomeText"
        Picasso.get().load(currentQueue.photoUrl).into(binding.photoQueueImageViewItem)

        binding.setQueueButton.setOnClickListener {
            activeQueueModel.setQueue(currentQueue)
            findNavController().navigate(R.id.action_queueFragment_to_navigation_queue)
        }

    }

}

