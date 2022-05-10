package ru.hse.equeue

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

import ru.hse.equeue.databinding.AuthActivityBinding
import ru.hse.equeue.network.settings.Singletons

class GoogleAuthActivity : AppCompatActivity() {

    private lateinit var binding: AuthActivityBinding

    override fun onStart() {
        super.onStart()
        Singletons.init(applicationContext)

        val account: GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null) {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken("608288448751-cgjuabq7onf4oi1jg9d8hh6gdq4oma1k.apps.googleusercontent.com")
                .build()
            val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, 200)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = AuthActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        findViewById<Button>(R.id.google_signIn).setOnClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken("608288448751-cgjuabq7onf4oi1jg9d8hh6gdq4oma1k.apps.googleusercontent.com")
                .build()
            val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, 200)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 200) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                var client = task.getResult(ApiException::class.java)
                val intent = Intent(applicationContext, MainActivity::class.java)
                intent.putExtra("gToken", client.idToken)
                startActivity(intent)
            } catch (e: ApiException) {
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}