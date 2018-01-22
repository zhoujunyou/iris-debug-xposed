package com.zjy.irissqlxposed.cmd;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService;

/**
 * Description:
 *
 * @author:zhou.junyou Create by:Android Studio
 * Date:2018/1/22
 */
public class SqlDebugBroadCast extends BroadcastReceiver {
    public static String INTENT_ACTION = "com.zjy.irissqlxposed.sqlite_debug";
    public static String COMMAND_NAME_KEY = "cmd";
    public static String TARGET_KEY = "uid";

    public static final String START_DEBUG = "1";
    public static final String STOP_DEBUG = "2";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (INTENT_ACTION.equals(intent.getAction())) {
            try {
                int uid = intent.getIntExtra(TARGET_KEY, 0);
                if (uid == android.os.Process.myUid()) {
                    String cmd = intent.getStringExtra(COMMAND_NAME_KEY);
                    processCommand(context, cmd);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void processCommand(Context context, String cmd) {
        if (cmd.equals(START_DEBUG)) {
            Log.i(INTENT_ACTION + "-" + context.getPackageName(), " start sqlite debug");
            SQLiteStudioService.instance().start(context);
        } else if (cmd.equals(STOP_DEBUG)) {
            SQLiteStudioService.instance().stop();
            Log.i(INTENT_ACTION + "-" + context.getPackageName(), " stop sqlite debug");
        }
    }
}
