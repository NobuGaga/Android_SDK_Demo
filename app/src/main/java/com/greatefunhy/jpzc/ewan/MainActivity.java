package com.greatefunhy.jpzc.ewan;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.content.pm.PackageManager;
import android.widget.Toast;

import org.json.JSONException;

import com.tencent.bugly.crashreport.CrashReport;
import com.unity.IBaseSDK;
import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;

import cn.ewan.supersdk.open.SuperPlatform;

public class MainActivity extends UnityPlayerActivity {
    //回调Unity Object名, 回调挂载的脚本方法, 避免挂载的多个脚本有同名方法
    private final static String UnityObjectName = "Main Camera";
    //对应平台SDK接口类
    private YiWanSDK mSDKInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createBuglySDK();
        createYiWanSDK();
        CopyFunction.Init(this);
        /**
         * TODO 超级SDK生命周期onCreate,注意要先于initSDK接口调用,必接
         */
        SuperPlatform.getInstance().onCreate(this);
    }
    @Override
    protected void onStart() {
        super.onStart();
        /**
         * TODO 超级SDK生命周期onStart,必接
         */
        SuperPlatform.getInstance().onStart(mSDKInstance.mActivity);
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        /**
         * TODO 超级SDK生命周期onRestart,必接
         */
        SuperPlatform.getInstance().onRestart(mSDKInstance.mActivity);
    }
    @Override
    protected void onResume() {
        super.onResume();
        /**
         * TODO 超级SDK生命周期onResume,必接
         */
        SuperPlatform.getInstance().onResume(mSDKInstance.mActivity);
    }
    @Override
    protected void onPause() {
        super.onPause();
        /**
         * TODO 超级SDK生命周期onPause,必接
         */
        SuperPlatform.getInstance().onPause(mSDKInstance.mActivity);
    }
    @Override
    protected void onStop() {
        super.onStop();
        /**
         * TODO 超级SDK生命周期onStop,必接
         */
        SuperPlatform.getInstance().onStop(mSDKInstance.mActivity);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        /**
         * TODO 超级SDK生命周期onDestroy,必接
         */
        SuperPlatform.getInstance().onDestroy(mSDKInstance.mActivity);
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        /**
         * TODO 超级SDK生命周期onNewIntent,必接
         */
        SuperPlatform.getInstance().onNewIntent(mSDKInstance.mActivity, intent);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /**
         * TODO 超级SDK生命周期onActivityResult,必接
         */
        SuperPlatform.getInstance().onActivityResult(requestCode, resultCode, data);
    }

    public void SDKInit() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() { mSDKInstance.SDKInit(); }
        });
    }
    public void SDKLogin(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() { mSDKInstance.SDKLogin(); }
        });
    }
    public void SDKLogin(String params){
        SDKLogin();
    }

    public void SDKSwitchAccount() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() { mSDKInstance.SDKSwithAccount(); }
        });
    }

    private JsonData mPayData;

    public void SDKPay(String param) {
        try {
            mPayData = new JsonData(param);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (null != mPayData) {
                        double price = mPayData.getDataDouble("price");
                        if (-1 == price)
                            price = mPayData.getDataInt("price");
                        String productName = mPayData.getDataString("productName");
                        int num = mPayData.getDataInt("num");
                        int playerID = mPayData.getDataInt("roleID");
                        int serverID = mPayData.getDataInt("serverID");
                        String param =  mPayData.getDataString("param");
                        mPayData = null;
                        mSDKInstance.SDKPay(price, productName, num, playerID, serverID, param);
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void OnSDKPay(int stateCode) {
        JsonData json = new JsonData();
        json.setData("func", UnityFuncName.Unity_callSDKPay);
        json.setData("stateCode", stateCode);
        sendMessageToUnity(UnityFuncName.Unity_callSDKCallBack, json.toString());
    }

    private String mCollectData;
    public void SDKCollectData(String data){
        mCollectData = data;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSDKInstance.SDKCollectData(mCollectData);
                mCollectData = null; }
        });
    }

    public void SDKLogout() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() { mSDKInstance.SDKLogout(); }
        });
    }
    public void SDKExit() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() { mSDKInstance.SDKExit(); }
        });
    }

    private void createBuglySDK() {
        CrashReport.initCrashReport(this, "94e560efc9", false);
    }

    private void createYiWanSDK() {
        mSDKInstance = new YiWanSDK(this);
        //设置开启调试信息等级
        Boolean isLog = getMetaDataBoolean("IsLog");
        String strLevel = getMetaDataString("LogLevel");
        IBaseSDK.ELogLevel logLevel =  IBaseSDK.ELogLevel.getByString(strLevel);
        if (null != logLevel)
            mSDKInstance.setLogState(isLog, logLevel);
    }

    //---------------------------------------------通用复制粘贴方法---------------------------------------------
    public void CopyText(String param) {
        try {
            JsonData data = new JsonData(param);
            String text = data.getDataString("text");
            if (null != text)
                CopyFunction.Copy(text);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void PasteText() {
        JsonData json = new JsonData();
        json.setData("func", UnityFuncName.Unity_callPaste);
        json.setData("text", CopyFunction.Paste());
        sendMessageToUnity(UnityFuncName.Unity_callSDKCallBack, json.toString());
    }

    //---------------------------------------------获取AnroidManifest.xml的Meta数据---------------------------------------------
    private Bundle mBundle;
    private void initBundle() {
        try {
            if (null == mBundle)
                mBundle = getPackageManager().getActivityInfo(getComponentName(), PackageManager.GET_META_DATA).metaData;
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public int getMetaDataInt(String key){
        initBundle();
        return mBundle.getInt(key, -1);
    }
    public String getMetaDataString(String key){
        return getMetaDataString(key, "");
    }
    public String getMetaDataString(String key, String defaultValue){
        initBundle();
        return mBundle.getString(key, defaultValue);
    }
    private String getMetaDataIntOrString(String key){
        String tempStr = getMetaDataString(key, "");
        if ("" == tempStr) {
            int tempInt = getMetaDataInt(key);
            if (-1 ==tempInt )
                return "";
            else
                return String.valueOf(tempInt);
        }
        else
            return tempStr;
    }
    public Boolean getMetaDataBoolean(String key){
        initBundle();
        return mBundle.getBoolean(key, false);
    }
    //---------------------------------------------         获取Appy应用数据       -------------------------------------------
    // 获取版本号名称
    public String getAppVersion() {
        String verName = "";
        try {
            verName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }

    // 获取应用程序名称
    public String getAppName() {
        try {
            PackageManager packageManager = getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return getResources().getString(labelRes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    //--------------------------------------------------------------------------------------------------------------------------
    public void sendMessageToUnity(String callback, String data) {
        mSDKInstance.Log("sendMessageToUnity data:" + data);
        UnityPlayer.UnitySendMessage(UnityObjectName, callback, data);
    }

    // 显示Toast消息
    public void ShowToast(final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }
}