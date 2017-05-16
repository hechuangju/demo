package com.zeno.lib.codec;

import com.zeno.lib.model.OutFrame;

public class H264Encoder {
	public static H264Encoder newInstanse(int w, int h, int fps) {
		H264Encoder h264Encoder = new H264Encoder(w, h, fps);
		h264Encoder.height = h;
		h264Encoder.width = w;
		h264Encoder.fps = fps;
		int Tag = h264Encoder.openEncoder(w, h, fps);
		return h264Encoder;
	}

	int width, height;
	float fps;

	H264Encoder(int w, int h, int pts) {
		super();
	}

	// / Error
	public static final int ERR_NONE = 0;
	public static final int ERR_OPENCODEC_FAILED = -1001; // �򿪱�����ʧ��
	public static final int ERR_ALLOCMEMORY_FAILED = -1002; // �����ڴ�ʧ��
	public static final int ERR_SWSCONTEXT_FAILED = -1003; // ȡת������ʧ��
	public static final int ERR_INVALID_DATA = -1004; // ��ݴ���
	static {
		System.loadLibrary("ffmpeg");
		System.loadLibrary("shrek");
	}

	native int openDecoder(int w, int h, float fps);

	native int openEncoder(int w, int h, float fps);

	public native int close();

	public native OutFrame decoder(byte[] In, int in_len, byte[] outBuf,
			int outbuf_size);

	public native OutFrame Encode(byte[] In, int in_len, byte[] outBuf,
			int outbuf_size);
}
