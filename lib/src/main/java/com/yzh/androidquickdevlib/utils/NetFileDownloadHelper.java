package com.yzh.androidquickdevlib.utils;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetFileDownloadHelper {
    private static final int START_DOWNLOAD = 1;
    private static final int DOWNLOADING = 2;
    private static final int DOWNLOAD_FINISH = 3;
    private static final int DOWNLOAD_CANCELLED = 4;
    private static final int DOWNLOAD_EXCEPTION = 5;

    private static final int ERR_SD_CARD_NOT_READY = 501;
    private static final int ERR_BAD_NEWWORK = 502;
    private static final int ERR_UNKNOWN_ERROR = 503;

    private String fileUrl;
    private String fileSavePath;
    private FileDownloadThread mDownloadThread = null;
    IDownLoadStatusListener downLoadStatusListener;

    public interface IDownLoadStatusListener {
        void onStart(NetFileDownloadHelper netFileDownloadHelper);

        void onCanceled();

        void onError(String msg);

        void onFinished(String fileSavePath);

        void onProgressUpdated(int percentage);
    }

    public NetFileDownloadHelper(String fileUrl, String fileSavePath) {
        this.fileUrl = fileUrl;
        this.fileSavePath = fileSavePath;
    }

    /**
     * 开始下载
     */
    public void start() {
        cancel();
        this.mDownloadThread = new FileDownloadThread();
        this.mDownloadThread.start();
    }

    /**
     * 取消下载
     */
    public void cancel() {
        if (this.mDownloadThread != null && this.mDownloadThread.isAlive()) {
            this.mDownloadThread.cancel();
        }
    }


    /**
     * 设置下载状态监听器
     *
     * @param downLoadStatusListener
     */
    public void setDownLoadStatusListener(IDownLoadStatusListener downLoadStatusListener) {
        this.downLoadStatusListener = downLoadStatusListener;
    }

    private class FileDownloadThread extends Thread {
        private boolean isCanceled = false;

        public void cancel() {
            this.isCanceled = true;
        }

        @Override
        public void run() {
            try {
                sendMessage(START_DOWNLOAD, 0);

                // SD未能使用
                if (!Environment.getExternalStorageState()
                        .equals(Environment.MEDIA_MOUNTED) || !FileUtils.makeDirs(fileSavePath)) {
                    sendMessage(DOWNLOADING, ERR_SD_CARD_NOT_READY);
                    return;
                }

                // 创建连接
                final URL url = new URL(fileUrl);
                final byte buf[] = new byte[1024];
                final HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.connect();

                // 获取文件大小
                final int length = conn.getContentLength();
                // 创建输入流
                final InputStream is = conn.getInputStream();
                final FileOutputStream fos = new FileOutputStream(new File(fileSavePath));
                int count = 0;

                do {
                    // 下载是被取消的
                    if (isCanceled) {
                        sendMessage(DOWNLOAD_CANCELLED, 0);
                        break;
                    }

                    int numread = is.read(buf);
                    count += numread;
                    // 计算进度条位置
                    final int progress = (int) (((float) count / length) * 100);
                    // 更新进度
                    sendMessage(DOWNLOADING, progress);

                    if (numread <= 0) {
                        // 下载完成
                        sendMessage(DOWNLOAD_FINISH, 0);
                        break;
                    }
                    // 写入文件
                    fos.write(buf, 0, numread);
                } while (true);
                fos.flush();
                fos.close();
                is.close();
            }
            catch (Exception e) {
                e.printStackTrace();
                if (e instanceof IOException) {
                    sendMessage(DOWNLOAD_EXCEPTION, ERR_BAD_NEWWORK);
                }
                else {
                    sendMessage(DOWNLOAD_EXCEPTION, ERR_UNKNOWN_ERROR);
                }
            }
        }
    }


    private void sendMessage(int messageType, int arg) {
        final Message message = this.handler.obtainMessage(messageType, arg, 0);
        this.handler.sendMessage(message);
    }

    /**
     * handler
     */
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case START_DOWNLOAD:
                    if (downLoadStatusListener != null) {
                        downLoadStatusListener.onStart(NetFileDownloadHelper.this);
                    }
                    break;
                case DOWNLOADING:
                    if (downLoadStatusListener != null) {
                        downLoadStatusListener.onProgressUpdated(msg.arg1);
                    }
                    break;
                case DOWNLOAD_FINISH:
                    if (downLoadStatusListener != null) {
                        downLoadStatusListener.onFinished(fileSavePath);
                    }
                    break;
                case DOWNLOAD_CANCELLED:
                    if (downLoadStatusListener != null) {
                        downLoadStatusListener.onCanceled();
                    }
                    break;
                case DOWNLOAD_EXCEPTION:
                    final int errorCode = msg.arg1;
                    if (downLoadStatusListener != null) {
                        downLoadStatusListener.onError("下载失败,错误码:" + errorCode);
                    }
                    break;
                default:
                    break;
            }
        }

        ;
    };

}
