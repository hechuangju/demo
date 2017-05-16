package com.chuangju.pathnote;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chuangju.pathnote.lib.view.MicroClassRecordView;

import java.util.ArrayList;

public class MicroClassRecordFragment extends Fragment {
    private static final String EXTRA_MICROCLASS_TEXT = "EXTRA_MICROCLASS_TEXT";
    private static final String EXTRA_MICROCLASS_PATH = "EXTRA_MICRO_CLASS_PATH";
    private static final String EXTRA_MICROCLASS_MATERIAL_ARRAY = "EXTRA_MICRO_CLASS_MATERIAL_ARRAY";
    private static final String EXTRA_QUESTION_BASE_URL = "EXTRA_QUESTION_BASE_URL";
    private static final String EXTRA_QUESTION_CONTENT = "EXTRA_QUESTION_CONTENT";
    private MicroClassRecordView microClassRecordView;

    public static MicroClassRecordFragment create(String srcFile, String text, ArrayList<String> meterialArray, String baseUrl, String htmlContent) {
        MicroClassRecordFragment microClassRecordFragment = new MicroClassRecordFragment();
        Bundle mBundle = new Bundle();
        if (!TextUtils.isEmpty(srcFile))
            mBundle.putString(EXTRA_MICROCLASS_PATH, srcFile);
        if (!TextUtils.isEmpty(text))
            mBundle.putString(EXTRA_MICROCLASS_TEXT, text);
        if (meterialArray != null && meterialArray.size() > 0)
            mBundle.putStringArrayList(EXTRA_MICROCLASS_MATERIAL_ARRAY, meterialArray);
        if (!TextUtils.isEmpty(baseUrl))
            mBundle.putString(EXTRA_QUESTION_BASE_URL, baseUrl);
        if (!TextUtils.isEmpty(htmlContent))
            mBundle.putString(EXTRA_QUESTION_CONTENT, htmlContent);
        microClassRecordFragment.setArguments(mBundle);
        return microClassRecordFragment;
    }

    public static MicroClassRecordFragment newInstance() {
        MicroClassRecordFragment fragment = new MicroClassRecordFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_microclass_record, container, false);
        microClassRecordView = (MicroClassRecordView) view.findViewById(R.id.microRecordView);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onViewCreated(view, savedInstanceState);
    }

    public void changColor(int color) {
        microClassRecordView.setPaintColor(color);
    }

    public void changeSiz(float size) {
        microClassRecordView.setPaintWidth(size);
    }

    public void Revocation() {
        microClassRecordView.Revocation();
    }

    public void clear() {
        microClassRecordView.reSet();
    }

    public MicroClassRecordView getDrawView() {
        return microClassRecordView;
    }

    private volatile boolean stopLoader = false;

    public void stopLoader() {
        stopLoader = true;
    }

    public void changeBgColor(int color) {
        microClassRecordView.setBackgroundColor(color);
    }

    public void changeSize(int width, int height) {
        microClassRecordView.setBackLayout(width, height);
    }
}
