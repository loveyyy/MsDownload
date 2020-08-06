package com.morningsun.download;

import com.morningsun.download.model.DownLoadInfo;

/**
 * Create By morningsun  on 2020-06-12
 */
public interface OnDownLoadListener {

    void onPending(DownLoadInfo downLoadInfo);

    void OnLOADING(DownLoadInfo downLoadInfo);

    void onPorgree(DownLoadInfo downLoadInfo, int start, int size);

    void onStop(DownLoadInfo downLoadInfo, int start, int size);

    void onCompelet(DownLoadInfo downLoadInfo);

    void onFailed(DownLoadInfo downLoadInfo);

}
