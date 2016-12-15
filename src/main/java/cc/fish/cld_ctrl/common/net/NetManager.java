package cc.fish.cld_ctrl.common.net;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import cc.fish.cld_ctrl.ad.CldAdImpl;
import cc.fish.cld_ctrl.ad.entity.RequestAd;
import cc.fish.cld_ctrl.ad.entity.ResponseAd;
import cc.fish.cld_ctrl.appstate.entity.ReqFeedback;
import cc.fish.cld_ctrl.appstate.entity.RespUpdate;
import cc.fish.cld_ctrl.appstate.interfaces.UpdateCallback;
import cc.fish.fishhttp.net.RequestHelper;
import cc.fish.fishhttp.net.annotation.NetInject;
import cc.fish.fishhttp.net.annotation.NetMethod;
import cc.fish.fishhttp.net.annotation.NetUrl;
import cc.fish.fishhttp.net.annotation.Result;
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

    final static String URL_MAIN            = "http://192.168.1.201:5555";
    final static String URL_AD              = URL_MAIN + "/cld/ad";
    final static String URL_AD_SHOW         = URL_MAIN + "/cld/ad/show";
    final static String URL_AD_CLICK        = URL_MAIN + "/cld/ad/click";

    final static String URL_APP_UPDATE      = URL_MAIN + "/cld/app/update";
    final static String URL_APP_FEEDBACK    = URL_MAIN + "/cld/app/feedback";

    final static String PARAM_VID           = "vid";
    final static String PARAM_AD_ID         = "ad_slot";
    final static String PARAM_APP_ID        = "app_id";
    final static String PARAM_VER_CODE      = "version_code";
    final static String PARAM_CHANNEL       = "channel";


    @NetMethod(RequestHelper.Method.GET)
    @Result(RespUpdate.class)
    @NetUrl(URL_APP_UPDATE)
    private RequestHelper<RespUpdate> updateRequest = new RequestHelper<>();

    @NetMethod(RequestHelper.Method.POST)
    @Result(Object.class)
    @NetUrl(URL_APP_FEEDBACK)
    private RequestHelper<Object> feedbackRequest   = new RequestHelper<>();




    private RequestHelper<Object> getFastRequestHelper() {
        RequestHelper<Object> fastRequest = new RequestHelper<>()
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

    public void uploadShowAd(int ad_slot) {
        getFastRequestHelper().Url(URL_AD_SHOW)
                .UrlParam(PARAM_AD_ID, ad_slot + "", true)
                .UrlParam(PARAM_VID, CldAdImpl.getRequestAd().getVid())
                .get(mContext, mHandler);
    }

    public void uploadClickAd(int ad_slot) {
        getFastRequestHelper().Url(URL_AD_CLICK)
                .UrlParam(PARAM_AD_ID, ad_slot + "", true)
                .UrlParam(PARAM_VID, CldAdImpl.getRequestAd().getVid())
                .get(mContext, mHandler);
    }

    public void checkUpdate(int app_id, int ver_code, String channel, UpdateCallback callback) {
        updateRequest.UrlParam(PARAM_APP_ID, app_id + "", true)
                .UrlParam(PARAM_VER_CODE, ver_code + "")
                .UrlParam(PARAM_CHANNEL, channel)
                .Success(result -> {
                    RespUpdate ru = (RespUpdate) result;
                    callback.update(ru.getDownload_url(),
                            ru.getIs_force() == 1,
                            ru.getContent(),
                            ru.getVersion_name(),
                            ru.getVersion_code());
                })
                .Failed(msg -> ZLog.e("cld app update", (String) msg))
                .get(mContext, mHandler);
    }

    public void feedback(ReqFeedback reqFeedback) {
        feedbackRequest.PostJson(reqFeedback).post(mContext, mHandler);
    }

}
