package cc.fish.cld_ctrl.ad;

import android.view.View;

/**
 * Created by fish on 16-12-13.
 */

public interface AdCallback<T> {
    View success(T dispEntity);
    void noDisplay();
}
