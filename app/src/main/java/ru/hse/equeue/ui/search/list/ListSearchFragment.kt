package ru.hse.equeue.ui.search.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import ru.hse.equeue.R
import ru.hse.equeue.databinding.FragmentSearchListBinding
import ru.hse.equeue.ui.queues.ActiveQueueViewModel
import ru.hse.equeue.ui.search.SearchViewModel

class ListSearchFragment : Fragment() {

    private val searchViewModel: SearchViewModel by activityViewModels()
    private val queueViewModel: ActiveQueueViewModel by activityViewModels()


    private var _binding: FragmentSearchListBinding? = null
    private lateinit var adapter: QueuesAdapter

    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        searchViewModel.cleanQueues()
        searchViewModel.getPageQueues()
        getQueueByPageEvent()
        _binding = FragmentSearchListBinding.inflate(inflater, container, false)
        adapter = QueuesAdapter(findNavController(), queueViewModel, searchViewModel)
        val layoutManager = LinearLayoutManager(context)
        binding.searchListQueue.layoutManager = layoutManager
        binding.searchListQueue.adapter = adapter
        adapter.queus = searchViewModel.queue
        return binding.root
    }

    private fun getQueueByPageEvent() {
        searchViewModel.pageQueueResult.observe(viewLifecycleOwner) {
            it.onSuccess { result ->
                searchViewModel.addQueues(result.content)
                adapter.notifyDataSetChanged()
            }.onFailure {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toMapButton.setOnClickListener {
            findNavController().navigate(R.id.action_listSearchFragment_to_mapSearchFragment)
        }
    }

}

