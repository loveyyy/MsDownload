package com.morningsun.download;



import android.util.Log;

import com.morningsun.download.http.HttpClient;
import com.morningsun.download.model.DownLoadProgree;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

import static okhttp3.internal.Util.closeQuietly;


/**
 * Create By morningsun  on 2020-06-11
 */
public class DownloadTask extends Thread {
    //任务信息
    private DownLoadProgree downLoadProgree;
    //是否停止
    private boolean Stop=false;
    //当前任务下载进度
    private int off=0;
    private String filePath;

    public static final int PENDING=1;
    public static final int LOADING = 2;
    public static final int PROGRESS = 3;
    public static final int STOP = 4;
    public static final int FAILED = 5;
    public static final int FINISHED = 6;

    @Override
    public void run() {
        start();
    }


    public DownloadTask(DownLoadProgree downLoadProgree, String filePath) {
        this.downLoadProgree=downLoadProgree;
        this.filePath = filePath;
    }

    public void start() {
        downLoadProgree.setState(LOADING);
        EventBus.getDefault().post(downLoadProgree);
        download();
    }

    private void download() {
        HttpClient.Instance.getCall(downLoadProgree.getDownLoadInfo().getUrl(),"bytes=" + downLoadProgree.getStart() + "-" + downLoadProgree.getEnd())
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        downLoadProgree.setState(FAILED);
                        downLoadProgree.setDownloadSize(0);
                        EventBus.getDefault().post(downLoadProgree);
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) {
                        try {
                            File file = new File(filePath, downLoadProgree.getDownLoadInfo().getFileName()+"_"+downLoadProgree.getId());
                            BufferedSource bufferedSource = null;
                            BufferedSink bufferedSink = null;
                            try {
                                bufferedSource = response.body().source();
                                bufferedSink = Okio.buffer(Okio.sink(file));
                                byte[] buffer = new byte[8192];//缓冲数组2kB
                                int len;
                                while ((len = bufferedSource.read(buffer)) != -1) {
                                    if (!Stop) {
                                        bufferedSink.write(buffer);
                                        off+= len;
                                        //更新数据库 发送消息
                                        downLoadProgree.setState(PROGRESS);
                                        downLoadProgree.setDownloadSize(len);
                                        EventBus.getDefault().post(downLoadProgree);
                                    } else {
                                        closeQuietly(bufferedSource);
                                        closeQuietly(bufferedSink);
                                        //更新数据库
                                        downLoadProgree.setState(STOP);
                                        downLoadProgree.setDownloadSize(0);
                                        downLoadProgree.setStart(off);
                                        EventBus.getDefault().post(downLoadProgree);
                                        return;
                                    }
                                }
                                downLoadProgree.setState(FINISHED);
                                downLoadProgree.setDownloadSize(off);
                                EventBus.getDefault().post(downLoadProgree);
                            } finally {
                                //关闭IO流
                                if(bufferedSource!=null){
                                    closeQuietly(bufferedSource);
                                }
                                if(bufferedSink!=null){
                                    closeQuietly(bufferedSink);
                                }

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.e("DownLoadTask","下载出错" + e);
                            downLoadProgree.setState(FAILED);
                            downLoadProgree.setDownloadSize(0);
                            EventBus.getDefault().post(downLoadProgree);
                        }
                    }
                });

    }





    public void setStop(boolean stop) {
        Stop = stop;
    }


}
