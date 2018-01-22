package com.zjy.irissqlxposed.dex;

import android.text.TextUtils;

import com.google.gson.GsonBuilder;
import com.zjy.irissqlxposed.SpDataSourceProvider;
import com.zjy.irissqlxposed.ui.PackageInfoData;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Description:
 *
 * @author:zhou.junyou Create by:Android Studio
 * Date:2018/1/22
 */
public class FindDexModule {
    private static FindDexModule INSTANCE = new FindDexModule();
    XSharedPreferences shared;
    Class Dex;
    Method Dex_getBytes;
    Method getDex;

    private FindDexModule() {
        getDex = null;
    }

    public static FindDexModule getInstance() {
        return INSTANCE;
    }

    public void init(XC_LoadPackage.LoadPackageParam packageParam) {
        Class stringClass = null;
        shared = new XSharedPreferences(SpDataSourceProvider.PACKAGE_NAME, SpDataSourceProvider.PACKAGE);
        shared.reload();
        initRefect();
        String json = shared.getString(SpDataSourceProvider.PACKAGES_DEX_ENABLE, null);
        PackageInfoData packageInfoData = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().fromJson(json, PackageInfoData.class);
        if (packageInfoData == null || packageInfoData.pkgName == null) {
            XposedBridge.log("没有指定apk,请打开模块选择要脱dex的apk");
        } else if (!packageParam.packageName.equals(packageInfoData.pkgName)) {
        } else {
            XposedBridge.log(new StringBuffer().append(packageInfoData.pkgName).append(" has hook").toString());
            String className = "java.lang.ClassLoader";
            ClassLoader classLoader = packageParam.classLoader;
            String methodName = "loadClass";
            try {
                stringClass = Class.forName("java.lang.String");
            } catch (ClassNotFoundException e) {
                throw new NoClassDefFoundError(e.getMessage());
            }
            XposedHelpers.findAndHookMethod(className, classLoader, methodName, stringClass, Boolean.TYPE, new LoadClassHook(this, packageInfoData, packageParam));
        }
    }

    public void initRefect() {
        Class clazz = null;
        try {
            Dex = Class.forName("com.android.dex.Dex");
            Dex_getBytes = Dex.getDeclaredMethod("getBytes");
            clazz = Class.forName("java.lang.Class");
            getDex = clazz.getDeclaredMethod("getDex");
        } catch (Exception e) {
            XposedBridge.log(e.toString());
            e.printStackTrace();
        }
    }


    public class LoadClassHook extends XC_MethodHook {
        private final FindDexModule mFindDexModule;
        private final PackageInfoData mPackageInfoData;
        private final XC_LoadPackage.LoadPackageParam mPackageParam;

        public LoadClassHook(FindDexModule findDexModule, PackageInfoData packageInfoData, XC_LoadPackage.LoadPackageParam packageParam) {
            super();
            mFindDexModule = findDexModule;
            mPackageInfoData = packageInfoData;
            mPackageParam = packageParam;
        }

        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        }

        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            XposedBridge.log(" after hook : ");
            Class findModuleClass = null;
            String className = "";
            Object result = param.getResult();
            if (result == null) {
                return;
            }
            try {
                className = ((Class) result).getName();
                findModuleClass = Class.forName("com.zjy.irissqlxposed.dex.FindDexModule");
                findModuleClass.getClassLoader();
                Class.forName(className, false, ClassLoader.getSystemClassLoader());
                byte[] bytes = (byte[]) mFindDexModule.Dex_getBytes.invoke(getDex.invoke(result));
                if (bytes == null) {
                    return;
                }
                String dir = TextUtils.isEmpty(mPackageInfoData.dexDir)?"/sdcard":mPackageInfoData.dexDir;
                File dexFile = new File(dir, new StringBuffer(mPackageParam.packageName).append(bytes.length).append(".dex").toString());
                if (dexFile.exists()) {
                    return;
                }
                writeByte(bytes, dexFile.getAbsolutePath());
            } catch (Exception e) {
                XposedBridge.log(e.toString());
            }

        }
    }


    public static void writeByte(byte[] bytes, String filePath) {
        try {
            FileOutputStream v2 = new FileOutputStream(filePath);
            v2.write(bytes);
            v2.close();
        } catch (Exception v4) {
        }
    }

}
