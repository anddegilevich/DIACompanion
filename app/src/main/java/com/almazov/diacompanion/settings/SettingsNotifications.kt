package com.almazov.diacompanion.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.almazov.diacompanion.R
import kotlinx.android.synthetic.main.fragment_settings_notifications.view.*

class SettingsNotifications : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_settings_notifications, container, false)

        view.switch_notification.setOnCheckedChangeListener { buttonView, isChecked ->
            if (buttonView?.isPressed == true) {
                sendNotification()
            } else {
                sendNotification()
            }
        }


        return view
    }

    private fun sendNotification() {

        val builder = NotificationCompat.Builder(requireContext(),"channel_dia_reminder").apply {
            setSmallIcon(R.drawable.logo)
            setContentTitle("Example")
            setContentText("Example")
            setPriority(NotificationCompat.PRIORITY_DEFAULT)
        }
        with(NotificationManagerCompat.from(requireContext())) {
            notify(0,builder.build())
        }
    }

}