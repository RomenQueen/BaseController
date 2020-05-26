package com.hzaz.base.impl_part;

import android.view.View;

public interface ViewController {
    View getView(int viewId);

    void setView2Data(int viewId, Object obj);

    void setOnClickListener(int[] ids, View.OnClickListener clickListener);
}
