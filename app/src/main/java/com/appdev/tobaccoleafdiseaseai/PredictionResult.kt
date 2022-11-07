package com.appdev.tobaccoleafdiseaseai

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.appdev.tobaccoleafdiseaseai.databinding.ActivityPredictionResultBinding

class PredictionResult : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityPredictionResultBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)

        val capturedImage: Bitmap? = Image.Image
        val predictedClass = intent.getStringExtra("predClass")

        val img1 : ImageView = findViewById(R.id.img1)
        val img2 : ImageView = findViewById(R.id.img2)
        val img3 : ImageView = findViewById(R.id.img3)
        val img4 : ImageView = findViewById(R.id.img4)
        val diseaseName: TextView = findViewById(R.id.diseaseName)
        val diseaseInfo: TextView = findViewById(R.id.diseaseInfo)
        val diseaseManagement: TextView = findViewById(R.id.diseaseManagement)


        img1.setImageBitmap(capturedImage)
        diseaseName.text = predictedClass

        val preferences: SharedPreferences =
            this.getSharedPreferences("LANGUAGE", Context.MODE_PRIVATE)
        val language = preferences.getString("SAVED_LANGUAGE", "eng")
        val localeHelper = LocaleHelper()
        val context = localeHelper.setLocale(this, language.toString())
        val resources = context.resources

        when(predictedClass){
            "Angular leaf spot" -> {
                img2.setImageResource(R.drawable.al1)
                img3.setImageResource(R.drawable.al2)
                img4.setImageResource(R.drawable.al3)

                diseaseInfo.text = resources.getString(R.string.als_info)
                diseaseManagement.text = resources.getString(R.string.als_mgmt)
            }
            "Black shank" -> {
                img2.setImageResource(R.drawable.bsh1)
                img3.setImageResource(R.drawable.bsh2)
                img4.setImageResource(R.drawable.bsh3)

                diseaseInfo.text = resources.getString(R.string.bsh_info)
                diseaseManagement.text = resources.getString(R.string.bsh_mgmt)
            }
            "Brown spot" -> {
                img2.setImageResource(R.drawable.bs1)
                img3.setImageResource(R.drawable.bs2)
                img4.setImageResource(R.drawable.bs3)

                diseaseInfo.text = resources.getString(R.string.bs_info)
                diseaseManagement.text = resources.getString(R.string.bs_mgmt)
            }
            "Leaf curl" -> {
                img2.setImageResource(R.drawable.lc1)
                img3.setImageResource(R.drawable.lc2)
                img4.setImageResource(R.drawable.lc3)

                diseaseInfo.text = resources.getString(R.string.lc_info)
                diseaseManagement.text = resources.getString(R.string.lc_mgmt)

            }
            "Powdery mildew" -> {
                img2.setImageResource(R.drawable.pm1)
                img3.setImageResource(R.drawable.pm2)
                img4.setImageResource(R.drawable.pm3)

                diseaseInfo.text = resources.getString(R.string.pm_info)
                diseaseManagement.text = resources.getString(R.string.pm_mgmt)
            }
            "Weather fleck" -> {
                img2.setImageResource(R.drawable.wf1)
                img3.setImageResource(R.drawable.wf2)
                img4.setImageResource(R.drawable.wf3)

                diseaseInfo.text = resources.getString(R.string.wf_info)
                diseaseManagement.text = resources.getString(R.string.wf_mgmt)
            }
        }

        val expert1: CardView = findViewById(R.id.expert1)

        expert1.setOnClickListener{
            startActivity(Intent(this, ContactExpert::class.java))
        }

        binding.contactExpertDescription.text = resources.getString(R.string.expert_description)


    }
}