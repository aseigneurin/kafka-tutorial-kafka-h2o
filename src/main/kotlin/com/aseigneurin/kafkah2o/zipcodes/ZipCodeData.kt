package com.aseigneurin.kafkah2o.zipcodes

import java.io.File

fun main() {
    val data = ZipCodeDataLoader().loadData()
    data.forEach { println("${it.key} -> ${it.value.count()} keys") }
}

typealias ZipCode = String
typealias ZipCodeData = Map<String, String>

class ZipCodeDataLoader {
    private val path = File("data/zipcodes/")
    private val lineRegex = Regex("<tr><td class=\"label\"><span class=\"Tips2\" title=\".+?\">(.+?):</span></td><td class=\"info\">(.+?)</td></tr>")
    private val tagRegex = Regex("(<\\w+.*?>|(</\\w+>))")

    fun loadData(): Map<ZipCode, ZipCodeData> {
        val res = path.list { f, name -> name.endsWith(".html") }
                .map { File(path, it) }
                .map { parse(it) }
                .map {
                    val zipCode = it.getValue("Zip Code")
                    Pair(zipCode, it)
                }
                .toMap()
        return res
    }

    fun parse(file: File): ZipCodeData {
        val data = mutableMapOf<String, String>()
        file.forEachLine {
            val match = lineRegex.find(it)
            if (match != null) {
                val key = match.groups.get(1)!!.value
                val value = match.groups.get(2)!!.value
                        .replace("&nbsp;", "")
                        .replace("<br>", "; ")
                        .replace(tagRegex, "")
                data[key] = value
            }
        }
        return data.toMap()
    }
}