package ru.hse.equeue.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.squareup.picasso.Picasso
import ru.hse.equeue.R
import ru.hse.equeue.databinding.FragmentProfileUserQueueExistBinding

class ProfileWithQueueFragment : Fragment() {

    private var _binding: FragmentProfileUserQueueExistBinding? = null
    private val profileModel: ProfileViewModel by activityViewModels()

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileUserQueueExistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBinding()
    }

    private fun initBinding() {
        binding.queueByOwner.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_profile_to_profileQueueFragment)
        }
        binding.queueName.text = profileModel.user.value?.queue?.name
        binding.queueAddress.text = profileModel.user.value?.queue?.address
        binding.queueStatus.text = profileModel.user.value?.queue?.status?.status
        Picasso.get().load(profileModel.user.value?.queue?.photoUrl).into(binding.queueImage);
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}