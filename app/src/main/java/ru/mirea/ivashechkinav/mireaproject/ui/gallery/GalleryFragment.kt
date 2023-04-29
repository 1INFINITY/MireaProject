package ru.mirea.ivashechkinav.mireaproject.ui.gallery

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.mirea.ivashechkinav.mireaproject.databinding.FragmentGalleryBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class GalleryFragment : Fragment() {
    private val REQUEST_CODE_PERMISSION = 100
    private val CAMERA_REQUEST = 0
    private var isWork = false
    private var imageUri: Uri? = null
    private lateinit var binding: FragmentGalleryBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(GalleryViewModel::class.java)
        binding = FragmentGalleryBinding.inflate(inflater, container, false)
        initFileProvider()
        checkPermissions()
        initCamera()
        return binding.root
    }

    private fun initCamera() {
        val callback: ActivityResultCallback<ActivityResult?> =
            object : ActivityResultCallback<ActivityResult?> {
                override fun onActivityResult(result: ActivityResult?) {
                    if (result == null) return
                    if (result.resultCode === Activity.RESULT_OK) {
                        val data: Intent? = result.data
                        binding.imCriminalImage.setImageURI(imageUri)
                    }
                }
            }

        val cameraActivityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
            ActivityResultContracts.StartActivityForResult(),
            callback
        )
        binding.imCriminalImage.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            // проверка на наличие разрешений для камеры
            checkPermissions()
            if (true) {
                try {
                    val photoFile = createImageFile()
                    // генерирование пути к файлу на основе authorities
                    val authorities = requireContext().packageName + ".fileprovider"
                    imageUri = FileProvider.getUriForFile(
                        requireContext(), authorities,
                        photoFile!!
                    )
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                    cameraActivityResultLauncher.launch(cameraIntent)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
    private fun initFileProvider() {
        val photoFile = createImageFile()
        val authorities = requireContext().packageName + ".fileprovider"
        imageUri = FileProvider.getUriForFile(requireContext(), authorities, photoFile!!)
    }

    private fun checkPermissions() {
        val PERMISSIONS_REQUEST_CODE = 123
        val cameraPermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
        val storagePermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val hasPermissions = cameraPermission == PackageManager.PERMISSION_GRANTED && storagePermission == PackageManager.PERMISSION_GRANTED

        if (!hasPermissions) {
            isWork = true
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                PERMISSIONS_REQUEST_CODE
            )
        }
    }
    @Throws(IOException::class)
    private fun createImageFile(): File? {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
        val imageFileName = "IMAGE_" + timeStamp + "_"
        val storageDirectory: File? = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(imageFileName, ".jpg", storageDirectory)
    }
}