package ru.mirea.ivashechkinav.mireaproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import ru.mirea.ivashechkinav.mireaproject.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.java.simpleName
    private lateinit var binding: ActivityLoginBinding

    // START declare_auth
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        mAuth = FirebaseAuth.getInstance()
        setContentView(binding.root)

        binding.btnSignIn.setOnClickListener {
            signIn(binding.etvEmail.text.toString(), binding.etvPassword.text.toString())
        }
        binding.verifyEmailButton.setOnClickListener {
            sendEmailVerification()
        }
        binding.btnCreateAccount.setOnClickListener {
            createAccount(binding.etvEmail.text.toString(), binding.etvPassword.text.toString())
        }
        binding.btnSignOut.setOnClickListener {
            signOut()
        }
    }

    // [START on_start_check_user]
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth.currentUser
        updateUI(currentUser)
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            startMainActivity()
            binding.statusTextView.setText(
                getString(
                    R.string.emailpassword_status_fmt,
                    user.email,
                    user.isEmailVerified
                )
            )
            binding.detailTextView.setText(getString(R.string.firebase_status_fmt, user.uid))
            binding.emailPasswordButtons.setVisibility(View.GONE)
            binding.emailPasswordFields.setVisibility(View.GONE)
            binding.signedInButtons.setVisibility(View.VISIBLE)
            binding.verifyEmailButton.setEnabled(!user.isEmailVerified)
        } else {
            binding.statusTextView.setText(R.string.signed_out)
            binding.detailTextView.setText(null)
            binding.emailPasswordButtons.setVisibility(View.VISIBLE)
            binding.emailPasswordFields.setVisibility(View.VISIBLE)
            binding.signedInButtons.setVisibility(View.GONE)
        }
    }

    private fun createAccount(email: String, password: String) {
        Log.d(TAG, "createAccount:$email")
        if (!validateForm()) {
            return
        }
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(
            this
        ) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "createUserWithEmail:success")
                val user = mAuth.currentUser
                updateUI(user)
            } else {
                // If sign in fails, display a message to the user.
                Log.w(
                    TAG, "createUserWithEmail:failure",
                    task.exception
                )
                Toast.makeText(this@LoginActivity, "Authentication failed.", Toast.LENGTH_SHORT)
                    .show()
                updateUI(null)
            }
        }
// [END create_user_with_email]
    }

    private fun signIn(email: String, password: String) {
        Log.d(TAG, "signIn:$email")
        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
            this
        ) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "signInWithEmail:success")
                val user = mAuth.currentUser
                updateUI(user)
            } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "signInWithEmail:failure", task.exception)
                Toast.makeText(this@LoginActivity, "Authenticationfailed.", Toast.LENGTH_SHORT)
                    .show()
                updateUI(null)
            }
            // [START_EXCLUDE]
            if (!task.isSuccessful) {
                binding.statusTextView.setText(R.string.auth_failed)
            }

            // [END_EXCLUDE]
        }
        // [END sign_in_with_email]
    }

    private fun signOut() {
        mAuth.signOut()
        updateUI(null)
    }

    private fun sendEmailVerification() {
// Disable button
        binding.verifyEmailButton.setEnabled(false)
        // Send verification email
// [START send_email_verification]
        val user = mAuth.currentUser ?: return
        user.sendEmailVerification().addOnCompleteListener(
            this
        ) { task ->
            // [START_EXCLUDE]
            // Re-enable button
            binding.verifyEmailButton.setEnabled(true)
            if (task.isSuccessful) {
                Toast.makeText(
                    this@LoginActivity,
                    "Verification email sent to " + user!!.email,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Log.e(TAG, "sendEmailVerification", task.exception)
                Toast.makeText(
                    this@LoginActivity,
                    "Failed to send verification email.",
                    Toast.LENGTH_SHORT
                ).show()
            }

            // [END_EXCLUDE]
        }
// [END send_email_verification]
    }

    private fun validateForm(): Boolean {
        return binding.etvEmail.text.toString() != "" && binding.etvPassword.text.toString() != ""
    }
}