package com.almazov.diacompanion.settings

var infoList = mutableListOf<InfoBlock>()

class InfoBlock(
    var title: String,
    var infoBlockId: Int,
    val id: Int? = infoList.size
)