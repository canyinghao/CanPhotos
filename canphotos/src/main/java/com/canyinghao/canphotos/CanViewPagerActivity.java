package com.canyinghao.canphotos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.canyinghao.canphotos.preview.MultiTouchViewPager;
import com.canyinghao.canphotos.preview.PhotoDraweeView;
import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.BaseDataSubscriber;
import com.facebook.datasource.DataSource;
import com.facebook.datasource.DataSubscriber;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.animated.base.AnimatedDrawable;
import com.facebook.imagepipeline.animated.factory.AnimatedDrawableFactory;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.image.CloseableAnimatedImage;
import com.facebook.imagepipeline.image.CloseableBitmap;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.relex.circleindicator.CircleIndicator;

public class CanViewPagerActivity extends AppCompatActivity {

    public static final String ARRAY_LIST = "ARRAY_LIST";
    public static final String ARRAY_STRING = "ARRAY_STRING";
    public static final String POSITION_KEY = "POSITION_KEY";
    public static final String FILE_SAVE = "FILE_SAVE";

    private CanToolBar toolBar;

    private MultiTouchViewPager viewPager;

    private CircleIndicator indicator;

    private List<String> mList = new ArrayList<>();

    private int position_key;
    private boolean isDownLoad;

    private Map<Integer, Bitmap> map = new HashMap<Integer, Bitmap>();

    private Context context;

    private int W;

    private File parent;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);

        context = this;
        findViewById(R.id.main).setBackgroundColor(CanPhotoHelper.getInstance().getBackGroundColor());
        toolBar = (CanToolBar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle(null);
        viewPager = (MultiTouchViewPager) findViewById(R.id.view_pager);
        indicator = (CircleIndicator) findViewById(R.id.indicator);

        W = CanPhotoHelper.getInstance().getScreenDisplayMetrics(this).widthPixels;


        if (getIntent().hasExtra(ARRAY_LIST)) {

            List<String> list = getIntent().getStringArrayListExtra(ARRAY_LIST);

            if (list != null && !list.isEmpty()) {

                mList.addAll(list);
            }

        }

        if (getIntent().hasExtra(ARRAY_STRING)) {

            String[] strs = getIntent().getStringArrayExtra(ARRAY_STRING);

            if (strs != null && strs.length > 0) {

                for (String str : strs) {

                    mList.add(str);
                }
            }

        }

        if (getIntent().hasExtra(POSITION_KEY)) {

            position_key = getIntent().getIntExtra(POSITION_KEY, 0);


        }


        if (getIntent().hasExtra(FILE_SAVE)) {

            String filePath = getIntent().getStringExtra(FILE_SAVE);

            if (TextUtils.isEmpty(filePath)) {
                parent = getExternalCacheDir();
            } else {
                parent = new File(filePath);
                isDownLoad = true;
            }


        }


        viewPager.setAdapter(new DraweePagerAdapter());
        indicator.setViewPager(viewPager);

        if (mList.size() <= 1 || mList.size() >= 10) {
            indicator.setVisibility(View.GONE);
        }

        if (position_key < mList.size()) {
            viewPager.setCurrentItem(position_key);
        }

        toolBar.setTextCenter((position_key + 1) + "/" + mList.size());

        initListener();
    }


    public void initListener() {


        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                toolBar.setTextCenter((position + 1) + "/" + mList.size());

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    public class DraweePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mList.size();
        }

        public String getItem(int position) {

            return mList.get(position);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup viewGroup, final int position) {


            View view = LayoutInflater.from(context).inflate(R.layout.item_view_pager, null);


            final PhotoDraweeView photoDraweeView = (PhotoDraweeView) view.findViewById(R.id.image);
            final ProgressWheel pw = (ProgressWheel) view.findViewById(R.id.progressWheel);

            ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(getItem(position))).setLocalThumbnailPreviewsEnabled(true)
                    .setProgressiveRenderingEnabled(true)
                    .setResizeOptions(new ResizeOptions(W, W)).build();
            PipelineDraweeControllerBuilder controller = Fresco.newDraweeControllerBuilder();

            controller.setAutoPlayAnimations(true);

            controller.setImageRequest(imageRequest);
            controller.setOldController(photoDraweeView.getController());

            controller.setControllerListener(new BaseControllerListener<ImageInfo>() {
                @Override
                public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                    super.onFinalImageSet(id, imageInfo, animatable);
                    if (imageInfo == null) {
                        return;
                    }
                    photoDraweeView.update(imageInfo.getWidth(), imageInfo.getHeight());


                }

                @Override
                public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {

                }
            });
            photoDraweeView.setController(controller.build());


//            如果是网络图片可以下载，下载图片
            if (isDownLoad) {

                pw.setVisibility(View.VISIBLE);

                CallerThreadExecutor ct = CallerThreadExecutor.getInstance();
                ct.execute(new Runnable() {
                    @Override
                    public void run() {
                        pw.setVisibility(View.VISIBLE);


                    }
                });


                ImagePipeline imagePipeline = Fresco.getImagePipeline();
                DataSource<CloseableReference<CloseableImage>>
                        dataSource = imagePipeline.fetchDecodedImage(imageRequest, context);


                DataSubscriber dataSubscriber =
                        new BaseDataSubscriber<CloseableReference<CloseableImage>>() {
                            @Override
                            public void onNewResultImpl(
                                    DataSource<CloseableReference<CloseableImage>> dataSource) {

                                if (!dataSource.isFinished()) {

                                }


                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        pw.setVisibility(View.GONE);
                                    }
                                });


                                CloseableReference<CloseableImage> imageReference = dataSource.getResult();


                                if (imageReference != null) {
                                    try {


                                        CloseableImage image = imageReference.get();
                                        // do something with the image


                                        if (image instanceof CloseableBitmap) {
                                            handleBitmap((CloseableBitmap) image, position);
                                        } else if (image instanceof CloseableAnimatedImage) {
                                            handleAnimateBitmap((CloseableAnimatedImage) image, position);
                                        }


                                    } finally {
                                        imageReference.close();
                                        CloseableReference.closeSafely(imageReference);
                                    }
                                }
                            }

                            @Override
                            public void onFailureImpl(DataSource dataSource) {
                                Throwable throwable = dataSource.getFailureCause();
                                // handle failure

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        pw.setVisibility(View.GONE);
                                    }
                                });

                            }

                            @Override
                            public void onProgressUpdate(DataSource dataSource) {


                                pw.setProgress(dataSource.getProgress());

                            }
                        };

                dataSource.subscribe(dataSubscriber, ct);

            } else {

                pw.setVisibility(View.GONE);
            }


            try {
                viewGroup.addView(view, ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return view;
        }
    }


    private void handleAnimateBitmap(CloseableAnimatedImage animatedImage, int position) {

        AnimatedDrawableFactory animatedDrawableFactory =
                Fresco.getImagePipelineFactory().getAnimatedDrawableFactory();
        AnimatedDrawable drawable =
                animatedDrawableFactory.create(animatedImage.getImageResult());

        Bitmap bitmap = drawable2Bitmap(drawable);
        map.put(position, bitmap);


    }

    private void handleBitmap(CloseableBitmap closeableBitmap, int position) {
        Bitmap bitmap = closeableBitmap.getUnderlyingBitmap();
        map.put(position, bitmap);

    }


    /**
     * 保存图片
     *
     * @param menuItem
     */
    public void clickMenu(MenuItem menuItem) {

        int postion = viewPager.getCurrentItem();

        Bitmap bitmap = map.get(postion);

        if (bitmap != null) {


            boolean save = CanPhotoHelper.getInstance().saveBitmapToSdCard(context, bitmap, parent);
            if (save) {


                CanPhotoHelper.getInstance().showSnackbar(toolBar, "保存图片成功");
            } else {


                CanPhotoHelper.getInstance().showSnackbar(toolBar, "保存图片失败");

            }
        } else {

            CanPhotoHelper.getInstance().showSnackbar(toolBar, "保存图片失败");
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (isDownLoad) {

            getMenuInflater().inflate(R.menu.actions_download, menu);


        }


        return true;
    }


    /**
     * Drawable 转 bitmap
     *
     * @param drawable
     * @return
     */
    public Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof NinePatchDrawable) {
            Bitmap bitmap = Bitmap
                    .createBitmap(
                            drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight(),
                            drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                    : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            return bitmap;
        } else {
            return null;
        }
    }


    @Override
    protected void onDestroy() {


        if (!map.isEmpty()) {
            map.clear();
            map = null;
        }


        super.onDestroy();
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
