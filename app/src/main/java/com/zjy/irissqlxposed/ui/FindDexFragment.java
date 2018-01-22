package com.zjy.irissqlxposed.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.zjy.irissqlxposed.cmd.SqlDebugSender;

/**
 * Description:
 *
 * @author:zhou.junyou Create by:Android Studio
 * Date:2018/1/22
 */
public class FindDexFragment extends PackageListFragment {
    public static final String TAG = FindDexFragment.class.getSimpleName();
    private boolean supportFindDex = true;


    public static FindDexFragment newInstance() {

        Bundle args = new Bundle();

        FindDexFragment fragment = new FindDexFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkSupport();
    }


    private void checkSupport() {
        try {
            Class.forName("com.android.dex.Dex");
        } catch (ClassNotFoundException v4) {
            setFindDexEnable(false);
            new AlertDialog.Builder(mContext)
                    .setMessage("这台设备不支持本软件脱壳")
                    .setPositiveButton("知道了", null).show();

        }


    }

    private void setFindDexEnable(boolean b) {
        supportFindDex = b;
    }

    @Override
    protected void onItemChecked(CheckBox view, PackageInfoData item) {
        super.onItemChecked(view, item);
        item.sqlDebugEnable = view.isChecked();
        mDataSource.applyCheckRule(item);
        SqlDebugSender.sendCmd(item);
    }

    @Override
    protected BaseQuickAdapter.OnItemClickListener getItemClickListener() {
        if (!supportFindDex) {
            Toast.makeText(mContext, "这台设备不支持本软件脱壳", Toast.LENGTH_SHORT).show();
            return null;
        }
        return new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                PackageInfoData item = (PackageInfoData) adapter.getItem(position);
                item.findDexEnable = true;
                item.dexDir = item.appinfo.dataDir;
                mDataSource.saveDexPackage(item);
                new AlertDialog.Builder(mContext).setMessage(new StringBuffer("设置保存成功，请重新打开目标软件，hook包名")
                        .append(item.pkgName).append("\n\n\ndex输出目录:").append(item.appinfo.dataDir))
                        .setPositiveButton("OK", null)
                        .show();
            }
        };
    }
}
