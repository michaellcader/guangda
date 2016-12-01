package com.thinkive.android.trade_bz.a_stock.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.thinkive.framework.CoreApplication;
import com.android.thinkive.framework.ThinkiveInitializer;
import com.android.thinkive.framework.WebViewManager;
import com.android.thinkive.framework.compatible.ListenerController;
import com.android.thinkive.framework.config.ConfigManager;
import com.android.thinkive.framework.fragment.BaseWebFragment;
import com.android.thinkive.framework.message.AppMessage;
import com.android.thinkive.framework.message.MessageManager;
import com.android.thinkive.framework.module.IModule;
import com.android.thinkive.framework.module.ModuleManager;
import com.android.thinkive.framework.storage.MemoryStorage;
import com.android.thinkive.framework.util.Constant;
import com.thinkive.android.trade_bz.R;
import com.thinkive.android.trade_bz.a_hk.activity.HKMultiTradeActivity;
import com.thinkive.android.trade_bz.a_in.activity.InFundMainActivity;
import com.thinkive.android.trade_bz.a_level.activity.LFundTradeMainActivity;
import com.thinkive.android.trade_bz.a_net.activity.NetVoteMainActivity;
import com.thinkive.android.trade_bz.a_option.activity.OptionMainActivity;
import com.thinkive.android.trade_bz.a_out.activity.FundTradeMainActivity;
import com.thinkive.android.trade_bz.a_rr.activity.RSecuritiesMarginActivity;
import com.thinkive.android.trade_bz.a_stock.activity.ChangePasswordActivity;
import com.thinkive.android.trade_bz.a_stock.activity.HistoryEntrustOrTradeActivity;
import com.thinkive.android.trade_bz.a_stock.activity.HistoryMoneyActivity;
import com.thinkive.android.trade_bz.a_stock.activity.MultiTradeActivity;
import com.thinkive.android.trade_bz.a_stock.activity.NewStockWebActivity;
import com.thinkive.android.trade_bz.a_stock.activity.OneKeyActivity;
import com.thinkive.android.trade_bz.a_stock.activity.SignAgreementActivity;
import com.thinkive.android.trade_bz.a_stock.activity.TradeH5Activity;
import com.thinkive.android.trade_bz.a_stock.activity.TradeLoginActivity;
import com.thinkive.android.trade_bz.a_stock.activity.TransferBanktActivity;
import com.thinkive.android.trade_bz.a_stock.adapter.FastMenuAdapter;
import com.thinkive.android.trade_bz.a_stock.adapter.MoreMenuAdapter;
import com.thinkive.android.trade_bz.a_stock.bean.MoneySelectBean;
import com.thinkive.android.trade_bz.a_stock.bean.TradeFastItemBean;
import com.thinkive.android.trade_bz.a_stock.bll.TradeMainServicesImpl;
import com.thinkive.android.trade_bz.a_stock.controll.TradeMainViewController;
import com.thinkive.android.trade_bz.a_trans.activity.TransStockMainActivityTrade;
import com.thinkive.android.trade_bz.dialog.LoadingDialog;
import com.thinkive.android.trade_bz.dialog.MessageOkCancelDialog;
import com.thinkive.android.trade_bz.others.constants.Constants;
import com.thinkive.android.trade_bz.others.tools.EncryptManager;
import com.thinkive.android.trade_bz.others.tools.ThinkiveTools;
import com.thinkive.android.trade_bz.others.tools.TradeFlags;
import com.thinkive.android.trade_bz.others.tools.TradeLoginManager;
import com.thinkive.android.trade_bz.others.tools.TradeTools;
import com.thinkive.android.trade_bz.others.tools.TradeWebFragmentManager;
import com.thinkive.android.trade_bz.receivers.TradeBaseBroadcastReceiver;
import com.thinkive.android.trade_bz.utils.LogUtil;
import com.thinkive.android.trade_bz.utils.ToastUtil;
import com.thinkive.android.trade_bz.utils.TradeUtils;
import com.thinkive.android.trade_bz.views.TrimGridView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

/**
 * 描述：交易首页Fragment
 * 交易模块唯一指定模块Fragment，接受一切外部模块、H5发来的消息，并转发给模块内的各个类。
 * 交易模块内外消息通信交通枢纽
 *
 * @author 王志鸿
 * @version 1.0
 * @corporation 深圳市思迪信息技术有限公司
 * @date 2015/6/2
 * @changeby liuguangjian
 */
public class NormalTradeFragment extends AbsTitlebarFragment implements IModule {

    public static final String PREFERENCE_KEY_PHONE_NUMBER = "preference_key_phone_number_normal";

    private View mRootView;

    /**
     * 交易首页Activity
     */
    private FragmentActivity mActivity = null;
    /**
     * 页面控件事件监听控制器，从本类的Activity中取得
     */
    private TradeMainViewController mHomeController;
    /**
     * 手机验证功能的业务类
     */
    private TradeMainServicesImpl mServices;
    /**
     * 登录后实现页面跳转的广播接收器，登录后登录业务类发出广播，通知本对象进行页面跳转
     */
    private MainBroadcastReceiver mBroadcastReceiver;
    /**
     * 登录前单击的按钮ID
     */
    private int mClickBtnBeforeLogin;
    /**
     * 登录前，如果在行情模块点击了“买入”或“卖出”按钮，则使用本变量记住行情模块传入的信息
     */
    private JSONObject mJsonDataFromHq;
    /**
     * webview管理器
     */
    private WebViewManager mWebViewManager;
    /**
     *
     */
    private String mFuncNo999;
    private String mTemp_token_key;
    private TradeLoginManager mTradeLoginManager;
    private LoadingDialog loadingDialog;//请求数据状态框


    private TrimGridView mFastMenuGv;//快捷菜单GridView
    private TrimGridView mMoreMenuGv;//更多菜单
    private FastMenuAdapter mFastAdapter;
    private MoreMenuAdapter mMoreAdapter;
    private List<TradeFastItemBean> mFastBeansList = new ArrayList<>();//顶部快捷菜单GridView数据集
    private List<TradeFastItemBean> mMoreBeanList = new ArrayList<>();//更多菜单GridView数据集

    private RelativeLayout mShowMoreRl;//点击展示更多
    private boolean isShowMore = false;//更多是否显示
    private ImageView mRotateArrow;//更多右侧带动画的箭头
    /*
    * 箭头的顺时针逆时针动画
    */
    private ObjectAnimator mArrowClwsAnimator;
    private ObjectAnimator mArrowCcwsAnimator;
    private RelativeLayout mChageTradePwdRl;//修改交易密码
    private RelativeLayout mBuyNewRl;//新股申购
    private TextView mLogOutTv;//底部注销按钮
    private float mDensity;//像素密度
    private int mHiddenViewMeasuredHeight;//点击更多拉出的父布局高度
    private String[] mFastMenuTitles = {"买入", "卖出", "委托撤单", "个人资产", "个人持仓", "当日委托", "当日成交", "银行转账"};
    private int[] mImgRes = {R.mipmap.buyin, R.mipmap.sellout, R.mipmap.repeal_bill, R.mipmap.personal_ass, R.mipmap.take_position, R.mipmap.today_trust, R.mipmap.today_vol, R.mipmap.bank_trans};
    private TradeParentFragment mParentFragment;//持有的 在TradeParentFragment初始化好的TradeParentFragment对象
    private ScrollView mScrollView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = super.onCreateView(inflater, container, savedInstanceState);
            setTitleRootVisibility(View.GONE);
            View rootView = inflater.inflate(R.layout.fragment_normaltrade, null);
            setSubViewToContainer(rootView);
            findViews(rootView);
            initGrid();
            fillData();
            setListeners();
            initViews();
        }
        return mRootView;
    }


    private void fillData() {
        for (int i = 0; i < mImgRes.length; i++) {
            mFastBeansList.add(new TradeFastItemBean(mImgRes[i], mFastMenuTitles[i], null));
        }
        mFastAdapter.setListData(mFastBeansList);
        mFastAdapter.notifyDataSetChanged();

        mMoreBeanList.add(new TradeFastItemBean(0, "历史委托", null));
        mMoreBeanList.add(new TradeFastItemBean(0, "历史成交", null));
        mMoreBeanList.add(new TradeFastItemBean(0, "资金查询", null));
        mMoreBeanList.add(new TradeFastItemBean(0, "资金流水", null));
        mMoreAdapter.setListData(mMoreBeanList);
        mMoreAdapter.notifyDataSetChanged();
    }


    @Override
    public void onResume() {
        super.onResume();
        //实时更新主页状态
        updateLogoutBtnState();
        //防止测试点点点
        mParentFragment.setLogTvClickable(true);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            //实时更新主页状态
            updateLogoutBtnState();
            //防止测试点点点
            mParentFragment.setLogTvClickable(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        TradeUtils.hideSystemKeyBoard(mActivity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mBroadcastReceiver.unregister();
    }

    @Override
    protected void initData() {
        mServices = new TradeMainServicesImpl(this);
        mHomeController = new TradeMainViewController(this);
        // 登录后实现页面跳转的广播接收器，登录后登录业务类发出广播，通知本对象进行页面跳转
        mBroadcastReceiver = new MainBroadcastReceiver(ThinkiveInitializer.getInstance().getContext());
        // 登录后实现页面跳转的广播接收器，登录后登录业务类发出广播，通知本对象进行页面跳转
        mBroadcastReceiver.setActions(TradeBaseBroadcastReceiver.ACTION_START_ACTIVITY,
                TradeBaseBroadcastReceiver.ACTION_H5_UPDATE_FINISH,
                TradeBaseBroadcastReceiver.ACTION_CHANGE_PWD_SUCCESS,
                TradeBaseBroadcastReceiver.ACTION_ERROR_999,
                TradeBaseBroadcastReceiver.ACTION_TRANSFORM_TRADE_LOGIN);
        mBroadcastReceiver.register();
        mWebViewManager = WebViewManager.getInstance();
        mTradeLoginManager = TradeLoginManager.getInstance();
    }

    @Override
    protected void findViews(View view) {
        mFastMenuGv = (TrimGridView) view.findViewById(R.id.gv_fast_menu);
        mMoreMenuGv = (TrimGridView) view.findViewById(R.id.gv_more_menu);
        mShowMoreRl = (RelativeLayout) view.findViewById(R.id.rl_more);
        mRotateArrow = (ImageView) view.findViewById(R.id.iv_more_can_rotate);

        mBuyNewRl = (RelativeLayout) view.findViewById(R.id.rl_new_buy);
        mChageTradePwdRl = (RelativeLayout) view.findViewById(R.id.rl_change_tradepwd);
        mLogOutTv = (TextView) view.findViewById(R.id.tv_exit_logout);
        mScrollView = (ScrollView) view.findViewById(R.id.scroll_parent);


    }

    @Override
    protected void setListeners() {
        registerListener(ListenerController.ON_ITEM_CLICK, mFastMenuGv, mHomeController);
        registerListener(ListenerController.ON_ITEM_CLICK, mMoreMenuGv, mHomeController);

        registerListener(ListenerController.ON_CLICK, mShowMoreRl, mHomeController);
        registerListener(ListenerController.ON_CLICK, mBuyNewRl, mHomeController);
        registerListener(ListenerController.ON_CLICK, mChageTradePwdRl, mHomeController);

        mLogOutTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TradeFlags.check(TradeFlags.FLAG_NORMAL_TRADE_YES)) {
                    logOff();
                }
            }
        });
//        mMoreMenuGv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    System.out.println("mMoreMenuGv   获取焦点");
//                } else {
//                    System.out.println("mMoreMenuGv   失去焦点");
//                }
//            }
//        });

    }

    private void initGrid() {
        //更多展开的参数
        mDensity = getResources().getDisplayMetrics().density;
        mHiddenViewMeasuredHeight = (int) (mDensity * 76 + 0.5);


        mFastAdapter = new FastMenuAdapter(getActivity());
        mFastAdapter.setListData(mFastBeansList);
        mFastMenuGv.setAdapter(mFastAdapter);

        mMoreAdapter = new MoreMenuAdapter(getActivity());
        mMoreAdapter.setListData(mMoreBeanList);
        mMoreMenuGv.setAdapter(mMoreAdapter);

        mMoreMenuGv.setVisibility(GONE);
    }

    @Override
    protected void initViews() {
        setTheme();
        // 设置交易模块的RSA加密参数，以便后面的程序加密
        EncryptManager.getInstance().requestRsaParam();
        String webviewName = TradeUtils.getPreUrl(TradeWebFragmentManager.sWebCacheFragment.getUrl());
        mWebViewManager.preLoad(TradeWebFragmentManager.sWebCacheFragment.getUrl(), webviewName, false);

    }

    @Override
    protected void setTheme() {

    }

    /**
     * 暴露给外界的交易首页初始化方法，集中处理交易模块的初始化操作
     *
     * @param activity 外部调用的Activity
     */
    public void init(FragmentActivity activity) {
        // TODO：注意，这样传递Activity对象，可能会在后台重启后出现错误。
        mActivity = activity;
        initData();
        ModuleManager.getInstance().registerModule(this, Constants.MODULE_NAME_TRADE);
    }

    public void setParent(TradeParentFragment parent) {
        mParentFragment = parent;
    }

    /**
     * 获得我的持仓数据
     *
     * @param data
     */
    public void onGetMyHoldData(MoneySelectBean data) {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    /**
     * 获得我的持仓数据 失败
     */
    public void onGetMyHoldDataFail() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    @Override
    public String onMessageReceive(final AppMessage appMessage) {
        final int msgId = appMessage.getMsgId();
        CoreApplication.getInstance().getHandler().post(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = appMessage.getContent();
                LogUtil.printLog("d", "交易模块收到消息，消息号是：" + msgId
                        + "。消息内容是：" + jsonObject.toString());
                switch (msgId) {
                    case 60252: // H5页面返回键的消息
                        Activity activity = TradeWebFragmentManager.sWebCacheFragment.getActivity();
                        if (activity != null) {
                            activity.finish();
                        }
                        break;
                    case 60403: // 统一登录退出登录消息
                        setLogout();
                        break;
                    case 7060403: // 统一账户校验成功（手机号登录成功）
                        //                        try {
                        //                            String temp_token_key = jsonObject.getString("moduleName");
                        //                            if (temp_token_key.contains(Constants.MODULE_NAME_TRADE)) {
                        //                                mTemp_token_key = temp_token_key;
                        //                                TradeFlags.addFlag(TradeFlags.FLAG_PHONE_LOGIN);
                        //                                MemoryStorage memoryStorage = new MemoryStorage();
                        //                                String temp_token = memoryStorage.loadData(temp_token_key);
                        //                                mServices.startServerSession(temp_token);
                        //                            }
                        //                        } catch (JSONException je) {
                        //                            je.printStackTrace();
                        //                        }
                        break;
                    case 60200: // 资金账号校验成功（业务模块登录成功）
                        try {
                            String acct_type = jsonObject.getString("account_type");
                            String acct_value = jsonObject.getString("account");
                            addCheckStatus(acct_type);
                            mServices.requestUserMsgFromServer(acct_value, acct_type);
                        } catch (JSONException je) {
                            je.printStackTrace();
                        }
                        break;
                    case 50113:
                        String sso_flag = jsonObject.optString("sso_flag");
                        if ("0".equals(sso_flag)) {
                            mClickBtnBeforeLogin = 0;
                        }

                        break;
                    case 50101:
                        JSONObject params = jsonObject.optJSONObject("params");
                        if (params != null && "my_stock".equals(params.optString("toPage"))) {
                            mClickBtnBeforeLogin = R.id.tv_my_hold;
                            onClickMyHold();
                        }
                        break;
                }
                MessageManager.getInstance(mActivity).buildMessageReturn(1, null, null);
            }
        });
        return null;
    }

    /**
     * 根据账户类型添加校验的标志位
     *
     * @param acct_type 账户类型
     */
    private void addCheckStatus(String acct_type) {
        int flag;
        switch (acct_type) {
            case TradeLoginManager.LOGIN_TYPE_CREDIT_ACCOUNT:
                flag = TradeFlags.FLAG_CREDIT_ACCOUNT_CHECK_SUCCESS;
                break;
            case TradeLoginManager.LOGIN_TYPE_OPTION_ACCOUNT:
                flag = TradeFlags.FLAG_OPTION_ACCOUNT_CHECK_SUCCESS;
                break;
            default:
                flag = TradeFlags.FLAG_NORMAL_ACCOUNT_CHECK_SUCCESS;
                break;
        }
        TradeFlags.addFlag(flag);
    }

    /**
     * 在行情模块中单击了买入或者卖出时
     * 已登录时直接进入买卖页面，并传入数据；未登录时，进入登录页并传入数据，登录成功后再用广播将数据传回本方法。
     */
    public void onClickBuyOrSaleInHq(JSONObject jsonObject) {
        if (TradeFlags.check(TradeFlags.FLAG_NORMAL_TRADE_YES)) { // 如果已登录
            Intent intent = new Intent(mActivity, MultiTradeActivity.class);
            Bundle bundle = new Bundle();
            try {
                bundle.putString("hq_stock_code", jsonObject.getString("code"));
                bundle.putString("hq_market", jsonObject.getString("market"));
                // 买入还是卖出的标志
                String type = jsonObject.getString("type");
                if (type.equals("buy")) { // 买入
                    bundle.putInt("ViewPagerFragmentPos", 0);
                } else { // 不是买入就当做卖出
                    bundle.putInt("ViewPagerFragmentPos", 1);
                }
            } catch (JSONException je) {
                je.printStackTrace();
            }
            intent.putExtras(bundle);
            startActivity(intent);
        } else { // 如果没登录
            mClickBtnBeforeLogin = -1;
            mJsonDataFromHq = jsonObject;
            startLogin(mClickBtnBeforeLogin, TradeLoginManager.LOGIN_TYPE_NORMAL);
        }
    }

    /**
     * 更新右上角“退出”按钮状态（是否可见）
     * 注：只有普通交易登录成功了，才显示
     */
    public void updateLogoutBtnState() {
        if (!isAdded()) {
            return;
        }
        if (mParentFragment.isNormalTrade()) {
            if (TradeFlags.check(TradeFlags.FLAG_NORMAL_TRADE_YES)) {
                showExitBtn();
                mParentFragment.setLoginToExit();
                mLogOutTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (TradeFlags.check(TradeFlags.FLAG_NORMAL_TRADE_YES)) {
                            logOff();
                        }
                    }
                });
                if (loadingDialog == null && mClickBtnBeforeLogin == 0) {//只有之间点击的登录按钮的时候，会返回交易主界面，在onResume的时候，才需要查询持仓数据
                    loadingDialog = new LoadingDialog(getContext());
                }
                if (loadingDialog != null) {
                    loadingDialog.show(R.string.querying_data);
                    loadingDialog.dismiss();
                }
            } else {
                mParentFragment.setExitTologin();
                setAccountBtnText("");
                hideExitBtn();
            }
        }
        mParentFragment.initLoginTvListener();
    }

    /**
     * 展示交易首页页面，隐藏手机验证页面，并设置标题
     */


    /**
     * 交易首页按键事件监听
     *
     * @param view 被单击的控件
     */
    public void onClick(View view) {
        int viewId = view.getId();
        onClick(viewId);
    }


    /**
     * 交易首页GV点击事件监听
     *
     * @param parent gridview等
     */
    public void onItemClick(GridView parent, int position) {
        if (TradeUtils.isFastClick2()) {
            return;
        }
        if (parent.getCount() == 8) {
            switch (position) {
                case 0:
                    onClickBuying();
                    break;
                case 1:
                    onClickSell();
                    break;
                case 2:
                    onClickRevocation();
                    break;
                case 3:
                    onClickMyHold();
                    break;
                case 4:
                    onClickMyHold();
                    break;
                case 5:
                    onClickTodayEntrust();
                    break;
                case 6:
                    onClickTodayTrade();
                    break;
                case 7:
                    onClickTransferAccount();
                    break;
            }
        } else if (parent.getCount() == 4) {
            switch (position) {
                case 0:
                    onclickHistoryEntrust();
                    break;
                case 1:
                    onclickHistoryTrade();
                    break;
                case 2:
                    ToastUtil.showToast("资金查询");
                    break;
                case 3:
                    onclickMoneyWater();
                    break;
                case 4:
                    break;
            }

        }
    }

       /*
        * 资金流水
        */
    private void onclickMoneyWater() {
        if (!TradeFlags.check(TradeFlags.FLAG_NORMAL_TRADE_YES)) { // 未登录时，调转到登录页面
            startLogin(2503, TradeLoginManager.LOGIN_TYPE_NORMAL);
        } else { // 已登录时
            Intent intent = new Intent(mActivity, HistoryMoneyActivity.class);
            mActivity.startActivity(intent);
        }
    }

    /*
    * 历史成交点击事件
    */
    private void onclickHistoryTrade() {
        if (!TradeFlags.check(TradeFlags.FLAG_NORMAL_TRADE_YES)) { // 未登录时，调转到登录页面
            startLogin(2501, TradeLoginManager.LOGIN_TYPE_NORMAL);
        } else { // 已登录时
            Intent intent = new Intent(mActivity, HistoryEntrustOrTradeActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("childePos", 0);
            intent.putExtras(bundle);
            mActivity.startActivity(intent);
        }
    }

    /*
    * 历史委托点击事件
    */
    private void onclickHistoryEntrust() {
        if (!TradeFlags.check(TradeFlags.FLAG_NORMAL_TRADE_YES)) { // 未登录时，调转到登录页面
            startLogin(2500, TradeLoginManager.LOGIN_TYPE_NORMAL);
        } else { // 已登录时
            Intent intent = new Intent(mActivity, HistoryEntrustOrTradeActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("childePos", 1);
            intent.putExtras(bundle);
            mActivity.startActivity(intent);
        }
    }

    /**
     * 交易首页按键事件监听
     *
     * @param viewId 被单击的控件的Id
     */
    public void onClick(int viewId) {
        if (viewId == R.id.rl_more) {
            if (!isShowMore) {
                mMoreMenuGv.setVisibility(View.VISIBLE);
                mMoreMenuGv.setFocusable(true);
                mMoreMenuGv.setFocusableInTouchMode(true);
                mMoreMenuGv.requestFocus();
                mMoreMenuGv.requestFocusFromTouch();
                mMoreMenuGv.performClick();
                expandGv();
                arrowTrunRight();
                isShowMore = true;

            } else {
                closeGv();
                arrowTurnLeft();
                isShowMore = false;
            }
        }
        if (TradeUtils.isFastClick2()) {
            return;
        }
        mClickBtnBeforeLogin = viewId;
        if (viewId == R.id.rl_new_buy) {
            onClickNewStock();
        } else if (viewId == R.id.rl_change_tradepwd) {
            onClickModifiedTradePwd();
        } else if (viewId == R.id.tv_exit_logout) {

        }
    }

    private ValueAnimator createDropAnimator(final View v, int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator arg0) {
                int value = (int) arg0.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
                layoutParams.height = value;
                v.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }

    private void closeGv() {
        ValueAnimator animator = createDropAnimator(mMoreMenuGv, mHiddenViewMeasuredHeight, 0);
        animator.setDuration(200);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mMoreMenuGv.setVisibility(GONE);
            }
        });
        animator.start();
    }

    private void expandGv() {
        mMoreMenuGv.setVisibility(View.VISIBLE);
        ValueAnimator animator = createDropAnimator(mMoreMenuGv, 0, mHiddenViewMeasuredHeight);
        animator.setDuration(200);
        animator.start();

    }

    //关闭显示更多时箭头动画
    private void arrowTurnLeft() {
        if (mArrowCcwsAnimator == null) {
            mArrowCcwsAnimator = ObjectAnimator.ofFloat(mRotateArrow, "rotation", 90F, 0F);
        }
        mArrowCcwsAnimator.setDuration(200);
        mArrowCcwsAnimator.start();
    }

    //展开时箭头动画
    private void arrowTrunRight() {
        if (mArrowClwsAnimator == null) {
            mArrowClwsAnimator = ObjectAnimator.ofFloat(mRotateArrow, "rotation", 0F, 90F);
        }
        mArrowClwsAnimator.setDuration(200);
        mArrowClwsAnimator.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mArrowCcwsAnimator != null) {
            mArrowCcwsAnimator = null;
        }
        if (mArrowClwsAnimator != null) {
            mArrowClwsAnimator = null;
        }
    }

    public void tradeMainLogin() {
        mClickBtnBeforeLogin = 0;
        startLogin(mClickBtnBeforeLogin, TradeLoginManager.LOGIN_TYPE_NORMAL);
        mParentFragment.setLogTvClickable(false);
    }

    public void tradeMainLogout() {
        logOff();
    }

    /**
     * 买入
     */
    private void onClickBuying() {
        if (!TradeFlags.check(TradeFlags.FLAG_NORMAL_TRADE_YES)) { // 未登录时，调转到登录页面
            startLogin(2000, TradeLoginManager.LOGIN_TYPE_NORMAL);
        } else { // 已登录时
            Intent intent = new Intent(mActivity, MultiTradeActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("ViewPagerFragmentPos", 0);
            intent.putExtras(bundle);
            mActivity.startActivity(intent);
        }
    }

    /**
     * 卖出
     */
    private void onClickSell() {
        if (!TradeFlags.check(TradeFlags.FLAG_NORMAL_TRADE_YES)) { // 未登录时，调转到登录页面
            startLogin(2001, TradeLoginManager.LOGIN_TYPE_NORMAL);
        } else { // 已登录时
            Intent intent = new Intent(mActivity, MultiTradeActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("ViewPagerFragmentPos", 1);
            intent.putExtras(bundle);
            mActivity.startActivity(intent);
        }
    }

    /**
     * 撤单
     */
    private void onClickRevocation() {
        if (!TradeFlags.check(TradeFlags.FLAG_NORMAL_TRADE_YES)) { // 未登录时，调转到登录页面
            startLogin(2002, TradeLoginManager.LOGIN_TYPE_NORMAL);
        } else { // 已登录时
            Intent intent = new Intent(mActivity, MultiTradeActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("ViewPagerFragmentPos", 2);
            intent.putExtras(bundle);
            mActivity.startActivity(intent);
        }
    }

    /**
     * 查询
     */
    private void onClickQuery() {
        if (!TradeFlags.check(TradeFlags.FLAG_NORMAL_TRADE_YES)) { // 未登录时，调转到登录页面
            startLogin(2003, TradeLoginManager.LOGIN_TYPE_NORMAL);
        } else { // 已登录时
            Intent intent = new Intent(mActivity, MultiTradeActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("ViewPagerFragmentPos", 3);
            intent.putExtras(bundle);
            mActivity.startActivity(intent);
        }
    }

    /**
     * 个人
     */
    private void onClickMyHold() {
        if (!TradeFlags.check(TradeFlags.FLAG_NORMAL_TRADE_YES)) { // 未登录时，调转到登录页面
            startLogin(2004, TradeLoginManager.LOGIN_TYPE_NORMAL);
        } else { // 已登录时
            Intent intent = new Intent(mActivity, MultiTradeActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("ViewPagerFragmentPos", 4);
            intent.putExtras(bundle);
            mActivity.startActivity(intent);
        }
    }

    /**
     * 当日委托
     */
    private void onClickTodayEntrust() {
        if (!TradeFlags.check(TradeFlags.FLAG_NORMAL_TRADE_YES)) { // 未登录时，调转到登录页面
            startLogin(2005, TradeLoginManager.LOGIN_TYPE_NORMAL);
        } else { // 已登录时
            //            Intent intent = new Intent(mActivity, TodayEntrustOrTradeActivity.class);
            //            Bundle bundle = new Bundle();
            //            bundle.putInt("ViewPagerFragmentPos", 0);
            //            intent.putExtras(bundle);
            //            mActivity.startActivity(intent);
            Intent intent = new Intent(mActivity, MultiTradeActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("ViewPagerFragmentPos", 3);
            bundle.putInt("childePos", 0);
            intent.putExtras(bundle);
            mActivity.startActivity(intent);
        }
    }

    /**
     * 当日成交
     */
    private void onClickTodayTrade() {
        if (!TradeFlags.check(TradeFlags.FLAG_NORMAL_TRADE_YES)) { // 未登录时，调转到登录页面
            startLogin(2006, TradeLoginManager.LOGIN_TYPE_NORMAL);
        } else { // 已登录时
            //            Intent intent = new Intent(mActivity, TodayEntrustOrTradeActivity.class);
            //            Bundle bundle = new Bundle();
            //            bundle.putInt("ViewPagerFragmentPos", 1);
            //            intent.putExtras(bundle);
            //            mActivity.startActivity(intent);
            Intent intent = new Intent(mActivity, MultiTradeActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("ViewPagerFragmentPos", 3);
            bundle.putInt("childePos", 1);
            intent.putExtras(bundle);
            mActivity.startActivity(intent);
        }
    }

    /**
     * 新股申购
     */
    private void onClickNewStock() {
        NewStockWebActivity.getNormalNewStockFragment().setUrl(ConfigManager.getInstance().getAddressConfigValue("NORMAL_NEWSTOCK_URL"));
        NewStockWebActivity.getNormalNewStockFragment().preloadUrl(mActivity, ConfigManager.getInstance().getAddressConfigValue("NORMAL_NEWSTOCK_URL"));
        Intent intent = new Intent(mActivity, NewStockWebActivity.class);
        intent.putExtra("loginType", NewStockWebActivity.NORMAL);
        mActivity.startActivity(intent);
    }

    /**
     * 融资融券
     */
    private void onClickTwoFinance() {
        if (TradeFlags.check(TradeFlags.FLAG_CREDIT_TRADE_YES)) {
            Intent intent = new Intent(mActivity, RSecuritiesMarginActivity.class);
            mActivity.startActivity(intent);
        } else {
            startLogin(R.id.tv_two_finance, TradeLoginManager.LOGIN_TYPE_CREDIT);
        }
    }

    /**
     * 场外基金交易
     */
    private void onClickFundTrade() {
        if (TradeFlags.check(TradeFlags.FLAG_NORMAL_TRADE_YES)) {
            Intent intent = new Intent(mActivity, FundTradeMainActivity.class);
            mActivity.startActivity(intent);
        } else { // 未登录时，调转到登录页面
            startLogin(R.id.tv_fun_trade, TradeLoginManager.LOGIN_TYPE_NORMAL);
        }
    }

    /**
     * 场内基金交易
     */
    private void onClickInFundTrade() {
        if (TradeFlags.check(TradeFlags.FLAG_NORMAL_TRADE_YES)) {
            Intent intent = new Intent(mActivity, InFundMainActivity.class);
            mActivity.startActivity(intent);
        } else { // 未登录时，调转到登录页面
            startLogin(R.id.tv_fun_trade_in, TradeLoginManager.LOGIN_TYPE_NORMAL);
        }
    }

    /**
     * 分级基金交易
     */
    private void onClickLevelFundTrade() {
        if (TradeFlags.check(TradeFlags.FLAG_NORMAL_TRADE_YES)) {
            Intent intent = new Intent(mActivity, LFundTradeMainActivity.class);
            mActivity.startActivity(intent);
        } else { // 未登录时，调转到登录页面
            startLogin(R.id.tv_fun_trade_level, TradeLoginManager.LOGIN_TYPE_NORMAL);

        }
    }

    /**
     * OTC交易
     */
    private void onClickTradeOTC() {
        if (TradeFlags.check(TradeFlags.FLAG_NORMAL_TRADE_YES)) {
            final Intent intent = new Intent(mActivity, TradeH5Activity.class);
            TradeWebFragmentManager.sWebCacheFragment.setFuncModule("4");
            TradeWebFragmentManager.sWebCacheFragment.preloadUrl(mActivity);
            TradeWebFragmentManager.sWebCacheFragment.prepareMsgToH5ForSkip(TradeLoginManager.LOGIN_TYPE_NORMAL);
            intent.setClass(mActivity, TradeH5Activity.class);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mActivity.startActivity(intent);
                }
            }, 150);
        } else {
            startLogin(R.id.tv_fun_otc, TradeLoginManager.LOGIN_TYPE_NORMAL);
        }
    }

    /**
     * 港股通
     */
    private void onClickHkTrade() {
        if (TradeFlags.check(TradeFlags.FLAG_NORMAL_TRADE_YES)) {
            Intent intent = new Intent(mActivity, HKMultiTradeActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("ViewPagerFragmentPos", 0);
            intent.putExtras(bundle);
            mActivity.startActivity(intent);
        } else { // 未登录时，调转到登录页面
            startLogin(R.id.tv_home_g_g_t, TradeLoginManager.LOGIN_TYPE_NORMAL);
        }
    }

    /**
     * 股转
     */
    private void onClickTransStock() {
        if (TradeFlags.check(TradeFlags.FLAG_NORMAL_TRADE_YES)) {
            Intent intent = new Intent(mActivity, TransStockMainActivityTrade.class);
            Bundle bundle = new Bundle();
            bundle.putInt("ViewPagerFragmentPos", 0);
            intent.putExtras(bundle);
            mActivity.startActivity(intent);
        } else { // 未登录时，调转到登录页面
            startLogin(R.id.tv_home_trans_stock, TradeLoginManager.LOGIN_TYPE_NORMAL);
        }
    }

    /**
     * 个股期权
     */
    private void onClickOneStockOption() {
        if (TradeFlags.check(TradeFlags.FLAG_OPTION_TRADE_YES)) {
            Intent intent = new Intent(mActivity, OptionMainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("ViewPagerFragmentPos", 0);
            intent.putExtras(bundle);
            mActivity.startActivity(intent);
        } else { // 未登录时，调转到登录页面
            startLogin(R.id.tv_one_stock_option, TradeLoginManager.LOGIN_TYPE_OPTION);
        }
    }

    /**
     * 银证转账
     */
    private void onClickTransferAccount() {
        if (TradeFlags.check(TradeFlags.FLAG_NORMAL_TRADE_YES)) {
            Intent intent = new Intent(mActivity, TransferBanktActivity.class);
            final Bundle bundle = new Bundle();
            bundle.putString("userType", TradeLoginManager.LOGIN_TYPE_NORMAL);
            intent.putExtras(bundle);
            mActivity.startActivity(intent);
        } else {
            startLogin(2007, TradeLoginManager.LOGIN_TYPE_NORMAL);
        }
    }

    /**
     * 一键归集
     */
    private void onClickOneKeyCollect() {
        if (TradeFlags.check(TradeFlags.FLAG_NORMAL_TRADE_YES)) {
            Intent intent = new Intent(mActivity, OneKeyActivity.class);
            mActivity.startActivity(intent);
        } else { // 未登录时，调转到登录页面
            startLogin(R.id.tv_oneKey_collection, TradeLoginManager.LOGIN_TYPE_NORMAL);
        }
    }

    /**
     * 修改交易密码
     */
    private void onClickModifiedTradePwd() {

        if (TradeFlags.check(TradeFlags.FLAG_NORMAL_TRADE_YES)) {
            Intent intent = new Intent(mActivity, ChangePasswordActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("userType", TradeLoginManager.LOGIN_TYPE_NORMAL);
            intent.putExtras(bundle);
            mActivity.startActivity(intent);
            //什么账号都没有登录，默认使用普通交易登录
        } else {
            startLogin(2201, TradeLoginManager.LOGIN_TYPE_NORMAL);
            //只登录了一种账户类型
        }
    }

    /**
     * 退市板块协议签署
     */
    private void onClickSignAgreement() {
        if (TradeFlags.check(TradeFlags.FLAG_NORMAL_TRADE_YES)) {
            Intent intent = new Intent(mActivity, SignAgreementActivity.class);
            mActivity.startActivity(intent);
        } else { // 未登录时，调转到登录页面
            startLogin(R.id.tv_sign_agreement, TradeLoginManager.LOGIN_TYPE_NORMAL);
        }
    }

    /**
     * 网络投票
     */
    private void onClickNetVote() {
        if (TradeFlags.check(TradeFlags.FLAG_NORMAL_TRADE_YES)) {
            Intent intent = new Intent(mActivity, NetVoteMainActivity.class);
            mActivity.startActivity(intent);
        } else { // 未登录时，调转到登录页面
            startLogin(R.id.tv_net_direction, TradeLoginManager.LOGIN_TYPE_NORMAL);
        }
    }

    /**
     * 预售邀约
     */
    private void onClickPresell() {

    }

    /**
     * 开始进行登录
     *
     * @param clickIdBeforeLogin 用户启动登录所单击的按钮的id
     * @param loginType          登录类型
     */
    private void startLogin(int clickIdBeforeLogin, String loginType) {
        if (TradeLoginManager.IF_UNITY_LOGIN) {
            //手机号登录成功建立会话失败 会话未建立
            if (!TradeFlags.check(TradeFlags.FLAG_CREATE_SESSION_SUCCESS) && TradeFlags.check(TradeFlags.FLAG_PHONE_LOGIN)) {
                //重新请求建立会话
                if (mTemp_token_key != null && mTemp_token_key.contains(Constants.MODULE_NAME_TRADE)) {
                    MemoryStorage memoryStorage = new MemoryStorage();
                    String temp_token = memoryStorage.loadData(mTemp_token_key);
                    mServices.startServerSession(temp_token);
                }
            }
            //            sendMsgToSSO(loginType);
        } else {
            Intent intent = new Intent(mActivity, TradeLoginActivity.class);
            intent.putExtra(MainBroadcastReceiver.INTENT_KEY_CLICK_VIEW_ID, clickIdBeforeLogin);
            intent.putExtra(Constants.LOGIN_TYPE, loginType);

            mActivity.startActivity(intent);
        }
    }


    public void logOff() {
        if (TradeUtils.isFastClick2()) {
            return;
        }
        MessageOkCancelDialog dialog = new MessageOkCancelDialog(mActivity, new MessageOkCancelDialog.IOkListener() {
            @Override
            public void onClickOk() {
                if (TradeFlags.check(TradeFlags.FLAG_NORMAL_TRADE_YES)) {
                    mServices.requestLogout();
                }
                setLogout();
            }
        });
        dialog.setCancelBtnVisiable(true);
        dialog.setMsgText(R.string.dialog_logout_normal);
        dialog.show();
    }

    /**
     * 注销时的操作
     * 默认注销所有已登录的账户
     */
    public void setLogout() {
        clearAllFlags();
        clearAllUserInfo();
        //更新页面状态
        updateLogoutBtnState();
        //清除供给H5的用户信息
        MemoryStorage memoryStorage = new MemoryStorage();
        memoryStorage.removeData(Constants.NORMAL_LOGIN_USERINFO_FORH5);
        memoryStorage.removeData(Constants.CREDIT_COOKIE_KEY);
        //        CommonUtil.syncWebviewCookies(getActivity(), NewStockWebActivity.NORMAL_URL,"");
        try {
            sendMessageNormalLogout(NewStockWebActivity.getNormalNewStockFragment());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendMessageNormalLogout(BaseWebFragment baseWebFragment) throws JSONException {
        JSONObject param = new JSONObject();
        //退出登录发个消息
        AppMessage appMessage = new AppMessage(111111, param);
        baseWebFragment.sendMessageToH5(appMessage);
    }

    /**
     * 清除标志位
     */
    public void clearAllFlags() {
        String shakePhone = ThinkiveTools.getDataToMemoryByMsg("shakePhone");
        if (TextUtils.isEmpty(shakePhone)) {
            TradeFlags.removeFlag(TradeFlags.FLAG_PHONE_LOGIN);
        }
        //移除普通交易标志位
        TradeFlags.removeFlag(TradeFlags.FLAG_GET_NORMAL_USERINFO_SUCCESS);
        TradeFlags.removeFlag(TradeFlags.FLAG_NORMAL_ACCOUNT_CHECK_SUCCESS);

        if (TradeFlags.check(TradeFlags.FLAG_NOT_UNITY_LOGIN_TYPE)) {//单独登录
            if (!TradeFlags.check(TradeFlags.FLAG_NORMAL_TRADE_YES) && !TradeFlags.check(TradeFlags.FLAG_CREDIT_TRADE_YES)) {
                TradeFlags.removeFlag(TradeFlags.FLAG_CREATE_SESSION_SUCCESS);
                mServices.requestClearSession();
            }
        }
    }

    /**
     * 清除用户信息
     */
    public void clearAllUserInfo() {
        //清除用户信息
        mTradeLoginManager.clearNormalUserInfo();
    }

    /**
     * 600003接口成功返回了用户信息之后
     * 获取到用户信息后，更新首页布局
     *
     * @param account
     * @param acctType
     */
    public void onGetUserInfo(String account, String acctType) {
        // 信用账号
        if (TradeLoginManager.LOGIN_TYPE_CREDIT_ACCOUNT.equals(acctType)) {
            TradeLoginManager.sCreditLoginAccount = account;
            TradeLoginManager.sCreditLoginType = acctType;
            TradeFlags.addFlag(TradeFlags.FLAG_GET_CREDIT_USERINFO_SUCCESS);
            // 个股期权
        } else if (TradeLoginManager.LOGIN_TYPE_OPTION_ACCOUNT.equals(acctType)) {
            TradeLoginManager.sOptionLoginAccount = account;
            TradeLoginManager.sOptionLoginType = acctType;
            TradeFlags.addFlag(TradeFlags.FLAG_GET_OPTION_USERINFO_SUCCESS);
            // 普通交易
        } else {
            TradeLoginManager.sNormalLoginAccount = account;
            TradeLoginManager.sNormalLoginType = acctType;
            TradeFlags.addFlag(TradeFlags.FLAG_GET_NORMAL_USERINFO_SUCCESS);
        }
        autoSkipPage();
    }

    /**
     * 登录成功时执行
     * 自动跳转到触发登录的页面
     */
    public void autoSkipPage() {
        // 之前在行情模块点击了“买入”、“卖出”按钮，召唤了统一登录
        if (mClickBtnBeforeLogin == -1) {
            onClickBuyOrSaleInHq(mJsonDataFromHq);
        } else if (mClickBtnBeforeLogin > 0) {
            onClick(mClickBtnBeforeLogin);
        } else {
            TradeTools.sendMsgToLoginForSubmitFinish(mActivity);
        }
        // 重置该变量的值，防止本次操作影响下一次操作
        mClickBtnBeforeLogin = 0;
    }

    /**
     * 给外壳模块的SSO模块发送消息，以进行统一登录
     *
     * @param loginType 登陆类型  信用账户或普通账户
     */
    public void sendMsgToSSO(String loginType) {
        String acctType;
        //如果登录类型是融资融券
        if (TradeLoginManager.LOGIN_TYPE_CREDIT.equals(loginType)) {
            acctType = TradeLoginManager.LOGIN_TYPE_CREDIT_ACCOUNT;
            //如果是个股期权
        } else if (TradeLoginManager.LOGIN_TYPE_OPTION.equals(loginType)) {
            acctType = TradeLoginManager.LOGIN_TYPE_OPTION_ACCOUNT;
            //否则就是普通交易
        } else {
            acctType = ConfigManager.getInstance().getSystemConfigValue("NORMAL_ACCT_TYPE");
        }
        JSONObject paramJsonObject = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            paramJsonObject.put("moduleName", Constants.MODULE_NAME_TRADE);
            paramJsonObject.put("toPage", "");
            paramJsonObject.put("isLoad", "");
            paramJsonObject.put("acct_type", acctType);
            jsonObject.put("moduleName", Constants.MODULE_NAME_SSO);
            jsonObject.put("params", paramJsonObject);
            AppMessage appMessage = new AppMessage("home", 50101, jsonObject);
            MessageManager.getInstance(mActivity).sendMessage(appMessage);
        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

    public void showExitBtn() {
        mLogOutTv.setVisibility(View.VISIBLE);
    }

    public void hideExitBtn() {
        mLogOutTv.setVisibility(View.GONE);
    }

    public void scrollToTop() {
        mScrollView.fullScroll(ScrollView.FOCUS_UP);
    }

    /**
     * 负责接收广播以实现登录结束后，自动从本页跳转到登录前用户点击过得按钮所对应的页面。
     * 例如，未登录时，用户点击买入按钮，进入登录页，登录后发送广播，
     */
    public class MainBroadcastReceiver extends TradeBaseBroadcastReceiver {

        public static final String INTENT_KEY_CLICK_VIEW_ID = "click_view_id";
        public static final String INTENT_KEY_JSON_FORM_HQ = "json_from_hq";
        //修改密码成功后(返回修改的类型)
        public static final String CHANGE_PWD_TYPE_RESULT = "change_pwd_type_result";
        //修改密码时是统一登陆还是单独登录
        public static final String CHANGE_PWD_TYPE = "change_pwd_type";

        public MainBroadcastReceiver(Context context) {
            super(context);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            super.onReceive(context, intent);
            String action = intent.getAction();
            switch (action) {
                case ACTION_START_ACTIVITY:  // 如果是登录后跳转指令广播

                    int viewId = intent.getIntExtra(INTENT_KEY_CLICK_VIEW_ID, 2000);

                        if (viewId != -1) { // 此时，说明当初是未登录时，在交易主页点击某个按钮，触发登录的
                            if (viewId >= 2000 && viewId < 3000) {
                                if (viewId - 2000 >= 500) {
                                    viewId = viewId - 2500;
                                    onItemClick(mMoreMenuGv, viewId);
                                } else if (viewId - 2000 < 100) {
                                    viewId = viewId - 2000;
                                    onItemClick(mFastMenuGv, viewId);
                                } else if (viewId - 2000 < 500&&viewId - 2000>=100) {
                                    viewId = viewId - 2200;
                                    if (viewId == 1) {
                                        onClickModifiedTradePwd();
                                    }
                                }
                            }
                        } else { // 此时，说明当初是在未登录时，从行情模块点击“买入”或“卖出”按钮，触发登录的
                            onClickBuyOrSaleInHq(mJsonDataFromHq);
                        }

                        break;
                        case ACTION_CHANGE_PWD_SUCCESS:  // 如果是修改密码成功的广播
                            //修改了那种账户的密码
                            String userType = intent.getStringExtra(CHANGE_PWD_TYPE_RESULT);
                            //登陆类型  单独登陆还是统一账户登陆
                            boolean notUnityLogin = intent.getBooleanExtra(CHANGE_PWD_TYPE, true);
                            //如果是统一登陆
                            if (notUnityLogin) {
                                switch (userType) {
                                    case TradeLoginManager.LOGIN_TYPE_CREDIT:  //信用
                                        mClickBtnBeforeLogin = 0;
                                        mTradeLoginManager.clearCreditUserInfo();
                                        startLogin(mClickBtnBeforeLogin, TradeLoginManager.LOGIN_TYPE_CREDIT);
                                        break;
                                    case TradeLoginManager.LOGIN_TYPE_OPTION:  //期权
                                        mClickBtnBeforeLogin = 0;
                                        mTradeLoginManager.clearOptionUserInfo();
                                        startLogin(mClickBtnBeforeLogin, TradeLoginManager.LOGIN_TYPE_OPTION);
                                        break;
                                    default:   //普通交易
                                        mClickBtnBeforeLogin = 0;
                                        mTradeLoginManager.clearNormalUserInfo();
                                        startLogin(mClickBtnBeforeLogin, TradeLoginManager.LOGIN_TYPE_NORMAL);
                                        break;
                                }
                                // 如果交易单独登录
                            } else {
                                startLoginWithoutUnity();
                            }
                            break;
                        case ACTION_ERROR_999:  //所有已登录的账户都清空
                            Bundle bundle = intent.getExtras();
                            mFuncNo999 = bundle.getString(Constant.FUNC_NO);
                            boolean isUnityLogin = bundle.getBoolean("isUnityLogin", true);
                            //统一账户才会执行重建会话操作
                            if (isUnityLogin) {
                                if (!mFuncNo999.equals("600003")) { //不是600003报的超时
                                    clearAllUserInfo();//清除用户信息
                                    clearAllFlags(); //清除所有标志位
                                    mClickBtnBeforeLogin = 0;
                                    // 先发送广播，退出资金账号校验状态。外壳、交易模块分别接收处理
                                    TradeBaseBroadcastReceiver.sendBroadcast(mActivity, new Intent(), ACTION_LOGOUT_FUND_ACCOUNT);
                                    // 退出所有的二级页面
                                    clearSecondPage();
                                    // 开启资金账号校验页面
                                    startLogin(mClickBtnBeforeLogin, TradeLoginManager.LOGIN_TYPE_NORMAL);
                                } else {
                                    mTradeLoginManager.reGetToken();//重构token
                                }
                            } else {  //如果交易单独登录
                                startLoginWithoutUnity();
                            }
                            break;
                        case ACTION_LOGOUT_FUND_ACCOUNT:  // 发起退出资金账号校验状态
                            mClickBtnBeforeLogin = 0;
                            break;
                        case ACTION_TRANSFORM_TRADE_LOGIN:
                            Intent intent2 = new Intent(mActivity, TradeLoginActivity.class);
                            intent2.putExtra("clickIdBeforeLogin", mClickBtnBeforeLogin);
                            intent2.putExtra("loginType", TradeLoginManager.LOGIN_TYPE_NORMAL);
                            mActivity.startActivity(intent2);
                            break;
                    }
        }
    }

    /**
     * 退出所有二级页面
     */
    private void clearSecondPage() {
        AppMessage appMessage = new AppMessage(Constants.MODULE_NAME_HOME, 50119, new JSONObject());
        MessageManager.getInstance(mActivity).sendMessage(appMessage);
    }

    /**
     * 开启单独登录
     */
    private void startLoginWithoutUnity() {
        clearSecondPage();
        mClickBtnBeforeLogin = 0;
        if (TradeFlags.check(TradeFlags.FLAG_PHONE_LOGIN)) {//手机号已登录
            startLogin(mClickBtnBeforeLogin, TradeLoginManager.LOGIN_TYPE_NORMAL);
        } else {
            Intent intent = new Intent(mActivity, TradeLoginActivity.class);
            intent.putExtra("clickIdBeforeLogin", mClickBtnBeforeLogin);
            intent.putExtra("loginType", TradeLoginManager.LOGIN_TYPE_NORMAL);
            mActivity.startActivity(intent);
        }
    }

    public String getFuncNo999() {
        return mFuncNo999;
    }

    public void setFuncNo999(String funcNo999) {
        mFuncNo999 = funcNo999;
    }
}
