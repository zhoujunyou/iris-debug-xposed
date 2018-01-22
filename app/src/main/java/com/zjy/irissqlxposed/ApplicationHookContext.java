package com.zjy.irissqlxposed;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;

import com.zjy.irissqlxposed.cmd.SqlDebugBroadCast;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Description:
 *
 * @author:zhou.junyou Create by:Android Studio
 * Date:2018/1/19
 */
public class ApplicationHookContext {
    private static ApplicationHookContext INSTANCE = new ApplicationHookContext();

    private ApplicationHookContext() {
    }

    public static ApplicationHookContext getInstance() {
        return INSTANCE;
    }

    XC_LoadPackage.LoadPackageParam mPackageParam;
    private boolean HAS_REGISTER = false;



    public void initModuleContext(XC_LoadPackage.LoadPackageParam packageParam) {
        this.mPackageParam = packageParam;
        String appClassName = this.mPackageParam.appInfo.className;
        if (appClassName == null) {
            Method hookOnCreateMethod = null;
            try {
                hookOnCreateMethod = Application.class.getDeclaredMethod("onCreate", new Class[]{});
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            XposedBridge.hookMethod(hookOnCreateMethod, new ApplicationOnCreateHook());

        } else {
            Class<?> hookApplicationClass = null;
            try {
                hookApplicationClass = mPackageParam.classLoader.loadClass(appClassName);
                if (hookApplicationClass != null) {
                    Method hookOnCreateMethod = hookApplicationClass.getDeclaredMethod("onCreate", new Class[]{});
                    if (hookOnCreateMethod != null) {
                        XposedBridge.hookMethod(hookOnCreateMethod, new ApplicationOnCreateHook());
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                Method hookOnCreateMethod;
                try {
                    hookOnCreateMethod = Application.class.getDeclaredMethod("onCreate", new Class[]{});
                    if (hookOnCreateMethod != null) {
                        XposedBridge.hookMethod(hookOnCreateMethod, new ApplicationOnCreateHook());
                    }
                } catch (NoSuchMethodException e1) {
                    e1.printStackTrace();
                }

            }
        }
    }

    private  class ApplicationOnCreateHook extends XC_MethodHook{
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            super.beforeHookedMethod(param);
        }

        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            super.afterHookedMethod(param);
            Context context = (Context) param.thisObject;
            if(!HAS_REGISTER){
                IntentFilter filter = new IntentFilter(SqlDebugBroadCast.INTENT_ACTION);
                context.registerReceiver(new SqlDebugBroadCast(),filter);
                HAS_REGISTER =true;
            }
        }
    }
}
