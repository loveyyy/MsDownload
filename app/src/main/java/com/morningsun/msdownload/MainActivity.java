package com.morningsun.msdownload;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.morningsun.download.MsDownLoad;
import com.morningsun.download.MsDownLoadBuilder;
import com.morningsun.download.OnDownLoadListener;
import com.morningsun.download.model.DownLoadInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<DownLoadInfo> downLoadInfos=new ArrayList<>();
                DownLoadInfo downLoadInfo=new DownLoadInfo();
                downLoadInfo.setId(System.currentTimeMillis());
                downLoadInfo.setUrl("https://alimov2.a.yximgs.com/upic/2018/06/28/07/BMjAxODA2MjgwNzM1NTVfNDQxMTQzMTg5XzY4NjQ2MTEwOTdfMl8z_hd3_B36b3e018145c233bb37d02b62a24cf3b.mp4");
                downLoadInfo.setFileName("测试.mp3");
                downLoadInfos.add(downLoadInfo);

                DownLoadInfo downLoadInfo1=new DownLoadInfo();
                downLoadInfo1.setUrl("https://mvvideo11.meitudata.com/5a5c94961bda29059_H264_11_5.mp4");
                downLoadInfo1.setFileName("测试1.mp3");
                downLoadInfos.add(downLoadInfo1);

                DownLoadInfo downLoadInfo2=new DownLoadInfo();
                downLoadInfo2.setUrl("https://tx2.a.yximgs.com/upic/2019/02/03/20/BMjAxOTAyMDMyMDAwMzJfMTIwMDU4MjA4N18xMDQwMjQzMjg4MV8xXzM=_b_Ba41546070e00679b090604729712ec41.mp4");
                downLoadInfo2.setFileName("测试2.mp3");
                downLoadInfos.add(downLoadInfo2);

                MsDownLoad msDownLoadBuilder=new MsDownLoadBuilder().build(Environment.getExternalStorageDirectory().getPath() + File.separator + "mv");
                msDownLoadBuilder.addListen(new OnDownLoadListener() {
                    @Override
                    public void onPending(DownLoadInfo downLoadInfo) {

                    }

                    @Override
                    public void OnLOADING(DownLoadInfo downLoadInfo) {

                    }

                    @Override
                    public void onPorgree(DownLoadInfo downLoadInfo, int start, int size) {
//                        Log.e("Tag",downLoadInfo.getFileName()+"----"+start+"---"+size);
                    }

                    @Override
                    public void onStop(DownLoadInfo downLoadInfo, int start, int size) {

                    }

                    @Override
                    public void onCompelet(DownLoadInfo downLoadInfo) {
//                        Log.e("Tag",downLoadInfo.getFileName()+"----完成");
                    }

                    @Override
                    public void onFailed(DownLoadInfo downLoadInfo) {

                    }
                });
                msDownLoadBuilder.enqueue(downLoadInfos);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
