package ru.hse.equeue.ui.search.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import ru.hse.equeue.R
import ru.hse.equeue.databinding.FragmentSearchListBinding
import ru.hse.equeue.ui.search.SearchViewModel
import ru.hse.equeue.ui.search.queue.QueueViewModel

class ListSearchFragment : Fragment() {

    private val searchViewModel: SearchViewModel by activityViewModels()
    private val queueViewModel: QueueViewModel by activityViewModels()


    private var _binding: FragmentSearchListBinding? = null
    private lateinit var adapter: QueuesAdapter

    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSearchListBinding.inflate(inflater, container, false)
        adapter = QueuesAdapter(findNavController(),queueViewModel)
        val layoutManager = LinearLayoutManager(context)
        binding.searchListQueue.layoutManager = layoutManager
        binding.searchListQueue.adapter = adapter
        adapter.queus = searchViewModel.getQueues()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toMapButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_navigation_search_to_mapSearchFragment2,
                null
            )
        }
    }

}

