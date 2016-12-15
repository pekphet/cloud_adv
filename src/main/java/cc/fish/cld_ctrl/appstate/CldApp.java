package cc.fish.cld_ctrl.appstate;

import cc.fish.cld_ctrl.appstate.interfaces.UpdateCallback;

/**
 * Created by fish on 16-12-15.
 */

public class CldApp {
    public static void checkUpdate(UpdateCallback callback) {
        CldAppImpl.checkUpdate(callback);
    }

    public static void feedback(String content) {
        CldAppImpl.feedback(content);
    }

    public static void download(String url, String fileName) {
        CldAppImpl.download(url, fileName);
    }
}
