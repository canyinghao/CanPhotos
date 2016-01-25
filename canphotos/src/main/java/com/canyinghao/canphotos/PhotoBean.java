package com.canyinghao.canphotos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class PhotoBean implements Serializable {

    public String name;   //相册名字
    public String count; //数量
    public int bitmap;  // 相册第一张图片

    public List<PictureBean> bitList = new ArrayList<PictureBean>();


}
