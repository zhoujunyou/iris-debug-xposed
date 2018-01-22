package com.zjy.irissqlxposed.ui;

import java.util.List;

/**
 * Description:
 *
 * @author:zhou.junyou Create by:Android Studio
 * Date:2018/1/19
 */
public interface DataSource {
    int ALL = 0;
    int SYS = 1;
    int USER = 2;


    io.reactivex.Observable<List<PackageInfoData>> getPackageWithType(int type);

    void applyCheckRule(PackageInfoData data);

    void refreshPackages();

    void saveDexPackage(PackageInfoData data);
}
