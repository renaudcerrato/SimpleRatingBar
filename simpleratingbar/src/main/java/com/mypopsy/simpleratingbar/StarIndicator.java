package com.mypopsy.simpleratingbar;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;

/**
 * Created by Cerrato Renaud <renaud.cerrato@gmail.com>
 * https://github.com/renaudcerrato
 * 1/16/17
 */
public class StarIndicator extends View {

    private final StarDrawable mOutline = new StarDrawable();
    private final StarDrawable mFilled = new StarDrawable();

    private final Rect mVisualBounds = new Rect();
    private final Rect mTmp = new Rect();

    private HorizontalStarDrawable mFilledStarsDrawable;
    private HorizontalStarDrawable mOutlineStarsDrawable;

    private ClipDrawable mClipDrawable;
    private android.graphics.drawable.Drawable mForeground;

    private int mNumStars;
    private float mStarSize;
    private int mGravity;
    private boolean isDirty;
    private float mStarPadding;

    public StarIndicator(Context context) {
        this(context, null);
    }

    public StarIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.starIndicatorStyle);
    }

    public StarIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {

        mFilledStarsDrawable = new HorizontalStarDrawable(mFilled);
        mOutlineStarsDrawable = new HorizontalStarDrawable(mOutline);

        mClipDrawable = new ClipDrawable(mFilledStarsDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);
        mForeground = new LayerDrawable(new android.graphics.drawable.Drawable[]{mOutlineStarsDrawable, mClipDrawable});

        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.StarIndicator, defStyleAttr, defStyleRes);

        setNumStars(a.getInt(R.styleable.StarIndicator_android_numStars, 5));
        setRating(a.getFloat(R.styleable.StarIndicator_android_rating, 0));
        setGravity(a.getInt(R.styleable.StarIndicator_android_gravity, Gravity.CENTER));
        setStarPadding(a.getDimensionPixelSize(R.styleable.StarIndicator_srb_starPadding, Math.round(toPixel(1))));

        setStarBackgroundColor(a.getColor(R.styleable.StarIndicator_srb_starBackgroundColor, Color.LTGRAY));
        setStarBorderColor(a.getColor(R.styleable.StarIndicator_srb_starBorderColor, 0));
        setStarFillColor(a.getColor(R.styleable.StarRatingBar_srb_starFillColor, StarDrawable.DEFAULT_FILL_COLOR));

        setStarCornerRadius(a.getDimensionPixelSize(R.styleable.StarIndicator_srb_starCornerRadius, 0));
        setStarBorderWidth(a.getDimensionPixelSize(R.styleable.StarIndicator_srb_starBorderWidth, 0));
        setStarSize(a.getDimensionPixelSize(R.styleable.StarIndicator_srb_starSize, 0));

        a.recycle();
    }

    public void setStarCornerRadius(float radius) {
        mOutline.setCornerRadius(radius);
        mFilled.setCornerRadius(radius);
        invalidate();
    }

    public void setStarBorderColor(int color) {
        mOutline.setBorderColor(color);
        mFilled.setBorderColor(color);
        invalidate();
    }

    public void setStarBorderWidth(int pixel) {
        mOutline.setBorderWidth(pixel);
        mFilled.setBorderWidth(pixel);
        invalidate();
    }

    public void setStarFillColor(int color) {
        mFilled.setFillColor(color);
        invalidate();
    }

    public void setStarBackgroundColor(int color) {
        mOutline.setFillColor(color);
        invalidate();
    }

    public void setRating(float newRating) {
        mClipDrawable.setLevel(Math.round((newRating * 10000f) / mNumStars));
        invalidate();
    }

    public void setGravity(int gravity) {
        mGravity = gravity;
        mFilledStarsDrawable.setGravity(gravity);
        mOutlineStarsDrawable.setGravity(gravity);
        isDirty = true;
        invalidate();
    }

    public float getRating() {
        return (mClipDrawable.getLevel()*mNumStars)/10000f;
    }

    public void setNumStars(int numStars) {
        if(mNumStars != numStars) {
            mNumStars = numStars;
            requestLayout();
        }
    }

    public void setStarSize(int size) {
        if(mStarSize != size) {
            mStarSize = size;
            requestLayout();
        }
    }

    public void setStarPadding(int padding) {
        if(mStarPadding != padding) {
            mStarPadding = padding;
            requestLayout();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final int wmode = MeasureSpec.getMode(widthMeasureSpec);
        final int hmode = MeasureSpec.getMode(heightMeasureSpec);
        final int suggestedWidth = MeasureSpec.getSize(widthMeasureSpec);
        final int suggestedHeight = MeasureSpec.getSize(heightMeasureSpec);

        if(mStarSize <= 0) {
            if(mNumStars > 0) {
                mStarSize = suggestedWidth - getPaddingLeft() - getPaddingRight();
                mStarSize -= mStarPadding*(mNumStars - 1);
                mStarSize /= (float) mNumStars;
            }else
                mStarSize = 0;
        }

        mFilledStarsDrawable.setCount(mNumStars);
        mOutlineStarsDrawable.setCount(mNumStars);

        mFilledStarsDrawable.setSize(Math.round(mStarSize));
        mOutlineStarsDrawable.setSize(Math.round(mStarSize));
        mFilledStarsDrawable.setDivider(Math.round(mStarPadding));
        mOutlineStarsDrawable.setDivider(Math.round(mStarPadding));

        mFilledStarsDrawable.setBounds(0, 0, mFilledStarsDrawable.getIntrinsicWidth(), mFilledStarsDrawable.getIntrinsicHeight());
        mOutlineStarsDrawable.setBounds(0, 0, mOutlineStarsDrawable.getIntrinsicWidth(), mOutlineStarsDrawable.getIntrinsicHeight());
        mClipDrawable.setBounds(0, 0, mFilledStarsDrawable.getIntrinsicWidth(), mFilledStarsDrawable.getIntrinsicHeight());
        mForeground.setBounds(0, 0, mClipDrawable.getIntrinsicWidth(), mClipDrawable.getIntrinsicHeight());

        int width = mForeground.getBounds().width();
        int height = mForeground.getBounds().height();

        if(wmode == MeasureSpec.EXACTLY)
            width = suggestedWidth;
        else if(wmode == MeasureSpec.AT_MOST)
            width = Math.max(getSuggestedMinimumWidth(), Math.min(suggestedWidth, width));

        if(hmode == MeasureSpec.EXACTLY)
            height = suggestedHeight;
        else if(hmode == MeasureSpec.AT_MOST)
            height = Math.max(getSuggestedMinimumHeight(), Math.min(suggestedHeight, height));



        mVisualBounds.set(getPaddingLeft(), getPaddingTop(), width - getPaddingRight(), height - getPaddingBottom());

        setMeasuredDimension(width, height);
        isDirty = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if(isDirty) {
            Gravity.apply(mGravity, mForeground.getIntrinsicWidth(),
                    mForeground.getIntrinsicHeight(), mVisualBounds, mTmp);
            isDirty = false;
        }

        if(mTmp.left != 0 || mTmp.top != 0) {
            canvas.save();
            canvas.translate(mTmp.left, mTmp.top);
        }

        mForeground.draw(canvas);

        if(mTmp.left != 0 || mTmp.top != 0) {
            canvas.restore();
        }
    }

    private float toPixel(float dp){
        Resources r = getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }
}
