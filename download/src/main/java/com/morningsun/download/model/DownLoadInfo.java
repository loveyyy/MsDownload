package com.morningsun.download.model;


import java.util.List;

/**
 * Create By morningsun  on 2020-06-17
 */
public class DownLoadInfo {
    private Long id;
    private String fileName;
    private String filepath;
    private String url;
    private int totalSize;
    private int downLoadSize;
    private int state;
    private List<DownLoadProgree> downLoadProgree;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }

    public int getDownLoadSize() {
        return downLoadSize;
    }

    public void setDownLoadSize(int downLoadSize) {
        this.downLoadSize = downLoadSize;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public List<DownLoadProgree> getDownLoadProgree() {
        return downLoadProgree;
    }

    public void setDownLoadProgree(List<DownLoadProgree> downLoadProgree) {
        this.downLoadProgree = downLoadProgree;
    }
}
