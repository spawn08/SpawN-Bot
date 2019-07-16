package com.spawn.ai.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.spawn.ai.R;
import com.spawn.ai.utils.DotProgressBar;

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
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        android.content.Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        infoWebview.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        infoWebview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                dotProgressBar.setVisibility(View.GONE);
                infoWebview.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                dotProgressBar.setVisibility(View.VISIBLE);
                infoWebview.setVisibility(View.GONE);
            }
        });

        infoWebview.loadUrl(url);

    }
}
