package com.canyinghao.canphotos;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

/**
 * 自定义的ToolBar，添加了两个TextView作为按钮，一个TextView居中作为标题
 * 不过一般没有用添加的按钮，按钮还是用自带的NavigationIcon作为返回按钮，onCreateOptionsMenu添加右边菜单按钮
 */
public class CanToolBar extends Toolbar {
    public TextView tv_left;
    public TextView tv_center;
    public TextView tv_right;

    public CanToolBar(Context context) {
        this(context, null);

    }

    public CanToolBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        defultSet();
    }



    private void init() {
        int dp_15 = (int) getResources().getDimension(R.dimen.dimen_15);

        tv_left = new TextView(getContext());
        Toolbar.LayoutParams paramsLeft = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.MATCH_PARENT);
        paramsLeft.gravity = Gravity.LEFT;
        tv_left.setLayoutParams(paramsLeft);
        tv_left.setTextColor(Color.WHITE);
        tv_left.setGravity(Gravity.CENTER);
        tv_left.setTextSize(15);
        tv_left.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.button_back, 0, 0, 0);
        tv_left.setPadding(dp_15, 0, dp_15, 0);
        tv_left.setSingleLine();
        addView(tv_left);
        tv_center = new TextView(getContext());

        Toolbar.LayoutParams params = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        tv_center.setLayoutParams(params);
        tv_center.setTextColor(Color.WHITE);
        tv_center.setTextSize(20);
        tv_center.setGravity(Gravity.CENTER);
        tv_center.setSingleLine();
        tv_center.setEllipsize(TextUtils.TruncateAt.END);

        addView(tv_center);
        tv_right = new TextView(getContext());

        Toolbar.LayoutParams paramsRight = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.MATCH_PARENT);
        paramsRight.gravity = Gravity.RIGHT;
        tv_right.setLayoutParams(paramsRight);
        tv_right.setTextColor(Color.WHITE);
        tv_right.setGravity(Gravity.CENTER);
        tv_right.setTextSize(15);
        tv_right.setPadding(dp_15, 0, dp_15, 0);

        tv_right.setSingleLine();
        addView(tv_right);


    }


    /**
     * 默认设置
     */
    public void defultSet() {
        tv_right.setVisibility(View.GONE);
        tv_left.setVisibility(View.GONE);


        tv_left.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getContext() instanceof Activity) {

                    ((Activity) getContext()).onBackPressed();
                }
            }
        });


        setNavigationIcon(R.mipmap.button_back);
        setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getContext() instanceof Activity) {
                    ((Activity) getContext()).onBackPressed();
                }
            }
        });


    }


    /**
     * 设置标题
     *
     * @param cs
     */
    public void setTextCenter(CharSequence cs) {
        tv_center.setText(cs);


    }

    public void setTextLeft(CharSequence cs) {
        tv_left.setVisibility(View.VISIBLE);

        tv_left.setText(cs);
    }

    public void setTextRight(CharSequence cs) {
        tv_right.setVisibility(View.VISIBLE);
        tv_right.setText(cs);
    }

}
