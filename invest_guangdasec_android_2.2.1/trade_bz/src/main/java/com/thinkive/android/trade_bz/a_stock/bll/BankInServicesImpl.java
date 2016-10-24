package com.thinkive.android.trade_bz.a_stock.bll;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.thinkive.android.trade_bz.a_stock.bean.BankTransferBean;
import com.thinkive.android.trade_bz.a_stock.fragment.TransferBankInFragment;
import com.thinkive.android.trade_bz.interfaces.IRequestAction;
import com.thinkive.android.trade_bz.others.tools.EncryptManager;
import com.thinkive.android.trade_bz.others.tools.TradeLoginManager;
import com.thinkive.android.trade_bz.request.Option305023;
import com.thinkive.android.trade_bz.request.Option305024;
import com.thinkive.android.trade_bz.request.Option305025;
import com.thinkive.android.trade_bz.request.Option305028;
import com.thinkive.android.trade_bz.request.Request300200;
import com.thinkive.android.trade_bz.request.Request300201;
import com.thinkive.android.trade_bz.request.Request300202;
import com.thinkive.android.trade_bz.request.Request300204;
import com.thinkive.android.trade_bz.request.Request303037;
import com.thinkive.android.trade_bz.request.Request303038;
import com.thinkive.android.trade_bz.request.Request303039;
import com.thinkive.android.trade_bz.request.Request303041;
import com.thinkive.android.trade_bz.utils.LoadingDialogUtil;
import com.thinkive.android.trade_bz.utils.ToastUtils;
import com.thinkive.android.trade_bz.utils.TradeUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *  银证转账业务类
 * Announcements：
 * @author 张雪梅
 * @company Thinkive
 * @date 2015/8/10
 */

public class BankInServicesImpl extends BasicServiceImpl {
    private TransferBankInFragment mFragment = null;
    private EncryptManager mEncryptManager;
    private String mUserType;
    private LoadingDialogUtil loadingDialogUtil;//请求数据状态框 工具类

    public BankInServicesImpl(TransferBankInFragment fragment, String userType) {
        mFragment = fragment;
        mUserType = userType;
        mEncryptManager= EncryptManager.getInstance();
        loadingDialogUtil = new LoadingDialogUtil(fragment.getContext());
    }
    @Override
    public void onResume() {

    }
    @Override
    public void onStop() {

    }
    /**
     * 请求存管银行数据列表
     */
    public void requestBankList() {
        HashMap<String, String> map = new HashMap<String, String>();
        if (mUserType != null && mUserType.equals(TradeLoginManager.LOGIN_TYPE_NORMAL)) {  //普通账户
            new Request300200(map, new IRequestAction() {
                @Override
                public void onSuccess(Context context, Bundle bundle) {
                    ArrayList<BankTransferBean> dataList = bundle.getParcelableArrayList(Request300200.BUNDLE_KEY_BANK);
                    for(BankTransferBean bean:dataList){
                        bean.setBank_account(TradeUtils.HideSomeData(bean.getBank_account()));
                    }
                    mFragment.getBankSelectData(dataList);
                }
                @Override
                public void onFailed(Context context, Bundle bundle) {
                    ToastUtils.toast(context, bundle.getString(Request300200.ERROR_INFO));
                }
            }).request();
        } else if (mUserType != null && mUserType.equals(TradeLoginManager.LOGIN_TYPE_CREDIT)) {  //融资融券账户
            new Request303037(map, new IRequestAction() {
                @Override
                public void onSuccess(Context context, Bundle bundle) {
                    ArrayList<BankTransferBean> dataList = bundle.getParcelableArrayList(Request303037.BUNDLE_KEY_R_BANK);
                    for(BankTransferBean bean:dataList){
                        bean.setBank_account(TradeUtils.HideSomeData(bean.getBank_account()));
                    }
                    mFragment.getBankSelectData(dataList);
                }
                @Override
                public void onFailed(Context context, Bundle bundle) {
                    ToastUtils.toast(context, bundle.getString(Request303037.ERROR_INFO));
                }
            }).request();
        } else if (mUserType != null && mUserType.equals(TradeLoginManager.LOGIN_TYPE_OPTION)) {  //个股期权账户
            new Option305023(map, new IRequestAction() {
                @Override
                public void onSuccess(Context context, Bundle bundle) {
                    ArrayList<BankTransferBean> dataList = bundle.getParcelableArrayList(Option305023.BUNDLE_KEY_R_BANK);
                    for(BankTransferBean bean:dataList){
                        bean.setBank_account(TradeUtils.HideSomeData(bean.getBank_account()));
                    }
                    mFragment.getBankSelectData(dataList);
                }
                @Override
                public void onFailed(Context context, Bundle bundle) {
                    ToastUtils.toast(context, bundle.getString(Option305023.ERROR_INFO));
                }
            }).request();
        }
    }
    /**
     * 请求当前银行账户的余额
     */
    public void requestOtherMoney(String code, String moneyType, String pwd) {
        HashMap<String, String> map = new HashMap<String, String>();
        if (mUserType != null && mUserType.equals(TradeLoginManager.LOGIN_TYPE_NORMAL)) { //普通账户
            map.put("bank_code", code);
            map.put("money_type", moneyType);
            if(!TextUtils.isEmpty(pwd)){
                map.put("bank_password",mEncryptManager.getRsaEncryptStr(pwd));
            }
            mFragment.setOnRequestStatus();
            new Request300204(map, new IRequestAction() {
                @Override
                public void onSuccess(Context context, Bundle bundle) {
                    BankTransferBean dataList = (BankTransferBean) bundle.getSerializable(Request300204.BUNDLE_KEY_OTHER_MONEY);
                    mFragment.getCurrentAccountMoney(dataList);
                }
                @Override
                public void onFailed(Context context, Bundle bundle) {
                    mFragment.getCurrentAccountMoney();
                    ToastUtils.toast(context, bundle.getString(Request300204.ERROR_INFO));
                }
            }).request();
        }else if (mUserType != null && mUserType.equals(TradeLoginManager.LOGIN_TYPE_CREDIT)) {  //融资融券账户
            map.put("bank_code", code);
            map.put("money_type", moneyType);
            if(!TextUtils.isEmpty(pwd)){
                map.put("bank_password",mEncryptManager.getRsaEncryptStr(pwd));
            }
            mFragment.setOnRequestStatus();
            new Request303041(map, new IRequestAction() {
                @Override
                public void onSuccess(Context context, Bundle bundle) {
                    BankTransferBean dataList = (BankTransferBean) bundle.getSerializable(Request303041.BUNDLE_KEY_R_OTHER_MONEY);
                    mFragment.getCurrentAccountMoney(dataList);
                }
                @Override
                public void onFailed(Context context, Bundle bundle) {
                    mFragment.getCurrentAccountMoney();
                    ToastUtils.toast(context, bundle.getString(Request303041.ERROR_INFO));
                }
            }).request();
        }else if (mUserType != null && mUserType.equals(TradeLoginManager.LOGIN_TYPE_OPTION)) {  //个股期权账户
            map.put("bank_code", code);
            map.put("money_type", moneyType);
            if(!TextUtils.isEmpty(pwd)){
                map.put("bank_password",mEncryptManager.getRsaEncryptStr(pwd));
            }
            mFragment.setOnRequestStatus();
            new Option305028(map, new IRequestAction() {
                @Override
                public void onSuccess(Context context, Bundle bundle) {
                    BankTransferBean dataList = (BankTransferBean) bundle.getSerializable(Option305028.BUNDLE_KEY_R_OTHER_MONEY);
                    mFragment.getCurrentAccountMoney(dataList);
                }
                @Override
                public void onFailed(Context context, Bundle bundle) {
                    mFragment.getCurrentAccountMoney();
                    ToastUtils.toast(context, bundle.getString(Option305028.ERROR_INFO));
                }
            }).request();
        }
    }

    /**
     * 请求查询银证转账的业务信息
     */
    public void requestBankServices(String code, final String direction) {
        loadingDialogUtil.showLoadingDialog(0);
        HashMap<String, String> map = new HashMap<String, String>();
        if (mUserType != null && mUserType.equals(TradeLoginManager.LOGIN_TYPE_NORMAL)) { //普通账户
            map.put("bank_code", code);
            map.put("transfer_direction", direction);
            new Request300201(map, new IRequestAction() {
                @Override
                public void onSuccess(Context context, Bundle bundle) {
                    loadingDialogUtil.hideDialog();
                    BankTransferBean dataList = (BankTransferBean) bundle.getSerializable(Request300201.BUNDLE_KEY_OTHER_MONEY);
                    if(direction.equals("2")){
                        mFragment.onGetIsNeedPwd(dataList);
                    }else{
                        mFragment.getCurrentServices(dataList);
                    }
                }
                @Override
                public void onFailed(Context context, Bundle bundle) {
                    loadingDialogUtil.hideDialog();
                    ToastUtils.toast(context, bundle.getString(Request300201.ERROR_INFO));
                }
            }).request();
        }else if (mUserType != null && mUserType.equals(TradeLoginManager.LOGIN_TYPE_CREDIT)) {  //融资融券账户
            map.put("bank_code", code);
            map.put("transfer_direction", direction);
            new Request303038(map, new IRequestAction() {
                @Override
                public void onSuccess(Context context, Bundle bundle) {
                    loadingDialogUtil.hideDialog();
                    BankTransferBean dataList = (BankTransferBean) bundle.getSerializable(Request303038.BUNDLE_KEY_R_OTHER_MONEY);
                    if(direction.equals("2")){
                        mFragment.onGetIsNeedPwd(dataList);
                    }else{
                        mFragment.getCurrentServices(dataList);
                    }
                }
                @Override
                public void onFailed(Context context, Bundle bundle) {
                    loadingDialogUtil.hideDialog();
                    ToastUtils.toast(context, bundle.getString(Request303038.ERROR_INFO));
                }
            }).request();
        }else if (mUserType != null && mUserType.equals(TradeLoginManager.LOGIN_TYPE_OPTION)) {  //个股期权账户
            map.put("bank_code", code);
            map.put("transfer_direction", direction);
            new Option305024(map, new IRequestAction() {
                @Override
                public void onSuccess(Context context, Bundle bundle) {
                    loadingDialogUtil.hideDialog();
                    BankTransferBean dataList = (BankTransferBean) bundle.getSerializable(Option305024.BUNDLE_KEY_R_OTHER_MONEY);
                    if(direction.equals("2")){
                        mFragment.onGetIsNeedPwd(dataList);
                    }else{
                        mFragment.getCurrentServices(dataList);
                    }
                }
                @Override
                public void onFailed(Context context, Bundle bundle) {
                    loadingDialogUtil.hideDialog();
                    ToastUtils.toast(context, bundle.getString(Option305024.ERROR_INFO));
                }
            }).request();
        }
    }
    /**
     * 请求当前转账的结果
     */
    public void requestTransferBank(String code, String moneyType, String direction, String money, String fundPwd, String bankPwd) {
        loadingDialogUtil.showLoadingDialog(0);
        HashMap<String, String> map = new HashMap<String, String>();
        if (mUserType != null && mUserType.equals(TradeLoginManager.LOGIN_TYPE_NORMAL)) { //普通账户
            map.put("bank_code", code);
            map.put("money_type", moneyType);
            map.put("transfer_direction", direction);
            map.put("tranamt", money);
            if(fundPwd != null && !TextUtils.isEmpty(fundPwd)){//资金密码
                map.put("fund_password", mEncryptManager.getRsaEncryptStr(fundPwd));
            }else if(bankPwd != null && !TextUtils.isEmpty(bankPwd)){//银行密码
                map.put("bank_password", mEncryptManager.getRsaEncryptStr(bankPwd));
            }
            new Request300202(map, new IRequestAction() {
                @Override
                public void onSuccess(Context context, Bundle bundle) {
                    loadingDialogUtil.hideDialog();
                    BankTransferBean dataList = (BankTransferBean) bundle.getSerializable(Request300202.BUNDLE_KEY_TRANSFER);
                    mFragment.getTransferResult(dataList);
                }
                @Override
                public void onFailed(Context context, Bundle bundle) {
                    loadingDialogUtil.hideDialog();
                    ToastUtils.toast(context, bundle.getString(Request300202.ERROR_INFO));
                }
            }).request();
        }else if (mUserType != null && mUserType.equals(TradeLoginManager.LOGIN_TYPE_CREDIT)) {  //融资融券账户
            map.put("bank_code", code);
            map.put("money_type", moneyType);
            map.put("transfer_direction", direction);
            map.put("tranamt", money);
            if(fundPwd != null && !TextUtils.isEmpty(fundPwd)){//资金密码
                map.put("fund_password", mEncryptManager.getRsaEncryptStr(fundPwd));
            }else if(bankPwd != null && !TextUtils.isEmpty(bankPwd)){//银行密码
                map.put("bank_password", mEncryptManager.getRsaEncryptStr(bankPwd));
            }
            new Request303039(map, new IRequestAction() {
                @Override
                public void onSuccess(Context context, Bundle bundle) {
                    loadingDialogUtil.hideDialog();
                    BankTransferBean dataList = (BankTransferBean) bundle.getSerializable(Request303039.BUNDLE_KEY_R_TRANSFER);
                    mFragment.getTransferResult(dataList);
                }
                @Override
                public void onFailed(Context context, Bundle bundle) {
                    loadingDialogUtil.hideDialog();
                    ToastUtils.toast(context, bundle.getString(Request303039.ERROR_INFO));
                }
            }).request();
        }else if (mUserType != null && mUserType.equals(TradeLoginManager.LOGIN_TYPE_OPTION)) {  //个股期权账户
            map.put("bank_code", code);
            map.put("money_type", moneyType);
            map.put("transfer_direction", direction);
            map.put("tranamt", money);
            if(fundPwd != null && !TextUtils.isEmpty(fundPwd)){//资金密码
                map.put("fund_password", mEncryptManager.getRsaEncryptStr(fundPwd));
            }else if(bankPwd != null && !TextUtils.isEmpty(bankPwd)){//银行密码
                map.put("bank_password", mEncryptManager.getRsaEncryptStr(bankPwd));
            }
            new Option305025(map, new IRequestAction() {
                @Override
                public void onSuccess(Context context, Bundle bundle) {
                    loadingDialogUtil.hideDialog();
                    BankTransferBean dataList = (BankTransferBean) bundle.getSerializable(Option305025.BUNDLE_KEY_R_TRANSFER);
                    mFragment.getTransferResult(dataList);
                }
                @Override
                public void onFailed(Context context, Bundle bundle) {
                    loadingDialogUtil.hideDialog();
                    ToastUtils.toast(context, bundle.getString(Option305025.ERROR_INFO));
                }
            }).request();
        }
    }
}
