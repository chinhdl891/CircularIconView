package com.example.circulariconview;

import android.graphics.Bitmap;
import android.graphics.RectF;

public class AppItem {
    public String packageName;
    public String appName;
    public int icon;
    public RectF bounds; // Vị trí icon trên màn hình

    public AppItem(String packageName, String appName, int icon) {
        this.packageName = packageName;
        this.appName = appName;
        this.icon = icon;
        this.bounds = new RectF();
    }
}
