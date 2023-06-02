package com.android.demo.watersign;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.webkit.SslErrorHandler;
import android.webkit.WebBackForwardList;
import android.webkit.WebHistoryItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "web_view";
    private static final String CAR_SERIES_LIST_URL = "https://vw.faw-vw.com/models/";
    private static final String CHAR_EMPTY = "";
    private static final String CHAR_SLASH = "/";
    private static final String CHAR_OPEN_PARENTHESIS = "(";
    private static final String CHAR_CLOSE_PARENTHESIS = ")";
    private static final String CHAR_SEMICOLON = ";";
    private static final String CHAR_SINGLE_QUOTATION_MARK = "'";
    private static final String SYSTEM_DIALOG_REASON_KEY = "reason";
    private static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
    private static final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
    private static final String JS_FILE_NAME = "js/PropertyModifier.js";
    private static final String AGREEMENT = "javascript:";
    private static final String METHOD_NAME = "modifyProperty";
    private static final String[] CLASS_NAMES = new String[] {"testdrive_bra_index_modles", "headNav headNav1", "foot_box1"};
    private WebView mWebView = null;
    private ClickReceiver mClickReceiver = null;
    private float mXDown = 0;
    private float mYDown = 0;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mClickReceiver = new ClickReceiver();
        IntentFilter homeIntentFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(mClickReceiver, homeIntentFilter);

        mWebView = findViewById(R.id.web_view);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @SuppressLint("WebViewClientOnReceivedSslError")
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if(view != null && !TextUtils.isEmpty(url) && url.contains(CAR_SERIES_LIST_URL)) {
                    // 读取assets目录下的js文件
                    AssetManager assetManager = getAssets();
                    if (assetManager == null) {
                        return;
                    }
                    InputStream inputStream = null;
                    try {
                        inputStream = assetManager.open(JS_FILE_NAME);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (inputStream == null) {
                        return;
                    }
                    // 将读取的内容转换为字符串
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line = null;
                    try {
                        while (!TextUtils.isEmpty(line = bufferedReader.readLine())) {
                            stringBuilder.append(line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    // webview调用javascript脚本
                    view.loadUrl(AGREEMENT + stringBuilder.toString());
                    for (String className : CLASS_NAMES) {
                        StringBuilder urlStringBuilder = new StringBuilder();
                        urlStringBuilder.append(AGREEMENT)
                                .append(METHOD_NAME)
                                .append(CHAR_OPEN_PARENTHESIS)
                                .append(CHAR_SINGLE_QUOTATION_MARK)
                                .append(className)
                                .append(CHAR_SINGLE_QUOTATION_MARK)
                                .append(CHAR_CLOSE_PARENTHESIS)
                                .append(CHAR_SEMICOLON);
                        view.loadUrl(urlStringBuilder.toString());
                    }
                }
                super.onPageFinished(view, url);
            }
        });
        mWebView.loadUrl(CAR_SERIES_LIST_URL);
        mWebView.getSettings().setSupportZoom(true); // 设置支持缩放
        mWebView.getSettings().setUseWideViewPort(true); // 为图片天添加缩放功能
        mWebView.getSettings().setBuiltInZoomControls(true); // 设置出现缩放工具
        mWebView.getSettings().setDisplayZoomControls(false); // 设置缩放控件隐藏
        mWebView.getSettings().setJavaScriptEnabled(true); // 设置支持javascript脚本
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode != KeyEvent.KEYCODE_BACK || !mWebView.canGoBack()) {
//            return super.onKeyDown(keyCode, event);
//        }
//        Log.i(TAG, "needUnLock: " + needUnLock());
//        if (needUnLock()) {
//            Log.i(TAG, "start ULockActivity.");
//            startActivity(new Intent(MainActivity.this, UnLockActivity.class));
//        }
//        mWebView.goBack();
//        return true;
//    }

    @Override
    public void onBackPressed() {
        if (needUnLock()) {
            startActivity(new Intent(MainActivity.this, UnLockActivity.class));
        }
        if (mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack();
        }
        if (mWebView.getUrl().equals(CAR_SERIES_LIST_URL)) {
            mWebView.destroy();
            finish();
        }
    }

    /**
     * 是否需要解锁
     *
     * @return the boolean
     */
    private boolean needUnLock() {
        if (mWebView == null) {
            return true;
        }
        String currentUrl = mWebView.getUrl();
        Log.i(TAG, "currentUrl is: " + currentUrl);
        if (TextUtils.isEmpty(currentUrl)) {
            return true;
        }
        String currentCarSeries = extractCarSeries(currentUrl);
        if (TextUtils.isEmpty(currentCarSeries)) {
            return true;
        }
        Log.i(TAG, "currentCarSeries is: " + currentCarSeries);
        String lastUrl = getLastUrl();
        Log.i(TAG, "lastUrl is: " + lastUrl);
        if (TextUtils.isEmpty(lastUrl)) {
            return true;
        }
        String lastCarSeries = extractCarSeries(lastUrl);
        if (TextUtils.isEmpty(lastCarSeries)) {
            return true;
        }
        Log.i(TAG, "lastCarSeries is: " + lastCarSeries);
        return !currentCarSeries.equals(lastCarSeries);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mWebView.goBack();//返回上个页面
    }

//    /**
//     * @param requestCode
//     * @param resultCode
//     * @param data        锁屏密码校验回调
//     */
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 1101) {
//            if (resultCode == RESULT_OK) {
//                Toast.makeText(this, "校验成功", Toast.LENGTH_LONG).show();
//            } else {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    showScreenLockPwd();
//                }
//            }
//        }
//    }
//
//    /**
//     * 跳转锁屏密码校验页面
//     */
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    public void showScreenLockPwd() {
//        Intent intent = mKeyguardMgr.createConfirmDeviceCredentialIntent(null, null);
//        if (intent != null) {
//            startActivityForResult(intent, 1101);
//        } else {
//            Toast.makeText(this, "intent==null", Toast.LENGTH_LONG).show();
//        }
//    }

    /**
     * 获取上一个页面的url
     *
     * @return 上一个页面的url
     */
    private String getLastUrl() {
        WebBackForwardList backForwardList = mWebView.copyBackForwardList();
        if (backForwardList == null || backForwardList.getSize() == 0) {
            return null;
        }
        int currentIndex = backForwardList.getCurrentIndex();
        WebHistoryItem historyItem = backForwardList.getItemAtIndex(currentIndex - 1);
        if (historyItem == null) {
            return null;
        }
        return historyItem.getUrl();
    }

    /**
     * 从指定url中提取车系信息
     *
     * @param url 指定url
     * @return 车系信息
     */
    private String extractCarSeries(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        if (!url.contains(CAR_SERIES_LIST_URL)) {
            return null;
        }
        String tmp = url.replace(CAR_SERIES_LIST_URL, CHAR_EMPTY);
        if (TextUtils.isEmpty(tmp)) {
            return null;
        }
        int index = tmp.indexOf(CHAR_SLASH);
        if (index < 0 || index >= tmp.length()) {
            return null;
        }
        return tmp.substring(0, index);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mClickReceiver != null) {
            try {
                unregisterReceiver(mClickReceiver);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class ClickReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String intentAction = intent.getAction();
            if (!TextUtils.equals(intentAction, Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                return;
            }
            String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
            if (!TextUtils.equals(reason, SYSTEM_DIALOG_REASON_HOME_KEY) && !TextUtils.equals(reason, SYSTEM_DIALOG_REASON_RECENT_APPS)) {
                return;
            }

            Log.i(TAG, "enter home or menu and start ULockActivity.");
            startActivity(new Intent(MainActivity.this, UnLockActivity.class));
            finish();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mXDown = event.getX();
                mYDown = event.getY();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_MOVE:
                float disX = event.getX() - mXDown;
                float disY = event.getY() - mYDown;
                if (Math.abs(disX) > Math.abs(disY)) { // 左右滑
                    if (needUnLock()) {
                        Log.i(TAG, "back gesture start ULockActivity.");
                        startActivity(new Intent(MainActivity.this, UnLockActivity.class));
                    }
                    mWebView.goBack();
                } else {
                    if (disY < 0) { // 上滑
                        Log.i(TAG, "recent apps gesture start ULockActivity.");
                        startActivity(new Intent(MainActivity.this, UnLockActivity.class));
                        finish();
                    }
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }
}