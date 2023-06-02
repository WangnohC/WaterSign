package com.android.demo.watersign;

import android.os.Bundle;
import android.view.KeyEvent;

import androidx.appcompat.app.AppCompatActivity;

import com.android.demo.watersign.palette.DigitalPasswordPanel;

/**
 * 解锁
 *
 * @author wangchong
 * @since 2022-0708
 */
public class UnLockActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock);

        DigitalPasswordPanel digitalPasswordPanel= findViewById(R.id.password_panel);
        digitalPasswordPanel.setOnVerifyListener(new DigitalPasswordPanel.OnVerifyListener() {
            @Override
            public void onSucceed() {
                UnLockActivity.this.finish();
            }

            @Override
            public void onFailed() {
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 屏蔽后退键
        if(KeyEvent.KEYCODE_BACK == event.getKeyCode())
        {
            return true; // 阻止事件继续向下分发
        }
        return super.onKeyDown(keyCode, event);
    }
}
