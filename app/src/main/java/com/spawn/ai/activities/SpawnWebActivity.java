package com.spawn.ai.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.spawn.ai.R;

public class SpawnWebActivity extends AppCompatActivity {

    private WebView infoWebview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spawn_web);
        infoWebview = (WebView) findViewById(R.id.info_wiki);

        WebSettings webSettings = infoWebview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setBuiltInZoomControls(true);
        android.content.Intent intent = getIntent();
        String url = intent.getStringExtra("url");

        infoWebview.loadUrl(url);

    }
}
