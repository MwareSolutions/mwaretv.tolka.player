package com.tolkaplayer

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.WritableMap
import com.facebook.react.modules.core.DeviceEventManagerModule


class PlayerModule(private var context: ReactApplicationContext) : ReactContextBaseJavaModule() {

    override fun getName(): String {
        return "PlayerModule"
    }

    @ReactMethod
    fun addListener(eventName: String?) {
    }

    @ReactMethod
    fun removeListeners(count: Int?) {
    }

    @ReactMethod
    fun sendDataToReactNative(data: String?) {
        val params = Arguments.createMap()
        params.putString("data", data)
        sendEvent(context, "EventFromAndroid", params)
    }

    @ReactMethod
    fun sendChannelList(data: String?) {
        val params = Arguments.createMap()
        params.putString("data", data)
        sendEvent(context, "ChannelFetchListEvent", params)
    }

    private fun sendEvent(
        reactContext: ReactApplicationContext,
        eventName: String,
        params: WritableMap
    ) {
        reactContext
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
            .emit(eventName, params)
    }

    @ReactMethod
    fun scan( isDebug:Boolean) {
        if(context.applicationContext is MainApplication) {
            (context.applicationContext as MainApplication).scan(isDebug)

        }
    }
}