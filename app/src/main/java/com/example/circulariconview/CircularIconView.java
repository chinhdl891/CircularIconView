package com.example.circulariconview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.List;

public class CircularIconView extends View {

    private List<Integer> iconResIds = Arrays.asList(
            R.drawable.ic_chrome, R.drawable.ic_youtube, R.drawable.ic_snapchat,
            R.drawable.ic_discord, R.drawable.ic_tiktok, R.drawable.ic_tinder,
            R.drawable.ic_spotify, R.drawable.ic_google_maps
    );

    private Bitmap avatarBitmap;
    private List<Bitmap> iconBitmaps;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint avatarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float rotationAngle = 0f; // Góc quay hiện tại của icon
    private ValueAnimator rotationAnimator;

    public CircularIconView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        loadBitmaps();
        setupRotationAnimation();
    }

    private void loadBitmaps() {
        avatarBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.avatar);
        iconBitmaps = new java.util.ArrayList<>();
        for (int resId : iconResIds) {
            iconBitmaps.add(BitmapFactory.decodeResource(getResources(), resId));
        }
    }

    private void setupRotationAnimation() {
        rotationAnimator = ValueAnimator.ofFloat(0, 360);
        rotationAnimator.setDuration(5000); // Quay trong 5 giây
        rotationAnimator.setRepeatCount(ValueAnimator.INFINITE);
        rotationAnimator.setInterpolator(new android.view.animation.LinearInterpolator());
        rotationAnimator.addUpdateListener(animation -> {
            rotationAngle = (float) animation.getAnimatedValue();
            invalidate();
        });
        rotationAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        float radius = Math.min(getWidth(), getHeight()) / 3f;

        // Vẽ ảnh đại diện hình tròn
        float avatarSize = radius;
        Bitmap circleAvatar = getCircularBitmap(avatarBitmap, (int) avatarSize);
        canvas.drawBitmap(circleAvatar, centerX - avatarSize / 2, centerY - avatarSize / 2, avatarPaint);

        // Vẽ các icon theo vòng tròn và xoay
        float angleStep = 360f / iconBitmaps.size();
        float iconSize = radius * 0.4f;

        canvas.save();
        canvas.rotate(rotationAngle, centerX, centerY); // Xoay toàn bộ nhóm icon

        for (int i = 0; i < iconBitmaps.size(); i++) {
            double angle = Math.toRadians(angleStep * i);
            float iconX = (float) (centerX + radius * Math.cos(angle)) - iconSize / 2;
            float iconY = (float) (centerY + radius * Math.sin(angle)) - iconSize / 2;

            RectF iconRect = new RectF(iconX, iconY, iconX + iconSize, iconY + iconSize);
            canvas.drawBitmap(iconBitmaps.get(i), null, iconRect, paint);
        }

        canvas.restore();
    }

    // Tạo avatar hình tròn
    private Bitmap getCircularBitmap(Bitmap bitmap, int size) {
        Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, size, size);

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);

        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(Bitmap.createScaledBitmap(bitmap, size, size, false), rect, rect, paint);

        return output;
    }

    // Xử lý chạm để xoay icon
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (rotationAnimator.isRunning()) {
                rotationAnimator.pause(); // Dừng xoay khi chạm vào
            } else {
                rotationAnimator.start(); // Tiếp tục xoay nếu đang dừng
            }
            return true;
        }
        return super.onTouchEvent(event);
    }
}
