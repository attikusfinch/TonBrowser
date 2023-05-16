package com.amik.tonbrowser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amik.tonbrowser.WebView.ChromeClientOption;
import com.amik.tonbrowser.WebView.Error.ErrorPage;
import com.amik.tonbrowser.WebView.Url.UrlUtils;
import com.amik.tonbrowser.WebView.WebViewClientOption;
import com.amik.tonbrowser.WebView.WebViewProxy;

public class MainWebActivity extends AppCompatActivity {

    private WebView webView;
    private EditText searchBar;
    private Button refreshButton;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Toolbar toolbar;

    private UrlUtils urlUtils;
    private WebViewProxy webViewProxy;

    private static final int TIME_INTERVAL = 2000; // milliseconds, desired time passed between two back presses.
    private long mBackPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        SettingWebView();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void SettingWebView(){
        WebViewClientOption webViewClientOption = new WebViewClientOption();
        ChromeClientOption chromeClient = new ChromeClientOption(this);

        webViewClientOption.setWebView(webView);

        webView.addJavascriptInterface(new ErrorPage(searchBar, webView), "MobileBridge");

        webView.setWebViewClient(webViewClientOption);
        webView.setWebChromeClient(chromeClient);

        WebSettings webSettings = webView.getSettings();
        webSettings.setSupportZoom(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
    }

    private void search(){
        String url = searchBar.getText().toString();

        url = urlUtils.parseLink(url);

        webViewProxy.changeProxy(urlUtils.proxy);

        webView.loadUrl(url);
    }

    private void refreshPage(){
        Log.d("LOG", webView.getUrl());
        webView.reload();
        swipeRefreshLayout.setRefreshing(false);
    }

    private void init() {
        webView = findViewById(R.id.webView);
        searchBar = findViewById(R.id.SearchBar);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        toolbar = findViewById(R.id.toolbar);
        refreshButton = findViewById(R.id.refresh);

        urlUtils = new UrlUtils();
        webViewProxy = new WebViewProxy();

        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search();
                    return true;
                }
                return false;
            }
        });

        searchBar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View arg0, boolean has_focus) {
                if (has_focus) {
                    toolbar.setLogo(R.drawable.empty);
                } else {
                    toolbar.setLogo(R.drawable.logo);
                }
            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(MainWebActivity.this, R.anim.rotate_around_center);

                refreshPage();
                refreshButton.startAnimation(animation);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshPage();
            }
        });

        webViewProxy.setWebView(webView);
        webViewProxy.setProxy();

        webView.loadUrl("http://foundation.ton/");
    }

    @Override
    public void onBackPressed() {
        // if back button is pressed twice, exit the app
        if (webView.canGoBack()) {
            webView.goBack();

            String url = webView.getUrl();
            urlUtils.parseLink(url);
            webViewProxy.changeProxy(urlUtils.proxy);
        } else {
            if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis())
            {
                super.onBackPressed();
                return;
            }
            else { Toast.makeText(getBaseContext(), "Tap back button in order to exit", Toast.LENGTH_SHORT).show(); }

            mBackPressed = System.currentTimeMillis();
        }
    }
}