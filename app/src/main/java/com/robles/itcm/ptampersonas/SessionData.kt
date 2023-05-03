package com.robles.itcm.ptampersonas

object SessionData {
    private var data: MutableMap<String, Any> = mutableMapOf()

    fun setData(key: String, value: Any){
        data[key] = value
    }

    fun getData(key: String):Any?{
        return data[key]
    }

}