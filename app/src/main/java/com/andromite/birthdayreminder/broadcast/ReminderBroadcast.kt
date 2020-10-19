package com.andromite.birthdayreminder.broadcast

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.andromite.birthdayreminder.R
import java.util.*

class ReminderBroadcast : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        var builder =  NotificationCompat.Builder(context!!,"Birthday Reminder")
            .setSmallIcon(R.drawable.ic_undraw_happy_birthday_s72n)
            .setContentTitle("Today is " + intent!!.getStringExtra("name") +"'s Birthday")
            .setContentText("Wish them a very happy Birthday")
            .setPriority(NotificationCompat.PRIORITY_HIGH)


        var notificationManager = NotificationManagerCompat.from(context!!)
        notificationManager.notify(System.currentTimeMillis().toInt(),builder.build())

        setReminder(context)      // repeat alarm next year
    }

    fun setReminder(context: Context) {

        var intent = Intent(context,ReminderBroadcast::class.java)
        var pendingIntent = PendingIntent.getBroadcast(context,0,intent,0)

        var manager = context.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager

       val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR) +1      // increase year and set reminder again
        c.set(Calendar.YEAR,year)

        manager.set(
            AlarmManager.RTC_WAKEUP,
            c.timeInMillis,
            pendingIntent)
    }
}