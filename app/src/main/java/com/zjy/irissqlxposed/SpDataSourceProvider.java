package com.zjy.irissqlxposed;

import android.content.Context;
import android.util.SparseArray;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.zjy.irissqlxposed.ui.PackageInfoData;
import com.zjy.irissqlxposed.util.SPUtils;

import java.util.List;

/**
 * Description:
 *
 * @author:zhou.junyou Create by:Android Studio
 * Date:2018/1/20
 */
public class SpDataSourceProvider {
    public static final String PACKAGE = "package";
    public static final String PACKAGE_NAME = "com.zjy.irissqlxposed";

    public static final String PACKAGES_SQL_ENABLE = "packages_sql_enable";

    public static final String PACKAGES_DEX_ENABLE = "packages_dex_enable";


    protected final Context mContext;
    protected final SPUtils mSPUtils;

    private SpDataSourceProvider(Context context) {
        mContext = context;
        mSPUtils = SPUtils.getInstance(PACKAGE, context, Context.MODE_WORLD_READABLE);

    }

    private static SpDataSourceProvider instance = null;


    public static SpDataSourceProvider getInstance(Context context) {
        synchronized (SpDataSourceProvider.class) {
            if (instance == null) {
                instance = new SpDataSourceProvider(context);
            }
        }
        return instance;
    }


    public SparseArray<PackageInfoData> getLocalSqlEnablePackage() {
        String packages = mSPUtils.getString(PACKAGES_SQL_ENABLE);
        List<PackageInfoData> packageInfoDataList = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().fromJson(packages, new TypeToken<List<PackageInfoData>>() {
        }.getType());
        SparseArray<PackageInfoData> array = new SparseArray<>();
        if (packageInfoDataList == null) {
            return array;
        }
        for (PackageInfoData p :
                packageInfoDataList) {
            array.put(p.uid, p);
        }
        return array;

    }

    public void saveSqlEnablePackages(List<PackageInfoData> packageInfoData) {
        if (packageInfoData == null) {
            return;
        }
        for (int i = 0; i < packageInfoData.size(); i++) {
            if (!packageInfoData.get(i).sqlDebugEnable) {
                packageInfoData.remove(i);
                i--;
            }
        }
        mSPUtils.put(PACKAGES_SQL_ENABLE, new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(packageInfoData));
    }

    public void saveDexEnablePackage(PackageInfoData packageInfoData) {
        mSPUtils.put(PACKAGES_DEX_ENABLE, new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(packageInfoData));
    }

    public PackageInfoData getDexEnablePacakge() {
        String string = mSPUtils.getString(PACKAGES_DEX_ENABLE);
        PackageInfoData packageInfoData = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().fromJson(string, PackageInfoData.class);
        return packageInfoData;
    }
}
