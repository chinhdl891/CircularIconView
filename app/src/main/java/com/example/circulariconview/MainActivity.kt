package com.example.circulariconview

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val appItems: MutableList<AppItem> = ArrayList()
        appItems.add(
            AppItem(
                "com.android.chrome",
                "Chrome",
                BitmapFactory.decodeResource(resources, R.drawable.ic_chrome)
            )
        )
        appItems.add(
            AppItem(
                "com.discord",
                "Discord",
                BitmapFactory.decodeResource(resources, R.drawable.ic_discord)
            )
        )
        appItems.add(
            AppItem(
                "com.google.android.apps.maps", "Google Maps", BitmapFactory.decodeResource(
                    resources, R.drawable.ic_google_maps
                )
            )
        )
        appItems.add(
            AppItem(
                "com.snapchat.android",
                "Snapchat",
                BitmapFactory.decodeResource(resources, R.drawable.ic_snapchat)
            )
        )
        appItems.add(
            AppItem(
                "com.spotify.music",
                "Spotify",
                BitmapFactory.decodeResource(resources, R.drawable.ic_spotify)
            )
        )
        appItems.add(
            AppItem(
                "com.zhiliaoapp.musically",
                "TikTok",
                BitmapFactory.decodeResource(resources, R.drawable.ic_tiktok)
            )
        )
        appItems.add(
            AppItem(
                "com.tinder",
                "Tinder",
                BitmapFactory.decodeResource(resources, R.drawable.ic_tinder)
            )
        )
        appItems.add(
            AppItem(
                "com.google.android.youtube",
                "YouTube",
                BitmapFactory.decodeResource(resources, R.drawable.ic_youtube)
            )
        )


// Gán danh sách vào CircularIconView
        val circularView = findViewById<CircularIconView>(R.id.circularIconView)
        circularView.setAppItems(appItems)


// Xử lý khi click vào icon
        circularView.setOnItemClickListener { appItem: AppItem ->
            Toast.makeText(this, "Clicked on: " + appItem.appName, Toast.LENGTH_SHORT).show()
            openApp(appItem.packageName)
        }

        val btnExport = findViewById<Button>(R.id.btn_export)
        val imvList = findViewById<ImageView>(R.id.imv_result_list)
        val imvAvatar = findViewById<ImageView>(R.id.imv_result_avatar)

        btnExport.setOnClickListener {
            val bitmaps: Pair<Bitmap, Bitmap> = circularView.iconsAndAvatarBitmaps
            val listIconBitmap = bitmaps.first
            val avatarBitmap = bitmaps.second
            imvList.setImageBitmap(listIconBitmap)
            imvAvatar.setImageBitmap(avatarBitmap)

        }
    }

    private fun openApp(packageName: String) {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        if (intent != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "App not installed", Toast.LENGTH_SHORT).show()
        }
    }

}