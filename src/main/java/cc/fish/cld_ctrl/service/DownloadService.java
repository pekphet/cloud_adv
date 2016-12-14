package cc.fish.cld_ctrl.service;

import android.app.DownloadManager;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;

/**
 * Created by fish on 16-12-14.
 */

public class DownloadService extends Service {
    private DownloadManager downloadManager;
    private static long enqueueID;//根据队列的id查找下载apk的Uri
    private DownloadReceiver receiver;
    /**
     * 下载文件存放目录
     */
    private static String downloadDir = "ad/download/";

    private String downloadUrl;

    private String appName;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return super.onStartCommand(intent, flags, startId);
        }
        downloadUrl = intent.getStringExtra("downloadUrl");
        appName = intent.getStringExtra("appName");
        if (downloadUrl != null && !"".equals(downloadUrl)) {
            startDownload();
        }
        receiver = new DownloadReceiver();
        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        return super.onStartCommand(intent, flags, startId);
    }

    private void startDownload() {
        try {
            downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
            request.setTitle(appName);
            request.setMimeType("application/vnd.android.package-archive");
            if (!Environment.getExternalStoragePublicDirectory(downloadDir).getParentFile().exists()) {
                Environment.getExternalStoragePublicDirectory(downloadDir).getParentFile().mkdirs();
            }
            request.setDestinationInExternalPublicDir(downloadDir, appName);
            enqueueID = downloadManager.enqueue(request);
//            DownloadQueueConfigure.putRecord(this, appName, enqueueID);    // used for cancel
        } catch (Exception e) {
        }
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        stopSelf();
        super.onDestroy();
    }

//    private void cancelDownload(String appName) {
//        if (receiver != null) {
//            unregisterReceiver(receiver);
//        }
//        long id = DownloadQueueConfigure.getRecord(this, appName);
//        if (id != -1) {
//            downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
//            downloadManager.remove(id);
//        }
//
//    }
}