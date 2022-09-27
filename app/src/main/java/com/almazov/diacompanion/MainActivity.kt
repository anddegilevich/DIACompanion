package com.almazov.diacompanion

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.almazov.diacompanion.onboard.OnBoardingViewPager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_DIACompanion);
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }
}