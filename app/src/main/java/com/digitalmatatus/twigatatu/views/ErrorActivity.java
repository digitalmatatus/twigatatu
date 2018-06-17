package com.digitalmatatus.twigatatu.views;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.widget.Toast;

import com.digitalmatatus.twigatatu.R;

import tr.xip.errorview.ErrorView;

public class ErrorActivity extends AppCompatActivity {

    static {
        AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_AUTO);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

        final ErrorView mErrorView = (ErrorView) findViewById(R.id.error_view);
        mErrorView.setTitle(R.string.error_title_damn)
                .setTitleColor(getResources().getColor(R.color.apptheme_primary))
                .setSubtitle(R.string.error_subtitle_failed_one_more_time)
                .setRetryText(R.string.report)
                .setImage(R.drawable.sadface);

        mErrorView.setRetryListener(new ErrorView.RetryListener() {
            @Override
            public void onRetry() {
                Toast.makeText(ErrorActivity.this, R.string.info_retrying, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getBaseContext(),MainActivity2.class);
                startActivity(intent);

                /*new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                    }
                }, 0);*/
            }
        });
    }
}