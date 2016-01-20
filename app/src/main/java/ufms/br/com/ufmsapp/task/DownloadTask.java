package ufms.br.com.ufmsapp.task;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import ufms.br.com.ufmsapp.R;
import ufms.br.com.ufmsapp.utils.GetFileMimeType;

public class DownloadTask extends AsyncTask<String, Integer, String> {

    private Context context;
    private PowerManager.WakeLock mWakeLock;
    int iconResId;
    String title;
    String msg;

    private String fileName;
    private static final String UPLOAD_PATH_REPLACE = "/uploads/";
    public static final int NOTIFICATION_ID = 100;
    static NotificationCompat.Builder builder;
    static NotificationManager manager;
    protected PendingIntent piOpenNotification;

    public DownloadTask(Context context, int iconResId,
                        String title, String msg) {
        this.context = context;
        this.iconResId = iconResId;
        this.title = title;
        this.msg = msg;

        fileName = title.replace(UPLOAD_PATH_REPLACE, "");

        buildNotification();

    }

    private void buildNotification() {
        manager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        builder = new NotificationCompat.Builder(context);

        builder.setSmallIcon(iconResId);
        builder.setContentTitle(fileName);
        builder.setContentText(msg);
        builder.setDefaults(Notification.DEFAULT_ALL);

        builder.setAutoCancel(true);

        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
        intent.setDataAndType(Uri.fromFile(file), GetFileMimeType.getMimeType(title.replace(UPLOAD_PATH_REPLACE, "")));

        piOpenNotification = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(piOpenNotification);

        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(msg));
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // take CPU lock to prevent CPU from going off if the user
        // presses the power button during download
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getName());
        mWakeLock.acquire();

        builder.setProgress(100, 0, false);
        manager.notify(NOTIFICATION_ID, builder.build());
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        builder.setProgress(100, progress[0], false);
        //manager.notify(NOTIFICATION_ID, builder.build());
    }

    @Override
    protected void onPostExecute(String result) {
        mWakeLock.release();

        if (result != null) {
            Toast.makeText(context, context.getString(R.string.txt_download_error) + result, Toast.LENGTH_LONG).show();
        } else {
            builder.setContentText(context.getString(R.string.txt_download_finalizado));
            builder.setProgress(0, 0, false);
            manager.notify(NOTIFICATION_ID, builder.build());
        }
    }

    @Override
    protected String doInBackground(String... sUrl) {
        InputStream input = null;
        OutputStream output = null;

        HttpURLConnection connection = null;
        try {
            URL url = new URL(sUrl[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();

            // download the file
            input = connection.getInputStream();
            output = new FileOutputStream(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName));

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
                if (isCancelled()) {
                    input.close();
                    return null;
                }
                total += count;
                // publishing the progress....
                if (fileLength > 0) // only if total length is known
                    publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }
        } catch (Exception e) {
            return e.toString();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }
        return null;
    }
}