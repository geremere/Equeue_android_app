package ru.hse.equeue

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.hse.equeue.databinding.ActivityMainBinding
import ru.hse.equeue.network.settings.Singletons
import ru.hse.equeue.ui.profile.ProfileFragment
import ru.hse.equeue.ui.profile.ProfileViewModel
import ru.hse.equeue.ui.search.SearchFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val tokenModel: TokenViewModel by viewModels()
    private val profileModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tokenModel.getToken(intent.extras?.get("gToken").toString())

        binding = ActivityMainBinding.inflate(layoutInflater)
        if (Singletons.appSettings.getCurrentToken() != null && Singletons.appSettings.getCurrentToken()
                ?.isNotBlank() == true
        ) {
            setContentView(binding.root)
            val navView: BottomNavigationView = binding.navView

            val navController = findNavController(R.id.nav_host_fragment_activity_main)
            NavigationUI.setupWithNavController(navView, navController)
        }
        observeGetTokenEvent()

    }

    private fun observeGetTokenEvent() {
        tokenModel.token.observe(this) { result ->
            result
                .onSuccess {
                    Singletons.appSettings.setCurrentToken(it)
                    profileModel.getUser()
                    setContentView(binding.root)
                    val navView: BottomNavigationView = binding.navView

                    val navController = findNavController(R.id.nav_host_fragment_activity_main)
                    navView.setupWithNavController(navController)
                }
                .onFailure {
                    if (it.isNetworkError) {
                        tokenModel.getToken(intent.extras?.get("gToken").toString())
                    } else {
                        Toast.makeText(applicationContext, it.message, Toast.LENGTH_SHORT).show()
                    }
                }

        }
    }
}