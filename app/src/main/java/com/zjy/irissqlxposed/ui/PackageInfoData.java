package com.zjy.irissqlxposed.ui;

import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Description:
 *
 * @author:zhou.junyou Create by:Android Studio
 * Date:2018/1/19
 */
public class PackageInfoData implements Serializable{
    /**
     * linux user id
     */
    @Expose
    public int uid;
    /**
     * rules saving & load
     **/
    @Expose
    public String pkgName;

    /**
     * application info
     */
    @Expose(serialize = false,deserialize = false)    public ApplicationInfo appinfo;
    /**
     * cached application icon
     */
    @Expose(serialize = false,deserialize = false)    public Drawable cached_icon;

    /**
     * install time
     */
    @Expose(serialize = false,deserialize = false)    public long installTime;


    /**
     * 1 system app
     * 2 user  app
     */
    @Expose(serialize = false,deserialize = false)

    public int grade;


    @Expose
    public boolean sqlDebugEnable;

    @Expose
    public boolean findDexEnable;

    @Expose
    public String dexDir;
}
