package com.thinkive.android.trade_bz.a_stock.activity;

import android.os.Bundle;
import android.view.View;

import com.thinkive.android.trade_bz.R;
import com.thinkive.android.trade_bz.a_stock.fragment.FundLoginFragment;
import com.thinkive.android.trade_bz.a_stock.fragment.LoginParentFragment;

/**
 * 描述：交易登录界面，该类的界面视图部分在TradeLoginFragment碎片
 *      这样使用的目的在于界面以后可以进行快速的移植
 * @author 黎丝军
 * @version 1.0
 * @corporation 深圳市思迪信息科技有限公司
 * @date 2015/6/3
 */
public class TradeLoginActivity extends AbsNavBarActivity {

    /**
     * 交易登录碎片，用于显示交易登录的界面元素
     */
    private LoginParentFragment mLoginParentFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitleBarVisible(View.GONE);
        initData();
        initViews();
    }

    @Override
    protected void initData() {
        mLoginParentFragment = new LoginParentFragment();

    }

    @Override
    protected void initViews() {
        super.initViews();
        setBackBtnVisibility(View.VISIBLE);
        replaceFragment(R.id.fl_container, mLoginParentFragment);
        mLoginParentFragment.setArguments(getIntent().getExtras());
    }

    /**
     * 获取交易登录碎片实例，该方法可能在TradeLoginViewController
     * 和中被使用到
     *
     * @return TradeLoginFragment
     */
    public FundLoginFragment getFundLoginFragment() {
        return mLoginParentFragment.getNormalLoginFragment();
    }

}