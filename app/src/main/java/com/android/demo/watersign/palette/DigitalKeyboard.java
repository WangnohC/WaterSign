package com.android.demo.watersign.palette;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;

import androidx.annotation.Nullable;

/**
 * 数字键盘
 *
 * @author wangchong
 * @since 2022-07-08
 */
public class DigitalKeyboard extends GridLayout{
    private static final int COLUMN_COUNT = 3; // 键盘的列数
    private static final int ROW_COUNT = 4; // 键盘的行数
    private static final int ALPHA = 180;
    private static final int RED = 255;
    private static final int GREEN = 255;
    private static final int BLUE = 255;
    private static final int DIGITAL_FONT_SIZE = 32;
    private static final int TEXT_FONT_SIZE = 20;
    private static final int DIGITAL_KEY_BOARD_RADIUS = 90;
    private static final String DELETE = "删除";
    private static final float WEIGHT = 1f;

    private OnClickListener mOnClickListener = null;

    public DigitalKeyboard(Context context) {
        this(context, null);
    }

    public DigitalKeyboard(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DigitalKeyboard(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * 初始化
     *
     * @param context app上下文
     */
    private void init(Context context) {
        setColumnCount(COLUMN_COUNT);
        setRowCount(ROW_COUNT);

        int count = COLUMN_COUNT * ROW_COUNT;
        for (int i = 1; i <= count; i++) {
            final Digit digit = new Digit(context, DIGITAL_KEY_BOARD_RADIUS);
            digit.setText(String.valueOf(i == count ? DELETE : (i == 11 ? 0 : i)));
            digit.setTextSize(i == count ? TEXT_FONT_SIZE : DIGITAL_FONT_SIZE);
            digit.setTextColor(Color.argb(ALPHA, RED, GREEN, BLUE));
            digit.setVisibility(i == 10 ? INVISIBLE : VISIBLE);
            digit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnClickListener.onClick(digit.getText().toString());
                }
            });
            LayoutParams layoutParams = new LayoutParams();
            layoutParams.columnSpec = spec((i - 1) % COLUMN_COUNT, getAlignmentMode(), WEIGHT);
            layoutParams.rowSpec = spec((i - 1) / COLUMN_COUNT, getAlignmentMode(), WEIGHT);
            layoutParams.setGravity(Gravity.CENTER);
            digit.setLayoutParams(layoutParams);
            addView(digit);
        }
    }

    public interface OnClickListener {
        void onClick(String content);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }
}
