package com.thinkive.android.trade_bz.request;

import android.os.Bundle;

import com.thinkive.android.trade_bz.R;
import com.thinkive.android.trade_bz.others.constants.Constants;
import com.thinkive.android.trade_bz.interfaces.IRequestAction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * 基金委托撤单
 * （与今日委托共用实体类）
 *
 * @author 张雪梅
 * @company Thinkive
 * @date 2015/7/7
 */
public class Fund302009 extends BaseNormalRequest {

    public static final String BUNDLE_KEY_REVOCATION = "Request302009_result";

    public Fund302009(HashMap<String, String> paramMap, IRequestAction action) {
        super(action);
        paramMap.put("funcNo", "302009");
        setParamHashMap(paramMap);
        setUrlName(Constants.URL_TRADE);
    }

    @Override
    void getJsonDataWithoutError(JSONObject jsonObject) {
        Bundle bundle = new Bundle();
        try {
            String resultTarget = jsonObject.getJSONArray("dsName").get(0).toString();
            JSONArray jsonArray = jsonObject.getJSONArray(resultTarget);
            if (jsonArray != null) {
                bundle.putString(BUNDLE_KEY_REVOCATION, jsonObject.getString(ERROR_INFO));
                transferAction(REQUEST_SUCCESS, bundle);
            } else {
                bundle.putString(BUNDLE_KEY_REVOCATION, mContext.getResources().getString(R.string.data_error));
                transferAction(REQUEST_SUCCESS, bundle);
            }
        } catch (JSONException je) {
            je.printStackTrace();
        }
    }
}
