package com.tolkaplayer

import android.util.Log
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.annotations.ReactProp
import com.google.gson.Gson
import com.tolka.tolkaSurfacePlayerSdk.model.SurfacePlayerChannel


class MyCustomViewManager : SimpleViewManager<TolkaPlayerView>() {

    val REACT_CLASS: String = "TolkaPlayerView"
    override fun getName(): String {
        return REACT_CLASS
    }

    override fun createViewInstance(p0: ThemedReactContext): TolkaPlayerView {
        return TolkaPlayerView(p0);
    }

    @ReactProp(name = "channel")
    fun setDebugProp(view: TolkaPlayerView?, map: ReadableMap?) {
        // Handle the prop
        map?.let {
            view?.let {
//                {"freq":0,"majorNum":8,"minorNum":1,"name":"WGTV-DT","svcId":1,"tunerType":3}
//                Log.e("TAG",Gson().toJson(map))
                val freq = map.getInt("freq")
                val majorNum = map.getInt("majorNum")
                val minorNum = map.getInt("minorNum")
                val name = map.getString("name")
                val svcId = map.getInt("svcId")
                val tunerType = map.getInt("tunerType")
                val obj : SurfacePlayerChannel =SurfacePlayerChannel(svcId,freq,tunerType,majorNum,minorNum,name)

                view.updateDataset(obj)
            }
        }
    }
}