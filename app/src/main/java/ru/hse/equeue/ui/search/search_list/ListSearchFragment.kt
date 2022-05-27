package ru.hse.equeue.ui.search.list

import android.app.AppOpsManager
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import ru.hse.equeue.databinding.FragmentSearchListBinding
import ru.hse.equeue.ui.queues.ActiveQueueViewModel
import ru.hse.equeue.ui.search.SearchViewModel
import ru.hse.equeue.ui.search.search_list.SearchListViewModel
import ru.hse.equeue.ui.search.search_list.SearchQueuesAdapter

class ListSearchFragment : Fragment() {

    private val searchViewModel: SearchViewModel by activityViewModels()
    private val queueViewModel: ActiveQueueViewModel by activityViewModels()
    private val searchQueuesModel: SearchListViewModel by activityViewModels()

    private var _binding: FragmentSearchListBinding? = null
    private lateinit var adapter: SearchQueuesAdapter

    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchListBinding.inflate(inflater, container, false)
        adapter = SearchQueuesAdapter(findNavController(), queueViewModel)
        searchViewModel.searchPageQueue("")
        val layoutManager = LinearLayoutManager(context)
        binding.searchListQueue.layoutManager = layoutManager
        binding.searchListQueue.adapter = adapter
        adapter.queus = searchQueuesModel.queue
        searchQueueEvent()
        return binding.root
    }

    private fun searchQueueEvent() {
        searchViewModel.searchQueuesResult.observe(viewLifecycleOwner) {
            it.onSuccess { result ->
                searchQueuesModel.setQueues(result.content)
                adapter.notifyDataSetChanged()
            }.onFailure {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.editSearch.requestFocus()
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE)  as InputMethodManager
        imm.showSoftInput(binding.editSearch, InputMethodManager.SHOW_IMPLICIT);
        binding.editSearch.doAfterTextChanged {
            searchViewModel.searchPageQueue(it.toString())
        }
    }

}

