package com.mypopsy.simpleratingbar;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.Gravity;

/**
 * Created by Cerrato Renaud <renaud.cerrato@gmail.com>
 * https://github.com/renaudcerrato
 * 1/16/17
 */
class StarDrawable extends Drawable {

    private static final boolean DEBUG = false;

    static final int DEFAULT_FILL_COLOR = 0xFFFFD203;

    private final Rect mTmpRect = new Rect();
    private final Rect mStarBounds = new Rect();
    private final Path mStarPath = new Path();

    private final Paint mBorderPaint;
    private final Paint mFillPaint;
    private final Paint mDebugPaint;
    private boolean isDirty = true;

    private int mBorderWidth;
    private int mFillColor;
    private int mBorderColor;
    private float mCornerRadius;
    private int mGravity = Gravity.CENTER;
    private int mSize;

    StarDrawable() {

        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeJoin(Paint.Join.ROUND);
        mBorderPaint.setStrokeCap(Paint.Cap.ROUND);

        mFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mFillPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mFillPaint.setAntiAlias(true);
        mFillPaint.setDither(true);
        mFillPaint.setStrokeJoin(Paint.Join.ROUND);
        mFillPaint.setStrokeCap(Paint.Cap.ROUND);


        if(DEBUG) {
            mDebugPaint = new Paint();
            mDebugPaint.setStyle(Paint.Style.STROKE);
            mDebugPaint.setColor(Color.BLACK);
            mDebugPaint.setStrokeWidth(1);
        }else
            mDebugPaint = null;

        setBorderColor(0);
        setFillColor(DEFAULT_FILL_COLOR);
    }

    public void setSize(int size) {
        if(mSize != size) {
            mSize = size;
            invalidateSelf();
        }
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        isDirty = true;
    }

    @Override
    public int getIntrinsicHeight() {
        return mSize;
    }

    @Override
    public int getIntrinsicWidth() {
        return getIntrinsicHeight();
    }

    public void setGravity(int gravity) {
        if(mGravity != gravity) {
            mGravity = gravity;
            isDirty = true;
            invalidateSelf();
        }
    }

    public int getGravity() {
        return mGravity;
    }

    public void setCornerRadius(float radius) {
        CornerPathEffect cornerPathEffect = new CornerPathEffect(mCornerRadius = radius);
        mBorderPaint.setPathEffect(cornerPathEffect);
        mFillPaint.setPathEffect(cornerPathEffect);
        isDirty = true;
        invalidateSelf();
    }

    public void setBorderColor(int color) {
        mBorderPaint.setColor(mBorderColor = color);
        invalidateSelf();
    }

    public void setBorderWidth(int pixel) {
        mBorderPaint.setStrokeWidth(mBorderWidth = pixel);
        isDirty = true;
        invalidateSelf();
    }

    public void setFillColor(int color) {
        mFillPaint.setColor(mFillColor = color);
        invalidateSelf();
    }

    @Override
    public void draw(Canvas canvas) {

        final boolean isFilled = Color.alpha(mFillColor) != 0;
        final boolean hasBorder = mBorderWidth > 0 && Color.alpha(mBorderColor) != 0;

        if(DEBUG) canvas.drawRect(0, 0, getBounds().width(), getBounds().height(), mDebugPaint);

        if(isFilled || hasBorder) {

            if(isDirty) {

                final float scale = getIntrinsicHeight();
                if(scale == 0) return;

                final float offset = (mBorderWidth < mCornerRadius ? mCornerRadius : mBorderWidth) * 0.5f;

                mStarPath.moveTo(0 + offset, .387f * scale);            // left tip
                mStarPath.lineTo(.359f * scale, .356f * scale);
                mStarPath.lineTo(.5f * scale, .025f * scale + offset);   // top tip
                mStarPath.lineTo(.639f * scale, .356f * scale);
                mStarPath.lineTo(1 * scale - offset, .387f * scale);     // right tip
                mStarPath.lineTo(.726f * scale, .624f * scale);
                mStarPath.lineTo(.807f * scale - offset, .974f * scale - offset); // bottom right tip
                mStarPath.lineTo(.5f * scale, .788f * scale);
                mStarPath.lineTo(.192f * scale + offset, .974f * scale - offset);  // bottom left tip
                mStarPath.lineTo(.272f * scale, .624f * scale);
                mStarPath.close();

                final Rect bounds = getBounds();
                mTmpRect.set(0, 0, bounds.width(), bounds.height());
                Gravity.apply(mGravity, getIntrinsicWidth(), getIntrinsicHeight(), mTmpRect, mStarBounds);

                isDirty = false;
            }

            if(mStarBounds.left > 0 || mStarBounds.top > 0) {
                canvas.save();
                canvas.translate(mStarBounds.left, mStarBounds.top);
            }

            if (isFilled) {
                canvas.drawPath(mStarPath, mFillPaint);
            }

            if (hasBorder) {
                canvas.drawPath(mStarPath, mBorderPaint);
            }

            if(mStarBounds.left > 0 || mStarBounds.top > 0) {
                canvas.restore();
            }
        }
    }

    @Override
    public void setAlpha(int alpha) {
        mFillPaint.setAlpha(alpha);
        mBorderPaint.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mFillPaint.setColorFilter(colorFilter);
        mBorderPaint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
