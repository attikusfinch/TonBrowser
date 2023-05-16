package com.amik.tonbrowser.WebView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.amik.tonbrowser.WebView.Url.UrlUtils;

public class WebViewClientOption extends WebViewClient {

    private WebViewProxy webViewProxy;
    private WebView webView;

    public WebViewClientOption(){
        this.webViewProxy = new WebViewProxy();
    }

    public void setWebView(WebView webView){
        this.webView = webView;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView webView, String url) {
        if (url.startsWith("http://") || url.startsWith("https://") || url.startsWith("file://")) return false;

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            webView.getContext().startActivity(intent);
            return true;
        } catch (Exception e) {
            Log.i("ERROR", "shouldOverrideUrlLoading Exception:" + e);
            return true;
        }
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        UrlUtils urlUtils = new UrlUtils();

        urlUtils.parseLink(url);

        webViewProxy.setWebView(this.webView);
        webViewProxy.changeProxy(urlUtils.proxy);
    }

    @Override
    public void onReceivedError (WebView view, int errorCode,
                                 String description, String failingUrl) {
        view.loadUrl("file:///android_asset/error.html");
    }
}