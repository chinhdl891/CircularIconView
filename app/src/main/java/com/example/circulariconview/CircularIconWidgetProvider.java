package com.example.circulariconview;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.example.circulariconview.R;

import java.util.ArrayList;
import java.util.List;

public class CircularIconWidgetProvider extends AppWidgetProvider {

    private static final String TAG = "WidgetCircular";
    private static final int FRAME_DELAY = 16; // 10 FPS (100ms mỗi frame)
    private final List<Bitmap> gifFrames = new ArrayList<>();
    private int currentFrame = 0;
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "onUpdate: Widget cập nhật!");

        for (int appWidgetId : appWidgetIds) {
            loadGifFrames(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    private void loadGifFrames(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        Glide.with(context)
                .asGif()
                .load(R.drawable.rotating_image)  // 🔥 Thay bằng GIF của bạn
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .listener(new RequestListener<GifDrawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                        Log.e(TAG, "❌ Lỗi tải GIF");
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                        extractFrames(resource, context, appWidgetManager, appWidgetId);
                        return false;
                    }
                })
                .submit();
    }

    private void extractFrames(GifDrawable gifDrawable, Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        gifFrames.clear();
        gifDrawable.start();
        int frameCount = gifDrawable.getFrameCount();

        for (int i = 0; i < frameCount; i++) {
            gifDrawable.setVisible(true, true);
            gifDrawable.setAlpha(255); // Đảm bảo hiển thị đúng màu
            Bitmap frame = Bitmap.createBitmap(
                    gifDrawable.getIntrinsicWidth(),
                    gifDrawable.getIntrinsicHeight(),
                    Bitmap.Config.ARGB_8888
            );

            Canvas canvas = new Canvas(frame);
            gifDrawable.draw(canvas);
            gifFrames.add(frame);
        }

        Log.d(TAG, "✅ Đã trích xuất " + gifFrames.size() + " frame từ GIF");
        startFrameAnimation(context, appWidgetManager, appWidgetId);
    }

    private void startFrameAnimation(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (!gifFrames.isEmpty()) {
                    RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_circular_icon);
                    remoteViews.setImageViewBitmap(R.id.imv_capture, gifFrames.get(currentFrame));

                    appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
                    currentFrame = (currentFrame + 1) % gifFrames.size();
                    handler.postDelayed(this, FRAME_DELAY);
                }
            }
        });
    }
}
