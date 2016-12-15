package cc.fish.cld_ctrl.common.util;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import cc.fish.cld_ctrl.common.service.DownloadService;

/**
 * Created by fish on 16-12-14.
 */

public class DownloadUtils {
    /**
     * 启动下载友商App服务
     */
    public static void startDownService(Context context, String url, String appName) {
        if (!isHaveWifi(context)) {
            return;
        }
        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra("downloadUrl", url);
        intent.putExtra("appName", appName);
        context.startService(intent);
    }

    private static boolean isHaveWifi(Context mContext) {
        ConnectivityManager connManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mWifi.isConnected()) {
            return true;
        } else {
            return false;
        }
    }
}
