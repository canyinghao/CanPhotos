package com.canyinghao.canphotos.preview;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import com.facebook.drawee.drawable.DrawableUtils;


public class ProgeressDrawble extends Drawable {

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int mBackgroundColor = 0x80000000;
    private int mColor = 0x800080FF;
    private int mPadding = 10;
    private int mBarWidth = 20;
    private int mLevel = 0;
    private boolean mHideWhenZero = false;


    /**
     * @param color
     */
    public void setColor(int color) {
        if (mColor != color) {
            mColor = color;
            invalidateSelf();
        }
    }



    public int getColor() {
        return mColor;
    }


    /**
     * @param backgroundColor
     */
    public void setBackgroundColor(int backgroundColor) {
        if (mBackgroundColor != backgroundColor) {
            mBackgroundColor = backgroundColor;
            invalidateSelf();
        }
    }


    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    /** Sets the progress bar padding. */
    public void setPadding(int padding) {
        if (mPadding != padding) {
            mPadding = padding;
            invalidateSelf();
        }
    }


    @Override
    public boolean getPadding(Rect padding) {
        padding.set(mPadding, mPadding, mPadding, mPadding);
        return mPadding != 0;
    }


    /**
     * @param barWidth
     */
    public void setBarWidth(int barWidth) {
        if (mBarWidth != barWidth) {
            mBarWidth = barWidth;
            invalidateSelf();
        }
    }



    public int getBarWidth() {
        return mBarWidth;
    }


    /**
     * @param hideWhenZero
     */
    public void setHideWhenZero(boolean hideWhenZero) {
        mHideWhenZero = hideWhenZero;
    }



    public boolean getHideWhenZero() {
        return mHideWhenZero;
    }

    @Override
    protected boolean onLevelChange(int level) {
        mLevel = level;
        invalidateSelf();
        return true;
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return DrawableUtils.getOpacityFromColor(mPaint.getColor());
    }

    @Override
    public void draw(Canvas canvas) {
        if (mHideWhenZero && mLevel == 0) {
            return;
        }
        drawBar(canvas, 500, mBackgroundColor);
        drawBar(canvas, mLevel/20, mColor);
    }

    /**
     * @param canvas
     * @param level
     * @param color
     */
    private void drawBar(Canvas canvas, int level, int color) {

        int length = level;

        mPaint.setColor(color);
        canvas.drawRect(20, 20, 20 + length, 20 + mBarWidth, mPaint);
    }
}