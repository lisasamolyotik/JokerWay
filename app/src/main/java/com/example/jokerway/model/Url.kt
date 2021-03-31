package com.example.jokerway.model

class Url(
    private val baseLink: String,
    private val teamId: String = "nodata",
    private val buyerId: String = "nodata",
    private val adset: String = "nodata",
    private val campaign_id: String = "nodata",
    private val pushToken: String = "nodata",
    private val afId: String = "nodata"
) {
    fun getLink(organic: String): String {
        val link = StringBuffer()
        link.append(baseLink)
        if (organic == "0") {
            return link.toString()
        }
        link.append(teamId)
        link.append(buyerId)
        link.append(adset)
        link.append(campaign_id)
        link.append(pushToken)
        link.append(afId)
        return link.toString()
    }
}