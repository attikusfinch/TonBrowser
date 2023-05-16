package com.amik.tonbrowser.WebView.Error;

import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.EditText;

import com.amik.tonbrowser.WebView.WebViewProxy;

public class ErrorPage {
    private WebViewProxy webViewProxy;
    private EditText searchBar;
    private WebView webView;

    public ErrorPage(EditText searchBar, WebView webView) {
        this.webViewProxy = new WebViewProxy();
        this.searchBar = searchBar;
        this.webView = webView;
    }

    @JavascriptInterface
    public void SetProxy() {
        webViewProxy.setWebView(webView);
        webViewProxy.changeProxy(true);
        webView.loadUrl(String.valueOf(searchBar.getText()));
    }

    @JavascriptInterface
    public void ClearProxy() {
        webViewProxy.setWebView(webView);
        webViewProxy.changeProxy(false);
        webView.loadUrl(String.valueOf(searchBar.getText()));
    }
}
