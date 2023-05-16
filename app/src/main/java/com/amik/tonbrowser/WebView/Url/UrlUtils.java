package com.amik.tonbrowser.WebView.Url;

import android.net.Uri;
import android.util.Patterns;
import android.webkit.URLUtil;

import com.amik.tonbrowser.WebView.WebViewProxy;

public class UrlUtils {

    public boolean proxy;

    public UrlUtils() {
        this.proxy = false;
    }

    private String buildUrl(String url) {
        if (url.startsWith("http://") || url.startsWith("https://"))
            return url;
        return "http://".concat(url);
    }

    public String parseLink(String url){
        url = url.strip();

        if(url.contains(".ton") || url.contains(".ton/")){
            url = buildUrl(url);
            proxy = true;
        } else if (Patterns.WEB_URL.matcher(url).matches()) {
            url = buildUrl(url);
        } else {
            url = "https://duckduckgo.com/?q=" + url;
        }
        return String.valueOf(Uri.parse(url));
    }
}
