package ru.hse.equeue.ui.queues

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.hse.equeue.R
import ru.hse.equeue.databinding.FragmentNotActiveQueueBinding
import ru.hse.equeue.ui.profile.ProfileViewModel

class NotActiveQueueFragment : Fragment() {

    private var _binding: FragmentNotActiveQueueBinding? = null
    private val activeQueueViewModel: ActiveQueueViewModel by activityViewModels()
    private val profileViewModel: ProfileViewModel by activityViewModels();

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotActiveQueueBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBinding()
        observeQueueLoaded()
    }

    private fun observeQueueLoaded() {
        activeQueueViewModel.queue.observe(viewLifecycleOwner) {
            val ft = parentFragmentManager.beginTransaction()
            val activeQueue: ActiveQueueFragment = ActiveQueueFragment()
            ft.replace(R.id.active_queue_root, activeQueue)
            ft.commit()
        }
    }

    private fun initBinding() {
        binding.activeQueueScanQr.setOnClickListener {
            val intent = Intent(activity, ActiveQueueScannerActivity::class.java)
            startActivity(intent)
        }
        binding.goToSearchScreen.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_queue_to_navigation_search)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}