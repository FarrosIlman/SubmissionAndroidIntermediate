package com.parrosz.storyu.posting

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.parrosz.storyu.data.Result
import com.parrosz.storyu.MainActivity
import com.parrosz.storyu.R
import com.parrosz.storyu.databinding.ActivityPostingBinding
import com.parrosz.storyu.login.LoginActivity
import com.parrosz.storyu.utils.AppExecutors
import com.parrosz.storyu.utils.reduceFileImage
import com.parrosz.storyu.utils.rotateBitmap
import java.io.File
import java.io.FileOutputStream


class PostingActivity : AppCompatActivity() {
    private val binding: ActivityPostingBinding by lazy {
        ActivityPostingBinding.inflate(layoutInflater)
    }

    private val postViewModel: PostingViewModel by viewModels {
        PostingViewModel.PostingViewModelFactory.getInstance(this)
    }

    private val appExecutor: AppExecutors by lazy {
        AppExecutors()
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var location: Location? = null
    private var file: File? = null
    private var isBack: Boolean = true
    private var reducingDone: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        supportActionBar?.setDisplayShowHomeEnabled(true)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()
        bindResult()
        setupButtons()
    }

    override fun onResume() {
        super.onResume()
        checkIfSessionValid()
    }

    private fun checkIfSessionValid() {
        postViewModel.getToken().observe(this) {
            if (it == "null") {
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }

    private fun setupButtons() {
        binding.btnPost.setOnClickListener {
            postViewModel.getToken().observe(this) {
                if (reducingDone) {
                    if (it == "null") {
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    } else {
                        uploadImage("Bearer $it")
                    }
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.wait_for_processing),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun bindResult() {
        file = intent.getSerializableExtra(PHOTO_RESULT_EXTRA) as File
        isBack = intent.getBooleanExtra(IS_CAMERA_BACK_EXTRA, true)

        val result = rotateBitmap(BitmapFactory.decodeFile((file as File).path), isBack)
        result.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(file))

        appExecutor.diskIO.execute {
            file = reduceFileImage(file as File)
            reducingDone = true
        }


        binding.imgStory.setImageBitmap(result)
    }

    private fun uploadImage(token: String) {
        if (binding.etDescription.text.isNullOrEmpty()) {
            Toast.makeText(this, getString(R.string.description_cannot_empty), Toast.LENGTH_SHORT)
                .show()
        } else {
            if (file != null) {
                binding.progressBar.visibility = View.VISIBLE
                val description = binding.etDescription.text.toString()

                val result = postViewModel.postStory(
                    token,
                    file as File,
                    description,
                    location?.latitude?.toFloat(),
                    location?.longitude?.toFloat()
                )

                result.observe(this) {
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
                            Toast.makeText(
                                this,
                                getString(R.string.image_posted),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getLastLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getLastLocation()
                }
            }
        }

    private fun getLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) && checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                this.location = location
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        const val PHOTO_RESULT_EXTRA = "photo_result_extra"
        const val IS_CAMERA_BACK_EXTRA = "is_camera_back_extra"
    }
}