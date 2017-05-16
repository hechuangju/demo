package com.zeno.lib.media;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class RTPHeader {

	public static final short Sign = 0x5852;
	private short sign = Sign;
	private int size;
	private short payloadtype;
	private short serial;
	private short seqnum;
	private int totalsize;
	private int srcid;
	private int dstid;
	private int channel;

	public RTPHeader() {
		super();
	}

	public RTPHeader(int size, short payloadtype, short serial, short seqnum,
			int totalsize, int srcid, int dstid, int channel) {
		super();
		this.size = size;
		this.payloadtype = payloadtype;
		this.serial = serial;
		this.seqnum = seqnum;
		this.totalsize = totalsize;
		this.srcid = srcid;
		this.dstid = dstid;
		this.channel = channel;
	}

	public byte[] creatData() {
		ByteBuffer buffer = ByteBuffer.allocate(sizeOfHeader());
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.clear();
		buffer.putShort(sign);
		buffer.putInt(size);
		buffer.putShort(payloadtype);
		buffer.putShort(serial);
		buffer.putShort(seqnum);
		buffer.putInt(totalsize);
		buffer.putInt(srcid);
		buffer.putInt(dstid);
		buffer.putInt(channel);
		return buffer.array();
	}

	public void setSign(short sign) {
		this.sign = sign;
	}

	public static RTPHeader readHeader(byte[] data) {
		RTPHeader header = new RTPHeader();
		ByteBuffer buffer = ByteBuffer.wrap(data);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.clear();
		header.setSign(buffer.getShort());
		header.setSize(buffer.getInt());
		header.setPayloadtype(buffer.getShort());
		header.setSerial(buffer.getShort());
		header.setSeqnum(buffer.getShort());
		header.setTotalsize(buffer.getInt());
		header.setSrcid(buffer.getInt());
		header.setDstid(buffer.getInt());
		header.setChannel(buffer.getInt());
		return header;
	}

	public static int sizeOfHeader() {
		return 28;
	}

	public short getSign() {
		return sign;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public short getPayloadtype() {
		return payloadtype;
	}

	public void setPayloadtype(short payloadtype) {
		this.payloadtype = payloadtype;
	}

	public short getSerial() {
		return serial;
	}

	public void setSerial(short serial) {
		this.serial = serial;
	}

	public short getSeqnum() {
		return seqnum;
	}

	public void setSeqnum(short seqnum) {
		this.seqnum = seqnum;
	}

	public int getTotalsize() {
		return totalsize;
	}

	public void setTotalsize(int totalsize) {
		this.totalsize = totalsize;
	}

	public int getSrcid() {
		return srcid;
	}

	public void setSrcid(int srcid) {
		this.srcid = srcid;
	}

	public int getDstid() {
		return dstid;
	}

	public void setDstid(int dstid) {
		this.dstid = dstid;
	}

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}

}
