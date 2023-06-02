package com.android.demo.watersign.palette;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.android.demo.watersign.utils.MD5Util;

import java.util.PrimitiveIterator;

public class DigitalPasswordPanel extends RelativeLayout {
    private static final String DEFAULT_TEXT = "请输入密码";
    private static final String SUCCESS_TEXT = "密码正确";
    private static final String FAIL_TEXT = "密码错误";
    private static final int TEXT_FONT_SIZE = 20;
    private static final int TOP_MARGIN = 48;
    private static final int BOTTOM_MARGIN = 48;
    private static final String DELETE = "删除";

    private TextView mTips = null;
    private int mInputBoxLength = 0;
    private InputBox mInputBox = null;
    private StringBuilder mInputPassword = null;
    private String mEncryptedPassword = null;
    private OnVerifyListener mOnVerifyListener = null;

    public DigitalPasswordPanel(Context context) {
        this(context, null);
    }

    public DigitalPasswordPanel(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DigitalPasswordPanel(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * 初始化
     *
     * @param context app上下文
     */
    private void init(Context context) {
        // 提示
        mTips = new TextView(context);
        mTips.setText(DEFAULT_TEXT);
        mTips.setTextSize(TEXT_FONT_SIZE);
        mTips.setTextColor(Color.BLACK);
        mTips.setId(View.generateViewId());
        LayoutParams textLayoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textLayoutParams.addRule(CENTER_HORIZONTAL);
        addView(mTips, textLayoutParams);

        // 获取已保存的密码信息
        getStoredPasswordInfo();

        // 密码输入框
        mInputBox = new InputBox(context, mInputBoxLength);
        mInputBox.setId(View.generateViewId());
        LayoutParams inputBoxLayoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        inputBoxLayoutParams.addRule(BELOW, mTips.getId());
        inputBoxLayoutParams.addRule(CENTER_HORIZONTAL);
        inputBoxLayoutParams.topMargin = TOP_MARGIN;
        inputBoxLayoutParams.bottomMargin = BOTTOM_MARGIN;
        addView(mInputBox, inputBoxLayoutParams);

        // 数字键盘
        mInputPassword = new StringBuilder();
        DigitalKeyboard digitalKeyboard = new DigitalKeyboard(context);
        digitalKeyboard.setOnClickListener(new DigitalKeyboard.OnClickListener() {
            @Override
            public void onClick(String content) {
                if (content.equals(DELETE)) {
                    clear();
                } else {
                    mInputBox.update();
                    mInputPassword.append(content);
                    if (mInputPassword.toString().length() >= mInputBoxLength) {
                        verifyPassword();
                    }
                }
            }
        });
        LayoutParams digitalKeyboardLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        digitalKeyboardLayoutParams.addRule(BELOW, mInputBox.getId());
        addView(digitalKeyboard, digitalKeyboardLayoutParams);
    }

    /**
     * 清除所有输入
     */
    private void clear() {
        mInputBox.clear();
        mInputPassword = new StringBuilder();
    }

    /**
     * 校验用户输入的密码
     */
    private void verifyPassword() {
        if (TextUtils.isEmpty(mInputPassword.toString())) {
            return;
        }
        String password = MD5Util.stringToMD5(mInputPassword.toString());
        if (!TextUtils.isEmpty(password) && password.equals(mEncryptedPassword)) {
            if (mOnVerifyListener != null) {
                mTips.setText(SUCCESS_TEXT);
                mOnVerifyListener.onSucceed();
            }
        } else {
            if (mOnVerifyListener != null) {
                mTips.setText(FAIL_TEXT);
                startVerifyFailedAnimation();
                mOnVerifyListener.onFailed();
            }
        }
        clear();
    }

    /**
     * 密码校验失败滑动动画
     */
    private void startVerifyFailedAnimation() {
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mTips, "translationX", 0, -10)
                .setDuration(50);
        ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(mTips, "translationX", 0, 10)
                .setDuration(50);
        animatorSet.playSequentially(objectAnimator, objectAnimator1);
        animatorSet.start();
    }

    /**
     *  获取已保存的密码信息
     */
    private void getStoredPasswordInfo() {
        // TODO 从系统中读取锁屏密码
        String plaintextPassword = "1234";
        mEncryptedPassword = MD5Util.stringToMD5(plaintextPassword);
        mInputBoxLength = plaintextPassword.length();
    }

    public interface OnVerifyListener {
        void onSucceed();
        void onFailed();
    }

    public void setOnVerifyListener(OnVerifyListener onVerifyListener) {
        mOnVerifyListener = onVerifyListener;
    }
}
