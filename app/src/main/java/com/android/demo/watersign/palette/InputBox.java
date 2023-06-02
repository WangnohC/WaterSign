package com.android.demo.watersign.palette;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import java.util.PrimitiveIterator;

/**
 * the view of input box
 *
 * @author wangchong
 * @since 2022-07-08
 */
public class InputBox extends LinearLayout {
    private static final int INPUT_BOX_RADIUS = 16;
    private static final int RIGHT_MARGIN = 48;
    private int mInputBoxLength = 0;
    private int mCurInputIndex = 0;

    public InputBox(Context context, int inputBoxLength) {
        this(context, null, inputBoxLength);
    }

    public InputBox(Context context, @Nullable AttributeSet attrs, int inputBoxLength) {
        this(context, attrs, 0, inputBoxLength);
    }

    public InputBox(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int inputBoxLength) {
        super(context, attrs, defStyleAttr);
        init(context, inputBoxLength);
    }

    /**
     * 初始化
     *
     * @param context app上下文
     */
    private void init(Context context, int inputBoxLength) {
        mInputBoxLength = inputBoxLength;
        for (int i = 0; i < mInputBoxLength; i++) {
            Digit digit = new Digit(context, INPUT_BOX_RADIUS);
            digit.setEnabled(false);
            LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (i < mInputBoxLength - 1) {
                layoutParams.rightMargin = RIGHT_MARGIN;
            }
            addView(digit,layoutParams);
        }
    }

    /**
     * 更新输入框输入进度
     */
    public void update() {
        mCurInputIndex++;
        if (mCurInputIndex > mInputBoxLength) {
            mCurInputIndex = 1;
        }
        for (int i = 0; i < mCurInputIndex; i++) {
            Digit digit = (Digit) getChildAt(i);
            if (digit != null) {
                digit.setSelected(true);
            }
        }
    }

    /**
     * 清空输入框的所有内容
     */
    public void clear() {
        mCurInputIndex = 0;
        for (int i = 0; i < mInputBoxLength; i++) {
            Digit digit = (Digit) getChildAt(i);
            if (digit != null) {
                digit.setSelected(false);
            }
        }
    }
}
