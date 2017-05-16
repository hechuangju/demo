package com.zeno.lib.media;

import android.util.Log;
import android.util.SparseArray;

import java.nio.ByteBuffer;

public class FrameItem {
	private int totalSize, revSize;
	private SparseArray<byte[]> FrameSubData;

	public boolean addSubData(int serial, byte[] data) {
		if (FrameSubData.get(serial) == null) {
			FrameSubData.put(serial, data);
			revSize += data.length;
			Log.v("FrameItem", "收到" + data.length + "数据,总长为" + totalSize);
		}
		return FrameBuiled();
	}

	public FrameItem(int totalSize) {
		super();
		this.totalSize = totalSize;
		FrameSubData = new SparseArray<byte[]>();
	}

	private boolean FrameBuiled() {
		return revSize == totalSize;
	}

	public byte[] getData() {
		ByteBuffer buffer = ByteBuffer.allocate(totalSize);
		buffer.clear();
		for (int i = 0; i < FrameSubData.size(); i++) {
			buffer.put(FrameSubData.get(i));
		}
		return buffer.array();
	}
}
