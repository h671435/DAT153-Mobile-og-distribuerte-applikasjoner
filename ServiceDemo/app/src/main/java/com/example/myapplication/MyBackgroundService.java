package com.example.myapplication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class MyBackgroundService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       new Thread(
               new Runnable() {
                   @Override
                   public void run() {
                       while (true) {
                           Log.e("Service", "Service is running in the background");
                           try {
                               Thread.sleep(2000); // Sleep for 2 seconds
                           } catch (InterruptedException e) {
                               e.printStackTrace();
                           }
                       }
                   }
               }
       ).start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
