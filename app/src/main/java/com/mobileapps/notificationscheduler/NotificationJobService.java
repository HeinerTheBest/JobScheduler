package com.mobileapps.notificationscheduler;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import static java.lang.Thread.sleep;

public class NotificationJobService extends JobService
{

    NotificationManager mNotifyManager;
    AsyncTask asyncTaskService;

    // Notification channel ID.
    private static final String PRIMARY_CHANNEL_ID =
            "primary_notification_channel";



    /**
     * Creates a Notification channel, for OREO and higher.
     */
    public void createNotificationChannel() {

        // Define notification manager object.
        mNotifyManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Notification channels are only available in OREO and higher.
        // So, add a check on SDK version.
        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.O) {

            // Create the NotificationChannel with all the parameters.
            NotificationChannel notificationChannel = new NotificationChannel
                    (PRIMARY_CHANNEL_ID,
                            "Job Service notification",
                            NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription
                    ("Notifications from Job Service");

            mNotifyManager.createNotificationChannel(notificationChannel);
        }
    }



    @Override
    public boolean onStartJob(final JobParameters jobParameters)
    {

        final Context context = this;

         asyncTaskService = new AsyncTask()
        {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Toast.makeText(NotificationJobService.this, "The process is starting in AsyncTaskService", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected Object doInBackground(Object[] objects)
            {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                //Create the notification channel
                createNotificationChannel();

                //Set up the notification content intent to launch the app when clicked
                PendingIntent contentPendingIntent = PendingIntent.getActivity
                        (context, 0, new Intent(context, MainActivity.class),
                                PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder builder = new NotificationCompat.Builder
                        (context, PRIMARY_CHANNEL_ID)
                        .setContentTitle("Job Service")
                        .setContentText("Your Job ran to completion!")
                        .setContentIntent(contentPendingIntent)
                        .setSmallIcon(R.drawable.ic_job_running)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setAutoCancel(true);


                mNotifyManager.notify(0, builder.build());
                Toast.makeText(NotificationJobService.this, "The process is finishing in AsyncTaskService", Toast.LENGTH_SHORT).show();

            }
        };

        asyncTaskService.execute();

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Toast.makeText(this, "THe work will be reschedule", Toast.LENGTH_SHORT).show();
        jobFinished(jobParameters,true);
        asyncTaskService.cancel(true);

        return true;
    }


}
