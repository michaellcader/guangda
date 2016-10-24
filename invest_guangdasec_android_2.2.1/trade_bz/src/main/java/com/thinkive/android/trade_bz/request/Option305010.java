package com.thinkive.android.trade_bz.request;

import android.os.Bundle;

import com.thinkive.android.trade_bz.R;
import com.thinkive.android.trade_bz.a_option.bean.OptionHistoryTradeBean;
import com.thinkive.android.trade_bz.interfaces.IRequestAction;
import com.thinkive.android.trade_bz.others.constants.Constants;
import com.thinkive.android.trade_bz.utils.JsonParseUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 个股期权历史成交
 * @author 张雪梅
 * @date 2016/6/14
 */
public class Option305010 extends BaseOptionRequest {

    public static final String BUNDLE_KEY_OPTION_HISTORY = "Option305010";

    public Option305010(HashMap<String, String> paramMap, IRequestAction action) {
        super(action);
        paramMap.put("funcNo", "305010");
        setParamHashMap(paramMap);
        setUrlName(Constants.URL_TRADE);
    }

    @Override
    void getJsonDataWithoutError(JSONObject jsonObject) {
        try {
            String resultTarget = jsonObject.getJSONArray("dsName").get(0).toString();
            JSONArray jsonArray = jsonObject.getJSONArray(resultTarget);
            if (jsonArray != null) {
                ArrayList<OptionHistoryTradeBean> dataList = JsonParseUtils.createBeanList(OptionHistoryTradeBean.class, jsonArray);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(BUNDLE_KEY_OPTION_HISTORY, dataList);
                transferAction(REQUEST_SUCCESS, bundle);
            } else {
                Bundle bundle = new Bundle();
                bundle.putString(BUNDLE_KEY_OPTION_HISTORY,mContext.getResources().getString(R.string.data_error));
                transferAction(REQUEST_SUCCESS, bundle);
            }
        } catch (JSONException je) {
            je.printStackTrace();
        }
    }
}
