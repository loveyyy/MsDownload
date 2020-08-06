package com.morningsun.download;

import android.util.Log;

import com.morningsun.download.http.HttpClient;
import com.morningsun.download.model.DownLoadInfo;
import com.morningsun.download.model.DownLoadProgree;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

import static okhttp3.internal.Util.closeQuietly;

/**
 * Create By morningsun  on 2020-08-04
 */
public class MsDownLoad {
    private final String TAG = "MsDownLoad";
    private int coreSize;
    private int DownLoadMax;
    private String filePath;

    //下载任务线程池
    private ExecutorService executorService;
    //正在下载的任务队列
    private Map<Long, List<DownloadTask>> queueTaskMap = Collections.synchronizedMap(new HashMap<Long, List<DownloadTask>>());
    //等待下载队列
    private List<DownLoadInfo> waitLoaded = Collections.synchronizedList(new ArrayList<DownLoadInfo>());
    //下载监听
    private OnDownLoadListener onDownLoadListener;


    MsDownLoad(int coreSize,
               int DownLoadMax,
               String filePath) {
        this.coreSize = coreSize;
        this.DownLoadMax = DownLoadMax;
        this.filePath = filePath;
        EventBus.getDefault().register(this);
    }

    /**
     * 任务下载
     */
    public synchronized boolean enqueue(List<DownLoadInfo> downLoadInfos) {
        for (final DownLoadInfo downLoadInfo : downLoadInfos) {
            final List<DownloadTask> downloadTasks = new ArrayList<>();
            File file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
            }
            HttpClient.Instance.getCall(downLoadInfo.getUrl(), "").enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Log.e(TAG, "获取任务信息失败");
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) {
                    int totalSize = (int) response.body().contentLength();
                    int rateSize = totalSize / 3;
                    downLoadInfo.setTotalSize(totalSize);
                    List<File> files = getFilesName(filePath, downLoadInfo.getFileName());
                    if (!files.isEmpty()) {
                        //存在文件进行 循环取得文件长度
                        for (File file1 : files) {
                            DownloadTask downloadTask;
                            int taskId = Integer.valueOf(file1.getName().split("_")[1]);
                            DownLoadProgree downLoadProgree = new DownLoadProgree();
                            downLoadProgree.setTaskId((long) taskId);
                            downLoadProgree.setDownLoadInfo(downLoadInfo);
                            if (taskId == 2) {
                                downLoadProgree.setStart(taskId * rateSize);
                                downLoadProgree.setEnd(totalSize);
                                downloadTask = new DownloadTask(downLoadProgree, filePath);
                            } else {
                                downLoadProgree.setStart(taskId * rateSize);
                                downLoadProgree.setEnd((taskId + 1) * rateSize - 1);
                                downloadTask = new DownloadTask(downLoadProgree, filePath);
                            }
                            if (queueTaskMap.entrySet().size() * 3 <= DownLoadMax) {
                                getExecutorService().execute(downloadTask);
                                downloadTasks.add(downloadTask);
                            } else {
                                waitLoaded.add(downLoadInfo);
                            }
                        }
                    } else {
                        for (int i = 0; i < 3; i++) {
                            DownloadTask downloadTask;
                            DownLoadProgree downLoadProgree = new DownLoadProgree();
                            downLoadProgree.setTaskId(downLoadInfo.getId());
                            downLoadProgree.setDownLoadInfo(downLoadInfo);
                            downLoadProgree.setId((long) i);
                            if (i == 2) {
                                downLoadProgree.setStart(i * rateSize);
                                downLoadProgree.setEnd(totalSize);
                                downloadTask = new DownloadTask(downLoadProgree, filePath);
                            } else {
                                downLoadProgree.setStart(i * rateSize);
                                downLoadProgree.setEnd((i + 1) * rateSize - 1);
                                downloadTask = new DownloadTask(downLoadProgree, filePath);
                            }
                            if (queueTaskMap.entrySet().size() * 3 <= DownLoadMax) {
                                getExecutorService().execute(downloadTask);
                                downloadTasks.add(downloadTask);
                            } else {
                                waitLoaded.add(downLoadInfo);
                                break;
                            }
                        }
                    }
                    queueTaskMap.put(downLoadInfo.getId(), downloadTasks);
                }
            });


        }
        return true;
    }


    /**
     * 任务停止
     */
    public synchronized void stop(long id) {
        if (queueTaskMap.containsKey(id)) {
            List<DownloadTask> downloadTasks = queueTaskMap.get(id);
            for (DownloadTask downloadTask : downloadTasks) {
                if (downloadTask.getState() == Thread.State.RUNNABLE) {
                    downloadTask.setStop(true);
                }
            }
        }
        promoteSyncTask();
    }

    /**
     * 任务下载完成
     */
    synchronized void finished(DownLoadInfo downLoadInfo) {
        mergeFiles(downLoadInfo.getFileName());
        queueTaskMap.remove(downLoadInfo.getId());
        promoteSyncTask();
    }


    /**
     * 删除下载任务，是否删除文件
     */
    public synchronized void deleteTask(DownLoadInfo downLoadInfo, boolean isDeleteFile) {
        if (downLoadInfo != null) {
            //判断任务是否进行中
            if (queueTaskMap.containsKey(downLoadInfo.getId())) {
                //停止任务
                stop(downLoadInfo.getId());
                if (isDeleteFile) {
                    for(int i = 0 ;i<3;i++){
                        File file = new File(downLoadInfo.getFilepath(), downLoadInfo.getFileName()+"_"+i);
                        file.delete();
                    }

                }
            }
            waitLoaded.remove(downLoadInfo.getId());
            promoteSyncTask();
        } else {
            Log.e(TAG, "删除任务不存在");
        }
    }

    /**
     * 调度pending状态的任务，开始下载
     */
    private synchronized void promoteSyncTask() {
        if (waitLoaded.isEmpty()) {
            enqueue(waitLoaded);
        }
    }


    /**
     * 注册监听
     */
    public void addListen(OnDownLoadListener onDownLoadListener) {
        this.onDownLoadListener = onDownLoadListener;
    }

    /**
     * evenbus 订阅
     */
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void getDownLoadInfo(DownLoadProgree downLoadProgree) {
        DownLoadInfo downLoadInfo = downLoadProgree.getDownLoadInfo();
        switch (downLoadProgree.getState()) {
            case DownloadTask.PENDING:
                //任务待进行
                break;
            case DownloadTask.LOADING:
                //任务准备中
                downLoadInfo.setState(DownloadTask.LOADING);
                if (onDownLoadListener != null) {
                    onDownLoadListener.OnLOADING(downLoadInfo);
                }
                break;
            case DownloadTask.PROGRESS:
                //任务下载中
                int downloadSize = downLoadInfo.getDownLoadSize() + downLoadProgree.getDownloadSize();
                downLoadInfo.setDownLoadSize(downloadSize);
                downLoadInfo.setState(DownloadTask.PROGRESS);
                if (onDownLoadListener != null) {
                    onDownLoadListener.onPorgree(downLoadInfo, downLoadInfo.getDownLoadSize(), downLoadInfo.getTotalSize());
                }
                break;
            case DownloadTask.STOP:
                //任务停止
                downLoadInfo.setState(DownloadTask.STOP);
                if (onDownLoadListener != null) {
                    onDownLoadListener.onStop(downLoadInfo, downLoadInfo.getDownLoadSize(), downLoadInfo.getTotalSize());
                }
                break;
            case DownloadTask.FAILED:
                //任务失败
                downLoadInfo.setState(DownloadTask.FAILED);
                if (onDownLoadListener != null) {
                    onDownLoadListener.onFailed(downLoadInfo);
                }
                queueTaskMap.get(downLoadInfo.getId()).get(downLoadProgree.getTaskId().intValue()).setStop(true);
                break;
            case DownloadTask.FINISHED:
                //任务完成
                if (downLoadInfo.getDownLoadSize() == downLoadInfo.getTotalSize()) {
                    finished(downLoadInfo);
                    downLoadInfo.setState(DownloadTask.FINISHED);
                    if (onDownLoadListener != null) {
                        onDownLoadListener.onCompelet(downLoadInfo);
                    }
                }
                break;
            default:
                break;
        }
    }


    /**
     * 初始化线程池
     */
    private ExecutorService getExecutorService() {
        if (executorService == null) {
            executorService = new ThreadPoolExecutor(coreSize, DownLoadMax,
                    60, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(),
                    Executors.defaultThreadFactory(), new RejectedExecutionHandler() {
                @Override
                public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {

                }
            });
        }

        return executorService;
    }

    private List<File> getFilesName(String path, String name) {
        List<File> files = new ArrayList<>();
        File file = new File(path);
        File[] tempList = file.listFiles();
        for (File file1 : tempList) {
            if (file1.isFile()) {
                String fileName = file1.getName();
                if (fileName.split("_")[0].equals(name)) {
                    files.add(file1);
                }
            }
        }
        return files;
    }

    private void mergeFiles(String name) {
        try {
            File file = new File(filePath,name);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            BufferedSource bufferedSource = null;
            BufferedSink bufferedSink = null;
            try {
                for (int i = 0; i < 3; i++) {
                    File file1 = new File(filePath, name + "_" + i);
                    bufferedSource = Okio.buffer(Okio.source(file1));
                    bufferedSink = Okio.buffer(Okio.sink(file));
                    byte[] buffer = new byte[8192];//缓冲数组2kB
                    while ((bufferedSource.read(buffer)) != -1) {
                        bufferedSink.write(buffer);
                    }
                    file1.delete();
                }
            }finally {
                if(bufferedSource!=null){
                    closeQuietly(bufferedSource);
                }
                if(bufferedSink!=null){
                    closeQuietly(bufferedSink);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
