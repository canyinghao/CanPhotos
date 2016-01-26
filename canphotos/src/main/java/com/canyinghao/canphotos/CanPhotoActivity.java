package com.canyinghao.canphotos;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.canyinghao.canadapter.CanHolderHelper;
import com.canyinghao.canadapter.CanOnItemListener;
import com.canyinghao.canadapter.CanRVAdapter;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;


/**
 * 图片选取主界面
 */
public class CanPhotoActivity extends AppCompatActivity {


    private CanToolBar toolBar;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;

    //    默认最多选5张
    private int max = 5;

    //  储存选取的图片
    private ArrayList<String> selectList = new ArrayList<String>();

    private AlbumAdapter adapter;


    private AppCompatActivity context;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        context = this;




        findViewById(R.id.main).setBackgroundColor(CanPhotoHelper.getInstance().getBackGroundColor());
        toolBar = (CanToolBar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle(null);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        fab = (FloatingActionButton) findViewById(R.id.fab);


        toolBar.setTextCenter(getString(R.string.photo_name));

        toolBar.setTextRight(getString(R.string.sure));


        adapter = new AlbumAdapter(recyclerView);

        CanPhotoHelper.getInstance().setRecyclerViewLayoutManager(context,recyclerView);
        recyclerView.setAdapter(adapter);

        adapter.setList(CanPhotoHelper.getInstance().getPhotoAlbum(this));


        initListener();
        initData();
    }


    public void initListener() {


        adapter.setOnItemListener(new CanOnItemListener() {


            public void onRVItemClick(ViewGroup parent, View itemView, int position) {

                PhotoBean item = adapter.getItem(position);

                Intent intent = new Intent(CanPhotoActivity.this, CanPicturesActivity.class);
                intent.putExtra(CanPhotoHelper.PHOTO_POSITION, position);
                intent.putExtra(CanPhotoHelper.PHOTO_TITLE, item.name);
                intent.putStringArrayListExtra(CanPhotoHelper.PHOTO_COLLECTION, selectList);
                intent.putExtra(CanPhotoHelper.PHOTO_MAX, max);


                startActivityForResult(intent, CanPhotoHelper.REQUEST_CODE_SEND_PICTURE);


            }


        });

        toolBar.tv_right.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                intent.putStringArrayListExtra(CanPhotoHelper.PHOTO_COLLECTION, selectList);
                setResult(RESULT_OK, intent);
                onBackPressed();
            }
        });


        fab.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                ArrayList<String> arrayList = CanPhotoHelper.getInstance().getLocalPreviewList(selectList);
                if (arrayList.size() > 0) {


                    CanPhotoHelper.getInstance().gotoPreview(context, arrayList, 0, null);
                }

            }
        });


    }


    public void initData() {

        getIntentData();

    }


    private void getIntentData() {
        Intent intent = getIntent();
        if (intent.hasExtra(CanPhotoHelper.PHOTO_COLLECTION)) {
            ArrayList<String> drr2 = intent
                    .getStringArrayListExtra(CanPhotoHelper.PHOTO_COLLECTION);
            selectList.addAll(drr2);

        }

        if (intent.hasExtra(CanPhotoHelper.PHOTO_MAX)) {
            max = intent.getIntExtra(CanPhotoHelper.PHOTO_MAX, 5);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null && data.hasExtra(CanPhotoHelper.PHOTO_COLLECTION)) {
            ArrayList<String> selectList2 = data
                    .getStringArrayListExtra(CanPhotoHelper.PHOTO_COLLECTION);
            selectList.clear();
            selectList.addAll(selectList2);
        }

    }


    class AlbumAdapter extends CanRVAdapter<PhotoBean> {


        public AlbumAdapter(RecyclerView recyclerView) {

            super(recyclerView, R.layout.item_album);
        }

        @Override
        protected void setView(CanHolderHelper helper, int position, PhotoBean bean) {


            helper.setVisibility(R.id.ll_hint, View.VISIBLE);

            String url = getItem(position).bitList.get(0).path;

            String name = getItem(position).name;
            String size = getItem(position).bitList.size() + "";

            helper.setText(R.id.name, name);
            helper.setText(R.id.count, size);

            SimpleDraweeView image = helper.getView(R.id.image);


            CanPhotoHelper.getInstance().setImageHeight(context,image,position);


            CanPhotoHelper.getInstance().setDraweeImage(image, "file://" + url, 0, 0);

        }

        @Override
        protected void setItemListener(CanHolderHelper helper) {


        }


    }


    @Override
    protected void onResume() {
        super.onResume();


        TextDrawable textDrawable = TextDrawable
                .builder()
                .beginConfig()
                .fontSize((int) getResources().getDimension(R.dimen.dimen_15))
                .endConfig()
                .buildRound(selectList.size() + "",
                        Color.TRANSPARENT);


        fab.setImageDrawable(textDrawable);
    }


    /**
     * 返回按钮
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
