package com.almazov.diacompanion.home

import android.view.View
import com.almazov.diacompanion.data.RecordEntity

interface InterfaceRecordsInfo{
    fun transitionToRecordInfo(view: View, record: RecordEntity)
}