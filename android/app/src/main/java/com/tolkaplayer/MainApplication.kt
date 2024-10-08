package com.tolkaplayer

import android.app.Application
import android.os.RemoteException
import android.util.Log
import com.facebook.react.PackageList
import com.facebook.react.ReactApplication
import com.facebook.react.ReactHost
import com.facebook.react.ReactNativeHost
import com.facebook.react.ReactPackage
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint.load
import com.facebook.react.defaults.DefaultReactHost.getDefaultReactHost
import com.facebook.react.defaults.DefaultReactNativeHost
import com.facebook.soloader.SoLoader
import com.tolka.tolkaSurfacePlayerSdk.ISurfacePlayerProxy
import com.tolka.tolkaSurfacePlayerSdk.SurfaceConnectionStateListener
import com.tolka.tolkaSurfacePlayerSdk.SurfacePlayStateListener
import com.tolka.tolkaSurfacePlayerSdk.SurfacePlayerProxy
import com.tolka.tolkaSurfacePlayerSdk.SurfacePlayerSdkContractV2
import com.tolka.tolkaSurfacePlayerSdk.SurfaceScanListener
import com.tolka.tolkaSurfacePlayerSdk.model.SurfacePlayerChannel

class MainApplication : Application(), ReactApplication {

    private val TAG = "Surface:TolkaPlayerView"
    var mProxy: ISurfacePlayerProxy? = null
    var mChannelList: ArrayList<SurfacePlayerChannel> = ArrayList()
    var IS_DEBUG_SCAN = true // only to scan a specific frequency
    private var proxyListner: ProxyListner? = null

    override val reactNativeHost: ReactNativeHost =
        object : DefaultReactNativeHost(this) {
            override fun getPackages(): List<ReactPackage> =
                PackageList(this).packages.apply {
                    // Packages that cannot be autolinked yet can be added manually here, for example:
                    // add(MyReactNativePackage())
                    add(PlayerPackage())
                }

            override fun getJSMainModuleName(): String = "index"

            override fun getUseDeveloperSupport(): Boolean = BuildConfig.DEBUG

            override val isNewArchEnabled: Boolean = BuildConfig.IS_NEW_ARCHITECTURE_ENABLED
            override val isHermesEnabled: Boolean = BuildConfig.IS_HERMES_ENABLED
        }

    override val reactHost: ReactHost
        get() = getDefaultReactHost(applicationContext, reactNativeHost)

    override fun onCreate() {
        super.onCreate()
        SoLoader.init(this, false)
        if (BuildConfig.IS_NEW_ARCHITECTURE_ENABLED) {
            // If you opted-in for the New Architecture, we load the native entry point for this app.
            load()
        }
        initProxy()
    }

    private fun initProxy() {
        mProxy = SurfacePlayerProxy()
        mProxy?.setConnectionStatusListener(object : SurfaceConnectionStateListener {
            override fun onConnecting(msg: String) {
                Log.d(TAG, "onConnecting: $msg")
                proxyListner?.onConnecting("onConnecting: $msg")
            }

            override fun onConnectionFail(msg: String) {
                Log.d(TAG, "onConnectionFail: $msg")
                proxyListner?.onConnecting("onConnectionFail: $msg")
            }

            override fun onReady() {
                Log.d(TAG, "onReady: ")
                try {
                    Log.d(TAG, "Sdk version local: " + mProxy?.getLocalSdkVersion())
                    Log.d(TAG, "Sdk version remote: " + mProxy?.getRemoteSdkVersion())
                    proxyListner?.onReady("onReady: ")
                    getChannelListOrScan()
                } catch (e: RemoteException) {
                    throw RuntimeException(e)
                }

            }
        })

        mProxy?.setPlayStateListener(object : SurfacePlayStateListener {
            override fun onPlaybackReady() {
                Log.d(TAG, "onPlaybackReady: ")
            }

            override fun onPlaybackError(msg: String) {
                Log.d(TAG, "onPlaybackError: $msg")
            }

            override fun onPlaybackEnd() {
                Log.d(TAG, "onPlaybackEnd: ")
            }

            override fun onDrmError(msg: String) {
                Log.d(TAG, "onDrmError: $msg")
            }

            override fun onProcessing(msg: String) {
                Log.d(TAG, "onProcessing: $msg")
            }
        })
    }

    public fun scan( isDebug:Boolean) {
        IS_DEBUG_SCAN = isDebug;
        mProxy?.connectService(applicationContext)
    }

    private fun getChannelListOrScan() {
        try {
            if (IS_DEBUG_SCAN) {
                if (mChannelList.isEmpty()) {
                    mChannelList.addAll(
                        mProxy!!.scan(
                            575000,
                            6000,
                            SurfacePlayerSdkContractV2.TunerType.ATSC3
                        )
                    )
                    Log.d(TAG, "scan: $mChannelList")
                }
                proxyListner?.onChannelListReady(mChannelList)
            } else {
                if (mChannelList.isEmpty()) {
                    scanAllChannels()
                } else {
                    proxyListner?.onChannelListReady(mChannelList)
                }
            }
        } catch (e: RemoteException) {
            Log.d(TAG, "getChannelList: error:" + e.message)
            mChannelList.clear()
            proxyListner?.onChannelListReady(mChannelList)
        }
    }

    private fun scanAllChannels() {
        try {
            mProxy!!.fullScan(object : SurfaceScanListener {
                override fun onScanProgress(
                    channels: List<SurfacePlayerChannel>,
                    tunerType: Int,
                    freq: Int,
                    progress: Int,
                    total: Int
                ) {
                    val tunerSystemName =
                        SurfacePlayerSdkContractV2.TunerType.getTunerSystemName(tunerType)
                    Log.d(
                        TAG,
                        "onScanProgress: $tunerSystemName:$freq($progress/$total)"
                    )
                    Log.d(TAG, "channels: $channels")
                }

                override fun onScanFinish(channels: List<SurfacePlayerChannel>) {
                    Log.d(TAG, "onScanFinish: $channels")
                    mChannelList.addAll(channels)
//          myNativeModule?.sendChannelList(Gson().toJson(mChannelList))
//          updateDataset()
                    proxyListner?.onChannelListReady(mChannelList)
                }

                override fun onScanError(msg: String) {
                    Log.d(TAG, "onScanError: $msg")
                    mChannelList.clear()
                    proxyListner?.onChannelListReady(mChannelList)
                }
            })
        } catch (e: RemoteException) {
            Log.d(TAG, "scanAllChannels: error=" + e.message)
            mChannelList.clear()
            proxyListner?.onChannelListReady(mChannelList)
        }
    }

    fun setListener(proxyListner: ProxyListner) {
        this.proxyListner = proxyListner
    }
}
