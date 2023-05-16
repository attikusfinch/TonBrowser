package com.amik.tonbrowser.WebView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Proxy;
import android.os.Build;
import android.util.ArrayMap;
import android.util.Log;
import android.webkit.WebView;

import androidx.webkit.ProxyConfig;
import androidx.webkit.ProxyController;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Executor;

public class WebViewProxy {

    private final String LOG_TAG = "WebViewProxy";

    private WebView webView;

    private final String proxyHost = "in1.ton.org";
    private final int proxyPort = 8080;

    public WebViewProxy() {
    }

    public void setWebView(WebView webView) {
        this.webView = webView;
    }

    @SuppressLint("RequiresFeature")
    public void setProxy() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            setProxyLegacy(proxyHost, proxyPort);
            return;
        }

        ProxyConfig proxyConfig = new ProxyConfig.Builder()
                .addProxyRule(proxyHost + ":" + proxyPort)
                .addDirect().build();
        ProxyController.getInstance().setProxyOverride(proxyConfig, new Executor() {
            @Override
            public void execute(Runnable command) {
                //do nothing
            }
        }, new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    @SuppressLint("RequiresFeature")
    public void clearProxy() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            clearProxyLegacy();
            return;
        }

        ProxyController.getInstance().clearProxyOverride(new Executor() {
            @Override
            public void execute(Runnable command) {
                //do nothing
            }
        }, new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    public void changeProxy(boolean proxy) {
        if (proxy) {
            setProxy();
        } else {
            clearProxy();
        }
    }

    public void setProxyLegacy(String host, int port) {
        Context applicationContext = webView.getContext().getApplicationContext();

        System.setProperty("http.proxyHost", host);
        System.setProperty("http.proxyPort", String.valueOf(port));

        try {
            Field loadedApkField = applicationContext.getClass().getField("mLoadedApk");
            loadedApkField.setAccessible(true);
            Object loadedApk = loadedApkField.get(applicationContext);
            @SuppressLint("PrivateApi") Class<?> loadedApkClass = Class.forName("android.app.LoadedApk");
            @SuppressLint("DiscouragedPrivateApi") Field receiversField = loadedApkClass.getDeclaredField("mReceivers");
            receiversField.setAccessible(true);
            ArrayMap<?, ?> receivers = (ArrayMap<?, ?>) receiversField.get(loadedApk);

            assert receivers != null;

            for (Object receiverMap : receivers.values()) {
                for (Object receiver : ((ArrayMap<?, ?>) receiverMap).keySet()) {
                    Class<?> receiverClass = receiver.getClass();
                    if (receiverClass.getName().contains("ProxyChangeListener")) {
                        Method onReceiveMethod = receiverClass.getDeclaredMethod("onReceive", Context.class, Intent.class);
                        Intent intent = new Intent(Proxy.PROXY_CHANGE_ACTION);

                        onReceiveMethod.invoke(receiver, applicationContext, intent);
                    }
                }
            }

            Log.d(LOG_TAG, "Successfully set the proxy");
        } catch (NoSuchFieldException | ClassNotFoundException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public void clearProxyLegacy() {
        Context applicationContext = webView.getContext().getApplicationContext();

        System.clearProperty("http.proxyHost");
        System.clearProperty("http.proxyPort");

        try {
            Field loadedApkField = applicationContext.getClass().getField("mLoadedApk");
            loadedApkField.setAccessible(true);
            Object loadedApk = loadedApkField.get(applicationContext);
            @SuppressLint("PrivateApi") Class<?> loadedApkClass = Class.forName("android.app.LoadedApk");
            @SuppressLint("DiscouragedPrivateApi") Field receiversField = loadedApkClass.getDeclaredField("mReceivers");
            receiversField.setAccessible(true);
            ArrayMap<?, ?> receivers = (ArrayMap<?, ?>) receiversField.get(loadedApk);

            assert receivers != null;

            for (Object receiverMap : receivers.values()) {
                for (Object receiver : ((ArrayMap<?, ?>) receiverMap).keySet()) {
                    Class<?> receiverClass = receiver.getClass();
                    if (receiverClass.getName().contains("ProxyChangeListener")) {
                        Method onReceiveMethod = receiverClass.getDeclaredMethod("onReceive", Context.class, Intent.class);
                        Intent intent = new Intent(Proxy.PROXY_CHANGE_ACTION);

                        onReceiveMethod.invoke(receiver, applicationContext, intent);
                    }
                }
            }

            Log.d(LOG_TAG, "Successfully set the proxy");
        } catch (NoSuchFieldException | ClassNotFoundException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}
