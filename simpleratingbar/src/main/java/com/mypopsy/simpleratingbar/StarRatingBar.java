package com.mypopsy.simpleratingbar;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.LayerDrawable;
import android.media.Image;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by Cerrato Renaud <renaud.cerrato@gmail.com>
 * https://github.com/renaudcerrato
 * 1/16/17
 */
public class StarRatingBar extends ViewGroup {

    private final StarDrawable mOutline = new StarDrawable();
    private final StarDrawable mFilled = new StarDrawable();
    private final Rect mBounds = new Rect();
    private final Rect mTmp = new Rect();
    private int mNumStars;
    private float mStarSize;
    private float mStarPadding;
    private int mRating;
    private LayoutInflater mLayoutInflater;
    private OnRatingBarChangeListener mListener;
    private int mGravity;


    public interface OnRatingBarChangeListener {
        void onRatingChanged(StarRatingBar ratingBar, int rating, boolean fromUser);
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setRating((Integer)v.getTag(), true);
        }
    };

    public StarRatingBar(Context context) {
        this(context, null);
    }

    public StarRatingBar(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.starRatingBarStyle);
    }

    public StarRatingBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }
    
    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {

        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.StarRatingBar, defStyleAttr, defStyleRes);

        setNumStars(a.getInt(R.styleable.StarRatingBar_android_numStars, 5));
        setRating((int) a.getFloat(R.styleable.StarRatingBar_android_rating, 0));
        setGravity(a.getInt(R.styleable.StarIndicator_android_gravity, Gravity.CENTER));

        setStarBackgroundColor(a.getColor(R.styleable.StarRatingBar_srb_starBackgroundColor, Color.LTGRAY));
        setStarBorderColor(a.getColor(R.styleable.StarRatingBar_srb_starBorderColor, 0));
        setStarFillColor(a.getColor(R.styleable.StarRatingBar_srb_starFillColor, StarDrawable.DEFAULT_FILL_COLOR));

        setStarCornerRadius(a.getDimensionPixelSize(R.styleable.StarRatingBar_srb_starCornerRadius, 0));
        setStarBorderWidth(a.getDimensionPixelSize(R.styleable.StarRatingBar_srb_starBorderWidth, 0));
        setStarSize(a.getDimensionPixelSize(R.styleable.StarRatingBar_srb_starSize, Math.round(toPixel(32))));
        setStarPadding(a.getDimensionPixelSize(R.styleable.StarRatingBar_srb_starPadding, Math.round(toPixel(4))));

        a.recycle();
    }

    private void setStarPadding(int padding) {
        mStarPadding = padding;
        requestLayout();
    }

    public void setGravity(int gravity) {
        if(mGravity != gravity) {
            mGravity = gravity;
            requestLayout();
        }
    }

    public void setOnRatingBarChangeListener(OnRatingBarChangeListener mListener) {
        this.mListener = mListener;
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

    public void setRating(int newRating) {
        setRating(newRating, false);
    }

    private void setRating(int newRating, boolean fromUser) {
        if(mRating != newRating) {
            for (int i = Math.min(newRating, mRating); i < getChildCount(); i++) {
                setStarDrawable(getChildAt(i), i < newRating);
            }
            mRating = newRating;
            if(mListener != null) mListener.onRatingChanged(this, newRating, fromUser);
        }
    }

    public int getRating() {
        return mRating;
    }

    public void setNumStars(int numStars) {
        if(mNumStars != numStars) {
            mNumStars = numStars;

            while(getChildCount() > numStars) {
                removeViewAt(getChildCount() - 1);
            }

            while(getChildCount() < numStars) {
                final View star = createStar();
                star.setOnClickListener(mClickListener);
                setStarDrawable(star, getChildCount() < mRating);
                addView(star);
                star.setTag(getChildCount());
            }

            requestLayout();
        }
    }

    public void setStarSize(int size) {
        if(mStarSize != size) {
            mStarSize = size;
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

        int width = Math.round(mNumStars*mStarSize + mStarPadding*(mNumStars - 1));
        int height = Math.round(mStarSize);

        if(wmode == MeasureSpec.EXACTLY)
            width = suggestedWidth;
        else if(wmode == MeasureSpec.AT_MOST)
            width = Math.max(getSuggestedMinimumWidth(), Math.min(suggestedWidth, width));

        if(hmode == MeasureSpec.EXACTLY)
            height = suggestedHeight;
        else if(hmode == MeasureSpec.AT_MOST)
            height = Math.max(getSuggestedMinimumHeight(), Math.min(suggestedHeight, height));

        mBounds.set(getPaddingLeft(), getPaddingTop(), width - getPaddingRight(), height - getPaddingBottom());
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        mFilled.setSize(Math.round(mStarSize));
        mOutline.setSize(Math.round(mStarSize));

        Gravity.apply(mGravity, (int) (mNumStars*mStarSize), (int) mStarSize, mBounds, mTmp);

        float left = mTmp.left;

        for(int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            child.layout(Math.round(left), mTmp.top, Math.round(left + mStarSize), Math.round(mTmp.top + mStarSize));
            invalidateStarDrawable(child);
            left+=mStarSize+mStarPadding;
        }
    }

    private ImageView getImageView(View view) {
        if(view instanceof ImageView)
            return (ImageView) view;
        else
            return (ImageView) view.findViewById(android.R.id.icon);
    }

    private void invalidateStarDrawable(View view) {
        final ImageView iv = getImageView(view);
        iv.invalidateDrawable(iv.getDrawable());
    }

    private void setStarDrawable(View view, boolean filled) {
        getImageView(view).setImageDrawable(filled ? mFilled : mOutline);
    }

    private View createStar() {
        if(mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(getContext());
        }
        return mLayoutInflater.inflate(R.layout.srb_star_item, this, false);
    }

    private float toPixel(float dp){
        Resources r = getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }
}
