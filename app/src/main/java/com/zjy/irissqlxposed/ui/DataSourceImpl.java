package com.zjy.irissqlxposed.ui;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ScaleDrawable;
import android.util.SparseArray;

import com.zjy.irissqlxposed.SpDataSourceProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.PublishSubject;

/**
 * Description:
 *
 * @author:zhou.junyou Create by:Android Studio
 * Date:2018/1/19
 */
public class DataSourceImpl implements DataSource {

    protected final SpDataSourceProvider mSpDataSource;
    Context mContext;
    SparseArray<PackageInfoData> packageList = new SparseArray<>();
    boolean isDirty = true;
    PublishSubject<PackageInfoData> packagePublishSubject = PublishSubject.create();


    private DataSourceImpl(Context context) {
        mContext = context;
        mSpDataSource = SpDataSourceProvider.getInstance(context);
        packagePublishSubject
                .debounce(3, TimeUnit.SECONDS)
                .doOnNext(new Consumer<PackageInfoData>() {
                    @Override
                    public void accept(PackageInfoData packageInfoData) throws Exception {
                        mSpDataSource.saveSqlEnablePackages(asList(packageList));
                    }
                }).subscribe();
    }

    private static DataSourceImpl instance = null;


    public static DataSourceImpl getInstance(Context context) {
        synchronized (DataSourceImpl.class) {
            if (instance == null) {
                instance = new DataSourceImpl(context);
            }
        }
        return instance;
    }


    @Override
    public Observable<List<PackageInfoData>> getPackageWithType(final int type) {
        if (packageList.size() != 0 && !isDirty) {

        } else {
            packageList.clear();
            SparseArray<PackageInfoData> localPackageDatas = mSpDataSource.getLocalSqlEnablePackage();
            PackageManager pkgmanager = mContext.getPackageManager();
            List<ApplicationInfo> installed = pkgmanager.getInstalledApplications(PackageManager.GET_META_DATA);
            for (int i = 0; i < installed.size(); i++) {
                ApplicationInfo apinfo = installed.get(i);
                if (apinfo == null) {
                    continue;
                }

                PackageInfoData app = new PackageInfoData();
                app.uid = apinfo.uid;
                app.installTime = new File(apinfo.sourceDir).lastModified();
                app.appinfo = apinfo;
                app.pkgName = apinfo.packageName;
                app.grade = getGrade(apinfo);
                app.cached_icon = new ScaleDrawable(pkgmanager.getApplicationIcon(app.appinfo), 0, 32, 32).getDrawable();
                if (localPackageDatas != null && localPackageDatas.get(app.uid) != null) {
                    app.sqlDebugEnable = localPackageDatas.get(app.uid).sqlDebugEnable;
                }
                packageList.put(app.uid, app);
                isDirty = false;
            }


        }
        List<PackageInfoData> packageInfoDataList = asList(packageList);
        Collections.sort(packageInfoDataList, new Comparator<PackageInfoData>() {
            @Override
            public int compare(PackageInfoData o1, PackageInfoData o2) {
                int p1 = o1.sqlDebugEnable ? 1 : 0;
                int p2 = o2.sqlDebugEnable ? 1 : 0;
                return p2 - p1;
            }
        });
        return Observable.fromIterable(packageInfoDataList)
                .filter(new Predicate<PackageInfoData>() {
                    @Override
                    public boolean test(PackageInfoData packageInfoData) throws Exception {
                        return type == ALL || type == packageInfoData.grade;
                    }
                }).toList().toObservable();
    }

    @Override
    public void applyCheckRule(PackageInfoData data) {
        if (data.sqlDebugEnable) {
            packageList.put(data.uid, data);
        } else {
            packageList.remove(data.uid);
        }
        packagePublishSubject.onNext(data);
    }

    @Override
    public void refreshPackages() {
        isDirty = true;
    }

    @Override
    public void saveDexPackage(PackageInfoData data) {
        mSpDataSource.saveDexEnablePackage(data);
    }


    private int getGrade(ApplicationInfo apinfo) {
        return (apinfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 ? USER : SYS;
    }

    public static <C> List<C> asList(SparseArray<C> sparseArray) {
        if (sparseArray == null) return null;
        List<C> arrayList = new ArrayList<C>(sparseArray.size());
        for (int i = 0; i < sparseArray.size(); i++)
            arrayList.add(sparseArray.valueAt(i));
        return arrayList;
    }
}
