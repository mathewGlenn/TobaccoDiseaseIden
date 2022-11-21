package com.appdev.tobaccoleafdiseaseai

import android.app.Dialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.appdev.tobaccoleafdiseaseai.Image.Companion.Image
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.FileProvider
import com.appdev.tobaccoleafdiseaseai.databinding.ActivityMain2Binding
import com.appdev.tobaccoleafdiseaseai.databinding.ActivityMainBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream


class MainActivity : AppCompatActivity() {

    private lateinit var classifier: Classifier
    lateinit var mutableBitmap: Bitmap

    val APP_TAG = "crop"
    var intermediateName = "1.jpg"
    var resultName = "2.jpg"
    var intermediateProvider: Uri? = null
    var resultProvider: Uri? = null
    var cameraActivityResultLauncher: ActivityResultLauncher<Intent>? = null
    var galleryActivityResultLauncher: ActivityResultLauncher<Intent>? = null
    var cropActivityResultLauncher: ActivityResultLauncher<Intent>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMain2Binding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)

        val preferences: SharedPreferences =
            this.getSharedPreferences("LANGUAGE", Context.MODE_PRIVATE)
        val language = preferences.getString("SAVED_LANGUAGE", "eng")
        val localeHelper = LocaleHelper()
        val context = localeHelper.setLocale(this, language.toString())
        val resources = context.resources

        binding.cardLibrary.setOnClickListener{
            startActivity(Intent(this, Library::class.java))
        }

        binding.cardExpert.setOnClickListener{
            startActivity(Intent(this, ContactExpert::class.java))
        }


        val utils = Utils()
        classifier = Classifier(utils.assetFilePath(this, "model.pt"))

        val takePhotoCard: CardView = binding.cardIdentify
        val settingsCard: CardView = binding.cardSettings

        val alert = Dialog(this)
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alert.setContentView(R.layout.dialog)
        val dialogTitle: TextView = alert.findViewById(R.id.dialogTitle)
        val dialogOpenCam: TextView = alert.findViewById(R.id.dOpenCamera)
        val dialogOpenGallery: TextView = alert.findViewById(R.id.dOpenGallery)
        val cardOpenCam: CardView = alert.findViewById(R.id.dCardOpenCam)
        val cardOpenGallery: CardView = alert.findViewById(R.id.dCardOpenGallery)

        dialogTitle.text = resources.getString(R.string.take_photo)
        dialogOpenCam.text = resources.getString(R.string.open_cam)
        dialogOpenGallery.text = resources.getString(R.string.choose_gallery)

        cardOpenCam.setOnClickListener{
            onLaunchCamera()
            Thread.sleep(1_000)
            alert.dismiss()
        }
        cardOpenGallery.setOnClickListener{
            onPickPhoto()
            Thread.sleep(1_000)
            alert.dismiss()
        }


        takePhotoCard.setOnClickListener {
            alert.show()
        }
        binding.btnIdentify.setOnClickListener{
            alert.show()
        }

        settingsCard.setOnClickListener{
            startActivity(Intent(this, Settings::class.java))
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
                mutableBitmap = imgBitmap.copy(Bitmap.Config.ARGB_8888, true)
                Image = mutableBitmap

                //compute the inference time
                //val taskStartTime = System.currentTimeMillis()
                //val (predClass, predProb) = classifier.predict(mutableBitmap)
                val (
                    predClass, predProb,
                ) = classifier.predict(mutableBitmap)
                //val inferenceTime = System.currentTimeMillis() - taskStartTime

                if (predClass == "Unknown"){
                    val i = Intent(this, NoResult::class.java)
                    startActivity(i)
                }else{
                    val i = Intent(this, PredictionResult::class.java)
                    i.putExtra("predClass", predClass)
                    //for measuring the inference time
                    //Toast.makeText(this, "Inference time: $inferenceTime", Toast.LENGTH_LONG).show()
                    startActivity(i)
                }
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