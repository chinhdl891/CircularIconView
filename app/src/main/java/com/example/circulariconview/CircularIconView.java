package com.example.circulariconview;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;

import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.MotionEvent;

import java.util.List;

import kotlin.Pair;

public class CircularIconView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private List<AppItem> appItems;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint avatarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Bitmap avatarBitmap;
    private Thread drawThread;
    private boolean isRunning = false;
    private float rotationAngle = 0f;
    private float rotationSpeed = 2.5f; // Điều chỉnh tốc độ xoay
    private OnItemClickListener itemClickListener;

    public CircularIconView(Context context) {
        super(context);
        getHolder().addCallback(this);
        avatarBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.avatar);
    }

    public CircularIconView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);

        // Bật chế độ trong suốt
        setZOrderOnTop(true);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);

        avatarBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.avatar);
    }


    // Đăng ký danh sách icon động
    public void setAppItems(List<AppItem> appItems) {
        this.appItems = appItems;
    }

    // Interface xử lý sự kiện click
    public interface OnItemClickListener {
        void onItemClick(AppItem item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    // Xử lý chạm vào icon
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();

            for (AppItem app : appItems) {
                if (app.bounds.contains(x, y)) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(app);
                    }
                    return true;
                }
            }

            // Chạm vào khoảng trống sẽ dừng hoặc tiếp tục xoay
            isRunning = !isRunning;
            if (isRunning) {
                startDrawing();
            }
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isRunning = true;
        startDrawing();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isRunning = false;
        try {
            if (drawThread != null) {
                drawThread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void startDrawing() {
        drawThread = new Thread(this);
        drawThread.start();
    }

    @Override
    public void run() {
        while (isRunning) {
            long startTime = System.currentTimeMillis();

            // Vẽ lại SurfaceView
            drawSurface();

            long timeDiff = System.currentTimeMillis() - startTime;
            long sleepTime = Math.max(16 - timeDiff, 0); // Giữ FPS khoảng 60
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void drawSurface() {
        SurfaceHolder holder = getHolder();
        Canvas canvas = holder.lockCanvas();
        if (canvas == null) return;

        // Xóa màn hình
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);


        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        float radius = Math.min(getWidth(), getHeight()) / 3f;

        // Vẽ avatar ở giữa
        float avatarSize = radius;
        Bitmap circleAvatar = getCircularBitmap(avatarBitmap, (int) avatarSize);
        canvas.drawBitmap(circleAvatar, centerX - avatarSize / 2, centerY - avatarSize / 2, avatarPaint);

        // Vẽ icon xoay xung quanh
        float angleStep = 360f / appItems.size();
        float iconSize = radius * 0.4f;

        for (int i = 0; i < appItems.size(); i++) {
            AppItem app = appItems.get(i);
            double angle = Math.toRadians(angleStep * i + rotationAngle);
            float iconX = (float) (centerX + radius * Math.cos(angle)) - iconSize / 2;
            float iconY = (float) (centerY + radius * Math.sin(angle)) - iconSize / 2;

            // Cập nhật vị trí icon
            app.bounds.set(iconX, iconY, iconX + iconSize, iconY + iconSize);

            // Vẽ icon
            canvas.drawBitmap(app.icon, null, app.bounds, paint);
        }

        // Xoay tiếp
        rotationAngle += rotationSpeed;
        if (rotationAngle >= 360) rotationAngle -= 360;

        holder.unlockCanvasAndPost(canvas);
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
    public Pair<Bitmap, Bitmap> getIconsAndAvatarBitmaps() {
        return new Pair<>(getListIconBitmap(), getAvatarBitmap());
    }

    private Bitmap getListIconBitmap() {
        int size = Math.min(getWidth(), getHeight()); // Kích thước phù hợp với màn hình
        Bitmap listBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(listBitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        float centerX = size / 2f;
        float centerY = size / 2f;
        float radius = size / 3f;
        float iconSize = radius * 0.4f;
        float angleStep = 360f / appItems.size();

        for (int i = 0; i < appItems.size(); i++) {
            AppItem app = appItems.get(i);
            double angle = Math.toRadians(angleStep * i + rotationAngle);
            float iconX = (float) (centerX + radius * Math.cos(angle)) - iconSize / 2;
            float iconY = (float) (centerY + radius * Math.sin(angle)) - iconSize / 2;

            RectF bounds = new RectF(iconX, iconY, iconX + iconSize, iconY + iconSize);
            canvas.drawBitmap(app.icon, null, bounds, paint);
        }

        return listBitmap;
    }


    private Bitmap getAvatarBitmap() {
        int size = Math.min(getWidth(), getHeight()); // Kích thước bằng với listIcon
        Bitmap avatarCanvasBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(avatarCanvasBitmap);

        // Avatar nhỏ hơn (ví dụ: chiếm 1/3 kích thước)
        int avatarSize = size / 3;
        Bitmap circularAvatar = getCircularBitmap(avatarBitmap, avatarSize);

        float centerX = (size - avatarSize) / 2f;
        float centerY = (size - avatarSize) / 2f;

        // Vẽ avatar vào chính giữa
        canvas.drawBitmap(circularAvatar, centerX, centerY, null);

        return avatarCanvasBitmap;
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Không cần xử lý
    }
}
