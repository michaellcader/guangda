package com.thinkive.android.trade_bz.a_rr.bll;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.android.thinkive.framework.CoreApplication;
import com.thinkive.android.trade_bz.R;
import com.thinkive.android.trade_bz.a_rr.bean.RStockLinkBean;
import com.thinkive.android.trade_bz.a_rr.fragment.RCreditBuyFragment;
import com.thinkive.android.trade_bz.a_stock.bean.CodeTableBean;
import com.thinkive.android.trade_bz.a_stock.bean.MoneySelectBean;
import com.thinkive.android.trade_bz.a_stock.bean.StockBuySellDish;
import com.thinkive.android.trade_bz.a_stock.bll.BasicServiceImpl;
import com.thinkive.android.trade_bz.interfaces.IRequestAction;
import com.thinkive.android.trade_bz.others.tools.TradeLoginManager;
import com.thinkive.android.trade_bz.others.tools.TradeTools;
import com.thinkive.android.trade_bz.request.Request303000;
import com.thinkive.android.trade_bz.request.Request303001;
import com.thinkive.android.trade_bz.request.Request303004;
import com.thinkive.android.trade_bz.request.RequestHQ20000;
import com.thinkive.android.trade_bz.request.RequestHQ20003;
import com.thinkive.android.trade_bz.utils.LoadingDialogUtil;
import com.thinkive.android.trade_bz.utils.ToastUtil;
import com.thinkive.android.trade_bz.utils.ToastUtils;
import com.thinkive.android.trade_bz.utils.TradeUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 融资买入
 * @author 张雪梅
 * @version 1.0
 * @corporation 深圳市思迪信息技术股份有限公司
 * @date 2015/6/5
 */
public class RCreditBuyServiceImpl extends BasicServiceImpl {

    private RCreditBuyFragment mFragment = null;
    /**
     * 委托价格保留小数位
     */
    public int mCount = 2;
    private LoadingDialogUtil loadingDialogUtil;//请求数据状态框 工具类
    private Context mContext;

    public RCreditBuyServiceImpl(RCreditBuyFragment fragment) {
        mFragment = fragment;
        loadingDialogUtil = new LoadingDialogUtil(fragment.getContext());
        mContext = CoreApplication.getInstance();
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onStop() {

    }



    public void request20000ForHqData(final String stockCode, final String market) {
        HashMap<String, String> paramMap = new HashMap<String, String>();
        if(!market.equals("SZ") && !market.equals("SH")){
            ToastUtils.toast(mContext, mContext.getResources().getString(R.string.toast_data_error));
            return;
        }
        paramMap.put("version", "1");
        paramMap.put("stock_list", market + ":" + stockCode);
        //名称：市场：代码：涨停：跌停：是否停牌:股票类型:现价
        paramMap.put("field","22:23:24:45:46:48:21:2");
        new RequestHQ20000(paramMap, new IRequestAction() {
            @Override
            public void onSuccess(Context context, Bundle bundle) {
                CodeTableBean data = (CodeTableBean)bundle.getSerializable(RequestHQ20000.BUNDLE_KEY_SOCKET);
                if(data.getIssuspend().equals("1")){//停牌
                    ToastUtils.toast(context, context.getResources().getString(R.string.trade_stock_has_suspend));
                    mFragment.onGetSuspendStock(data.getName(),data.getCode());
                }else if(data.getIssuspend().equals("2")) {//未停牌
                    mCount = TradeTools.transferStockType(data.getStockType());
                    if (mCount != 2) { //是国债或者基金
                        data.setDownLimit(TradeUtils.formatDouble3(data.getDownLimit()));
                        data.setUpLimit(TradeUtils.formatDouble3(data.getUpLimit()));
                        data.setNow(TradeUtils.formatDouble3(data.getNow()));
                    } else {
                        data.setDownLimit(TradeUtils.formatDouble2(data.getDownLimit()));
                        data.setUpLimit(TradeUtils.formatDouble2(data.getUpLimit()));
                        data.setNow(TradeUtils.formatDouble2(data.getNow()));
                    }
                    mFragment.onGetHqData(data);
                    requestStockWuDangPan(data.getCode(), data.getMarket(),data.getExchange_type(),true);
                }
            }
            @Override
            public void onFailed(Context context, Bundle bundle) {
                ToastUtils.toast(context, bundle.getString(RequestHQ20000.ERROR_INFO));
            }
        }).request();
    }

    /**
     * 请求服务器，获取股票行情五档买卖盘数据
     */
    public void requestStockWuDangPan(final String stockCode, final String market, final String exchangeType, final boolean isSetText) {
        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("stock_list", market + ":" + stockCode);
        paramMap.put("version", "1");
        new RequestHQ20003(paramMap, new IRequestAction() {
            @Override
            public void onSuccess(Context context, Bundle bundle) {
                StockBuySellDish bean = (StockBuySellDish) bundle.getSerializable(RequestHQ20003.BUNDLE_KEY_WUDANG);
                String nowPrice = bundle.getString(RequestHQ20003.NOW_PRICE);
                String increase = bundle.getString(RequestHQ20003.INCREASE_AMOUNT);
                if (bean != null) {
                    ArrayList<String> valueList = bean.getValueBuySale();
                    for (int i = 0; i <= 4; i++) { // 卖价五~卖价一
                        if (mCount != 2) {
                            valueList.set(i, TradeUtils.formatDouble3(valueList.get(i)));
                        } else {
                            valueList.set(i, TradeUtils.formatDouble2(valueList.get(i)));
                        }
                    }
                    for (int i = 5; i <= 9; i++) { // 卖量五~卖量一
                        valueList.set(i, TradeUtils.formateDataWithQUnit(valueList.get(i)));
                    }
                    for (int i = 10; i <= 14; i++) { // 买价一~买价五
                        if (mCount != 2) {
                            valueList.set(i, TradeUtils.formatDouble3(valueList.get(i)));
                        } else {
                            valueList.set(i, TradeUtils.formatDouble2(valueList.get(i)));
                        }
                    }
                    for (int i = 15; i <= 19; i++) { // 买量一~买量五
                        valueList.set(i, TradeUtils.formateDataWithQUnit(valueList.get(i)));
                    }
                    mFragment.onGetWuDangDishData(bean,market,exchangeType,isSetText,nowPrice,increase);
                }
            }
            @Override
            public void onFailed(Context context, Bundle bundle) {
                ToastUtils.toast(context, bundle.getString(RequestHQ20003.ERROR_INFO));
            }
        }).request();
    }
    /**
     * 股票数据联动数据请求
     */
    public void requestLinkageData(final String stock_code, String entrustPrice,
                                   final String exchangeType, final String market) {
        // 根据数据字典，将交易市场类别转化成服务器需要的数据，以作为入参发给服务器
        String stock_account = "";
        if (exchangeType.equals("0") || exchangeType.equals("1")) { // 深
            stock_account = TradeLoginManager.sCreditUserInfo_shen.getStock_account();
        }else if (exchangeType.equals("2") || exchangeType.equals("3")) { // 沪
            stock_account = TradeLoginManager.sCreditUserInfo_hu.getStock_account();
        }
        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("entrust_bs", "0");
        paramMap.put("stock_code", stock_code);
        paramMap.put("exchange_type", exchangeType);
        paramMap.put("stock_account", stock_account);
        paramMap.put("entrust_type", "6");
        if (entrustPrice != null && !TextUtils.isEmpty(entrustPrice)) {
            float priceFloat = Float.parseFloat(entrustPrice);
            if(priceFloat <= 0){
                entrustPrice = "";
            }
        }else{
            entrustPrice = "";
        }
        paramMap.put("entrust_price", entrustPrice);
        new Request303000(paramMap, new IRequestAction() {
            @Override
            public void onSuccess(Context context, Bundle bundle) {
                RStockLinkBean stockLinkageBean = (RStockLinkBean) bundle.getSerializable(Request303000.BUNDLE_KEY_LINKAGE);
                if (stockLinkageBean != null) {
                    // 可交易数量取整数
                    stockLinkageBean.setStock_max_amount(TradeUtils.formatDouble0(stockLinkageBean.getStock_max_amount()));
                    // 设置交易市场类别
                    stockLinkageBean.setExchange_type(exchangeType);
                    stockLinkageBean.setMarket(market);
                    // 传输数据到fragment
                    mFragment.onGetStockLinkAgeData(stockLinkageBean);
                    //信用交易可用资金查询
                    new Request303004(new HashMap<String, String>(), new IRequestAction() {
                        @Override
                        public void onSuccess(Context context, Bundle bundle) {
                            MoneySelectBean bean = (MoneySelectBean) bundle.getSerializable(Request303004.BUNDLE_KEY_R_MYHOLD_HEAD);
                            mFragment.onGetCanUseBalance(bean.getEnable_balance() + " 元");
                        }

                        @Override
                        public void onFailed(Context context, Bundle bundle) {
                            String error = bundle.getString(Request303004.BUNDLE_KEY_R_MYHOLD_HEAD);
                            ToastUtil.showToast(error);
                        }
                    }).request();
                }
            }
            @Override
            public void onFailed(Context context, Bundle bundle) {
                ToastUtils.toast(context, context.getResources().getString(R.string.trade_get_linkage_stock_failed) + bundle.getString(Request303000.ERROR_INFO));
                mFragment.clearDataInViewsExpectStockCodeEd();
            }
        }).request();
    }
    /**
     * 委托交易请求
     */
    public void requestBuyOrSell(String entrustBs,String limitOrMarketPriceFlag,String entrustBsXjNum) {
        loadingDialogUtil.showLoadingDialog(0);
        HashMap<String, String> paramMap = new HashMap<String, String>();
        RStockLinkBean stockLinkageBean = mFragment.getStockLinkageBean();
        paramMap.put("entrust_bs", entrustBs);
        if("1".equals(limitOrMarketPriceFlag)){
            paramMap.put("entrust_bs_xj",entrustBsXjNum);
        }
        paramMap.put("entrust_type", "6");
        paramMap.put("exchange_type", stockLinkageBean.getExchange_type());
        paramMap.put("stock_account", stockLinkageBean.getStock_account());
        paramMap.put("stock_code", stockLinkageBean.getStock_code());
        paramMap.put("entrust_price", mFragment.getEntrustPrice());
        paramMap.put("entrust_amount", mFragment.getEntrustAmount());
        new Request303001(paramMap, new IRequestAction() {
            @Override
            public void onSuccess(Context context, Bundle bundle) {
                loadingDialogUtil.hideDialog();
                mFragment.onSuccessEntrustTrade(bundle.getString(Request303001.BUNDLE_KEY_ENTRUST_ORDER));
                // 委托成功后，清空界面上的数据
                mFragment.clearDataInViews();
                mFragment.jumpToRevotion();

            }
            @Override
            public void onFailed(Context context, Bundle bundle) {
                loadingDialogUtil.hideDialog();
                mFragment.clearDataInViews();
                ToastUtils.toast(context, bundle.getString(Request303001.ERROR_INFO));
            }
        }).request();
    }
}
