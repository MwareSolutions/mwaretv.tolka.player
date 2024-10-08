package com.tolkaplayer

import com.tolka.tolkaSurfacePlayerSdk.model.SurfacePlayerChannel

interface ProxyListner {

    fun onConnecting(msg: String)

    fun onConnectionFail(msg: String)

    fun onReady(msg: String)

    fun onChannelListReady(list : List<SurfacePlayerChannel>)
}