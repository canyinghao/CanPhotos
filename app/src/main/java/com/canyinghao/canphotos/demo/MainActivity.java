package com.canyinghao.canphotos.demo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.canyinghao.canadapter.CanHolderHelper;
import com.canyinghao.canadapter.CanOnItemListener;
import com.canyinghao.canadapter.CanRVAdapter;
import com.canyinghao.canphotos.CanPhotoHelper;
import com.canyinghao.canphotos.TextDrawable;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;

    CanRVAdapter adapter;

    AppCompatActivity context;

    private int H;

    private File saveFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        setSupportActionBar(toolbar);

        H = CanPhotoHelper.getInstance().getScreenDisplayMetrics(context).widthPixels / 3;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CanPhotoHelper.getInstance().gotoPhotoSelect(context, null, 5);
            }
        });

        recyclerView.setLayoutManager(new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false));


        adapter = new CanRVAdapter<String>(recyclerView, R.layout.item_album) {
            @Override
            protected void setView(CanHolderHelper helper, int position, String url) {

                SimpleDraweeView image = helper.getView(com.canyinghao.canphotos.R.id.image);


                ViewGroup.LayoutParams params = image.getLayoutParams();
                params.height = H;
                image.setLayoutParams(params);

                if (!url.contains("://")){
                    url = "file://" + url;
                }

                CanPhotoHelper.getInstance().setDraweeImage(image, url, 0, 0);
            }

            @Override
            protected void setItemListener(CanHolderHelper viewHelper) {

            }


        };
        recyclerView.setAdapter(adapter);

        adapter.setOnItemListener(new CanOnItemListener() {

            public void onRVItemClick(ViewGroup parent, View itemView, int position) {


                ArrayList<String> arrayList = CanPhotoHelper.getInstance().getLocalPreviewList((ArrayList<String>) adapter.getList());


                CanPhotoHelper.getInstance().gotoPreview(context, arrayList, position, saveFile);
            }


        });


        TextDrawable textDrawable = TextDrawable
                .builder()
                .beginConfig()
                .fontSize(40)
                .endConfig()
                .buildRound("选取",
                        Color.TRANSPARENT);


        fab.setImageDrawable(textDrawable);

        adapter.addLastItem("http://www.it.com.cn/dghome/img/2009/06/23/17/090623_tv_tf2_13h.jpg");
        adapter.addLastItem("http://www.hq.xinhuanet.com/photo/2008-11/12/xinsrc_5831105121112593526741.jpg");

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {


            if (data != null && data.hasExtra(CanPhotoHelper.PHOTO_COLLECTION)) {

                ArrayList<String> list = data.getStringArrayListExtra(CanPhotoHelper.PHOTO_COLLECTION);

                adapter.addMoreList(list);
            }


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        switch (id) {

            case R.id.action_1:

                CanPhotoHelper.getInstance().setBackGroundColor(Color.parseColor("#222222"));

                break;
            case R.id.action_2:

                CanPhotoHelper.getInstance().setBackGroundColor(Color.parseColor("#eeeeee"));

                break;
            case R.id.action_3:
                CanPhotoHelper.getInstance().setSpanCount(1);
                break;
            case R.id.action_4:
                CanPhotoHelper.getInstance().setSpanCount(2);
                break;

            case R.id.action_5:
                CanPhotoHelper.getInstance().setSpanCount(3);
                break;

            case R.id.action_6:
                if (saveFile != null) {
                    saveFile = null;
                } else {
                    saveFile = Environment.getExternalStorageDirectory();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
