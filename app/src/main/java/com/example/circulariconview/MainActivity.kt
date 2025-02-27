package com.example.circulariconview

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.circulariconview.GifCreator.saveFrameToFile


class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val criCircularIconView = findViewById<CircularIconView>(R.id.circularIconView)

        val imageViewResult = findViewById<ImageView>(R.id.imv_result)
        findViewById<Button>(R.id.tv_update_widget).setOnClickListener {
            val bitmapAvt = criCircularIconView.exportAvatarBitmap()
            val bitmapRotate = criCircularIconView.iconsBitmap
            saveFrameToFile(this, bitmapAvt, 0);
            saveFrameToFile(this, bitmapRotate, 1);
        }

        imageViewResult.setOnClickListener {
            BatteryOptimizationHelper.disableBatteryOptimization(this)
        }
    }
}