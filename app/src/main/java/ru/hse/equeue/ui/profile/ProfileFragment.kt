package ru.hse.equeue.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.squareup.picasso.Picasso
import ru.hse.equeue.GoogleAuthActivity
import ru.hse.equeue.R
import ru.hse.equeue.databinding.FragmentProfileUserInfoBinding
import ru.hse.equeue.network.settings.Singletons

class ProfileFragment : Fragment() {

    private var _bindingProfile: FragmentProfileUserInfoBinding? = null
    private val profileModel: ProfileViewModel by activityViewModels()

    private val binding get() = _bindingProfile!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bindingProfile = FragmentProfileUserInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBinding()
        userLoadedEvent()
    }

    private fun initBinding() {
        binding.logout.setOnClickListener {
            Singletons.appSettings.setCurrentToken("")
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken("608288448751-cgjuabq7onf4oi1jg9d8hh6gdq4oma1k.apps.googleusercontent.com")
                .build()
            val mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
            mGoogleSignInClient.signOut()
            val intent = Intent(requireContext(), GoogleAuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun userLoadedEvent() {
        profileModel.userResponse.observe(viewLifecycleOwner) { result ->
            result
                .onSuccess { user ->
                    profileModel.setUser(user)
                    Picasso.get().load(user.photoUrl).into(binding.profileImage);
                    binding.profileName.text = user.name
                    binding.profileEmail.text = user.email
                    val ft = parentFragmentManager.beginTransaction()
                    if (user.queue == null) {
                        val withOutQueueFragment: ProfileWithOutQueueFragment =
                            ProfileWithOutQueueFragment()
                        ft.replace(R.id.profile_queue_container, withOutQueueFragment)
                        ft.commit()
                    } else {
                        val withQueueFragment = ProfileWithQueueFragment()
                        ft.replace(R.id.profile_queue_container, withQueueFragment)
                        ft.commit()
                    }
                }
                .onFailure {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _bindingProfile = null
    }
}