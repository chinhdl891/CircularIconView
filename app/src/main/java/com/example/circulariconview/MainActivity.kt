package com.example.circulariconview

import GifCreator.createGifFromFrames
import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.util.Util


class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val criCircularIconView = findViewById<CircularIconView>(R.id.circularIconView)

        val imageViewResult = findViewById<ImageView>(R.id.imv_result)
        findViewById<Button>(R.id.tv_update_widget).setOnClickListener {
            val bitmap = criCircularIconView.iconsBitmap
            createGifFromFrames(this, bitmap, "rotating_image.gif")

        }

        imageViewResult.setOnClickListener {
            BatteryOptimizationHelper.disableBatteryOptimization(this)
        }
    }
}