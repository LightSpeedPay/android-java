package com.in.lightspeedpay.pay.ui;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.in.lightspeedpay.pay.R;

public class PaymentScreenActivity extends AppCompatActivity {



    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment_screen);

        String pageUrl = getIntent().getStringExtra("pageUrl");

        assert pageUrl != null;
        Log.d("pageUrl", pageUrl);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) WebView webView = findViewById(R.id.webView);
        webView.loadUrl(pageUrl);
        // enable js and dom storage
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);


        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView wv, String url) {

                if (url.contains("pay?") ) {

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Log.d(TAG, "transaction url" + url);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                    return true;
                }

                if(url.contains("status=COMPLETED")) {

                        // destroy the webview
                    webView.loadUrl("about:blank");
                    webView.clearCache(true);
                    webView.clearHistory();
                    webView.clearFormData();
                    webView.clearSslPreferences();
                    webView.destroy();
                    finish();

                        Intent intent = new Intent(PaymentScreenActivity.this, SuccessActivity.class);
                        intent.putExtra("status", "COMPLETED");
                        startActivity(intent);

                        return true;
                }

                if (url.contains("status=FAILED")) {

                    // close the current activity
                    webView.loadUrl("about:blank");
                    webView.clearCache(true);
                    webView.clearHistory();
                    webView.clearFormData();
                    webView.clearSslPreferences();
                    webView.destroy();
                    finish();

                    Intent intent = new Intent(PaymentScreenActivity.this, FailActivity.class);
                    intent.putExtra("status", "FAILED");
                    startActivity(intent);
                    return true;
                }



                return false;

            }

        });
    }



}