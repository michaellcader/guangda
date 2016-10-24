package com.thinkive.android.trade_bz.a_hk.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.thinkive.android.trade_bz.R;
import com.thinkive.android.trade_bz.a_hk.adapter.HKCapitalLiabilitiesAdapter;
import com.thinkive.android.trade_bz.a_hk.bean.HKCapitalLiabilitiesBean;
import com.thinkive.android.trade_bz.a_stock.bll.BasicServiceImpl;
import com.thinkive.android.trade_bz.a_stock.fragment.AbsBaseFragment;
import com.thinkive.android.trade_bz.interfaces.IRequestAction;
import com.thinkive.android.trade_bz.request.HK301633;
import com.thinkive.android.trade_bz.utils.DateUtils;
import com.thinkive.android.trade_bz.utils.ToastUtils;
import com.thinkive.android.trade_bz.views.PullToRefresh.PullToRefreshBase;
import com.thinkive.android.trade_bz.views.PullToRefresh.PullToRefreshListView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 创建者     舒旺
 * 创建时间   2016/5/27 20:54
 * 描述	     负债查询
 * 更新者     $Author$
 * 更新时间   $Date$
 */
public class HKCapitalLiabilitiesFragment extends AbsBaseFragment implements PullToRefreshListView.OnRefreshListener {
    private LinearLayout mLinLoadingSet;
    private PullToRefreshListView mLvRefreshList;
    private LinearLayout mLinNotDataSet;
    private HKCapitalLiabilitiesServiceImpl mHkCapitalLiabilitiesService;
    private Context mContext;
    private HKCapitalLiabilitiesAdapter mHkCapitalLiabilitiesAdapter;
    private ListView mRefreshableView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        mContext = getActivity();
        View rootViw = inflater.inflate(R.layout.fragment_common_refresh_list, null);
        findViews(rootViw);
        initData();
        initViews();

        return rootViw;
    }

    @Override
    protected void findViews(View view) {
        mLinLoadingSet = (LinearLayout) view.findViewById(R.id.lin_loading_set);
        mLvRefreshList = (PullToRefreshListView) view.findViewById(R.id.lv_refresh_list);
        mLvRefreshList.setOnRefreshListener(this);
        mRefreshableView = mLvRefreshList.getRefreshableView();
        mRefreshableView.setDivider(null);
        mLinNotDataSet = (LinearLayout) view.findViewById(R.id.lin_not_data_set);
    }


    @Override
    protected void setListeners() {

    }

    @Override
    protected void initData() {
        mHkCapitalLiabilitiesService = new HKCapitalLiabilitiesServiceImpl(this);
        mHkCapitalLiabilitiesAdapter = new HKCapitalLiabilitiesAdapter(mContext);

    }

    @Override
    protected void initViews() {
        mHkCapitalLiabilitiesService.reuqestLiabilitiesServiceImpl();


    }

    @Override
    protected void setTheme() {

    }

    /**
     * 请求数据成功后回调给主线程
     */
    public void setlLiabilitiesData(ArrayList<HKCapitalLiabilitiesBean> lLiabilitiesData) {
        if (null == lLiabilitiesData || lLiabilitiesData.size() <= 0) {
            //没有数据
            mLinLoadingSet.setVisibility(View.GONE);
            mLinNotDataSet.setVisibility(View.VISIBLE);
            mLvRefreshList.setVisibility(View.GONE);
        } else {
            mLinLoadingSet.setVisibility(View.GONE);
            mLinNotDataSet.setVisibility(View.GONE);
            mLvRefreshList.setVisibility(View.VISIBLE);
            mHkCapitalLiabilitiesAdapter.setListData(lLiabilitiesData);
            mRefreshableView.setAdapter(mHkCapitalLiabilitiesAdapter);
        }
        refreshComplete();

    }

    /**
     * 刷新完成,收回下拉联
     */
    public void refreshComplete() {
        mLvRefreshList.onPullDownRefreshComplete();
        mLvRefreshList.onPullUpRefreshComplete();
        mLvRefreshList.setLastUpdatedLabel(DateUtils.getDateString("HH:mm:ss"));
    }

    /**
     * 被下拉时执行
     */
    public void onDownRefresh() {
        mHkCapitalLiabilitiesService.reuqestLiabilitiesServiceImpl();
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        onDownRefresh();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {

    }
}

class HKCapitalLiabilitiesServiceImpl extends BasicServiceImpl {

    private HKCapitalLiabilitiesFragment mHKCapitalLiabilitiesFragment;

    public HKCapitalLiabilitiesServiceImpl(HKCapitalLiabilitiesFragment hkCapitalLiabilitiesFragment) {
        this.mHKCapitalLiabilitiesFragment = hkCapitalLiabilitiesFragment;
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onStop() {

    }

    public void reuqestLiabilitiesServiceImpl() {
        HashMap<String, String> map = new HashMap<String, String>();
        new HK301633(map, new IRequestAction() {
            @Override
            public void onSuccess(Context context, Bundle bundle) {
                ArrayList<HKCapitalLiabilitiesBean> dataList = bundle.getParcelableArrayList(HK301633.BUNDLE_KEY_HK_HISTORY_TRADE);
                mHKCapitalLiabilitiesFragment.setlLiabilitiesData(dataList);
            }

            @Override
            public void onFailed(Context context, Bundle bundle) {
                ToastUtils.toast(context, bundle.getString(HK301633.ERROR_INFO));
            }
        }).request();
    }
}