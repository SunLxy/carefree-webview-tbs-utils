// CarefreeWebViewTBSUtilsModule.java

package com.carefree.webview.tbs.utils;

import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.tencent.smtt.export.external.TbsCoreSettings;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CarefreeWebViewTBSUtilsModule extends ReactContextBaseJavaModule {
    private DeviceEventManagerModule.RCTDeviceEventEmitter eventEmitter;
    private int isX5DownloadFinish = 0; // 下载X5内核完成
    private int isX5InstallFinish = 0; // 安装X5内核进度
    private int isX5DownloadProgress = 0; // 下载过程的通知，提供当前下载进度[0-100]
    private boolean isX5ViewInitFinished = false;// x5內核初始化完成的回调，true表x5内核加载成功，否则表加载失败，会自动切换到系统内核。
    private boolean isX5CoreInitFinished = false;// 内核初始化完毕
    private  boolean isDisableX5 = QbSdk.getIsSysWebViewForcedByOuter();// 是否禁用 x5内核


    private final ReactApplicationContext reactContext;

    public CarefreeWebViewTBSUtilsModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "CarefreeWebViewTBSUtils";
    }


    /**
     * 获取初始x5内核加载信息
     */
    @ReactMethod
    public void getInitX5sdkInfo(final Promise promise) {
        try {
            WritableMap map = Arguments.createMap();
            map.putInt("isX5DownloadFinish",isX5DownloadFinish);
            map.putInt("isX5InstallFinish",isX5InstallFinish);
            map.putInt("isX5DownloadProgress",isX5DownloadProgress);
            map.putBoolean("isX5ViewInitFinished",isX5ViewInitFinished);
            map.putBoolean("isX5CoreInitFinished",isX5CoreInitFinished);
            map.putBoolean("isDisableX5",isDisableX5);
            promise.resolve(map);
        } catch (Exception err) {
            promise.resolve(err.getMessage());
        }
    }

    /**
     * 下载x5内核
     */
    @ReactMethod
    public void initDownloadTBS() {
        // 在调用TBS初始化、创建WebView之前进行如下配置
        HashMap map = new HashMap();
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);
        QbSdk.initTbsSettings(map);
        QbSdk.setDownloadWithoutWifi(true);
        QbSdk.setNeedInitX5FirstTime(true);
        QbSdk.setTbsListener(
            new TbsListener() {
            @Override
            public void onDownloadFinish(int i) {
                isX5DownloadFinish = i;
                WritableMap map = Arguments.createMap();
                map.putString("type","downloadFinish");
                map.putInt("errorCode",i);
                map.putString("msg","下载X5内核完成");
                getDeviceEventEmitter().emit("CarefreeWebViewTBSUtils", map);
                //下载结束时的状态，下载成功时errorCode为100,其他均为失败，外部不需要关注具体的失败原因
                Log.d("QbSdk", "onDownloadFinish -->下载X5内核完成：" + i);
            }

            @Override
            public void onInstallFinish(int i) {
                isX5InstallFinish = i;
                WritableMap map = Arguments.createMap();
                map.putString("type","installFinish");
                map.putInt("errorCode",i);
                map.putString("msg","安装X5内核进度" + i);
                getDeviceEventEmitter().emit("CarefreeWebViewTBSUtils", map);
                //安装结束时的状态，安装成功时errorCode为200,其他均为失败，外部不需要关注具体的失败原因
                Log.d("QbSdk", "onInstallFinish -->安装X5内核进度：" + i);
            }

            @Override
            public void onDownloadProgress(int i) {
                //下载过程的通知，提供当前下载进度[0-100]
                isX5DownloadProgress = i;
                WritableMap map = Arguments.createMap();
                map.putString("type","downloadProgress");
                map.putString("msg","下载X5内核进度" + i);
                map.putInt("errorCode",i);
                getDeviceEventEmitter().emit("CarefreeWebViewTBSUtils", map);
                Log.d("QbSdk", "onDownloadProgress -->下载X5内核进度：" + i);
            }
            });

        QbSdk.PreInitCallback cb =
            new QbSdk.PreInitCallback() {
            @Override
            public void onViewInitFinished(boolean arg0) {
                isX5ViewInitFinished = arg0;
                WritableMap map = Arguments.createMap();
                map.putString("type","viewInitFinished");
                map.putString("msg","x5內核初始化完成");
                map.putBoolean("errorCode",arg0);
                getDeviceEventEmitter().emit("CarefreeWebViewTBSUtils", map);
                // x5內核初始化完成的回调，true表x5内核加载成功，否则表加载失败，会自动切换到系统内核。
                Log.d("QbSdk", " 内核加载 " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
                isX5CoreInitFinished = true;
                // 内核初始化完毕
                WritableMap map = Arguments.createMap();
                map.putString("type","coreInitFinished");
                map.putString("msg","内核初始化完毕");
                map.putBoolean("errorCode",true);
                getDeviceEventEmitter().emit("CarefreeWebViewTBSUtils", map);
                Log.d("QbSdk", "内核初始化完毕");
            }
            };
        // x5内核初始化接口
        QbSdk.initX5Environment(reactContext, cb);
    //  Log.i("QbSdk", "是否可以加载X5内核: " + QbSdk.canLoadX5(view.getContext()));
        Log.i("QbSdk", "app是否主动禁用了X5内核: " + QbSdk.getIsSysWebViewForcedByOuter());
    }

    // 事件发送
    private DeviceEventManagerModule.RCTDeviceEventEmitter getDeviceEventEmitter() {
        if (eventEmitter == null) {
            eventEmitter = reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class);
        }
        return eventEmitter;
    };

}
