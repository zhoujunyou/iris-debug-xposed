package com.zjy.irissqlxposed;

import android.content.pm.ApplicationInfo;

import com.zjy.irissqlxposed.dex.FindDexModule;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Description:
 *
 * @author:zhou.junyou Create by:Android Studio
 * Date:2018/1/19
 */
public class IrisXposedModule implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam.appInfo == null || (lpparam.appInfo.flags & (ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)) != 0) {
            return;
        }
        if (lpparam.isFirstApplication && !"com.zjy.irissqlxposed".equals(lpparam.packageName)) {
            XposedBridge.log("com.zjy.irissqlxposed load"+lpparam.packageName);
//            ApplicationHookContext.getInstance().initModuleContext(lpparam);
            FindDexModule.getInstance().init(lpparam);

        }

    }
}


