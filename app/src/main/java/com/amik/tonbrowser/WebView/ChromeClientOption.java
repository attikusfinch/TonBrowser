package com.amik.tonbrowser.WebView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;

public class ChromeClientOption extends WebChromeClient {
    private View mCustomView;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    private int mOriginalOrientation;
    private int mOriginalSystemUiVisibility;
    private final AppCompatActivity mActivity;

    public ChromeClientOption(AppCompatActivity activity) {
        mActivity = activity;
    }

    public Bitmap getDefaultVideoPoster() {
        if (mCustomView == null) {
            return null;
        }
        return BitmapFactory.decodeResource(mActivity.getResources(), 2130837573);
    }

    public void onHideCustomView() {
        ((FrameLayout) mActivity.getWindow().getDecorView()).removeView(this.mCustomView);
        this.mCustomView = null;
        mActivity.getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
        mActivity.setRequestedOrientation(this.mOriginalOrientation);
        this.mCustomViewCallback.onCustomViewHidden();
        this.mCustomViewCallback = null;
    }

    public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback) {
        if (this.mCustomView != null) {
            onHideCustomView();
            return;
        }
        this.mCustomView = paramView;
        this.mOriginalSystemUiVisibility = mActivity.getWindow().getDecorView().getSystemUiVisibility();
        this.mOriginalOrientation = mActivity.getRequestedOrientation();
        this.mCustomViewCallback = paramCustomViewCallback;
        ((FrameLayout) mActivity.getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mActivity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }
}

