package com.thinkive.android.trade_bz.request;

import android.content.Context;

import com.android.thinkive.framework.message.AppMessage;
import com.android.thinkive.framework.message.IMessageHandler;
import com.android.thinkive.framework.message.MessageManager;
import com.thinkive.android.trade_bz.a_stock.activity.NewStockWebActivity;

import org.json.JSONObject;

/**
 * Created by Administrator on 2016/11/17.
 */
public class Message50114 implements IMessageHandler {
    private Context mContext;

    @Override
    public String handlerMessage(Context context, AppMessage appMessage) {
        this.mContext = context;
        JSONObject msg = appMessage.getContent();
        String moduleName = msg.optString("moduleName");
        String params = msg.optString("params");

        if (mContext instanceof NewStockWebActivity) {
            ((NewStockWebActivity) mContext).finish();
        }

        return MessageManager.getInstance(context).buildMessageReturn(1, null, null);
    }
}