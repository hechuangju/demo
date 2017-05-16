package com.chuangju.pathnote;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.chuangju.pathnote.lib.view.MicroClassPlayView;


public class FullscreenActivity extends AppCompatActivity {

    protected static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    View controlsView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        controlsView = findViewById(R.id.fullscreen_content_controls);
        final MicroClassPlayView contentView = (MicroClassPlayView) findViewById(R.id.draw_convas_view);
        contentView.setOnClickListener(contentClickListener);
        findViewById(R.id.fragment_main).setOnClickListener(contentClickListener);
    }

    private View.OnClickListener contentClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            toolbar.bringToFront();
            controlsView.bringToFront();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                int mControlsHeight = controlsView.getHeight();
                int mShortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
                toolbar.animate().translationY(!getSupportActionBar().isShowing() ? 0 : -mControlsHeight).setDuration(mShortAnimTime);
                controlsView.animate().translationY(!getSupportActionBar().isShowing() ? 0 : mControlsHeight).setDuration(mShortAnimTime);
            } else {
                toolbar.setVisibility(!getSupportActionBar().isShowing() ? View.VISIBLE : View.GONE);
                controlsView.setVisibility(!getSupportActionBar().isShowing() ? View.VISIBLE : View.GONE);
            }
            if (!getSupportActionBar().isShowing()) {
                getSupportActionBar().show();
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            } else
                getSupportActionBar().hide();
        }
    };

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        delayedHide(AUTO_HIDE_DELAY_MILLIS);
    }

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                int mControlsHeight = controlsView.getHeight();
                int mShortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
                toolbar.animate().translationY(-mControlsHeight).setDuration(mShortAnimTime);
                controlsView.animate().translationY(mControlsHeight).setDuration(mShortAnimTime);
            } else {
                toolbar.setVisibility(!getSupportActionBar().isShowing() ? View.VISIBLE : View.GONE);
                controlsView.setVisibility(!getSupportActionBar().isShowing() ? View.VISIBLE : View.GONE);
            }
            getSupportActionBar().hide();
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
