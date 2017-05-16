package com.zeno.lib.model;

public class OutFrame {
	private int keyFrame;
	private int OutSize;
	private byte[] OutData;

	public OutFrame() {
	}

	public OutFrame(int keyFrame, int outSize, byte[] outData) {
		super();
		this.keyFrame = keyFrame;
		OutSize = outSize;
		OutData = outData;
	}

	public boolean isEmpty() {
		return (this == null || OutSize <= 0 || OutData == null || OutData.length < OutSize);
	}

	public int isKeyFrame() {
		return keyFrame;
	}

	public void setKeyFrame(int keyFrame) {
		this.keyFrame = keyFrame;
	}

	public int getOutSize() {
		return OutSize;
	}

	public void setOutSize(int outSize) {
		OutSize = outSize;
	}

	public byte[] getOutData() {
		return OutData;
	}

	public void setOutData(byte[] outData) {
		OutData = outData;
	}

	@Override
	public String toString() {
		return "OutFrame [keyFrame=" + keyFrame + ", OutSize=" + OutSize
				+ ", OutData=" + OutData.length + "]";
	}

}
