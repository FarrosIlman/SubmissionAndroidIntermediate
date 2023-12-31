package com.parrosz.storyu.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.parrosz.storyu.data.Result
import com.parrosz.storyu.MainActivity
import com.parrosz.storyu.R
import com.parrosz.storyu.databinding.ActivityLoginBinding
import com.parrosz.storyu.onboarding.OnboardingActivity
import com.parrosz.storyu.register.RegisterActivity

class LoginActivity : AppCompatActivity() {
    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModel.LoginViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupView()
    }

    override fun onResume() {
        super.onResume()
        initialCheck()
    }

    private fun initialCheck() {
        loginViewModel.checkIfFirstTime().observe(this) {
            if (it) {
                val intent = Intent(this, OnboardingActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
    }

    private fun setupView() {
        supportActionBar?.setDisplayShowHomeEnabled(true)

        binding.btnLogin.setOnClickListener {
            if (!binding.etLayoutEmail.text.isNullOrEmpty() && !binding.etLayoutPassword.text.isNullOrEmpty()) {
                val email = binding.etLayoutEmail.text.toString()
                val password = binding.etLayoutPassword.text.toString()
                loginViewModel.login(email, password).observe(this) {
                    when (it) {
                        is Result.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }

                        is Result.Error -> {
                            binding.progressBar.visibility = View.INVISIBLE
                            val error = it.error
                            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                        }

                        is Result.Success -> {
                            binding.progressBar.visibility = View.INVISIBLE
                            val data = it.data
                            loginViewModel.saveToken(data.loginResult.token)
                            Log.d("LoginActivity", "Token: ${data.loginResult.token}")
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            } else {
                binding.etLayoutEmail.error = resources.getString(R.string.email_cannot_empty)

                if (binding.etLayoutPassword.text.isNullOrEmpty()) {
                    binding.etLayoutPassword.error =
                        resources.getString(R.string.password_minimum)
                }
            }
        }

        binding.btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}