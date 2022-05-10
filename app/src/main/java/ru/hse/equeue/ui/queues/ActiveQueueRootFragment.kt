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
import ru.hse.equeue.databinding.FragmentActiveQueueRootBinding
import ru.hse.equeue.ui.profile.ProfileViewModel

class ActiveQueueRootFragment : Fragment() {

    private var _binding: FragmentActiveQueueRootBinding? = null
    private val activeQueueViewModel: ActiveQueueViewModel by activityViewModels()
    private val profileViewModel: ProfileViewModel by activityViewModels();

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentActiveQueueRootBinding.inflate(inflater, container, false)
        activeQueueViewModel.getActiveQueue(profileViewModel.user.value?.id!!)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val ft = parentFragmentManager.beginTransaction()
        val activeQueue: NotActiveQueueFragment = NotActiveQueueFragment()
        ft.replace(R.id.active_queue_root, activeQueue)
        ft.commit()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}