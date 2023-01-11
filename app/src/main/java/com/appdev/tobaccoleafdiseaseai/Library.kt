package com.appdev.tobaccoleafdiseaseai

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.appdev.tobaccoleafdiseaseai.databinding.ActivityLibraryBinding

class Library : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLibraryBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)

        val cardALS = binding.cardALS
        val cardBSH = binding.cardBSH
        val cardBS = binding.cardBS
        val cardLC = binding.cardLC
        val cardPM = binding.cardPM
        val cardWF = binding.cardWF

        cardALS.setOnClickListener {
            val intent = Intent(this, LibraryInfo::class.java)
            intent.putExtra("disease", "ALS")
            startActivity(intent)
        }

        cardBSH.setOnClickListener {
            val intent = Intent(this, LibraryInfo::class.java)
            intent.putExtra("disease", "BSH")
            startActivity(intent)
        }

        cardBS.setOnClickListener {
            val intent = Intent(this, LibraryInfo::class.java)
            intent.putExtra("disease", "BS")
            startActivity(intent)
        }

        cardLC.setOnClickListener {
            val intent = Intent(this, LibraryInfo::class.java)
            intent.putExtra("disease", "LC")
            startActivity(intent)
        }

        cardPM.setOnClickListener {
            val intent = Intent(this, LibraryInfo::class.java)
            intent.putExtra("disease", "PM")
            startActivity(intent)
        }

        cardWF.setOnClickListener {
            val intent = Intent(this, LibraryInfo::class.java)
            intent.putExtra("disease", "WF")
            startActivity(intent)
        }
    }
}