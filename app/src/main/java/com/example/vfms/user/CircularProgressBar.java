package com.example.vfms.user;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.example.vfms.R;

public class CircularProgressBar extends View {
    private Paint circlePaint;
    private Paint progressPaint;
    private int progress;

    public CircularProgressBar(Context context) {
        super(context);
        init();
    }

    public CircularProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(10); // Adjust the stroke width as needed
        circlePaint.setColor(getResources().getColor(R.color.circle_color)); // Customize the circle color

        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(10); // Same as circlePaint
        progressPaint.setColor(getResources().getColor(R.color.progress_color)); // Customize the progress color
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int radius = Math.min(centerX, centerY);

        // Draw the outer circle
        canvas.drawCircle(centerX, centerY, radius, circlePaint);

        // Calculate the angle based on the progress (0-360 degrees)
        int angle = (int) (360 * ((float) progress / 100));

        // Draw the progress arc
        canvas.drawArc(
                centerX - radius,
                centerY - radius,
                centerX + radius,
                centerY + radius,
                -90, // Start angle (top)
                angle, // Sweep angle
                false,
                progressPaint
        );
    }

    public void setProgress(int progress) {
        this.progress = progress;
        invalidate(); // Redraw the view when the progress changes
    }
}
