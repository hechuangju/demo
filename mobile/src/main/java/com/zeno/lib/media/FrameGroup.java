package com.zeno.lib.media;

import android.util.SparseArray;

public class FrameGroup {
	private int keyFrameNum;
	private int reciveIndex=0;
	public int getReciveIndex() {
		return reciveIndex;
	}

	public void setReciveIndex(int reciveIndex) {
		this.reciveIndex = reciveIndex;
	}

	private SparseArray<FrameItem> Frames;
	private boolean keyReceived=false;
	public FrameGroup(int keyFrameNum) {
		super();
		this.keyFrameNum = keyFrameNum;
		Frames = new SparseArray<FrameItem>();
	}

	public int getKeyFrameNum() {
		return keyFrameNum;
	}

	public SparseArray<FrameItem> getFrames() {
		return Frames;
	}

	public void setFrames(SparseArray<FrameItem> frames) {
		Frames = frames;
	}

	public boolean isKeyReceived() {
		return keyReceived;
	}

	public void setKeyReceived(boolean keyReceived) {
		this.keyReceived = keyReceived;
	}

}
