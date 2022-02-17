package com.example.dds

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
    }

    override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser
        if (currentUser != null) {
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
        findNavController().navigate(R.id.action_loginFragment_to_trackMissionFragment)
    }

    fun onLoginFailed() {
        Toast.makeText(
            context, "Authentication failed.",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }
}