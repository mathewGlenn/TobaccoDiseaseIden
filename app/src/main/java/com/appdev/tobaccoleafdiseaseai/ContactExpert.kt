package com.appdev.tobaccoleafdiseaseai

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*


lateinit var mutableBitmap: Bitmap

val APP_TAG = "crop"
var intermediateName = "1.jpg"
var resultName = "2.jpg"
var intermediateProvider: Uri? = null
var resultProvider: Uri? = null
var cameraActivityResultLauncher: ActivityResultLauncher<Intent>? = null
var galleryActivityResultLauncher: ActivityResultLauncher<Intent>? = null
var cropActivityResultLauncher: ActivityResultLauncher<Intent>? = null

class ContactExpert : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_expert)

        val imageView: ImageView = findViewById(R.id.imageView)
        val btnComposeEmail: Button = findViewById(R.id.btnComposeEmail)

        val expertInfo: TextView = findViewById(R.id.expertInfo)
        val reachExpert: TextView = findViewById(R.id.reachExpert)

        val preferences: SharedPreferences =
            this.getSharedPreferences("LANGUAGE", Context.MODE_PRIVATE)
        val language = preferences.getString("SAVED_LANGUAGE", "eng")
        val localeHelper = LocaleHelper()
        val context = localeHelper.setLocale(this, language.toString())
        val resources = context.resources

        expertInfo.text = resources.getString(R.string.expert_description)
        reachExpert.text = resources.getString(R.string.reach_expert)

        val messageToExpert: EditText = findViewById(R.id.editText1)
        val name: EditText = findViewById(R.id.editTextName)
        val phoneNum: EditText = findViewById(R.id.editTextPhoneNum)
        val address: EditText = findViewById(R.id.editTextAddress)

        var message = messageToExpert.text.toString()

        val options = arrayOf("Open Camera", "Choose from Gallery")
        val window: AlertDialog.Builder = AlertDialog.Builder(this)
        window.setTitle("Chose a photo to send to the expert")
        window.setItems(options) { dialog, which ->
            when (which) {
                0 -> {
                    onLaunchCamera()
                }
                1 -> {
                    onPickPhoto()
                }
                else -> {
                    //theres an error in what was selected
                    Toast.makeText(applicationContext,
                        "Hmmm I messed up. I detected that you clicked on : $which?",
                        Toast.LENGTH_LONG)
                        .show()
                }
            }
        }

        btnComposeEmail.setOnClickListener {

            val messageExtra =
                messageToExpert.text.toString() + "\n\n\n" + name.text.toString() + "\n" + address.text.toString() + "\n" + phoneNum.text.toString()

            val i = Intent(Intent.ACTION_SEND)
            i.type = "application/image"
            i.putExtra(Intent.EXTRA_EMAIL, arrayOf("nta_isabela@yahoo.com"))
            i.putExtra(Intent.EXTRA_SUBJECT, "report")
            i.putExtra(Intent.EXTRA_TEXT, messageExtra)
            i.putExtra(Intent.EXTRA_STREAM, resultProvider)

            startActivity(Intent.createChooser(i, "Send mail..."))
        }

        imageView.setOnClickListener {
            window.show()
        }

        cameraActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                onCropImage()
            }
        }
        galleryActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                saveBitmapFileToIntermediate(result.data!!.data)
                onCropImage()
            }
        }

        cropActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val cropImage = loadFromUri(resultProvider)
                val imgBitmap = getResizedBitmap(cropImage, 500)
                //imgBitmap is immutable so I copy it to a mutable
                //bitmap so we can use the bitmap for image processing
//                mutableBitmap = imgBitmap.copy(Bitmap.Config.ARGB_8888, true)
                mutableBitmap = imgBitmap
                //Image.Image = mutableBitmap
                imageView.setImageBitmap(imgBitmap)


            }
        }
    }


    fun onLaunchCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile = getPhotoFileUri(intermediateName)
        intermediateProvider =
            FileProvider.getUriForFile(
                this,
                "com.photostream.crop.fileprovider.tobbacoleafdiseaseidentifier",
                photoFile
            )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, intermediateProvider)
        if (intent.resolveActivity(packageManager) != null) {
            cameraActivityResultLauncher!!.launch(intent)
        }
    }

    // Trigger gallery selection for a photo
    fun onPickPhoto() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (intent.resolveActivity(packageManager) != null) {
            galleryActivityResultLauncher!!.launch(intent)
        }
    }

    private fun onCropImage() {
        grantUriPermission(
            "com.android.camera",
            intermediateProvider,
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
        )

        val intent = Intent("com.android.camera.action.CROP")
        intent.setDataAndType(intermediateProvider, "image/*")

        val list = packageManager.queryIntentActivities(intent, 0)

        grantUriPermission(
            list[0].activityInfo.packageName,
            intermediateProvider,
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION

        )

        val size: Int = list.size
        if (size == 0) {
            Toast.makeText(this, "Error: No image taken!", Toast.LENGTH_SHORT).show()
        } else {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            intent.putExtra("crop", "true")
            intent.putExtra("aspectX", 1)
            intent.putExtra("aspectY", 1)
            intent.putExtra("scale", true);
            val photoFile = getPhotoFileUri(resultName)
            // wrap File object into a content provider
            // required for API >= 24
            // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
            resultProvider = FileProvider.getUriForFile(
                this,
                "com.photostream.crop.fileprovider.tobbacoleafdiseaseidentifier",
                photoFile
            )
            intent.putExtra("return-data", false)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, resultProvider)
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
            val cropIntent = Intent(intent)
            val res = list[0]
            cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            cropIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            grantUriPermission(
                res.activityInfo.packageName,
                resultProvider,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            cropIntent.component =
                ComponentName(res.activityInfo.packageName, res.activityInfo.name)
            cropActivityResultLauncher!!.launch(cropIntent)
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    fun getPhotoFileUri(fileName: String): File {
        val mediaStorageDir = File(getExternalFilesDir(""), APP_TAG)
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(APP_TAG, "failed to create directory")
        }
        return File(mediaStorageDir.path + File.separator + fileName)
    }

    fun loadFromUri(photoUri: Uri?): Bitmap? {
        var image: Bitmap? = null
        try {
            image = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                // on newer versions of Android, use the new decodeBitmap method
                val source = ImageDecoder.createSource(
                    this.contentResolver,
                    photoUri!!
                )
                ImageDecoder.decodeBitmap(source)
            } else {
                // support older versions of Android by using getBitmap
                MediaStore.Images.Media.getBitmap(this.contentResolver, photoUri)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return image
    }

    private fun saveBitmapFileToIntermediate(sourceUri: Uri?) {
        try {
            val bitmap = loadFromUri(sourceUri)
            val imageFile = getPhotoFileUri(intermediateName)
            intermediateProvider =
                FileProvider.getUriForFile(
                    this,
                    "com.photostream.crop.fileprovider.tobbacoleafdiseaseidentifier",
                    imageFile
                )
            val out: OutputStream = FileOutputStream(imageFile)
            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getResizedBitmap(image: Bitmap?, maxSize: Int): Bitmap {
        var width = image!!.width
        var height = image.height
        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }

}