package ru.hse.equeue.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.hse.equeue.R
import ru.hse.equeue.databinding.FragmentProfileQueueNotExistBinding

class ProfileWithOutQueueFragment : Fragment() {

    private var _binding: FragmentProfileQueueNotExistBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileQueueNotExistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.createQueue.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_profile_to_profileCreateQueueFragment, null)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}