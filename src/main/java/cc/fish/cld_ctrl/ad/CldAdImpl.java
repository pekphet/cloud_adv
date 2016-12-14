package cc.fish.cld_ctrl.ad;

import android.content.Context;
import android.view.View;

import cc.fish.cld_ctrl.ad.entity.AdDeviceInfo;
import cc.fish.cld_ctrl.ad.entity.AdDisp;
import cc.fish.cld_ctrl.ad.entity.RequestAd;
import cc.fish.cld_ctrl.ad.entity.ResponseAd;
import cc.fish.cld_ctrl.ad.entity.enums.AdType;
import cc.fish.cld_ctrl.ad.net.NetCallback;
import cc.fish.cld_ctrl.ad.net.NetManager;
import cc.fish.cld_ctrl.ad.view.AdWebView;
import cc.fish.cld_ctrl.util.DeviceUtils;
import cc.fish.cld_ctrl.util.DownloadUtils;
import cc.fish.cld_ctrl.util.EntityUtils;
import cc.fish.fishhttp.util.ZLog;

/**
 * Created by fish on 16-12-14.
 */

public class CldAdImpl {

    private static AdDeviceInfo sDeviceInfo;
    private static RequestAd sRequestAd;

    public static void initDeviceInfo(Context applicationContext) {
        sRequestAd = new RequestAd();
        sDeviceInfo = DeviceUtils.getAdDeviceInfo(applicationContext);
        sRequestAd.setDevice_info(sDeviceInfo);
        sRequestAd.setVid(DeviceUtils.getImei(applicationContext));
        sRequestAd.setApp_id(DeviceUtils.getMetaAppId(applicationContext));
        sRequestAd.setChannel(DeviceUtils.getMetaChannel(applicationContext));
    }

    public static void show(AdType type, int ad_slot, View targetView, Context context){
        RequestAd requestAd = (RequestAd) EntityUtils.copy(sRequestAd, RequestAd.class);
        requestAd.setAd_slot(ad_slot);
        requestAd.setAd_type(type);
        NetManager.getInstance().syncAd(requestAd, new NetCallback<ResponseAd>() {
            @Override
            public void success(ResponseAd result) {
                switch (result.getAd_type()) {
                    case AreaWebAd:
                        break;
                    case DialogAd:
                        break;
                    case FullScreenAd:
                        break;
                    case InfoFlowAd:
                        break;
                    case LocateAd:
                        break;
                    case ScrollAd:
                        break;
                    case SuspendAd:
                        break;
                    default:
                        return;
                }
                uploadShow(ad_slot);
                setClickAction(result, targetView, context);
            }

            @Override
            public void failed(String msg) {
                ZLog.e("sync ad", msg);
            }

            @Override
            public void noDisp() {
            }
        });
    }

    public static void show(AdType type, int ad_slot, AdCallback<AdDisp> callback, Context context) {
        RequestAd requestAd = (RequestAd) EntityUtils.copy(sRequestAd, RequestAd.class);
        requestAd.setAd_slot(ad_slot);
        requestAd.setAd_type(type);
        NetManager.getInstance().syncAd(requestAd, new NetCallback<ResponseAd>() {
            @Override
            public void success(ResponseAd result) {
                setClickAction(result, callback.success(result.getAd_disp()), context);
                uploadShow(ad_slot);
            }
            @Override
            public void failed(String msg) {
                ZLog.e("sync ad define", msg);
            }
            @Override
            public void noDisp() {
                callback.noDisplay();
            }
        });
    }

    private static void setClickAction(ResponseAd result, View targetView, Context context) {
        switch (result.getAd_action()) {
            case Web:
                targetView.setOnClickListener(v -> {
                    AdWebView.startAdWebView(context, result.getLink_url(), result.getAd_slot());
                });
                break;
            case Download:
                targetView.setOnClickListener(v -> {
                    uploadClick(result.getAd_slot());
                    DownloadUtils.startDownService(context, result.getLink_url(), result.getAd_slot()+ "");
                });
                break;
            case AutoLoad:
                break;
            case OuterWeb:
                break;
            default:
                break;
        }
    }

    public static void uploadShow(int ad_slot) {
        NetManager.getInstance().uploadShowAd(ad_slot);
    }

    public static void uploadClick(int ad_slot) {
        NetManager.getInstance().uploadClickAd(ad_slot);
    }

    public static RequestAd getRequestAd() {
        return sRequestAd;
    }

}
