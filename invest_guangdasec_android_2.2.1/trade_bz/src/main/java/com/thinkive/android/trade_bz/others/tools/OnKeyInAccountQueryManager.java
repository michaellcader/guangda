package com.thinkive.android.trade_bz.others.tools;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;

import com.android.thinkive.framework.CoreApplication;
import com.thinkive.android.trade_bz.a_stock.adapter.OnkeyAccountListQueryAdapter;
import com.thinkive.android.trade_bz.a_stock.bean.CodeTableBean;
import com.thinkive.android.trade_bz.a_stock.bean.OneKeyBean;
import com.thinkive.android.trade_bz.utils.SizeUtil;
import com.thinkive.android.trade_bz.views.popupwindows.PopupWindowInListView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/12/7.
 */
public class OnKeyInAccountQueryManager {
    /**
     * 本类的单例实例
     */
    private static OnKeyInAccountQueryManager sInstance;
    /**
     * 显示模糊查询结果的popupwindow
     * 他的宽度由{@link #mPopupwindowWidth}指定，高度由
     */
    private PopupWindowInListView mPopupWindow;
    /**
     * 查询到的股票列表适配器
     */
    private OnkeyAccountListQueryAdapter mAdapter;
    /**
     * {@link #mPopupWindow}的宽度
     */
    private int mPopupwindowWidth;
    /**
     * 弹出框所依靠的控件
     */
    private View mWidthReferView;
    /**
     * 外部调用环境
     */
    private Context mContext;

    /**
     * 获取本类对象的方法
     *
     * @return 本类的单例对象
     */
    public synchronized static OnKeyInAccountQueryManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new OnKeyInAccountQueryManager(context);
        }
        return sInstance;
    }
    /**
     * 单例模式下的私有构造方法
     */
    private OnKeyInAccountQueryManager(Context context) {
        mContext = context;
        mAdapter = new OnkeyAccountListQueryAdapter(context);
    }


    public interface IHqCallBackStock {
        void onGetStockMsg(ArrayList<CodeTableBean> dataList);
    }

    public void execQueryList(ArrayList<OneKeyBean> dataList, View parentView) {
        mAdapter.setListData(dataList);
        mAdapter.notifyDataSetChanged();
        showQueryPopupWindow(parentView);
    }



    /**
     * 显示popupwindow
     *
     * @param parentView popupwindow将会显示在谁的下方
     */
    public void showQueryPopupWindow(View parentView) {
        if (mPopupwindowWidth == 0 && mWidthReferView != null) {
            mPopupwindowWidth = mWidthReferView.getWidth()-15;
        }
        dismissQueryPopupWindow();
        int count = mAdapter.getCount();
        if (count < 8) {
            mPopupWindow.showPopwindow(parentView, mPopupwindowWidth, mPopupWindow.getListViewHeight(mAdapter, SizeUtil.dp2px(CoreApplication.getInstance().getBaseContext(), 35f * 4 + 0.5f * 3)), 0, 0);
        } else {
            mPopupWindow.showPopwindow(parentView, mPopupwindowWidth, SizeUtil.dp2px(CoreApplication.getInstance().getBaseContext(), 35f * 4 + 0.5f * 3), 0, 0);
        }
    }

    /**
     * 停止模糊查询，隐藏之前用来展示查询结果的popupwindow
     */
    public void dismissQueryPopupWindow() {
        if (mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }

    /**
     * 设置popupwindow的宽度
     *
     * @param width popupwindow的宽度
     */
    public void setPopupwindowWidth(int width) {
        mPopupwindowWidth = width;
    }

    /**
     * 设置popupwindow的备用宽度参考对象
     * 当popupwindow的宽度设置失效的时候，也就是{@link #mPopupwindowWidth}等于0，
     * 恰好此时又要显示popupwindow的情况下；
     * 为了避免宽度为0导致popupwindow无法显示的问题，当宽度为0时，
     * 将使用widthReferView的宽度作为popupwindow的宽度
     *
     * @param widthReferView 此控件宽度，作为popupwindow的备用宽度值
     */
    public void setPopupWindowReserveWidthReferView(View widthReferView) {
        mWidthReferView = widthReferView;
    }

    public void initListViewPopupwindow(AdapterView.OnItemClickListener itemClickListener) {
        mPopupWindow = new PopupWindowInListView(mContext, itemClickListener);
        mPopupWindow.setListAdapter(mAdapter);
    }

    public OnkeyAccountListQueryAdapter getAdapter() {
        return mAdapter;
    }
}
