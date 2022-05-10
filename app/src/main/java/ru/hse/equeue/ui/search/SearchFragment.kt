package ru.hse.equeue.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.hse.equeue.R
import ru.hse.equeue.databinding.FragmentSearchBinding
import ru.hse.equeue.ui.search.queue.QueueViewModel


class SearchFragment : Fragment() {

    private val searchViewModel: SearchViewModel by activityViewModels()
    private val queueViewModel: QueueViewModel by activityViewModels()

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findNavController().navigate(R.id.action_navigation_search_to_mapSearchFragment2, null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}