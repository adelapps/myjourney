package ir.safareman.k60

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import android.util.Log

class ReminderReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    Log.d("ReminderReceiver", "Alarm received!")
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val channelId = "dts_step_reminders"

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(
        channelId,
        "یادآور پله‌های درمانی (DTS)",
        NotificationManager.IMPORTANCE_HIGH
      ).apply {
        description = "اعلان‌های مربوط به زمان دریافت پله جدید درمان"
      }
      notificationManager.createNotificationChannel(channel)
    }

    val mainIntent = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent = PendingIntent.getActivity(
      context,
      0,
      mainIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val notification = NotificationCompat.Builder(context, channelId)
      .setSmallIcon(android.R.drawable.ic_popup_reminder)
      .setContentTitle("تنظیم دوز جدید DTS")
      .setContentText("زمان دریافت پله جدید و تنظیم دوز فرا رسیده است.")
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setContentIntent(pendingIntent)
      .setAutoCancel(true)
      .build()

    notificationManager.notify(System.currentTimeMillis().toInt(), notification)
  }

  companion object {
    fun scheduleReminder(context: Context, stepId: Int, startDateMillis: Long) {
      val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
      val intent = Intent(context, ReminderReceiver::class.java).apply {
        putExtra("step_id", stepId)
      }
      val pendingIntent = PendingIntent.getBroadcast(
        context,
        stepId,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
      )

      // Time of reminder: Start Date + 21 days
      val triggerTime = startDateMillis + (21L * 24 * 60 * 60 * 1000)

      if (triggerTime > System.currentTimeMillis()) {
        try {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setAndAllowWhileIdle(
              android.app.AlarmManager.RTC_WAKEUP,
              triggerTime,
              pendingIntent
            )
          } else {
            alarmManager.set(
              android.app.AlarmManager.RTC_WAKEUP,
              triggerTime,
              pendingIntent
            )
          }
          Log.d("ReminderReceiver", "Scheduled reminder for step $stepId at $triggerTime")
        } catch (e: Exception) {
          Log.e("ReminderReceiver", "Failed to schedule alarm", e)
        }
      }
    }

    fun cancelReminder(context: Context, stepId: Int) {
      val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
      val intent = Intent(context, ReminderReceiver::class.java)
      val pendingIntent = PendingIntent.getBroadcast(
        context,
        stepId,
        intent,
        PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
      )
      if (pendingIntent != null) {
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
        Log.d("ReminderReceiver", "Cancelled reminder for step $stepId")
      }
    }
  }
}
