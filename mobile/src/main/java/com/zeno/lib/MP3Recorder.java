package com.zeno.lib;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MP3Recorder {
    private static final String TAG = MP3Recorder.class.getSimpleName();

    static {
        System.loadLibrary("mp3lame");
    }

    private static final int DEFAULT_SAMPLING_RATE = 22050;

    private static final int FRAME_COUNT = 160;
    private boolean isPause = false;
    /* Encoded bit rate. MP3 file will be encoded with bit rate 32kbps */
    private static final int BIT_RATE = 32;

    private AudioRecord audioRecord = null;

    private int bufferSize;

    private File mp3File;

    private RingBuffer ringBuffer;

    private byte[] buffer;

    private FileOutputStream os = null;

    private DataEncodeThread encodeThread;

    private int samplingRate;

    private int channelConfig;

    private PCMFormat audioFormat;
    private String filePath;
    private boolean isRecording = false;
    private boolean release = true;

    public boolean isrelease() {
        return release;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public File getRecordFile() {
        File file = new File(filePath);
        if (file.exists())
            return file;
        return null;
    }

    public MP3Recorder(int samplingRate, int channelConfig,
                       PCMFormat audioFormat) {
        this.samplingRate = samplingRate;
        this.channelConfig = channelConfig;
        this.audioFormat = audioFormat;
    }

    public boolean isRecording() {
        return isRecording ;
    }

    /**
     * Default constructor. Setup recorder with default sampling rate 1 channel,
     * 16 bits pcm
     */
    public MP3Recorder() {
        this(DEFAULT_SAMPLING_RATE, AudioFormat.CHANNEL_IN_MONO, PCMFormat.PCM_16BIT);
    }

    /**
     * Start recording. Create an encoding thread. Start record from this
     * thread.
     *
     * @throws IOException
     */
    public void startRecording() throws IOException {
        if (isRecording)
            return;
        Log.d(TAG, "Start recording");
        Log.d(TAG, "BufferSize = " + bufferSize);
        // Initialize audioRecord if it's null.
        if (audioRecord == null) {
            initAudioRecorder();
        }
        audioRecord.startRecording();
        release = false;
        new Thread() {

            @Override
            public void run() {
                isRecording = true;
                while (isRecording) {
                    if (isPause)
                        continue;
                    int bytes = audioRecord.read(buffer, 0, bufferSize);
                    if (bytes > 0) {
                        ringBuffer.write(buffer, bytes);
                    }
                }
                try {
                    if (audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING)
                        audioRecord.stop();
                    audioRecord.release();
                    audioRecord = null;
                    Message msg = Message.obtain(encodeThread.getHandler(),
                            DataEncodeThread.PROCESS_STOP);
                    msg.sendToTarget();

                    Log.d(TAG, "waiting for encoding thread");
                    encodeThread.join();
                    Log.d(TAG, "done encoding thread");
                } catch (Exception e) {
                    Log.d(TAG, "Faile to join encode thread");
                    if (audioRecord != null) {
                        audioRecord.release();
                        audioRecord = null;
                    }
                } finally {
                    if (os != null) {
                        try {
                            os.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    release = true;
                }

            }
        }.start();
    }

    public void pause() {
        isPause = true;
    }

    public void restore() {
        isPause = false;
    }

    public boolean isPaus() {
        if (!isRecording) {
            return false;
        }
        return isPause;
    }

    public void release() {
        if (audioRecord != null) {
            audioRecord.release();
            audioRecord = null;
        }
    }

    /**
     * @throws IOException
     */
    public void stopRecording() {
        Log.d(TAG, "stop recording");
        isRecording = false;
        isPause=false;
    }

    /**
     * Initialize audio recorder
     */
    private void initAudioRecorder() throws IOException {
        int bytesPerFrame = audioFormat.getBytesPerFrame();
        /*
		 * Get number of samples. Calculate the buffer size (round up to the
		 * factor of given frame size)
		 */
        int frameSize = AudioRecord.getMinBufferSize(samplingRate,channelConfig, audioFormat.getAudioFormat()) / bytesPerFrame;
        if (frameSize % FRAME_COUNT != 0) {
            frameSize = frameSize + (FRAME_COUNT - frameSize % FRAME_COUNT);
            Log.d(TAG, "Frame size: " + frameSize);
        }

        bufferSize = frameSize * bytesPerFrame;
		/* Setup audio recorder */
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                samplingRate, channelConfig, audioFormat.getAudioFormat(),
                bufferSize);

        // Setup RingBuffer. Currently is 10 times size of hardware buffer
        // Initialize buffer to hold data
        ringBuffer = new RingBuffer(10 * bufferSize);
        buffer = new byte[bufferSize];

        // Initialize lame buffer
        // mp3 sampling rate is the same as the recorded pcm sampling rate
        // The bit rate is 32kbps
        init(samplingRate, 1, samplingRate, BIT_RATE);

        // Initialize the place to put mp3 file
        mp3File = new File(getFilePath());
        os = new FileOutputStream(mp3File);

        // Create and run thread used to encode data
        // The thread will
        encodeThread = new DataEncodeThread(ringBuffer, os, bufferSize);
        encodeThread.start();
        audioRecord.setRecordPositionUpdateListener(encodeThread,
                encodeThread.getHandler());
        audioRecord.setPositionNotificationPeriod(FRAME_COUNT);
    }

    public static void init(int inSamplerate, int outChannel,
                            int outSamplerate, int outBitrate) {
        init(inSamplerate, outChannel, outSamplerate, outBitrate, 7);
    }

    public native static void init(int inSamplerate, int outChannel,
                                   int outSamplerate, int outBitrate, int quality);

    public native static int encode(short[] buffer_l, short[] buffer_r,
                                    int samples, byte[] mp3buf);

    public native static int flush(byte[] mp3buf);

    public native static void close();
}
