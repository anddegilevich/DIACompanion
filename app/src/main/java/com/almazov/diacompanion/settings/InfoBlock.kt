package com.almazov.diacompanion.settings

var infoList = mutableListOf<InfoBlock>()

class InfoBlock(
    var title: String,
    var info: String?,
    val id: Int? = infoList.size
)