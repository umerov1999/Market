package com.f0x1d.store.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.style.ReplacementSpan;

import com.f0x1d.store.App;

public class RoundedBackgroundSpan extends ReplacementSpan {
    private static final float MAGIC_NUMBER = ((float) dpToPx(2));
    private static final float PADDING_X = ((float) dpToPx(12));
    private static final float PADDING_Y = ((float) dpToPx(2));
    private final int mBackgroundColor;
    private final int mTextColor;
    private final float mTextSize;

    public RoundedBackgroundSpan(int i, int i2, float f) {
        this.mBackgroundColor = i;
        this.mTextColor = i2;
        this.mTextSize = f;
    }

    public static int dpToPx(int i) {
        return Math.round(((float) i) * (App.getInstance().getResources().getDisplayMetrics().xdpi / 160.0f));
    }

    public void draw(Canvas canvas, CharSequence charSequence, int i, int i2, float f, int i3, int i4, int i5, Paint paint) {
        Paint paint2 = new Paint(paint);
        paint2.setTextSize(this.mTextSize);
        paint2.setColor(this.mBackgroundColor);
        float dpToPx = (float) dpToPx(2);
        float f3 = (float) i3;
        float f4 = PADDING_Y;
        float f5 = f3 + dpToPx + f4 + this.mTextSize + f4 + dpToPx;
        canvas.drawRoundRect(new RectF(f, f3, ((float) getTagWidth(charSequence, i, i2, paint2)) + f, f5), 12.0f, 12.0f, paint2);
        paint2.setColor(this.mTextColor);
        canvas.drawText(charSequence, i, i2, f + PADDING_X, ((f5 - PADDING_Y) - dpToPx) - MAGIC_NUMBER, paint2);
    }

    private int getTagWidth(CharSequence charSequence, int i, int i2, Paint paint) {
        return Math.round(PADDING_X + paint.measureText(charSequence.subSequence(i, i2).toString()) + PADDING_X);
    }

    public int getSize(Paint paint, CharSequence charSequence, int i, int i2, Paint.FontMetricsInt fontMetricsInt) {
        Paint paint2 = new Paint(paint);
        paint2.setTextSize(this.mTextSize);
        return getTagWidth(charSequence, i, i2, paint2);
    }
}
