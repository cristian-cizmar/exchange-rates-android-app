package com.cristiancizmar.exchangerates.util

import com.cristiancizmar.exchangerates.model.ExchangeRate
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

fun JsonElement.toExchangeRate(): ExchangeRate {
    val jsonObject = this.asJsonObject
    val date = jsonObject.get("date").asString
    lateinit var rates: Map<String, Float>
    jsonObject.entrySet().forEach {
        if (it.key != "date") {
            val gson = Gson()
            val type: Type = object : TypeToken<Map<String?, Float?>?>() {}.type
            rates = gson.fromJson(it.value, type)
            rates = rates.mapKeys { keys -> keys.key.uppercase() }
        }
    }
    return ExchangeRate(date, rates)
}

/**
 * Reorders the items in a map by prioritizing the items in [priorityList]
 */
fun <T, R> Map<T, R>.addItemsFirst(priorityList: List<T>): Map<T, R> {
    val newMap = mutableMapOf<T, R>()
    priorityList.forEach { priorityItem ->
        val priorityValue = this[priorityItem]!!
        newMap[priorityItem] = priorityValue
    }
    newMap += this.filterKeys { !priorityList.contains(it) }.toMutableMap()
    return newMap
}

fun <T> List<T>.indexOfFirstDefault0(element: T): Int {
    var pos = this.indexOfFirst {
        it == element
    }
    if (pos == -1) {
        pos = 0
    }
    return pos
}