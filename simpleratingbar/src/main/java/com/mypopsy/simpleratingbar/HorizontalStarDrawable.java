package com.mypopsy.simpleratingbar;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.graphics.drawable.DrawableWrapper;
import android.view.Gravity;

/**
 * Created by Cerrato Renaud <renaud.cerrato@gmail.com>
 * https://github.com/renaudcerrato
 * 1/18/17
 */
class HorizontalStarDrawable extends DrawableWrapper {

    private final Rect mTmpRect = new Rect();
    private final Rect mGravityBounds = new Rect();

    private final StarDrawable mDrawable;

    private int mCount, mSize;
    private boolean isDirty = true;
    private int mGravity = Gravity.CENTER;
    private int mDivider;

    HorizontalStarDrawable(StarDrawable drawable) {
        super(drawable);
        mDrawable = drawable;
    }

    void setGravity(int gravity) {
        if (mGravity != gravity) {
            mGravity = gravity;
            isDirty = true;
            invalidateSelf();
        }
    }

    void setSize(int size) {
        if (mSize != size) {
            mSize = size;
            updateBounds();
        }
    }

    void setDivider(int divider) {
        this.mDivider = divider;
        invalidateSelf();
    }

    void setCount(int count) {
        if (mCount != count) {
            mCount = count;
            updateBounds();
        }
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        updateBounds();
        isDirty = true;
    }

    @Override
    public int getIntrinsicWidth() {
        return mDrawable.getIntrinsicWidth() * mCount + mDivider*(mCount - 1);
    }

    @Override
    public void draw(Canvas canvas) {

        if (isDirty) {
            final Rect bounds = getBounds();
            mTmpRect.set(0, 0, bounds.width(), bounds.height());
            Gravity.apply(mGravity, getIntrinsicWidth(), getIntrinsicHeight(), mTmpRect, mGravityBounds);
            isDirty = false;
        }

        canvas.save();

        if(mGravityBounds.left != 0 || mGravityBounds.top != 0) {
            canvas.translate(mGravityBounds.left, mGravityBounds.top);
        }

        for (int i = 0; i < mCount; i++) {
            if (i > 0) canvas.translate(mDrawable.getIntrinsicWidth()+mDivider, 0);
            mDrawable.draw(canvas);
        }

        canvas.restore();
    }

    private void updateBounds() {
        final Rect bounds = getBounds();
        int size = mSize;

        if (size <= 0) {
            final int w = bounds.width();
            final int h = bounds.height();
            size = Math.min(h, w / mCount);
        }

        mDrawable.setSize(size);
        mDrawable.setBounds(bounds.left, bounds.top, bounds.left + size, bounds.top + size);
        invalidateSelf();
    }
}
