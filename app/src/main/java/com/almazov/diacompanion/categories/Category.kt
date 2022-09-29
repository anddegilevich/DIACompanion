package com.almazov.diacompanion.categories

import android.media.Image

var categoryList = mutableListOf<Category>()

class Category(
    var icon: Int,
    var name: Int,
    var primary_color: Int,
    var secondary_color: Int,
    var action: Int,
    val id: Int? = categoryList.size
    )