package cc.fish.cld_ctrl.ad.net;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import cc.fish.cld_ctrl.ad.CldAdImpl;
import cc.fish.cld_ctrl.ad.entity.RequestAd;
import cc.fish.cld_ctrl.ad.entity.ResponseAd;
import cc.fish.fishhttp.net.RequestHelper;
import cc.fish.fishhttp.net.annotation.NetInject;
import cc.fish.fishhttp.util.ZLog;

/**
 * Created by fish on 16-12-13.
 */

public class NetManager {
    private static NetManager instance = null;
    private Context mContext;
    private Handler mHandler = new Handler(Looper.myLooper());

    private NetManager() {
        NetInject.inject(this);
    }

    public static NetManager getInstance (){
        if (instance == null) {
            instance = new NetManager();
        }
        return instance;
    }

    public void init (Context context) {
        instance.mContext = context;
    }
/*************************************************/

    final static String URL_MAIN        = "http://192.168.1.201:5555";
    final static String URL_AD          = URL_MAIN + "/cld/ad";
    final static String URL_SHOW        = URL_MAIN + "/cld/ad/show";
    final static String URL_CLICK       = URL_MAIN + "/cld/ad/click";

    final static String PARAM_VID       = "vid";
    final static String PARAM_AD_ID     = "ad_id";






    private RequestHelper<ResponseAd> getFastRequestHelper() {
        RequestHelper<ResponseAd> fastRequest = new RequestHelper<>()
                .Method(RequestHelper.Method.GET)
                .Result(ResponseAd.class);
        return fastRequest;
    }

    private RequestHelper<ResponseAd> getAdRequestHelper() {
        RequestHelper<ResponseAd> adRequest = new RequestHelper<>()
                .Url(URL_AD)
                .Method(RequestHelper.Method.POST)
                .Result(ResponseAd.class);
        return adRequest;
    }

    public void syncAd(RequestAd requestEntity, NetCallback<ResponseAd> callback) {
        getAdRequestHelper().PostJson(requestEntity)
                .Success(result -> {
                    if (result == null || ((ResponseAd)result).getAd_disp() == null) {
                        callback.noDisp();
                    } else {
                        callback.success((ResponseAd) result);
                    }
                })
                .Failed(msg -> {
                    ZLog.e("NET->SYNC_AD", (String)msg);
                    callback.failed((String) msg);
                }).post(mContext, mHandler);
    }

    public synchronized void uploadShowAd(int ad_slot) {
        getFastRequestHelper().Url(URL_SHOW)
                .UrlParam(PARAM_AD_ID, ad_slot + "", true)
                .UrlParam(PARAM_VID, CldAdImpl.getRequestAd().getVid())
                .get(mContext, mHandler);
    }

    public synchronized void uploadClickAd(int ad_slot) {
        getFastRequestHelper().Url(URL_CLICK)
                .UrlParam(PARAM_AD_ID, ad_slot + "", true)
                .UrlParam(PARAM_VID, CldAdImpl.getRequestAd().getVid())
                .get(mContext, mHandler);
    }

}
