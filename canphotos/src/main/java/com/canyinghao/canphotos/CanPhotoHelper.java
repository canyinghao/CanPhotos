package com.canyinghao.canphotos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CanPhotoHelper {
    //  选取图片
    public static final int REQUEST_CODE_SEND_PICTURE = 10;

    //    图片路径合集
    public static final String PHOTO_COLLECTION = "PHOTO_COLLECTION";
    //    最多选择的图片数量
    public static final String PHOTO_MAX = "PHOTO_MAX";


    public static final String PHOTO_POSITION = "PHOTO_POSITION";
    public static final String PHOTO_TITLE = "PHOTO_TITLE";


    private static CanPhotoHelper helper;
    // 背景颜色
    private int bgColor = Color.parseColor("#333333");

    //    每行显示数量
    private int spanCount = 3;

    // 图片高度
    private int H;


    private CanPhotoHelper() {


    }

    public static CanPhotoHelper getInstance() {

        if (helper == null) {
            helper = new CanPhotoHelper();


        }

        return helper;

    }


    public void setBackGroundColor(int color) {
        bgColor = color;

    }

    public int getBackGroundColor() {
        return bgColor;

    }

    public int getSpanCount() {
        return spanCount;
    }

    public void setSpanCount(int spanCount) {
        if (spanCount > 0) {
            this.spanCount = spanCount;

        }

        H = 0;


    }

    /**
     * 前去图片选择界面
     *
     * @param context
     * @param array
     * @param max
     */
    public void gotoPhotoSelect(Activity context, ArrayList<String> array, int max) {
        Intent intent = new Intent(context, CanPhotoActivity.class);
        if (array != null && !array.isEmpty()) {
            intent.putStringArrayListExtra(PHOTO_COLLECTION, array);
        }
        intent.putExtra(PHOTO_MAX, max);


        context.startActivityForResult(intent, REQUEST_CODE_SEND_PICTURE);
    }


    public ArrayList<String> getLocalPreviewList(List<String> list) {
        ArrayList<String> arrayList = new ArrayList<String>();
        for (int i = 0; i < list.size(); i++) {

            String str = list.get(i);
            if (!TextUtils.isEmpty(str) && !str.contains("://")) {
                str = "file://" + str;
            }
            arrayList.add(str);
        }

        return arrayList;

    }


    /**
     * 预览图片
     *
     * @param context
     * @param list
     * @param position
     * @param saveFile
     */
    public void gotoPreview(Activity context, ArrayList<String> list, int position, File saveFile) {

        Intent intent = new Intent(context, CanViewPagerActivity.class);
        intent.putStringArrayListExtra(CanViewPagerActivity.ARRAY_LIST, list);
        intent.putExtra(CanViewPagerActivity.POSITION_KEY, position);

        String savePath = "";
        if (saveFile != null) {
            savePath = saveFile.getAbsolutePath();
        }
        intent.putExtra(CanViewPagerActivity.FILE_SAVE, savePath);
        context.startActivity(intent);
    }


    /**
     * 设置图片高度
     *
     * @param context
     * @param image
     * @param position
     */
    public void setImageHeight(Context context, ImageView image, int position) {


        switch (spanCount) {
            case 1:
                if (H <= 0) {
                    H = getScreenDisplayMetrics(context).widthPixels / spanCount;
                    H = H / 2;
                }

                break;
            case 2:
                H = getScreenDisplayMetrics(context).widthPixels / spanCount;
                if (position == 0) {
                    H = (int) (H / 1.5f);

                }

                break;

            default:
                if (H <= 0) {
                    H = getScreenDisplayMetrics(context).widthPixels / spanCount;

                }
                break;

        }

        if (H <= 0) {
            H = getScreenDisplayMetrics(context).widthPixels / spanCount;


        }

        ViewGroup.LayoutParams params = image.getLayoutParams();
        params.height = H;
        image.setLayoutParams(params);
    }


    /**
     * 设置recyclerView的LayoutManager
     *
     * @param context
     * @param recyclerView
     */
    public void setRecyclerViewLayoutManager(Context context, RecyclerView recyclerView) {
        RecyclerView.LayoutManager manager = null;

        if (spanCount == 2) {

            manager = new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL);
        } else {
            manager = new GridLayoutManager(context, spanCount);
        }


        recyclerView.setLayoutManager(manager);

    }


    /*
     * 获取图片的字段信息
     */
    static final String[] STORE_IMAGES = {
            MediaStore.Images.Media.DISPLAY_NAME, // 名称
            MediaStore.Images.Media.DATA, MediaStore.Images.Media.LONGITUDE, // 经度
            MediaStore.Images.Media._ID, // id
            MediaStore.Images.Media.BUCKET_ID, // dir id 目录
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME // dir name 目录名字
    };

    /*
     * 按相册获取图片信息
     */
    public List<PhotoBean> getPhotoAlbum(Context context) {
        List<PhotoBean> albumList = new ArrayList<PhotoBean>();
        Cursor cursor = MediaStore.Images.Media.query(
                context.getContentResolver(),
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, STORE_IMAGES);
        Map<String, PhotoBean> countMap = new HashMap<String, PhotoBean>();
        PhotoBean pa = null;
        while (cursor.moveToNext()) {
            String path = cursor.getString(1);
            String id = cursor.getString(3);
            String dir_id = cursor.getString(4);
            String dir = cursor.getString(5);
            if (!new File(path).exists()) {
                continue;
            }

            if (!countMap.containsKey(dir_id)) {
                pa = new PhotoBean();
                pa.name = dir;
                pa.bitmap = (Integer.parseInt(id));
                pa.count = "1";
                pa.bitList.add(new PictureBean(Integer.valueOf(id), path));
                countMap.put(dir_id, pa);
            } else {
                pa = countMap.get(dir_id);
                pa.count = (String.valueOf(Integer.parseInt(pa.count) + 1));
                pa.bitList.add(new PictureBean(Integer.valueOf(id), path));
            }
        }
        cursor.close();
        Iterable<String> it = countMap.keySet();
        for (String key : it) {
            albumList.add(countMap.get(key));
        }
        return albumList;
    }


    /**
     * 消息提示
     *
     * @param v
     * @param message
     */
    public void showSnackbar(View v, String message) {


        Snackbar sb = Snackbar.make(v, message, Snackbar.LENGTH_SHORT);
        sb.getView().setBackgroundColor(Color.parseColor("#232429"));
        sb.setAction("", null).setActionTextColor(Color.WHITE).show();


    }


    /**
     * 设置图片
     *
     * @param image
     * @param url
     * @param width
     * @param heigth
     */
    public void setDraweeImage(SimpleDraweeView image, String url, int width, int heigth) {


        if (width <= 0) {
            width = dp2Px(image.getContext(), 50);
        }
        if (heigth <= 0) {
            heigth = dp2Px(image.getContext(), 50);
        }

        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url)).setLocalThumbnailPreviewsEnabled(true).setResizeOptions(new ResizeOptions(width, heigth)).build();
        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                .setImageRequest(imageRequest)
                .setAutoPlayAnimations(true)
                .build();
        image.setController(draweeController);

    }


    /**
     * 得到屏幕相关参数
     *
     * @param context
     * @return
     */
    public DisplayMetrics getScreenDisplayMetrics(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        Display display = manager.getDefaultDisplay();
        display.getMetrics(displayMetrics);

        return displayMetrics;

    }

    /**
     * dp转px
     *
     * @param dpValue
     * @return
     */
    public int dp2Px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px转dp
     */
    public int px2Dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;

        return (int) (pxValue / scale + 0.5f);
    }


    /**
     * 保存图片到sdcard
     *
     * @param bitmap
     * @return
     */
    public boolean saveBitmapToSdCard(Context context, Bitmap bitmap, File parent) {
        boolean save = false;
        if (bitmap == null) {
            return save;
        }
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return save;
        }


        FileOutputStream out = null;
        File f = null;
        try {


            long millis = Calendar.getInstance().getTimeInMillis();
            f = new File(parent, millis + ".jpg");


            if (!parent.exists()) {
                parent.mkdirs();
            }
            boolean createNewFile = f.createNewFile();
            if (!createNewFile) {
                return false;
            }
            out = new FileOutputStream(f);
            save = bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (save) {
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(f);
            intent.setData(uri);
            context.sendBroadcast(intent);
        }

        return save;
    }

}
