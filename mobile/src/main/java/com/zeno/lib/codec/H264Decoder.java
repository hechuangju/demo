package com.zeno.lib.codec;

public class H264Decoder {
	int width, height;
	float fps;

	public static H264Decoder newInstanse(int w, int h, int fps) {
		H264Decoder h264Encoder = new H264Decoder(w, h, fps);
		h264Encoder.height = h;
		h264Encoder.width = w;
		h264Encoder.fps = fps;
		h264Encoder.open(w, h, fps);
		return h264Encoder;
	}

	H264Decoder(int w, int h, int pts) {
		super();
	}

	// / Error
	public static final int ERR_NONE = 0;
	public static final int ERR_OPENCODEC_FAILED = -1001;
	public static final int ERR_ALLOCMEMORY_FAILED = -1002;
	public static final int ERR_SWSCONTEXT_FAILED = -1003;
	public static final int ERR_INVALID_DATA = -1004;
	static {
		System.loadLibrary("ffmpeg");
		System.loadLibrary("shrek");
	}

	public void Open() throws EncodeException {
		int openTag = open(width, height, fps);
		switch (openTag) {
		case ERR_OPENCODEC_FAILED:
			throw new EncodeException("ERR_OPENCODEC_FAILED");
		case ERR_ALLOCMEMORY_FAILED:
			throw new EncodeException("ERR_ALLOCMEMORY_FAILED");
		default:
			break;
		}
	}

	native int open(int w, int h, float fps);
}
