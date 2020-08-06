package com.morningsun.download;

/**
 * Create By morningsun  on 2020-08-04
 */
public final class MsDownLoadBuilder {
    private static final int CPU_NUM = Runtime.getRuntime().availableProcessors();

    /**
     * 最大核心线程数 默认为 CPU_NUM + 1
     */
    private int coreSize = CPU_NUM + 1;

    /**
     * 最大下载个数 默认为CPU_NUM * 2 + 1
     */
    private int DownLoadMax = CPU_NUM * 2 + 1;


    public MsDownLoadBuilder coreSize(int coreSize) {
        this.coreSize = coreSize;
        return this;
    }

    public MsDownLoadBuilder DownLoadMax(int DownLoadMax) {
        this.DownLoadMax = DownLoadMax;
        return this;
    }


    public MsDownLoad build(String filPath) {
            return new MsDownLoad(
                    coreSize,
                    DownLoadMax,
                    filPath);

    }


}
