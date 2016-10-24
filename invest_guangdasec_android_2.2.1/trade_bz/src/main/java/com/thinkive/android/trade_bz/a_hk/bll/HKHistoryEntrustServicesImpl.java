package com.thinkive.android.trade_bz.a_hk.bll;

import android.content.Context;
import android.os.Bundle;

import com.thinkive.android.trade_bz.a_hk.bean.HKHistoryEntrustBean;
import com.thinkive.android.trade_bz.a_hk.fragment.HKHistoryEntrustFragment;
import com.thinkive.android.trade_bz.a_stock.bll.BasicServiceImpl;
import com.thinkive.android.trade_bz.interfaces.IRequestAction;
import com.thinkive.android.trade_bz.request.HK301608;
import com.thinkive.android.trade_bz.utils.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *   港股通历史委托的业务类
 * @author 张雪梅
 * @company Thinkive
 * @date 2015/11/2
 */

public class HKHistoryEntrustServicesImpl extends BasicServiceImpl {
    private HKHistoryEntrustFragment mFragment = null;

    public HKHistoryEntrustServicesImpl(HKHistoryEntrustFragment mFragment) {
        this.mFragment = mFragment;
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onStop() {

    }

    /**
     * 请求历史委托数据
     * @param begin
     * @param end
     */
    public void requestHistoryEntrust(String begin, String end) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("begin_date", begin);
        map.put("end_date", end);
        new HK301608(map, new IRequestAction() {
            @Override
            public void onSuccess(Context context, Bundle bundle) {
                ArrayList<HKHistoryEntrustBean> dataList = bundle.getParcelableArrayList(HK301608.BUNDLE_KEY_HK_HISTORY_ENTRUST);
                mFragment.onGetHistoryEntrustData(dataList);
            }
            @Override
            public void onFailed(Context context, Bundle bundle) {
                ToastUtils.toast(context, bundle.getString(HK301608.ERROR_INFO));
            }
        }).request();
    }
}
