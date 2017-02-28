package com.example.locationtracker.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.example.locationtracker.Constants;
import com.example.locationtracker.MainActivity;
import com.example.locationtracker.R;
import com.google.android.gms.location.LocationRequest;

import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import timber.log.Timber;

public class LocationTracker extends Service {

    private ReactiveLocationProvider reactiveLocationProvider;
    private Observable<Location> updatedLocation;
    private Subscription subscription;

    private boolean running = false;

    public LocationTracker() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timber.d("onStartCommand");
        if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
            Timber.d("Received Start Foreground Intent ");
            if(!running) {
                Timber.d("Started running");
                running = true;
                Notification notification = createNotification();
                startLocationTracking();
                startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);
            } else {
                Timber.d("Doing nothing");
            }
        } else if (intent.getAction().equals(Constants.ACTION.STOPFOREGROUND_ACTION)) {
            Timber.d("Received Stop Foreground Intent");
            running = false;
            stopLocationTracking();
            stopForeground(true);
            stopSelf();
        }
        return START_STICKY;
    }

    private Notification createNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Intent stopIntent = new Intent(this, LocationTracker.class);
        stopIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
        PendingIntent pStopIntent = PendingIntent.getService(this, 0,
                stopIntent, 0);

        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_launcher);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Location Tracker")
                .setTicker("Location Tracker")
                .setContentText("Tracking your location")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .addAction(android.R.drawable.ic_media_pause, "Stop", pStopIntent)
                .build();

        return notification;
    }

    public void startLocationTracking() {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1000);

        reactiveLocationProvider = new ReactiveLocationProvider(this);
        updatedLocation = reactiveLocationProvider.getUpdatedLocation(locationRequest);
        subscription = updatedLocation.subscribe(new Subscriber<Location>() {
            @Override
            public void onCompleted() {
                Timber.d("onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e, e.toString());
            }

            @Override
            public void onNext(Location location) {
                Timber.d("onNext");
                Timber.d(location.toString());
            }
        });
    }

    public void stopLocationTracking() {
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }
}
