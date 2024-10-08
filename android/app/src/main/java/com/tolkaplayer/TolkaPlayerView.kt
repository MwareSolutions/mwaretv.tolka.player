package com.tolkaplayer

import android.content.Context
import android.graphics.Color
import android.util.Log
import com.facebook.react.bridge.ReactApplicationContext
import com.google.gson.Gson
import com.tolka.tolkaSurfacePlayerSdk.SurfacePlayerViewV2
import com.tolka.tolkaSurfacePlayerSdk.model.SurfacePlayerChannel

class TolkaPlayerView(private val contex: Context) : SurfacePlayerViewV2(contex), ProxyListner {

    private var reactContext: ReactApplicationContext? = null
    private var myNativeModule: PlayerModule? = null

    init {
        if (reactContext == null) {
            reactContext =
                (contex.applicationContext as MainApplication).reactNativeHost.getReactInstanceManager()
                    .getCurrentReactContext() as ReactApplicationContext?
        }
        reactContext?.let {
            myNativeModule = PlayerModule(it)
        }
        (contex.applicationContext as MainApplication).setListener(this)
        setBackgroundColor(Color.BLUE)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        for (i in 0 until childCount) {
            getChildAt(i).layout(l, t, r, b)
        }
    }

    override fun onConnecting(msg: String) {
        myNativeModule?.sendDataToReactNative(msg)
    }

    override fun onConnectionFail(msg: String) {
        myNativeModule?.sendDataToReactNative(msg)
    }

    override fun onReady(msg: String) {
        myNativeModule?.sendDataToReactNative(msg)
    }

    override fun onChannelListReady(list: List<SurfacePlayerChannel>) {
        myNativeModule?.sendChannelList(Gson().toJson(list))
    }

    public fun updateDataset(surfacePlayerChannel: SurfacePlayerChannel) {
        if ((contex.applicationContext as MainApplication).mChannelList.isNotEmpty()) {
            var position = 0
            (contex.applicationContext as MainApplication).mChannelList.forEachIndexed { index, item ->
                if (item.svcId == surfacePlayerChannel.svcId) {
                    position = index
                }
            }
            (contex.applicationContext as MainApplication).mProxy?.setSurfacePlayerView(this)
            (contex.applicationContext as MainApplication).mProxy?.play((contex.applicationContext as MainApplication).mChannelList[position].chKey)
            invalidate()
        }
    }
}