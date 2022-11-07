package com.appdev.tobaccoleafdiseaseai

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.appdev.tobaccoleafdiseaseai.databinding.ActivityLibraryInfoBinding

class LibraryInfo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLibraryInfoBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)

        val preferences: SharedPreferences =
            this.getSharedPreferences("LANGUAGE", Context.MODE_PRIVATE)
        val language = preferences.getString("SAVED_LANGUAGE", "eng")
        val localeHelper = LocaleHelper()
        val context = localeHelper.setLocale(this, language.toString())
        val resources = context.resources

        val disease = intent.getStringExtra("disease").toString()

        when(disease){
            "ALS" -> {
                binding.diseaseName.text = "Angular Leaf Spot"
                binding.diseaseInfo.text = resources.getString(R.string.als_info)
                binding.diseaseManagement.text = resources.getString(R.string.als_mgmt)

                binding.img1.setImageResource(R.drawable.al1)
                binding.img2.setImageResource(R.drawable.al2)
                binding.img3.setImageResource(R.drawable.al3)

            }

            "BSH" -> {
                binding.diseaseName.text = "Black Shank"
                binding.diseaseInfo.text = resources.getString(R.string.bsh_info)
                binding.diseaseManagement.text = resources.getString(R.string.bsh_mgmt)

                binding.img1.setImageResource(R.drawable.bsh1)
                binding.img2.setImageResource(R.drawable.bsh2)
                binding.img3.setImageResource(R.drawable.bsh3)
            }

            "BS" -> {
                binding.diseaseName.text = "Brown Spot"
                binding.diseaseInfo.text = resources.getString(R.string.bs_info)
                binding.diseaseManagement.text = resources.getString(R.string.bs_mgmt)

                binding.img1.setImageResource(R.drawable.bs1)
                binding.img2.setImageResource(R.drawable.bs2)
                binding.img3.setImageResource(R.drawable.bs3)
            }

            "LC" -> {
                binding.diseaseName.text = "Leaf Curl"
                binding.diseaseInfo.text = resources.getString(R.string.lc_info)
                binding.diseaseManagement.text = resources.getString(R.string.lc_mgmt)

                binding.img1.setImageResource(R.drawable.lc1)
                binding.img2.setImageResource(R.drawable.lc2)
                binding.img3.setImageResource(R.drawable.lc3)
            }

            "PM" -> {
                binding.diseaseName.text = "Powdery Mildew"
                binding.diseaseInfo.text = resources.getString(R.string.pm_info)
                binding.diseaseManagement.text = resources.getString(R.string.pm_mgmt)

                binding.img1.setImageResource(R.drawable.pm1)
                binding.img2.setImageResource(R.drawable.pm2)
                binding.img3.setImageResource(R.drawable.pm3)
            }

            "WF" -> {
                binding.diseaseName.text = "Weather Fleck"
                binding.diseaseInfo.text = resources.getString(R.string.wf_info)
                binding.diseaseManagement.text = resources.getString(R.string.wf_mgmt)

                binding.img1.setImageResource(R.drawable.wf1)
                binding.img2.setImageResource(R.drawable.wf2)
                binding.img3.setImageResource(R.drawable.wf3)
            }
        }
    }
}