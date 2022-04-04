package com.example.ecarrier

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
    }

    override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            requireActivity().findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE
            onLoginSuccessful()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loginBtn = view.findViewById<Button>(R.id.btn_login)!!
        loginBtn.setOnClickListener {
            var isValid = true

            val emailField = view.findViewById<EditText>(R.id.et_username)
            if (emailField.text.isNullOrEmpty()) {
                emailField.error = "Email not specified."

                isValid = false
            }

            val passwordField = view.findViewById<EditText>(R.id.et_password)
            if (passwordField.text.isNullOrEmpty()) {
                passwordField.error = "Password not specified."

                isValid = false
            }

            if (!isValid) return@setOnClickListener

            tryLogin(emailField.text.toString(), passwordField.text.toString())
        }
    }

    fun tryLogin(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) onLoginSuccessful() else onLoginFailed()
            }
    }

    fun onLoginSuccessful() {
        UserState.currentUser = auth.currentUser!!

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("123", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            Log.d("123", token)
//            Toast.makeText(requireContext(), token, Toast.LENGTH_SHORT).show()

            lifecycleScope.launch(Dispatchers.IO) {
                val t = withContext(Dispatchers.Default) {
                    Tasks.await(UserState.currentUser.getIdToken(true))
                }

                UserState.userToken = t.token!!

                val user = HttpClient.eCarrierApi.createUser("Bearer " + t.token!!, CreateUserRequest(UserState.currentUser.displayName, token))?.execute()

                UserState.userId = user?.body()?.id

                withContext(Dispatchers.Main){
                    findNavController().navigate(R.id.action_loginFragment_to_trackMissionFragment)
                }
            }
        })
    }

    fun onLoginFailed() {
        Toast.makeText(
            context, "Authentication failed.",
            Toast.LENGTH_LONG
        ).show()
    }
}