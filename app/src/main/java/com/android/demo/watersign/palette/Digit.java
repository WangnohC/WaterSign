package com.android.demo.watersign.palette;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

/**
 * The view of digit from 0 to 9
 *
 * @author wangchong
 * @since 2022-07-07
 */
public class Digit extends AppCompatTextView {
    private static final int ALPHA_SELECTED = 70;
    private static final int ALPHA_UNSELECTED = 40;
    private static final int RED = 0;
    private static final int GREEN = 0;
    private static final int BLUE = 0;
    
    private int mRadius = 0;
    private GradientDrawable mSelectDrawable = null;
    private GradientDrawable mUnSelectDrawable = null;

    public Digit(Context context, int radius) {
        this(context, null, radius);
    }

    public Digit(Context context, @Nullable AttributeSet attrs, int radius) {
        this(context, attrs, 0, radius);
    }

    public Digit(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int radius) {
        super(context, attrs, defStyleAttr);
        init(radius);
    }

    /**
     * 初始化
     * @param radius 数字背后的圆形半径
     */
    private void init(int radius) {
        mRadius = radius;
        mUnSelectDrawable = getDrawable(Color.argb(ALPHA_UNSELECTED, RED, GREEN, BLUE));
        mSelectDrawable = getDrawable(Color.argb(ALPHA_SELECTED, RED, GREEN, BLUE));
        setBackground(mUnSelectDrawable);
        setWidth(mRadius * 2);
        setHeight(mRadius * 2);
        setGravity(Gravity.CENTER);
        setClickable(true);
    }

    /**
     * 获取背景
     *
     * @param color 颜色
     * @return 背景
     */
    private GradientDrawable getDrawable(int color) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(color);
        gradientDrawable.setCornerRadius(mRadius);
        return gradientDrawable;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        if (!isEnabled() || !isClickable()) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                setBackground(mUnSelectDrawable);
                break;
            case MotionEvent.ACTION_DOWN:
                setBackground(mSelectDrawable);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        setBackground(selected ? mSelectDrawable : mUnSelectDrawable);
    }
}
