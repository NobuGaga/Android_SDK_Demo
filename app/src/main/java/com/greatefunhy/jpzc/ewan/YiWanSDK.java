package com.greatefunhy.jpzc.ewan;

import android.util.Log;

import com.http.function.HttpFunction;
import com.unity.IBaseSDK;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import cn.ewan.supersdk.open.CollectInfo;
import cn.ewan.supersdk.open.InitInfo;
import cn.ewan.supersdk.open.PayInfo;
import cn.ewan.supersdk.open.SuperInitListener;
import cn.ewan.supersdk.open.SuperLogin;
import cn.ewan.supersdk.open.SuperLoginListener;
import cn.ewan.supersdk.open.SuperLogoutListener;
import cn.ewan.supersdk.open.SuperPayListener;
import cn.ewan.supersdk.open.SuperPlatform;

public class YiWanSDK extends IBaseSDK {
    //主Activity引用
    public MainActivity mActivity;

    YiWanSDK(MainActivity activity){
        mActivity = activity;
    }

    //拼接账号用的平台ID(SPID)
    private String mSPID;
    //渠道appid
    private String mAPPID;

    @Override
    protected void Init() {
        // 封装初始化所需要的信息
        InitInfo info = new InitInfo();
        mAPPID = String.valueOf(mActivity.getMetaDataInt("badao_appid"));
        if (null == mAPPID)
            mAPPID = "";
        String signKey = mActivity.getMetaDataString("badao_singkey");
        String packageId = String.valueOf(mActivity.getMetaDataInt("packageId"));
        info.setAppId(mAPPID);              // 申请的应用ID
        info.setSignKey(signKey);           // 申请的客户端签名key
        info.setPacketid(packageId);        // 包id
        SuperPlatform.getInstance().init(mActivity, info, new SuperInitListener() {
            @Override
            public void onSuccess() {
                Log("onSuccess Init 初始化成功！");
                // 游戏方做初始化成功后的相关操作
                DeviceInfoManager.mainActivity = mActivity;
                JsonData data = new JsonData();
                data.setData("platform", mActivity.getMetaDataInt("platform"));
                mSPID = mActivity.getMetaDataString("spid");
                data.setData("spid", mSPID);
                data.setData("appId", mAPPID);
                data.setData("deviceID", DeviceInfoManager.getIMEI());
                data.setData("deviceMac", DeviceInfoManager.getMacAddress());
                data.setData("deviceName", DeviceInfoManager.getDeviceName());
                data.setData("deviceSysVer", DeviceInfoManager.getDeviceSystemVersion());
                data.setData("deviceSysName", mActivity.getMetaDataString("deviceSystemName"));
                data.setData("netMode", DeviceInfoManager.getNetworkType());
                data.setData("isHasSwitchAccount", SuperPlatform.getInstance().isHasSwitchAccount() ? 1 : 0);
                data.setData("isHasExitAccount", mActivity.getMetaDataBoolean("isHasExitAccount") ? 1 : 0);
                data.setData("versionUrl", mActivity.getMetaDataString("versionUrl"));
                data.setData("logUrl", mActivity.getMetaDataString("logUrl"));
                String ip = DeviceInfoManager.getWifiIP();
                if (null == ip)
                    ip = DeviceInfoManager.getLocalIpAddress();
                data.setData("ip", ip);
                data.setData("packageName", mActivity.getPackageName());
                data.setData("packageBuildVersion", mActivity.getAppVersion());
                data.setData("appName", mActivity.getAppName());
                mActivity.sendMessageToUnity(UnityFuncName.Unity_callSDKInit, data.toString());
            }
            @Override
            public void onFail(String msg) {
                Log("onFail Init 初始化失败！\n错误信息\t" + msg);
                // 游戏方做初始化失败后的相关处理
                mActivity.sendMessageToUnity(UnityFuncName.Unity_callOnSDKError, msg);
            }
        });
    }

    private SuperLogin mLoginData;
    private SuperLogin mSwitchData;

    @Override
    protected void Login(String loginType) {
        SuperPlatform.getInstance().login(mActivity, new SuperLoginListener() {
            @Override
            public void onLoginSuccess(SuperLogin login) {
                mLoginData = login;
                // 正常登入成功的回调
                Log("登入成功,openid = " + login.getOpenid());
                Log("登入成功,toker = " + login.getToken());
                Log("登入成功,sign = " + login.getSign());
                new Thread() {
                    @Override
                    public void run() {
                        if (null != mLoginData)
                            httpLoginHandle(UnityFuncName.Unity_callSDKLogin, mLoginData);
                    }
                }.start();
            }
            @Override
            public void onLoginFail(String msg) { }
            @Override
            public void onLoginCancel() { }
            @Override
            public void onNoticeGameToSwitchAccount() {
                JsonData json = new JsonData();
                json.setData("func", UnityFuncName.Unity_callSDKLogout);
                mActivity.sendMessageToUnity(UnityFuncName.Unity_callSDKCallBack, json.toString());
            }
            @Override
            public void onSwitchAccountSuccess(final SuperLogin login) {
                mSwitchData = login;
                // 切换帐号成功的回调，先释放旧角色，再重新加载游戏角色！
                Log("切换帐号成功,openid = " + login.getOpenid());
                Log("切换帐号成功,toker = " + login.getToken());
                Log("切换帐号成功,sign = " + login.getSign());
                new Thread() {
                    @Override
                    public void run() {
                        if (null != mSwitchData)
                            httpLoginHandle(UnityFuncName.Unity_callSDKSwitchAccount, mSwitchData);
                    }
                }.start();
            }
        });
    }

    private void httpLoginHandle(String funcName, SuperLogin data)  {
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("openid", (Object) data.getOpenid());
        dataMap.put("token", (Object) data.getToken());
        dataMap.put("sign", (Object) data.getSign());
        String loginUrl = mActivity.getMetaDataString("loginUrl");
        Log("loginUrl  " + loginUrl);
        Log("url data map  " + dataMap.toString());
        String jsonString = null;
        JsonData json = null;
        if (null != json) {
            String temp = json.getDataString("errmessage");
            if (null != temp)
                Log("http post errmessage  " + temp);
        }
        jsonString = null;
        json = null;
        jsonString = HttpFunction.httpGet(loginUrl, dataMap);
        Log("http post url  " + HttpFunction.testUrl);
        Log("http post param  " + HttpFunction.testParams);
        if (null != jsonString)
            Log("http post Json String data  " + jsonString);
        try {
            while (null != jsonString && 0 != jsonString.length() && !jsonString.startsWith("{")) {
                jsonString = jsonString.substring(1);
                Log("http post Json String change data  " + jsonString);
            }
            if (null != jsonString && 0 != jsonString.length()) {
                json = new JsonData(jsonString);
                Log("http post Json data  " + json.toString());
                json.setData("func", funcName);
                if (0 != json.getDataInt("errcode")) {
                    String msg = json.getDataString("errmessage");
                    Log(msg);
                    mActivity.SDKLogout();
                    json.remove("password");
                } else {
                    String account = json.getDataString("openid") + "&" + mSPID;
                    json.setData("account", account);
                    json.setData("sbid", SuperPlatform.getInstance().getTpUid());
                    json.setData("areaid", data.getAreaId());
                    json.remove("errmessage");
                }
                json.remove("errcode");
                mActivity.sendMessageToUnity(UnityFuncName.Unity_callSDKCallBack, json.toString());
            }
            else {
                String msg = "登录失败";
                Log("登录post返回信息错误");
                mActivity.SDKLogout();
                json = new JsonData();
                json.setData("func", funcName);
                json.setData("errmessage", msg);
                mActivity.sendMessageToUnity(UnityFuncName.Unity_callSDKCallBack, json.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void Logout() {
        /**
         * TODO 超级SDK退出对话框接口
         */
        SuperPlatform.getInstance().logout(mActivity, new SuperLogoutListener() {
            /**
             * 收到此回调,游戏弹窗自有的退出框,用户点击确认退出按钮后再调用超级SDK的退出接口exit()
             */
            @Override
            public void onGamePopExitDialog() {
                JsonData json = new JsonData();
                json.setData("func", UnityFuncName.Unity_callSDKExit);
                mActivity.sendMessageToUnity(UnityFuncName.Unity_callSDKCallBack, json.toString());
            }
            /**
             * 收到此回调,超级SDK内部已经处理退出弹窗的逻辑,游戏直接调用超级SDK的退出接口exit()
             */
            @Override
            public void onGameExit() {
                /**
                 * TODO 超级SDK退出接口,注意要先于游戏退出的逻辑调用,必接
                 */
                SuperPlatform.getInstance().exit(mActivity);
                mActivity.finish();
            }
        });
    }

    @Override
    protected void SwithAccount(){
        SuperPlatform.getInstance().switchAccount(mActivity);
    }

    @Override
    protected void Center(String type) {
        SuperPlatform.getInstance().enterPlatform(mActivity);
    }

    @Override
    protected void Pay(double price, String productName, int num, int playerID, int serverID, String param) {
        /**
         * TODO 超级SDK支付,必接
         */
        PayInfo payinfo = new PayInfo();
        // x元对应x*10元宝
        payinfo.setPrice(new Double(price).floatValue());
        // 当前服务器ID
        payinfo.setServerId(String.valueOf(serverID));
        payinfo.setProductName(productName);
        payinfo.setProductNumber(num);
        payinfo.setCutsomInfo(param);
        SuperPlatform.getInstance().pay(mActivity, payinfo, new SuperPayListener() {
            @Override
            public void onComplete() {
                mActivity.OnSDKPay(PayCallbackStateCode.Success);
                Log("支付成功！");
            }
            @Override
            public void onCancel() {
                mActivity.OnSDKPay(PayCallbackStateCode.Cancel);
                Log("支付取消！");
            }
            @Override
            public void onFail(String msg) {
                mActivity.OnSDKPay(PayCallbackStateCode.Fail);
                Log("支付失败！msg " + msg);
                mActivity.ShowToast(msg);
            }
        });
    }

    @Override
    protected void CollectData(Object data) {
        if (data instanceof String) {
            String value = (String) data;
            try {
                JsonData json = new JsonData(value);
                int type = json.getDataInt("type");
                int serverID = json.getDataInt("serverID");
                String serverName = json.getDataString("serverName");
                int roleID = json.getDataInt("roleID");
                String roleName = json.getDataString("roleName");
                int roleLevel = json.getDataInt("roleLevel");

                /**
                 * 参数一：1为登录动作，2为创建角色,3为角色升级,4为角色退出 （必传）
                 * 参数二：区服标识id（必传）
                 * 参数三：区服名称（必传）
                 * 参数四：角色id（必传）
                 * 参数五：角色名称（必传）
                 * 参数六：角色等级（必传）
                 * 参数七：扩展字段（可选,没有可传""）
                 * 参数八: 扩展参数键值对(可选,没有可传null)
                 */
                CollectInfo info = new CollectInfo(type, String.valueOf(serverID), serverName, String.valueOf(roleID), roleName, roleLevel, "", null);
                Log(info.toString());
                SuperPlatform.getInstance().collectData(mActivity, info);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    protected void Exit() {
        /**
         * TODO 超级SDK退出接口,注意要先于游戏退出的逻辑调用,必接
         */
        SuperPlatform.getInstance().exit(mActivity);
    }

    protected void AppUpdate() {}

    protected void Tracking() {}

    protected void Function(String param) {}

    @Override
    public void Log(String Tag, String msg){
        if (mIsLog) {
            switch (mLogLevel) {
                case None:
                    break;
                case VERBOSE:
                    Log.v(Tag, msg);
                    break;
                case DEBUG:
                    Log.d(Tag, msg);
                    break;
                case INFO:
                    Log.i(Tag, msg);
                    break;
                case WARN:
                    Log.w(Tag, msg);
                    break;
                case ERROR:
                    Log.e(Tag, msg);
                    break;
                default:
                    break;
            }
        }
    }
}

