package com.thinkive.android.trade_bz.a_rr.controll;

import android.view.View;

import com.thinkive.android.trade_bz.R;
import com.thinkive.android.trade_bz.a_rr.fragment.MyRRHoldStockFragment;
import com.thinkive.android.trade_bz.a_stock.controll.AbsBaseController;
import com.thinkive.android.trade_bz.views.HorizontalSlideLinearLayout;
import com.thinkive.android.trade_bz.views.PullToRefresh.PullToRefreshBase;
import com.thinkive.android.trade_bz.views.PullToRefresh.PullToRefreshListView;
import com.thinkive.android.trade_bz.views.PullToRefresh.PullToRefreshScrollView;
import com.thinkive.android.trade_bz.views.slideexpandlistview.ActionSlideExpandableListView;

/**
 * Created by Administrator on 2016/11/26.
 */
public class MMyRRHoldStockViewController extends AbsBaseController implements
        PullToRefreshListView.OnRefreshListener, ActionSlideExpandableListView.OnActionClickListener,
        HorizontalSlideLinearLayout.OnSlideListener {

    public static final int ON_ACTION_CLICK = 7974940;

    private MyRRHoldStockFragment mFragment = null;

    public MMyRRHoldStockViewController(MyRRHoldStockFragment Fragment) {
        mFragment = Fragment;
    }

    @Override
    public void register(int i, View view) {
        switch (i) {
            case ON_SCROLLVIEW_REFLASH:
                ((PullToRefreshScrollView)view).setOnRefreshListener(this);
                break;
            case ON_ACTION_CLICK:
                ((ActionSlideExpandableListView)view).setItemActionListener(this,
                        R.id.tv_hold_list_item_expend_buy, R.id.tv_hold_list_item_expend_sale,
                        R.id.tv_hold_list_item_expend_hq) ;
                break;
            case ON_SLIDE:
                ((HorizontalSlideLinearLayout)view).setOnSlideListener(this);
                break;
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        mFragment.onDownRefresh();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {

    }

    @Override
    public void onClick(View itemView, View clickedView, int position) {
        int clickedViewId = clickedView.getId();
        if (clickedViewId == R.id.tv_hold_list_item_expend_buy) { // item展开布局中的“买入”按钮
            mFragment.onClickHoldListviewExpandBuy(position);
        } else if (clickedViewId == R.id.tv_hold_list_item_expend_sale) { // item展开布局中的“卖出”按钮
            mFragment.onClickHoldListviewExpandSale(position);
        } else if (clickedViewId == R.id.tv_hold_list_item_expend_hq) { // item展开布局中的“行情”按钮
            mFragment.onClickHoldListviewDetails(position);
        }
    }

    @Override
    public void onToLeftSlide(HorizontalSlideLinearLayout horizontalSlideLinearLayout) {
        int resId = horizontalSlideLinearLayout.getId();
        if (resId == R.id.hsll_part1) {
            mFragment.onPart1LeftSlide();
        } else if (resId == R.id.hsll_part2) {
            mFragment.onPart2LeftSlide();
        }
    }

    @Override
    public void onToRightSlide(HorizontalSlideLinearLayout horizontalSlideLinearLayout) {
        int resId = horizontalSlideLinearLayout.getId();
        if (resId == R.id.hsll_part1) {
            mFragment.onPart1RightSlide();
        } else if (resId == R.id.hsll_part2) {
            mFragment.onPart2RightSlide();
        }
    }
}

