package com.chuangju.pathnote;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.chuangju.pathnote.lib.view.MicroClassPlayView;

import java.io.File;
import java.io.FileInputStream;

public class MicroClassPlayerActivity extends FullscreenActivity implements OnClickListener {
    private static final String EXTRA_MICRO_CLASS_FLAG = "com.shrek.youshi.MicroClassPlayerActivity.EXTRA_MICRO_CLASS_FLAG";
    private static final String EXTRA_MICRO_CLASS_URI = "com.shrek.youshi.MicroClassPlayerActivity.EXTRA_MICRO_CLASS_URI";
    private static final String EXTRA_MICRO_CLASS_NAME = "com.shrek.youshi.MicroClassPlayerActivity.EXTRA_MICRO_CLASS_NAME";
    private static final String EXTRA_MICRO_CLASS_SHARE_UUID = "com.shrek.youshi.MicroClassPlayerActivity.EXTRA_MICRO_CLASS_SHARE_UUID";
    private static final String EXTRA_MICRO_CLASS_SHARE_COVER = "com.shrek.youshi.MicroClassPlayerActivity.EXTRA_MICRO_CLASS_SHARE_COVER";
    protected final String TAG = MicroClassPlayerActivity.class.getSimpleName();
    private MicroClassPlayView microClassPlayView;
    private Handler checkHandler = new Handler();
    private SeekBar AudioSeekBar = null;
    private TextView currentTimeText, durationText;
    private ImageButton controlButton;
    private String shareUUid;
    private String shareCover;
    private String weikePath;
    public static final int requestCode = 577;

    public static final Intent createIntentByUri(Context context, String name, Uri uri, String shareUuid, String shareCover) {
        Intent intent = new Intent(context, MicroClassPlayerActivity.class);
        intent.putExtra(EXTRA_MICRO_CLASS_URI, uri.toString());
        intent.putExtra(EXTRA_MICRO_CLASS_NAME, name);
        intent.putExtra(EXTRA_MICRO_CLASS_SHARE_UUID, shareUuid);
        intent.putExtra(EXTRA_MICRO_CLASS_SHARE_COVER, shareCover);
        return intent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.microclass_play_menu_needsavemineweike, menu);
        return true;
    }

    private static final int requestMicroclasssCode = 4324;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                setResult(RESULT_OK);
                finish();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case requestMicroclasssCode:
                    setResult(RESULT_OK, data);
                    finish();
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private File srcFile;

    public void finishDown(File file) {
        final String path = getIntent().getStringExtra(EXTRA_MICRO_CLASS_URI);
        findViewById(R.id.load_view).setVisibility(View.GONE);
        try {
            if (file != null) {
                String dirFile = file.getPath();
                srcFile = new File(dirFile);
                supportInvalidateOptionsMenu();
                microClassPlayView.setupFile(dirFile);
                microClassPlayView.setScaleEnable(true);
                controlButton.setEnabled(true);
                AudioSeekBar.setEnabled(true);
                playMicroClass();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), R.string.unknow_error, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private MediaPlayer mediaPlayer;
    private boolean AudioUseAble = true;

    private void playMicroClass() {
        File file = new File(microClassPlayView.getVoicePath());
        if (file.exists()) {
            try {
                FileInputStream fis = new FileInputStream(file);
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDataSource(fis.getFD());
                fis.close();
                mediaPlayer.prepare();
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        controlButton.setSelected(false);
                    }
                });
                AudioSeekBar.setMax(mediaPlayer.getDuration());
                AudioSeekBar.setOnSeekBarChangeListener(changeListener);
                checkHandler.post(updateProgressRunnable);
            } catch (Exception e) {
                mediaPlayer.reset();
                mediaPlayer.release();
                AudioUseAble = false;
                if (microClassPlayView.getDuration() > 0) {
                    AudioSeekBar.setMax((int) microClassPlayView.getDuration());
                    AudioSeekBar.setOnSeekBarChangeListener(changeListener);
                    checkHandler.post(updateProgressRunnable);
                }
            }
        } else {
            Toast.makeText(getApplicationContext(), R.string.filenotFound, Toast.LENGTH_SHORT).show();
        }
    }

    private OnSeekBarChangeListener changeListener = new OnSeekBarChangeListener() {

        @Override
        public void onStopTrackingTouch(final SeekBar seekBar) {
            // TODO Auto-generated method stub
            final int progress = seekBar.getProgress();
            delayedHide(AUTO_HIDE_DELAY_MILLIS);
            microClassPlayView.post(new Runnable() {
                @Override
                public void run() {
                    microClassPlayView.clearCanvas();
                    microClassPlayView.seekTo(progress);
                    microClassPlayView.invalidate();
                    if (AudioUseAble)
                        mediaPlayer.seekTo(progress);
                    else
                        currentPosition = progress;
                    if (AudioUseAble)
                        mediaPlayer.start();
                    checkHandler.post(updateProgressRunnable);
                    currentTimeText.setText(getTimeString(progress));
                    durationText.setText(getTimeString(seekBar.getMax() - progress));
                }
            });
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub
            if (AudioUseAble)
                mediaPlayer.pause();
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, final int progress,
                                      boolean fromUser) {
            // TODO Auto-generated method stub

        }
    };
    private int currentPosition;
    private Runnable updateProgressRunnable = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            if (AudioUseAble) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    microClassPlayView.updatePosition(mediaPlayer.getCurrentPosition());
                    controlButton.post(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            controlButton.setSelected(true);
                            AudioSeekBar.setProgress(mediaPlayer.getCurrentPosition());
                            currentTimeText.setText(getTimeString(mediaPlayer.getCurrentPosition()));
                            durationText.setText(getTimeString(mediaPlayer.getDuration() - mediaPlayer.getCurrentPosition()));
                        }
                    });
                    checkHandler.postDelayed(this, 50);
                }
            } else if (microClassPlayView.getDuration() > 0) {
                if (currentPosition > microClassPlayView.getDuration()) {
                    controlButton.setSelected(false);
                    currentPosition = 0;
                    return;
                }
                microClassPlayView.updatePosition(currentPosition);
                controlButton.post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        controlButton.setSelected(true);
                        AudioSeekBar.setProgress(currentPosition);
                        currentTimeText.setText(getTimeString(currentPosition));
                        durationText.setText(getTimeString(microClassPlayView.getDuration() - currentPosition));
                    }
                });
                currentPosition += 50;
                checkHandler.postDelayed(this, 50);
            }
        }
    };

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if (AudioUseAble) {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                controlButton.setSelected(false);
            }
        } else {
            controlButton.setSelected(false);
            checkHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AudioUseAble) {
            if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                microClassPlayView.post(new Runnable() {

                    @Override
                    public void run() {
                        microClassPlayView.reSet();
                        mediaPlayer.start();
                        checkHandler.post(updateProgressRunnable);
                    }
                });
            }
        } else {
            microClassPlayView.post(new Runnable() {

                @Override
                public void run() {
                    microClassPlayView.reSet();
                    checkHandler.post(updateProgressRunnable);
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (checkHandler != null) {
            checkHandler.removeCallbacksAndMessages(null);
            checkHandler = null;
        }
        super.onDestroy();
    }

    private String getTimeString(long millis) {
        StringBuffer buf = new StringBuffer();

        int hours = (int) (millis / (1000 * 60 * 60));
        int minutes = (int) ((millis % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) (((millis % (1000 * 60 * 60)) % (1000 * 60)) / 1000);

        buf.append(String.format("%02d", hours)).append(":")
                .append(String.format("%02d", minutes)).append(":")
                .append(String.format("%02d", seconds));

        return buf.toString();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.controlButton:
                v.setSelected(!v.isSelected());
                if (v.isSelected()) {
                    microClassPlayView.reSet();
                    if (AudioUseAble)
                        mediaPlayer.start();
                    checkHandler.post(updateProgressRunnable);
                } else {
                    if (AudioUseAble)
                        mediaPlayer.pause();
                }
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        microClassPlayView = (MicroClassPlayView) findViewById(R.id.draw_convas_view);
        AudioSeekBar = (SeekBar) findViewById(R.id.currenttime_seekBar);
        currentTimeText = (TextView) findViewById(R.id.currenttime_textView);
        durationText = (TextView) findViewById(R.id.duration_textView);
        controlButton = (ImageButton) findViewById(R.id.controlButton);
        controlButton.setOnClickListener(this);
        controlButton.setEnabled(false);
        AudioSeekBar.setEnabled(false);
        Intent intent = getIntent();
        weikePath = intent.getStringExtra(EXTRA_MICRO_CLASS_URI);
        shareUUid = intent.getStringExtra(EXTRA_MICRO_CLASS_SHARE_UUID);
        shareCover = intent.getStringExtra(EXTRA_MICRO_CLASS_SHARE_COVER);
        final Uri microClassUri = Uri.parse(weikePath);
        finishDown(new File(microClassUri.getPath()));
    }
}
