package com.zjy.irissqlxposed.ui;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zjy.irissqlxposed.R;

import java.util.List;

/**
 * Description:
 *
 * @author:zhou.junyou Create by:Android Studio
 * Date:2018/1/19
 */
public class PackagesAdapter extends BaseQuickAdapter<PackageInfoData, BaseViewHolder> {
    public PackagesAdapter(@Nullable List<PackageInfoData> data) {
        super(R.layout.item_pacakge, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, PackageInfoData item) {
        helper.setImageDrawable(R.id.app_icon, item.cached_icon);
        helper.setText(R.id.tv_package_name, item.pkgName);
        helper.setChecked(R.id.item_checkbox, item.sqlDebugEnable);
        helper.addOnClickListener(R.id.item_checkbox);

    }
}
