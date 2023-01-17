package com.almazov.diacompanion.onboard.pages

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.almazov.diacompanion.R
import kotlinx.android.synthetic.main.fragment_setup_complete_page.view.*
import java.io.*


class SetupCompletePage : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_setup_complete_page, container, false)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        view.btn_finish.setOnClickListener {
            onBoardingFinish()
            findNavController().navigate(R.id.action_setupCompletePage_to_homePage)
        }

        return view
    }

    private fun onBoardingFinish(){

        try {
            val myInput: InputStream = requireContext().assets.open("model.model")
            val modelPath: String = requireContext().getDatabasePath("model.model").path
            val myOutput: OutputStream = FileOutputStream(modelPath)
            val buffer = ByteArray(1024)
            var length: Int
            while (myInput.read(buffer).also { length = it } > 0) {
                myOutput.write(buffer, 0, length)
            }
            myOutput.flush()
            myOutput.close()
            myInput.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

//        createNotificationChanel()

        val finished = true

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        sharedPreferences.edit().putBoolean("ON_BOARDING_FINISHED", finished).apply()

    }

    private fun createNotificationChanel() {
        val channel = NotificationChannel(
            "channel_dia_reminder","Reminder", NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Used to remind about entering your activities data"
        }
        val notificationManager: NotificationManager = requireContext()
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

}