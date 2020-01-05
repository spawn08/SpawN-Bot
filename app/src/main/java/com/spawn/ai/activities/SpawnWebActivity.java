package com.spawn.ai.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.spawn.ai.R;
import com.spawn.ai.utils.views.DotProgressBar;

public class SpawnWebActivity extends AppCompatActivity {

    private WebView infoWebview;
    private DotProgressBar dotProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spawn_web);
        infoWebview = (WebView) findViewById(R.id.info_wiki);
        dotProgressBar = (DotProgressBar) findViewById(R.id.web_loading);


        WebSettings webSettings = infoWebview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setPluginState(WebSettings.PluginState.ON_DEMAND);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setAppCacheEnabled(false);
        infoWebview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        CookieManager.getInstance().setAcceptCookie(true);
        android.content.Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        infoWebview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (request.getUrl().toString().contains("youtube.com")) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(request.getUrl().toString())));
                    finish();
                    return true;
                }

                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                dotProgressBar.setVisibility(View.GONE);
                infoWebview.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                dotProgressBar.setVisibility(View.VISIBLE);
                infoWebview.setVisibility(View.GONE);
            }
        });

        infoWebview.loadUrl(url);

    }
}
