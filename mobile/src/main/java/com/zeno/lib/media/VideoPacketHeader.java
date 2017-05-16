package com.zeno.lib.media;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class VideoPacketHeader {
	private int keynum;
	private int frmnum;
	private int offset;
	public VideoPacketHeader() {
		super();
	}

	public VideoPacketHeader(int keynum, int frmnum, int offset) {
		super();
		this.keynum = keynum;
		this.frmnum = frmnum;
		this.offset = offset;
	}

	public static int sizeOfHeader() {
		return 12;
	}

	public byte[] creatData() {
		ByteBuffer buffer = ByteBuffer.allocate(12);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.clear();
		buffer.putInt(keynum);
		buffer.putInt(frmnum);
		buffer.putInt(offset);
		return buffer.array();
	}

	public static VideoPacketHeader readData(byte[] data) {
		if (data == null || data.length < 12)
			return null;
		ByteBuffer buffer = ByteBuffer.wrap(data);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.clear();
		VideoPacketHeader header = new VideoPacketHeader();
		header.setKeynum(buffer.getInt());
		header.setFrmnum(buffer.getInt());
		header.setOffset(buffer.getInt());
		return header;
	}

	public int getKeynum() {
		return keynum;
	}

	public void setKeynum(int keynum) {
		this.keynum = keynum;
	}

	public int getFrmnum() {
		return frmnum;
	}

	public void setFrmnum(int frmnum) {
		this.frmnum = frmnum;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

}
