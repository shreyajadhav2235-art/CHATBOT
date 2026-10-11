package com.example.chatbot1.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;

public class SketchDialog {

    public interface OnSketchSendListener {
        void onSketchReady(Bitmap sketchBitmap, String description);
    }

    public static void show(Context context, OnSketchSendListener listener) {
        if (context == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("✏️ ML Sketch & Diagram Canvas");

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(24, 16, 24, 16);

        SketchView sketchView = new SketchView(context);
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 600);
        sketchView.setLayoutParams(viewParams);

        LinearLayout btnLayout = new LinearLayout(context);
        btnLayout.setOrientation(LinearLayout.HORIZONTAL);

        Button btnClear = new Button(context);
        btnClear.setText("Clear");
        btnClear.setOnClickListener(v -> sketchView.clear());

        Button btnEraser = new Button(context);
        btnEraser.setText("Eraser");
        btnEraser.setOnClickListener(v -> sketchView.toggleEraser());

        btnLayout.addView(btnClear);
        btnLayout.addView(btnEraser);

        layout.addView(sketchView);
        layout.addView(btnLayout);

        builder.setView(layout);

        builder.setPositiveButton("Send Sketch", (dialog, which) -> {
            Bitmap bmp = sketchView.getBitmap();
            if (listener != null && bmp != null) {
                listener.onSketchReady(bmp, "Hand-drawn ML Diagram / Equation");
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    static class SketchView extends View {
        private final Paint paint;
        private final Path path;
        private Bitmap bitmap;
        private Canvas canvas;
        private boolean isEraser = false;

        public SketchView(Context context) {
            super(context);
            path = new Path();
            paint = new Paint();
            paint.setColor(Color.parseColor("#29251F"));
            paint.setAntiAlias(true);
            paint.setStrokeWidth(6.0f);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeCap(Paint.Cap.ROUND);
            setBackgroundColor(Color.parseColor("#FFF9F0"));
        }

        public void clear() {
            if (canvas != null) {
                canvas.drawColor(Color.parseColor("#FFF9F0"));
                invalidate();
            }
        }

        public void toggleEraser() {
            isEraser = !isEraser;
            if (isEraser) {
                paint.setColor(Color.parseColor("#FFF9F0"));
                paint.setStrokeWidth(24.0f);
            } else {
                paint.setColor(Color.parseColor("#29251F"));
                paint.setStrokeWidth(6.0f);
            }
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            if (w > 0 && h > 0) {
                bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                canvas = new Canvas(bitmap);
                canvas.drawColor(Color.parseColor("#FFF9F0"));
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (bitmap != null) {
                canvas.drawBitmap(bitmap, 0, 0, null);
            }
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(x, y);
                    return true;
                case MotionEvent.ACTION_MOVE:
                    path.lineTo(x, y);
                    break;
                case MotionEvent.ACTION_UP:
                    if (canvas != null) {
                        canvas.drawPath(path, paint);
                    }
                    path.reset();
                    break;
                default:
                    return false;
            }
            invalidate();
            return true;
        }
    }
}