package com.chuangju.pathnote;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.chuangju.pathnote.lib.view.MicroClassRecordView;
import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;
import com.zeno.lib.MP3Recorder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;


public class MicroClassRecordActivity extends AppCompatActivity implements OnClickListener, DialogInterface.OnClickListener, OnCancelListener, ImageChooserListener {
    private static final String EXTRA_MICRO_CLASS_FLAG = "EXTRA_MICRO_CLASS_FLAG";
    private static final String EXTRA_MICRO_CLASS_TEXT = "EXTRA_MICRO_CLASS_TEXT";
    public static final String EXTRA_MICRO_CLASS_PATH = "EXTRA_MICRO_CLASS_PATH";
    private static final String EXTRA_MICRO_CLASS_MATERIAL_ARRAY = "EXTRA_MICRO_CLASS_MATERIAL_ARRAY";
    private static final String EXTRA_MICRO_CLASS_QUESTION_ID = "questionId";
    private static final String EXTRA_MICRO_CLASS_QUESTION_BASE_URL = "questionBaseUrl";
    private static final String EXTRA_MICRO_CLASS_QUESTION_CONTENT = "questionContent";
    private static final String TAG = MicroClassRecordActivity.class.getSimpleName();
    private MP3Recorder recorder = new MP3Recorder();
    private File weikeFile;
    private AppDialogFragment transCustomDialog, stopRecordDialogFragment, allowChangeBgDialog;
    private FLAG microClassFlag = FLAG.CHATMESSAGE;
    boolean canChangeBg = true;
    private ImageChooserManager imageChooserManager;
    private File outCropFile;

    private void chooseImage(int requestType) {
        imageChooserManager = new ImageChooserManager(this, requestType);
        imageChooserManager.setImageChooserListener(this);
        try {
            imageChooserManager.choose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public enum FLAG {
        CHATMESSAGE,
        SELFMICROCLASS,
        ANSWERQUESTION,
        ANSWERMICQUESTION
    }

    public static Intent newIntent(Context context, FLAG flag, Uri srcFile) {
        Intent intent = new Intent(context, MicroClassRecordActivity.class);
        intent.putExtra(EXTRA_MICRO_CLASS_FLAG, flag);
        intent.putExtra(EXTRA_MICRO_CLASS_PATH, srcFile.toString());
        return intent;
    }

    public static Intent newIntent(Context context, FLAG flag, String... Material) {
        Intent intent = new Intent(context, MicroClassRecordActivity.class);
        intent.putExtra(EXTRA_MICRO_CLASS_FLAG, flag);
        if (Material != null && Material.length > 0)
            intent.putStringArrayListExtra(EXTRA_MICRO_CLASS_MATERIAL_ARRAY, new ArrayList<>(Arrays.asList(Material)));
        return intent;
    }

    public static Intent newIntent(Context context, String questionId, String baseUrl, String htmlContent) {
        Intent intent = new Intent(context, MicroClassRecordActivity.class);
        intent.putExtra(EXTRA_MICRO_CLASS_FLAG, FLAG.ANSWERMICQUESTION);
        intent.putExtra(EXTRA_MICRO_CLASS_QUESTION_ID, questionId);
        intent.putExtra(EXTRA_MICRO_CLASS_QUESTION_BASE_URL, baseUrl);
        intent.putExtra(EXTRA_MICRO_CLASS_QUESTION_CONTENT, htmlContent);
        return intent;
    }

    public static Intent newIntent(Context context, String withText, FLAG flag, String... Material) {
        Intent intent = new Intent(context, MicroClassRecordActivity.class);
        intent.putExtra(EXTRA_MICRO_CLASS_TEXT, withText);
        intent.putExtra(EXTRA_MICRO_CLASS_FLAG, flag);
        if (Material != null && Material.length > 0)
            intent.putStringArrayListExtra(EXTRA_MICRO_CLASS_MATERIAL_ARRAY, new ArrayList<>(Arrays.asList(Material)));
        return intent;
    }

    public static final String EXTRA_PDF_PATH_STRING = "EXTRA_PDF_PATH_STRING";
    public static final String EXTRA_WHICH_PAGE_STRING = "EXTRA_WHICH_PAGE_STRING";

    public static Intent createIntent(Context context, String pdfPath, int which) {
        Intent intent = new Intent(context, MicroClassRecordActivity.class);
        intent.putExtra(EXTRA_PDF_PATH_STRING, pdfPath);
        intent.putExtra(EXTRA_WHICH_PAGE_STRING, which);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_microclass_record);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        new AlertDialog.Builder(this).setItems(new CharSequence[]{"横屏", "竖屏"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        break;
                    case 1:
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        break;
                }
            }
        }).show();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_main, new MicroClassRecordFragment()).commit();
        findViewById(R.id.recordbtn).setOnClickListener(this);
        microClassFlag = (FLAG) getIntent().getSerializableExtra(EXTRA_MICRO_CLASS_FLAG);
        if (microClassFlag == null) microClassFlag = FLAG.SELFMICROCLASS;
        transCustomDialog = AppDialogFragment.newInstanse(AppDialogType.CLEAR_ALL_PAINT);
        switch (microClassFlag) {
            case ANSWERMICQUESTION:
            case ANSWERQUESTION:
            case SELFMICROCLASS:
                stopRecordDialogFragment = AppDialogFragment.newInstanse(AppDialogType.STOPANDSAVEWEIKERECORD);
                break;
            case CHATMESSAGE:
                stopRecordDialogFragment = AppDialogFragment.newInstanse(AppDialogType.STOPWEIKERECORD);
                break;
        }
        allowChangeBgDialog = AppDialogFragment.newInstanse(AppDialogType.WEIKERECORD_ALLOWCHANGEBG);
    }

    @Override
    public void onBackPressed() {
        pause();
        new AlertDialog.Builder(this)
                .setTitle(R.string.exit_dialog_micro_class_record_title)
                .setOnCancelListener(this)
                .setNegativeButton(R.string.Ok,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                .setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        restore();
                    }
                }).show();
    }

    private int position = 0;
    private Runnable updateRecordTimeRb = new Runnable() {

        @Override
        public void run() {
            if (!recorder.isPaus()) {
                ((TextView) findViewById(R.id.recordtime)).setText(DateFormat.format("mm:ss", position * 1000).toString());
                position++;
            }
            findViewById(R.id.recordtime).postDelayed(this, 1000);
        }
    };

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.recordbtn:
                if (!v.isSelected()) {
                    v.setSelected(true);
                    v.setEnabled(false);
                    v.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            v.setEnabled(true);
                        }
                    }, 3000);
                    position = 0;
                    findViewById(R.id.recordtime).post(updateRecordTimeRb);
                    findViewById(R.id.recordbtn_info_text).setVisibility(View.GONE);
                    startRecord();
                } else {
                    if (recorder.isRecording()) {
                        pause();
                        stopRecordDialogFragment.show(getSupportFragmentManager(), "whether stop record");
                    }
                }
                break;

            default:
                break;
        }
    }

    protected void startRecord() {
        if (!recorder.isRecording()) {
            getMicroClassRecordFragment().stopLoader();
            weikeFile = new File(ChacheFileUtil.create(getApplicationContext()).getCacheWeikeDir(), UUID.randomUUID().toString());
            weikeFile.mkdirs();
            recorder.setFilePath(weikeFile.getPath() + "/" + "_sound" + ".mp3");
            try {
                recorder.startRecording();
                getDrawView().initRecord(System.currentTimeMillis());
                Bitmap bitmap = BitmapUtil.getViewBitmap(getDrawView());
                if (bitmap != null)
                    new AsyncTask<Bitmap, Void, Void>() {
                        @Override
                        protected Void doInBackground(Bitmap... params) {
                            try {
                                Bitmap bitmap = params[0];
                                FileOutputStream fos;
                                fos = new FileOutputStream(new File(weikeFile, "_background.jpg"));
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                                fos.flush();
                                fos.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return null;
                        }
                    }.execute(bitmap);
                supportInvalidateOptionsMenu();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private MicroClassRecordView getDrawView() {
        Fragment recordFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_main);
        if (recordFragment instanceof MicroClassRecordFragment) {
            return ((MicroClassRecordFragment) recordFragment).getDrawView();
        }
        return null;
    }

    private MicroClassRecordFragment getMicroClassRecordFragment() {
        Fragment recordFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_main);
        if (recordFragment instanceof MicroClassRecordFragment) {
            return ((MicroClassRecordFragment) recordFragment);
        }
        return null;
    }

    protected MP3Recorder getrecorder() {
        return recorder;
    }


    private void restore() {
        if (recorder.isPaus() && recorder.isRecording()) {
            recorder.restore();
            getDrawView().restore();
        }
    }

    protected void pause() {
        if (!recorder.isPaus() && recorder.isRecording()) {
            recorder.pause();
            getDrawView().pause();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_microclass_toolbar, menu);
        if (recorder.isRecording()) {
            for (int i = 0; i < menu.size(); i++) {
                if (menu.getItem(i).getItemId() != R.id.action_pen_btn)
                    menu.getItem(i).setVisible(false);
            }
        }
        return true;
    }

//    @Override
//    public void ColorChanged(int color) {
//        Fragment recordFragment = getSupportFragmentManager().findFragmentById(
//                R.id.fragment_main);
//        if (recordFragment instanceof MicroClassRecordFragment) {
//            ((MicroClassRecordFragment) recordFragment).changColor(color);
//        }
//    }
//
//    @Override
//    public void SizeChanged(float size) {
//        Fragment recordFragment = getSupportFragmentManager().findFragmentById(
//                R.id.fragment_main);
//        if (recordFragment instanceof MicroClassRecordFragment) {
//            ((MicroClassRecordFragment) recordFragment).changeSiz(size);
//        }
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
//            case R.id.action_pen_btn:
//                if (findViewById(R.id.menulist).getVisibility() == View.VISIBLE)
//                    findViewById(R.id.menulist).setVisibility(View.GONE);
//                else
//                    findViewById(R.id.menulist).setVisibility(View.VISIBLE);
//                return true;
//            case R.id.action_reply: {
//                final Fragment recordFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_main);
//                if (recordFragment instanceof MicroClassRecordFragment) {
//                    ((MicroClassRecordFragment) recordFragment).Revocation();
//                }
//                return true;
//            }
//            case R.id.action_trash_btn: {
//                new AlertDialog.Builder(this).setTitle(R.string.clear_page_content_message).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Fragment recordFragment = getSupportFragmentManager()
//                                .findFragmentById(R.id.fragment_main);
//                        if (recordFragment instanceof MicroClassRecordFragment) {
//                            ((MicroClassRecordFragment) recordFragment).clear();
//                        }
//                    }
//                }).setNegativeButton(R.string.cancel, null).create().show();
//                return true;
//            }
//            case R.id.action_website_btn: {
//                startActivityForResult(new Intent(this, WeiKeRecordWebCropActivity.class), WeiKeRecordWebCropActivity.REQUEST_CODE);
//                return true;
//            }
//            case R.id.action_bluetooth_btn:
//                if (!penClientCtrl.isConnected()) {
//                    startActivityForResult(new Intent(this, DeviceListActivity.class), 4);
//                } else {
//                    Toast.makeText(this, "设备已链接", Toast.LENGTH_SHORT).show();
//                    if (getMicroClassRecordFragment() != null && !getIntent().hasExtra(EXTRA_PDF_PATH_STRING))
//                        getMicroClassRecordFragment().changeSize(210, 297);
//                }
//                return true;
            case R.id.action_camera_btn:
                chooseImage(ChooserType.REQUEST_CAPTURE_PICTURE);
                return true;
            case R.id.action_pic_btn:
                chooseImage(ChooserType.REQUEST_PICK_PICTURE);
                return true;
//            case R.id.action_background_btn:
//                getBgSeletorPop().showAsDropDown(findViewById(R.id.action_background_btn), -80, 0);
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
//                case 4:
//                    String address;
//                    if ((address = data.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS)) != null) {
//                        penClientCtrl.connect(address);
//                    }
//                    break;
                case ChooserType.REQUEST_CAPTURE_PICTURE:
                case ChooserType.REQUEST_PICK_PICTURE:
                    imageChooserManager.submit(requestCode, data);
                    break;
                case PhotoPicUtil.REQUEST_DO_CROP: {
                    if (outCropFile != null && outCropFile.exists()) {
                        new AsyncTask<File, Void, String>() {
                            ProgressDialog dialog = new ProgressDialog(MicroClassRecordActivity.this);

                            @Override
                            protected void onPreExecute() {
                                super.onPreExecute();
                                dialog.setMessage(getString(R.string.loading));
                                dialog.show();
                            }

                            @Override
                            protected void onPostExecute(String filepath) {
                                super.onPostExecute(filepath);
                                dialog.dismiss();
                                getDrawView().addBitmap(BitmapFactory.decodeFile(filepath));
                            }

                            @Override
                            protected String doInBackground(File... params) {
                                Bitmap bitmap = BitmapUtil.getSmallBitmap(outCropFile.toString(), 1024, 1024);
                                String filepath = outCropFile.getPath();
                                if (bitmap != null) {
                                    try {
                                        FileOutputStream fos = new FileOutputStream(outCropFile);
                                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                                        fos.flush();
                                        fos.close();
                                    } catch (Exception e) {
                                    }
                                }
                                return filepath;
                            }
                        }.execute();
                    }
                    break;
                }
//                case WeiKeRecordWebCropActivity.REQUEST_CODE:
//                    String filepath = data.getStringExtra("filepath");
//                    getDrawView().addBitmap(BitmapFactory.decodeFile(filepath));
//                    break;
                case MicroClassPlayerActivity.requestCode:
                    setResult(
                            RESULT_OK,
                            new Intent().putExtra(EXTRA_MICRO_CLASS_PATH,
                                    weikeFile.getPath()).putExtra(EXTRA_MICRO_CLASS_QUESTION_ID, getIntent().getStringExtra(EXTRA_MICRO_CLASS_QUESTION_ID)));
                    finish();
                    break;
            }
        }
        if (resultCode == Activity.RESULT_CANCELED) {
            if (requestCode == MicroClassPlayerActivity.requestCode) {
                reInitRecord();
            }
        }
    }

    private void reInitRecord() {
        findViewById(R.id.recordbtn_info_text).setVisibility(View.VISIBLE);
        getDrawView().setRecord(false);
        getDrawView().reSet();
        recorder.stopRecording();
        findViewById(R.id.recordtime).removeCallbacks(updateRecordTimeRb);
        ((TextView) findViewById(R.id.recordtime)).setText("00:00");
        findViewById(R.id.recordbtn).setSelected(false);
        supportInvalidateOptionsMenu();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        restore();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (transCustomDialog.getDialog() == dialog) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    getDrawView().clearCanvas();
                    break;
            }
        }
        if (allowChangeBgDialog.getDialog() == dialog) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    canChangeBg = true;
                    break;
            }
        }
        if (stopRecordDialogFragment.getDialog() == dialog) {
            switch (which) {
                case 0:
                    reInitRecord();
                    break;
                case 1:
                    restore();
                    break;
                case 2:
                    if (dialog != null)
                        dialog.dismiss();
                    findViewById(R.id.recordbtn_info_text).removeCallbacks(updateRecordTimeRb);
                    position = 0;
                    findViewById(R.id.recordbtn).setSelected(false);
                    new XmlBuildTask(true).execute();
                    break;
                case 3:
                    if (dialog != null)
                        dialog.dismiss();
                    findViewById(R.id.recordbtn_info_text).removeCallbacks(updateRecordTimeRb);
                    position = 0;
                    findViewById(R.id.recordbtn).setSelected(false);
                    new XmlBuildTask(false).execute();
                    break;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        pause();
        unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        restore();
        IntentFilter filter = new IntentFilter(Const.Broadcast.ACTION_PEN_MESSAGE);
        filter.addAction(Const.Broadcast.ACTION_PEN_DOT);
        filter.addAction("firmware_update");

        registerReceiver(mBroadcastReceiver, filter);
    }

    class XmlBuildTask extends AsyncTask<Void, Canvas, Boolean> {
        private Bitmap bitmap;
        private boolean preview = false;
        List<List<BaseDraw>> list = new ArrayList<>();
        List<BaseDraw> interactiveDrawings;
        private ProgressDialog progressDialog;
        private int viewWidth, viewHeight;
        Canvas canvas;
        private Object lock = new Object();

        public XmlBuildTask(boolean preview) {
            this.preview = preview;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MicroClassRecordActivity.this);
            progressDialog.setMessage(getString(R.string.create_micro_class));
            progressDialog.show();
            getrecorder().stopRecording();
            if (getDrawView() != null) {
                getDrawView().setRecord(false);
                viewWidth = getDrawView().getMeasuredWidth();
                viewHeight = getDrawView().getMeasuredHeight();
                list.clear();
                interactiveDrawings = new ArrayList<>(getDrawView().getRecordList());
            }
        }

//        @Override
//        protected void onProgressUpdate(Canvas... values) {
//            super.onProgressUpdate(values);
//            synchronized (lock) {
//                if (getDrawView() != null)
//                    getDrawView().draw(canvas);
//                lock.notifyAll();
//            }
//        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                bitmap = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888);
                canvas = new Canvas(bitmap);
                if (getDrawView() != null)
                    getDrawView().draw(canvas);
//                synchronized (lock) {
//                    publishProgress();
//                    try {
//                        lock.wait();
//                    } catch (InterruptedException e) {
//                    }
//                }
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                for (int i = 0; i < interactiveDrawings.size(); i++) {
                    interactiveDrawings.get(i).formatPath(width, height);
                }
                list.add(interactiveDrawings);
                if (bitmap.getWidth() > 1024) {
                    width = 1024;
                    height = 1024 * bitmap.getHeight() / bitmap.getWidth();
                }
                String xmlString = WeiKeXmlBuilder.creatXml(list, bitmap.getWidth(), bitmap.getHeight());
                if (!TextUtils.isEmpty(xmlString)) {
                    File xmlFile = new File(weikeFile, "_draw.xml");
                    FileOutputStream xmlOut = new FileOutputStream(xmlFile);
                    byte[] bytes = xmlString.getBytes();
                    xmlOut.write(bytes);
                    xmlOut.close();
                    FileOutputStream fos = new FileOutputStream(new File(weikeFile, "_cover.jpg"));
                    Bitmap.createScaledBitmap(bitmap, width, height, false).compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    fos.close();
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (progressDialog != null)
                progressDialog.dismiss();
            if (!isFinishing()) {
                if (!preview) {
                    if (result)
                        setResult(RESULT_OK, new Intent().putExtra(EXTRA_MICRO_CLASS_PATH, weikeFile.getPath()).putExtra(EXTRA_MICRO_CLASS_QUESTION_ID, getIntent().getStringExtra(EXTRA_MICRO_CLASS_QUESTION_ID)));
                    else
                        setResult(RESULT_CANCELED);
                    finish();
                } else {
                    MicroClassPlayerActivity.FLAG playFlag = null;
                    switch (microClassFlag) {
                        case ANSWERMICQUESTION:
                        case ANSWERQUESTION:
                            playFlag = MicroClassPlayerActivity.FLAG.SAVEMINEWEIKE;
                            break;
                        case CHATMESSAGE:
                            playFlag = MicroClassPlayerActivity.FLAG.SENDCHATWEIKE;
                            break;
                        case SELFMICROCLASS:
                            playFlag = MicroClassPlayerActivity.FLAG.SAVEMINEWEIKE;
                            break;
                    }
                    startActivityForResult(MicroClassPlayerActivity.createIntentByUri(
                            getApplicationContext(), playFlag, "", Uri.fromFile(weikeFile), null, null), MicroClassPlayerActivity.requestCode);
                }
            }
        }

    }

    @Override
    public void onImageChosen(ChosenImage chosenImage) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                outCropFile = PhotoPicUtil.doCropAction(MicroClassRecordActivity.this, new File(image.getFilePathOriginal()), false);
            }
        });
    }

    @Override
    public void onError(String reason) {
        Log.v(TAG, reason);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (Const.Broadcast.ACTION_PEN_MESSAGE.equals(action)) {
                int penMsgType = intent.getIntExtra(Const.Broadcast.MESSAGE_TYPE, 0);
                String content = intent.getStringExtra(Const.Broadcast.CONTENT);

                handleMsg(penMsgType, content);
            } else if (Const.Broadcast.ACTION_PEN_DOT.equals(action)) {
                int sectionId = intent.getIntExtra(Const.Broadcast.SECTION_ID, 0);
                int ownerId = intent.getIntExtra(Const.Broadcast.OWNER_ID, 0);
                int noteId = intent.getIntExtra(Const.Broadcast.NOTE_ID, 0);
                int pageId = intent.getIntExtra(Const.Broadcast.PAGE_ID, 0);
                int x = intent.getIntExtra(Const.Broadcast.X, 0);
                int y = intent.getIntExtra(Const.Broadcast.Y, 0);
                int fx = intent.getIntExtra(Const.Broadcast.FX, 0);
                int fy = intent.getIntExtra(Const.Broadcast.FY, 0);
                int force = intent.getIntExtra(Const.Broadcast.PRESSURE, 0);
                long timestamp = intent.getLongExtra(Const.Broadcast.TIMESTAMP, 0);
                int type = intent.getIntExtra(Const.Broadcast.TYPE, 0);
                int color = intent.getIntExtra(Const.Broadcast.COLOR, 0);

                handleDot(sectionId, ownerId, noteId, pageId, x, y, fx, fy, force, timestamp, type, color);
            } else if (Const.Broadcast.ACTION_PEN_DOT.equals(action)) {
                penClientCtrl.suspendPenUpgrade();
            }
        }
    };

    private void handleDot(int sectionId, int ownerId, int noteId, int pageId, int x, int y, int fx, int fy, int force, long timestamp, int type, int color) {
        Log.e(getClass().getSimpleName(), x + "asdfasf");
        float currentX = ((float) x + (float) ((double) fx * 0.01D)) * 2.371f;
        float currentY = ((float) y + (float) ((double) fy * 0.01D)) * 2.371f;
        switch (type) {
            case 17:
                getMicroClassRecordFragment().getDrawView().penDown(currentX / 210f, currentY / 297f);
                break;
            case 18:
                getMicroClassRecordFragment().getDrawView().penMove(currentX / 210f, currentY / 297f);
                break;
            case 20:
                getMicroClassRecordFragment().getDrawView().penUp(currentX / 210f, currentY / 297f);
                break;
        }
//        mSampleView.addDot( sectionId, ownerId, noteId, pageId, x, y, fx, fy, force, timestamp, type, color );
    }

    private void handleMsg(int penMsgType, String content) {
        Log.d(TAG, "handleMsg : " + penMsgType);

        switch (penMsgType) {
            // Message of the attempt to connect a pen
            case PenMsgType.PEN_CONNECTION_TRY:

                Toast.makeText(this, "尝试链接设备.", Toast.LENGTH_SHORT).show();

                break;

            // Pens when the connection is completed (state certification process is not yet in progress)
            case PenMsgType.PEN_CONNECTION_SUCCESS:

                Toast.makeText(this, "链接设备成功.", Toast.LENGTH_SHORT).show();
                if (!getIntent().hasExtra(EXTRA_PDF_PATH_STRING))
                    getMicroClassRecordFragment().changeSize(210, 297);
                break;

            // Message when a connection attempt is unsuccessful pen
            case PenMsgType.PEN_CONNECTION_FAILURE:

                Toast.makeText(this, "链接设备失败.", Toast.LENGTH_SHORT).show();

                break;

            // When you are connected and disconnected from the state pen
            case PenMsgType.PEN_DISCONNECTED:

                Toast.makeText(this, "链接已被关闭.", Toast.LENGTH_SHORT).show();

                break;
        }
    }
}
