# MsDownload
Android 下载器 使用线程池+多线程下载+okhttp+okio

1.导入

implementation 'com.morningsun:msdownload:1.0.1'

2.使用
                String url = "https://alimov2.a.yximgs.com/upic/2018/06/28/07/BMjAxODA2MjgwNzM1NTVfNDQxMTQzMTg5XzY4NjQ2MTEwOTdfMl8z_hd3_B36b3e018145c233bb37d02b62a24cf3b.mp4";
                
                
                
                List<DownLoadInfo> downLoadInfos=new ArrayList<>();
                DownLoadInfo downLoadInfo=new DownLoadInfo();
                //必须设置唯一id
                downLoadInfo.setId(System.currentTimeMillis());
                //设置网络url
                downLoadInfo.setUrl(url);
                //设置文件名
                downLoadInfo.setFileName("测试.mp3");
                downLoadInfos.add(downLoadInfo);

                MsDownLoad msDownLoadBuilder=new MsDownLoadBuilder()
                .build(Environment.getExternalStorageDirectory().getPath() + File.separator + "mv"); //此处是存储文件的位置
             
                //添加网络监听
                msDownLoadBuilder.addListen(new OnDownLoadListener() {
                    @Override
                    public void onPending(DownLoadInfo downLoadInfo) {
                        //阻塞
                    }

                    @Override
                    public void OnLOADING(DownLoadInfo downLoadInfo) {
                      //等待
                    }

                    @Override
                    public void onPorgree(DownLoadInfo downLoadInfo, int start, int size) {
                      //下载进度
                        //Log.e("Tag",downLoadInfo.getFileName()+"----"+start+"---"+size);
                    }

                    @Override
                    public void onStop(DownLoadInfo downLoadInfo, int start, int size) {
                       //停止下载
                    }

                    @Override
                    public void onCompelet(DownLoadInfo downLoadInfo) {
                          //下载完成
                        //Log.e("Tag",downLoadInfo.getFileName()+"----完成");
                    }

                    @Override
                    public void onFailed(DownLoadInfo downLoadInfo) {
                          //下载失败
                    }
                });
                msDownLoadBuilder.enqueue(downLoadInfos);
