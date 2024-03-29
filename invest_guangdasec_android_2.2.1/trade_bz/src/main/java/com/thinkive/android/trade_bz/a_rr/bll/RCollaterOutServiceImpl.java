package com.thinkive.android.trade_bz.a_rr.bll;

import android.content.Context;
import android.os.Bundle;

import com.thinkive.android.trade_bz.a_rr.bean.RCollaterLinkBean;
import com.thinkive.android.trade_bz.a_rr.fragment.RCollaterOutFragment;
import com.thinkive.android.trade_bz.a_stock.bean.MyStoreStockBean;
import com.thinkive.android.trade_bz.a_stock.bll.BasicServiceImpl;
import com.thinkive.android.trade_bz.interfaces.IRequestAction;
import com.thinkive.android.trade_bz.others.tools.TradeLoginManager;
import com.thinkive.android.trade_bz.request.RR303003;
import com.thinkive.android.trade_bz.request.RR303014;
import com.thinkive.android.trade_bz.request.RR303015;
import com.thinkive.android.trade_bz.utils.LoadingDialogUtil;
import com.thinkive.android.trade_bz.utils.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *  融资融券--划转--担保品转出
 * @author 张雪梅
 * @company Thinkive
 * @date 15/8/13
 */
public class RCollaterOutServiceImpl extends BasicServiceImpl {

    private RCollaterOutFragment mFragment =null;
    private LoadingDialogUtil loadingDialogUtil;//请求数据状态框 工具类
    public RCollaterOutServiceImpl(RCollaterOutFragment fragment) {
        mFragment = fragment;
        loadingDialogUtil =  new LoadingDialogUtil(fragment.getContext());
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onStop() {

    }

    /**
     * 发起请求，获取持仓列表
     */
    public void requestRollaterOutList() {
        HashMap<String, String> map = new HashMap<String, String>();
        new RR303003(map, new IRequestAction() {
            @Override
            public void onSuccess(Context context, Bundle bundle) {
                ArrayList<MyStoreStockBean> dataList = bundle.getParcelableArrayList(RR303003.BUNDLE_KEY_ROLLATER);
                mFragment.getRollaterOutList(dataList);
            }
            @Override
            public void onFailed(Context context, Bundle bundle) {
                ToastUtils.toast(context, bundle.getString(RR303003.ERROR_INFO));
            }
        }).request();
    }

    /**
     * 请求划转联动
     * @param bean
     */
    public void requestLinkageData(final MyStoreStockBean bean) {
        loadingDialogUtil.showLoadingDialog(0);
        HashMap<String, String> map = new HashMap<String, String>();
//        map.put("entrust_way", TradeLoginManager.sNormalUserInfo_in_credit.getEntrust_way());
//        map.put("branch_no", TradeLoginManager.sNormalUserInfo_in_credit.getBranch_no());
//        map.put("fund_account", TradeLoginManager.sNormalUserInfo_in_credit.getFund_account());
//        map.put("cust_code", TradeLoginManager.sNormalUserInfo_in_credit.getCust_code());
//        map.put("op_station", TradeLoginManager.sNormalUserInfo_in_credit.getOp_station());

//        map.put("branch_no_crdt", TradeLoginManager.sCreditUserInfo.getBranch_no());
//        map.put("fund_account_crdt",TradeLoginManager.sCreditUserInfo.getFund_account());
//        map.put("client_id_crdt",TradeLoginManager.sCreditUserInfo.getCust_code());

        map.put("stock_code",bean.getStock_code());
        map.put("last_price",bean.getLast_price());
        map.put("stock_account",bean.getStock_account());
        map.put("exchange_type",bean.getExchange_type());
        map.put("entrust_bs","1");
        new RR303014(map, new IRequestAction() {
            @Override
            public void onSuccess(Context context, Bundle bundle) {
                loadingDialogUtil.hideDialog();
                RCollaterLinkBean data = (RCollaterLinkBean)bundle.getSerializable(RR303014.BUNDLE_KEY_TRANSFER_LIAN);
                mFragment.onGetLinkData(bean,data);
            }
            @Override
            public void onFailed(Context context, Bundle bundle) {
                loadingDialogUtil.hideDialog();
                ToastUtils.toast(context, bundle.getString(RR303014.ERROR_INFO));
            }
        }).request();
    }

    /**
     * 请求划转
     */
    public void requestTranferredResult(RCollaterLinkBean bean, MyStoreStockBean nameAndCodeBean, String enableAmount) {
        loadingDialogUtil.showLoadingDialog(0);
        HashMap<String, String> map = new HashMap<String, String>();
//        map.put("entrust_way", TradeLoginManager.sNormalUserInfo_in_credit.getEntrust_way());
//        map.put("branch_no",  TradeLoginManager.sNormalUserInfo_in_credit.getBranch_no());
//        map.put("fund_account",  TradeLoginManager.sNormalUserInfo_in_credit.getFund_account());
//        map.put("cust_code",  TradeLoginManager.sNormalUserInfo_in_credit.getCust_code());
//        map.put("op_station",  TradeLoginManager.sNormalUserInfo_in_credit.getOp_station());
//        map.put("stock_account",bean.getStock_account());
//        map.put("seat_no",bean.getSeat_no());
        map.put("stock_account",bean.getStock_account_crdt());
        map.put("seat_no",bean.getSeat_no_crdt());
        map.put("exchange_type",bean.getExchange_type());
        map.put("stock_code",nameAndCodeBean.getStock_code());
        map.put("entrust_amount",enableAmount);

//   map.put("branch_no_crdt", TradeLoginManager.sCreditUserInfo.getBranch_no());
        map.put("client_id_crdt", TradeLoginManager.sCreditUserInfo.getCust_code());
        map.put("fund_account_crdt",TradeLoginManager.sCreditUserInfo.getFund_account());
        map.put("stock_account_crdt",bean.getStock_account_crdt());
        map.put("seat_no_crdt",bean.getSeat_no_crdt());
        map.put("entrust_bs","77");
        map.put("last_price",bean.getLast_price());
        map.put("cost_price",bean.getCost_price());
        map.put("password_crdt",TradeLoginManager.sCreditUserInfo.getPassword());
        new RR303015(map, new IRequestAction() {
            @Override
            public void onSuccess(Context context, Bundle bundle) {
                loadingDialogUtil.hideDialog();
                ToastUtils.toast(context, bundle.getString(RR303015.BUNDLE_KEY_TRANSFEER));
            }
            @Override
            public void onFailed(Context context, Bundle bundle) {
                loadingDialogUtil.hideDialog();
                ToastUtils.toast(context, bundle.getString(RR303015.ERROR_INFO));
            }
        }).request();
    }
}
