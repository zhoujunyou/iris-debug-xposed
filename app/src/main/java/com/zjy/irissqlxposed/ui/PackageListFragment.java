package com.zjy.irissqlxposed.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.zjy.irissqlxposed.R;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Description:
 *
 * @author:zhou.junyou Create by:Android Studio
 * Date:2018/1/22
 */
public class PackageListFragment extends Fragment {
    protected DataSource mDataSource;
    protected CompositeDisposable mCompositeDisposable;
    protected PackagesAdapter mPackagesAdapter;
    protected Context mContext;
    private int packageType = DataSource.ALL;
    protected SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataSource = DataSourceImpl.getInstance(getContext());
        mCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_package_list, container, false);
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        RecyclerView recPackages = view.findViewById(R.id.list_app);
        recPackages.setLayoutManager(new LinearLayoutManager(getContext()));
        mPackagesAdapter = new PackagesAdapter(new ArrayList<PackageInfoData>());
        recPackages.setAdapter(mPackagesAdapter);
        RadioGroup radioGroup = view.findViewById(R.id.appFilterGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rpkg_all:
                        getPackageInfos(DataSource.ALL);
                        break;
                    case R.id.rpkg_sys:
                        getPackageInfos(DataSource.SYS);
                        break;
                    case R.id.rpkg_user:
                        getPackageInfos(DataSource.USER);
                        break;
                    default:
                }
            }
        });
        mSwipeRefreshLayout = view.findViewById(R.id.layout_swipe);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mDataSource.refreshPackages();
                getPackageInfos(packageType);
            }
        });
        getPackageInfos(DataSource.ALL);
        mPackagesAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                int id = view.getId();
                PackageInfoData item = (PackageInfoData) adapter.getItem(position);
                if (id == R.id.item_checkbox) {
                    onItemChecked((CheckBox) view, item);
                }
            }
        });

        mPackagesAdapter.setOnItemClickListener(getItemClickListener());

    }

    protected BaseQuickAdapter.OnItemClickListener getItemClickListener() {
        return null;
    }

    protected void onItemChecked(CheckBox view, PackageInfoData item) {

    }

    private void getPackageInfos(int type) {
        packageType = type;
        DisposableObserver<List<PackageInfoData>> observer = mDataSource.getPackageWithType(type)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<List<PackageInfoData>>() {
                    @Override
                    public void onNext(List<PackageInfoData> packageInfoData) {
                        mPackagesAdapter.setNewData(packageInfoData);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        mCompositeDisposable.add(observer);
    }

}
