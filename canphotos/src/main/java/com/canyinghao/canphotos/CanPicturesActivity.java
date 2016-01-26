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
import android.widget.ImageView;

import com.canyinghao.canadapter.CanHolderHelper;
import com.canyinghao.canadapter.CanOnItemListener;
import com.canyinghao.canadapter.CanRVAdapter;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;


public class CanPicturesActivity extends AppCompatActivity {


    private CanToolBar toolBar;

    private RecyclerView recyclerView;

    private FloatingActionButton fab;

    private AppCompatActivity context;


    //   存储选取的图片
    private List<String> selectList = new ArrayList<String>();
    //    存储以前选取的图片
    private ArrayList<String> oldSelectList = new ArrayList<String>();


//    private int numberAlbum = 0;
    //    默认最多选5张
    private int max;

    private ImageAdapter adapter;




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


        adapter = new ImageAdapter(recyclerView);

        CanPhotoHelper.getInstance().setRecyclerViewLayoutManager(context,recyclerView);

        recyclerView.setAdapter(adapter);
        initListener();

        initData();

    }


    public void initListener() {


        toolBar.tv_right.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                oldSelectList.addAll(selectList);
                Intent intent = new Intent();
                intent.putStringArrayListExtra(CanPhotoHelper.PHOTO_COLLECTION, oldSelectList);
                setResult(RESULT_OK, intent);
                onBackPressed();
            }
        });


        adapter.setOnItemListener(new CanOnItemListener() {

            public void onRVItemClick(ViewGroup parent, View itemView, int position) {


            }

        });

        fab.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                ArrayList<String> arrayList = new ArrayList<String>();
                arrayList.addAll(oldSelectList);
                arrayList.addAll(selectList);

                arrayList = CanPhotoHelper.getInstance().getLocalPreviewList(arrayList);


                if (arrayList.size() > 0) {

                    CanPhotoHelper.getInstance().gotoPreview(context, arrayList, 0, null);
                }

            }
        });

    }


    public void initData() {

        Intent intent = getIntent();
        if (intent.hasExtra(CanPhotoHelper.PHOTO_COLLECTION)) {
            ArrayList<String> selectList2 = intent
                    .getStringArrayListExtra(CanPhotoHelper.PHOTO_COLLECTION);
            oldSelectList.addAll(selectList2);

        }

        int photoPosition = 0;
        if (intent.hasExtra(CanPhotoHelper.PHOTO_POSITION)) {
            photoPosition = intent.getIntExtra(CanPhotoHelper.PHOTO_POSITION, 0);
        }
        if (intent.hasExtra(CanPhotoHelper.PHOTO_TITLE)) {
            String name = intent.getStringExtra(CanPhotoHelper.PHOTO_TITLE);
            toolBar.setTextCenter(name);
        }

        if (intent.hasExtra(CanPhotoHelper.PHOTO_MAX)) {
            max = intent.getIntExtra(CanPhotoHelper.PHOTO_MAX, 5);
        }

        // 获取所有图片的集合

        List<PictureBean> picturesList = CanPhotoHelper.getInstance().getPhotoAlbum(this).get(photoPosition)
                .bitList;

        for (int i = 0; i < picturesList.size(); i++) {
            String path = picturesList.get(i).path;
            if (oldSelectList.contains(path)) {
                selectList.add(path);
                oldSelectList.remove(path);
            }

        }

        adapter.setList(picturesList);


    }




    class ImageAdapter extends CanRVAdapter<PictureBean> {


        public ImageAdapter(RecyclerView recyclerView) {
            super(recyclerView, R.layout.item_album);
        }

        @Override
        protected void setView(CanHolderHelper helper, final int position, PictureBean bean) {


            String url = getItem(position).path;


            SimpleDraweeView image = helper.getView(R.id.image);

            final ImageView selectedView = helper.getView(R.id.isselected);



            CanPhotoHelper.getInstance().setImageHeight(context, image,position);

            CanPhotoHelper.getInstance().setDraweeImage(image, "file://" + url, 0, 0);

            // 查看集合是否包含该地址
            if (selectList.contains(url)) {
                selectedView.setVisibility(View.VISIBLE);
            } else {

                selectedView.setVisibility(View.GONE);
            }


            helper.getConvertView().setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    if (selectedView.getVisibility() == View.GONE) {
                        if (selectList.size() + oldSelectList.size() >= max) {
                            CanPhotoHelper.getInstance().showSnackbar(toolBar, getString(R.string.photo_max, max));
                            return;
                        }

                        selectedView.setVisibility(View.VISIBLE);

                        // 将地址添加到集合中

                        selectList.add(getItem(position).path);

                        setPreViewText();
                    } else {

                        selectedView.setVisibility(View.GONE);
                        // 移除添加过的地址

                        selectList.remove(getItem(position).path);
                        setPreViewText();
                    }

                }
            });


        }

        @Override
        protected void setItemListener(CanHolderHelper helper) {


        }


    }


    private void setPreViewText() {


        TextDrawable textDrawable = TextDrawable
                .builder()
                .beginConfig()
                .fontSize((int) getResources().getDimension(R.dimen.dimen_15))
                .endConfig()
                .buildRound(oldSelectList.size() + selectList.size() + "",
                        Color.TRANSPARENT);


        fab.setImageDrawable(textDrawable);
    }

    @Override
    protected void onResume() {
        super.onResume();

        setPreViewText();

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
