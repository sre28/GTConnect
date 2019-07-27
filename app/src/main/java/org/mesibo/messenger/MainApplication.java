/** Copyright (c) 2019 Mesibo
 * https://mesibo.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the terms and condition mentioned on https://mesibo.com
 * as well as following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions, the following disclaimer and links to documentation and source code
 * repository.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution.
 *
 * Neither the name of Mesibo nor the names of its contributors may be used to endorse
 * or promote products derived from this software without specific prior written
 * permission.
 *
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * Documentation
 * https://mesibo.com/documentation/
 *
 * Source Code Repository
 * https://github.com/mesibo/messenger-app-android
 *
 */

package org.mesibo.messenger;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.mesibo.api.Mesibo;
import com.mesibo.mediapicker.MediaPicker;
import com.mesibo.calls.MesiboCall;
import com.mesibo.messaging.MesiboUI;
import android.location.LocationManager;
import android.location.LocationListener;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Scanner;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Mesibo on 29/09/17.
 */

public class MainApplication extends Application implements Mesibo.RestartListener, LocationListener {
    public static final String TAG = "MesiboSampleApplication";
    private static Context mContext = null;
    private static MesiboCall mCall = null;
    private static AppConfig mConfig = null;
    // define url
    public static String URL = "<YOUR_API_LINK>";

    OkHttpClient client = new OkHttpClient();
    Location location = null;
    TextView txtRequest;
    String str;

    class MyAsyncTask extends AsyncTask<Request, Void, Response> {

        @Override
        protected Response doInBackground(Request... requests) {
            Response response = null;
            try {
                response = client.newCall(requests[0]).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);
            try {

                str = response.body().string();
                //String ss = "{\"ad\":[]}";

//                Toast.makeText(getBaseContext(), Integer.toString(21), Toast.LENGTH_LONG).show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        Mesibo.setRestartListener(this);
        mConfig = new AppConfig(this);
        SampleAPI.init(getApplicationContext());

        LocationManager locationManager = (LocationManager)
                getSystemService(this.LOCATION_SERVICE);

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30, 1, this);

        }
        catch(SecurityException a)
        {
            Toast.makeText(getBaseContext(), " Bye", Toast.LENGTH_LONG).show();
        }
        //Toast.makeText(getBaseContext(), "Gps ", Toast.LENGTH_LONG).show();

        mCall = MesiboCall.getInstance();
        mCall.init(this);

        RequestBody formBody = new FormBody.Builder()
                .build();

        MesiboUI.Config opt = MesiboUI.getConfig();
        opt.mToolbarColor = 0xff00868b;
        opt.emptyUserListMessage = "Ask your family and friends to download so that you can try out Mesibo functionalities";
        MediaPicker.setToolbarColor(opt.mToolbarColor);
        str = "";
    }

    public void testMessage (String message , Intent intent, int a){

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , intent,
                PendingIntent.FLAG_ONE_SHOT);


        String channelId = "some_channel_id";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        android.support.v4.app.NotificationCompat.Builder notificationBuilder =
                new android.support.v4.app.NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentTitle("GT Notification")
                        .setContentText(message)
                        .setAutoCancel(true)
                        //.setSound(defaultSoundUri)
                        .setBadgeIconType(android.support.v4.app.NotificationCompat.BADGE_ICON_SMALL)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }

        assert notificationManager != null;
        notificationManager.notify(a /* ID of notification */, notificationBuilder.build());
    }


    public void onLocationChanged(Location location) {

        String strr = "Latitude: "+location.getLatitude()+" Longitude: "+location.getLongitude();

        //Toast.makeText(getBaseContext(), strr, Toast.LENGTH_LONG).show();

            String str1;
            str1 = "https://advertiser-gt-aic.herokuapp.com/userlocation?lat=";
            str1 += location.getLatitude();
            str1 += "&long=";
            str1 += location.getLongitude();
            str1 += "&user=userinfo";

        Request request = new Request.Builder()
                .url(str1)
                .build();

        int counter = 0;
        int i;
        for(i=0;i<str.length();i++)
        {
            if(str.charAt(i) =='\"')
            {
                counter++;
            }
        }

        if (counter == 2)
        {

        }
        else if(counter == 4)
        {
            Scanner s = new Scanner(str).useDelimiter("\"");
            s.next();
            s.next();
            s.next();
            String temp = s.next();

            //Toast.makeText(getBaseContext(), temp, Toast.LENGTH_LONG).show();
            s.close();
            Intent notificationIntent = new Intent(this, MainApplication.class);
            testMessage(temp, notificationIntent, 1);
        }
        else if(counter == 6)
        {
            Scanner s = new Scanner(str).useDelimiter("\"");
            s.next();
            s.next();
            s.next();
            String temp = s.next();
            s.next();
            String temp2 = s.next();

            //Toast.makeText(getBaseContext(), temp, Toast.LENGTH_LONG).show();

            //Toast.makeText(getBaseContext(), temp2, Toast.LENGTH_LONG).show();
            Intent notificationIntent1 = new Intent(this, MainApplication.class);
            testMessage(temp, notificationIntent1, 1);
            Intent notificationIntent2 = new Intent(this, MainApplication.class);
            testMessage(temp2, notificationIntent2, 2);
            s.close();
        }
        else if(counter == 8 || counter > 8)
        {
            Scanner s = new Scanner(str).useDelimiter("\"");
            s.next();
            s.next();
            s.next();
            String temp = s.next();
            s.next();
            String temp2 = s.next();
            s.next();
            String temp3 = s.next();

            //Toast.makeText(getBaseContext(), temp, Toast.LENGTH_LONG).show();

            //Toast.makeText(getBaseContext(), temp2, Toast.LENGTH_LONG).show();

            //Toast.makeText(getBaseContext(), temp3, Toast.LENGTH_LONG).show();
            Intent notificationIntent1 = new Intent(this, MainApplication.class);
            testMessage(temp, notificationIntent1, 1);
            Intent notificationIntent2 = new Intent(this, MainApplication.class);
            testMessage(temp2, notificationIntent2, 2);
            Intent notificationIntent3 = new Intent(this, MainApplication.class);
            testMessage(temp3, notificationIntent3, 3);
            s.close();
        }

        //Toast.makeText(getBaseContext(), Integer.toString(counter), Toast.LENGTH_LONG).show();

        /*String temp;
        String temp2;
        String ss = "{\"ad\":[]}";
        if(!str.isEmpty())
        {
            Scanner s = new Scanner(str).useDelimiter("\"");
            s.next();
            s.next();
            s.next();
            temp = s.next();
            s.next();
            temp2 = s.next();
            Intent notificationIntent = new Intent(this, MainApplication.class);
            testMessage(temp, notificationIntent);
        }
        else if(str == ss) {
            temp = "";
            temp2 = "";
        }
        else
        {
            temp = "";
            temp2 = "";
        }*/
        new MyAsyncTask().execute(request);

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {

        /******** Called when User on Gps  *********/

        Toast.makeText(getBaseContext(), "Gps turned on ", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderDisabled(String provider) {

        /******** Called when User off Gps *********/

        Toast.makeText(getBaseContext(), "Gps turned off ", Toast.LENGTH_LONG).show();
    }

    public static String getRestartIntent() {
        return "com.mesibo.sampleapp.restart";
    }

    public static Context getAppContext() {
        return mContext;
    }

    @Override
    public void Mesibo_onRestart() {
        Log.d(TAG, "OnRestart");
        StartUpActivity.newInstance(this, true);
    }

}

