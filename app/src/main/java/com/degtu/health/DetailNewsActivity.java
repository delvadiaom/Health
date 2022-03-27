package com.degtu.health;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.degtu.health.databinding.ActivityDetailNewsBinding;

public class DetailNewsActivity extends AppCompatActivity {

    ActivityDetailNewsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailNewsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        binding.webview.setWebViewClient(new WebViewClient());
        binding.webview.loadUrl(url);
    }
}