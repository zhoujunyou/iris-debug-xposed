package com.zjy.irissqlxposed.cmd;

import android.util.Log;

import com.zjy.irissqlxposed.ui.PackageInfoData;
import com.zjy.irissqlxposed.util.ShellUtil;

/**
 * Description:
 *
 * @author:zhou.junyou Create by:Android Studio
 * Date:2018/1/22
 */
public class SqlDebugSender {
    public static final String TAG = SqlDebugSender.class.getSimpleName();
    public static String CMD_PATTERN = "am broadcast -a com.zjy.irissqlxposed.sqlite_debug --user 0 --ei uid %s --es cmd %s";

    public static void sendCmd(PackageInfoData data) {
        String cmd = data.sqlDebugEnable ? "1" : "2";
        String format = String.format(CMD_PATTERN, data.uid + "", cmd);
        Log.i(TAG, format);
        ShellUtil.CommandResult commandResult = ShellUtil.execCommand(format, false);
        if(commandResult.result!=0){
            Log.i(TAG, commandResult.errorMsg);
        }
    }
}
