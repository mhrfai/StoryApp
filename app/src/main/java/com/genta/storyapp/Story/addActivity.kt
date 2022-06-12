package com.genta.storyapp.Story

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.genta.storyapp.Data.API.ApiConfig
import com.genta.storyapp.Data.Response.MsgGetStory
import com.genta.storyapp.Model.UserPreference
import com.genta.storyapp.View.Login.LoginActivity
import com.genta.storyapp.View.Main.MainActivity
import com.genta.storyapp.ViewModelFactory
import com.genta.storyapp.databinding.ActivityAddBinding
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class addActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddBinding
    private var getFile: File? = null
    private lateinit var token : String
    private lateinit var viewModel: addViewModel

    companion object {
        const val EXTRA_TOKEN = "token"
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        token = intent.getStringExtra(EXTRA_TOKEN).toString()
        val mBundle = Bundle()

        mBundle.putString(EXTRA_TOKEN,token)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)



        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
        viewModel= ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[addViewModel::class.java]

        viewModel._getUser().observe(this,{user->
            if (user.isLogin){
                Log.d("token ku saat login: ",user.token)
                supportActionBar?.title = user.name
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        })

        binding.cameraXButton.setOnClickListener {
            setKamera()
        }
        binding.galleryButton.setOnClickListener {
            setGaleri()
        }
        binding.uploadButton.setOnClickListener {
            uploadImage()
        }
        setUpview()


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun setUpview() {
        supportActionBar?.title = "Post Story"
    }

    fun setKamera() {
        val intent = Intent(this@addActivity, cameraActivity::class.java)
        launcherIntentCameraX.launch(intent)

    }
    fun setGaleri(){
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Pilih Gambar")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            getFile = myFile
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean
            val result = rotateBitmap(
                    BitmapFactory.decodeFile(myFile.path),
                    isBackCamera
            )

            binding.previewImageView.setImageBitmap(result)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this)
            getFile = myFile
            binding.previewImageView.setImageURI(selectedImg)
        }
    }

    private fun uploadImage() {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)
            val description = binding.inputDeskripsi.text.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )
            val service = ApiConfig.getApiService().postStory("Bearer $token",description,imageMultipart)
            service.enqueue(object : Callback<MsgGetStory>{
                override fun onResponse(
                    call: Call<MsgGetStory>,
                    response: Response<MsgGetStory>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null && !responseBody.error) {
                            Toast.makeText(this@addActivity, "Upload Berhasil", Toast.LENGTH_SHORT).show()
                            Intent(this@addActivity, MainActivity::class.java).let {
                                startActivity(it)
                            }
                        }
                    } else {
                        Toast.makeText(this@addActivity, "Upload Gagal", Toast.LENGTH_SHORT).show()
                        Log.d("token ku : ",token)
                    }

                }
                override fun onFailure(call: Call<MsgGetStory>, t: Throwable) {
                    Toast.makeText(this@addActivity, "Periksa jaringan anda", Toast.LENGTH_SHORT).show()

                }

            })


        } else {
            Toast.makeText(this@addActivity, "Silakan masukkan berkas gambar terlebih dahulu.", Toast.LENGTH_SHORT).show()
        }

    }
}

