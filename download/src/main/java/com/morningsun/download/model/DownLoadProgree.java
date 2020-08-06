package com.morningsun.download.model;


/**
 * Create By morningsun  on 2020-07-27
 */
public class DownLoadProgree {
    private Long id;
    private Long taskId;
    private int start;
    private int end;
    private int state;
    private int downloadSize;
    private DownLoadInfo downLoadInfo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getDownloadSize() {
        return downloadSize;
    }

    public void setDownloadSize(int downloadSize) {
        this.downloadSize = downloadSize;
    }

    public DownLoadInfo getDownLoadInfo() {
        return downLoadInfo;
    }

    public void setDownLoadInfo(DownLoadInfo downLoadInfo) {
        this.downLoadInfo = downLoadInfo;
    }
}
