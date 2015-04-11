/*
* Part of Protocoder http://www.protocoder.org
* A prototyping platform for Android devices 
*
* Copyright (C) 2013 Victor Diaz Barrales victormdb@gmail.com
* 
* Protocoder is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Protocoder is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU Lesser General Public License
* along with Protocoder. If not, see <http://www.gnu.org/licenses/>.
*/

package org.protocoder;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.FileObserver;
import android.os.IBinder;
import android.util.Log;
import android.view.ViewConfiguration;

import org.protocoder.appApi.Protocoder;
import org.protocoderrunner.apprunner.AppRunnerContext;
import org.protocoderrunner.events.Events;
import org.protocoderrunner.events.Events.ProjectEvent;
import org.protocoderrunner.network.IDEcommunication;
import org.protocoderrunner.project.Project;
import org.protocoderrunner.project.ProjectManager;
import org.protocoderrunner.utils.AndroidUtils;
import org.protocoderrunner.utils.MLog;

import java.lang.reflect.Field;

import de.greenrobot.event.EventBus;

@SuppressLint("NewApi")
public class MainService extends Service {

    private static final String TAG = "MainService";

    MainService mContext;

    // file observer
    private FileObserver fileObserver;
    // connection change listener
    private ConnectivityChangeReceiver connectivityChangeReceiver;

    BroadcastReceiver mStopServerReceiver;

    //singleton that controls protocoder
    private Protocoder mProtocoder;


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

        Log.d(TAG, "onCreate");

        mProtocoder = Protocoder.getInstance(this);
        mProtocoder.init();

        // Get and store the application context
        AppRunnerContext.get().init(getApplicationContext());

       /*
        *  Views
        */

        //if (savedInstanceState == null) {
        //    addFragments();
        //} else {
        //    mProtocoder.protoScripts.reinitScriptList();
        //}

        // Check when mContext file is changed in the protocoder dir
        fileObserver = new FileObserver(ProjectManager.FOLDER_USER_PROJECTS, FileObserver.CREATE | FileObserver.DELETE) {

            @Override
            public void onEvent(int event, String file) {
                if ((FileObserver.CREATE & event) != 0) {

                    MLog.d(TAG, "File created [" + ProjectManager.FOLDER_USER_PROJECTS + "/" + file + "]");

                    // check if its mContext "create" and not equal to probe because
                    // thats created every time camera is
                    // launched
                } else if ((FileObserver.DELETE & event) != 0) {
                    MLog.d(TAG, "File deleted [" + ProjectManager.FOLDER_USER_PROJECTS + "/" + file + "]");

                }
            }
        };

        connectivityChangeReceiver = new ConnectivityChangeReceiver();

        // ***** onResume code ******
        Log.d(TAG, "onResume");

        MLog.d(TAG, "Registering as an EventBus listener in MainService");

        // TEST
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        mStopServerReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                mProtocoder.app.killConnections();
            }
        };

        registerReceiver(connectivityChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        mProtocoder.app.startServers();
        IntentFilter filterSend = new IntentFilter();
        filterSend.addAction("org.protocoder.intent.action.STOP_SERVER");
        registerReceiver(mStopServerReceiver, filterSend);
        fileObserver.startWatching();

        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");

            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            // presumably, not relevant
        }

        IDEcommunication.getInstance(this).ready(false);



    }

    /**
     * onDestroy
     */
    @Override
    public void onDestroy() {
        super.onDestroy();


        Log.d(TAG, "onDestroy");

        // TEST
        //EventBus.getDefault().unregister(this);

        unregisterReceiver(mStopServerReceiver);
        fileObserver.stopWatching();
        unregisterReceiver(connectivityChangeReceiver);

        mProtocoder.app.killConnections();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "onStartCommand");

        // If we get killed, after returning from here, restart
        //return START_NOT_STICKY;
        return START_STICKY;
    }

    // TODO call intent and kill it in an appropiate way
    public void onEventMainThread(ProjectEvent evt) {
        // Using transaction so the view blocks
        MLog.d(TAG, "event -> " + evt.getAction());

        if (evt.getAction() == "run") {
            Project p = evt.getProject();
            mProtocoder.protoScripts.run(p.getFolder(), p.getName());
        } else if (evt.getAction() == "save") {
            Project p = evt.getProject();
            //mProtocoder.protoScripts.refresh(p.getFolder(), p.getName());
        } else if (evt.getAction() == "new") {
            MLog.d(TAG, "creating new project " + evt.getProject().getName());
            mProtocoder.protoScripts.createProject("projects", evt.getProject().getName());
        } else if (evt.getAction() == "update") {
            mProtocoder.protoScripts.listRefresh();
        }
    }


    // execute lines
    public void onEventMainThread(Events.ExecuteCodeEvent evt) {
        String code = evt.getCode();
        MLog.d(TAG, "event -> " + code);

        //TODO apprunner
        // if (debugApp) {
        //     interp.eval(code);
        // }
    }

    // check if connection has changed
    public class ConnectivityChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            AndroidUtils.debugIntent("connectivityChangerReceiver", intent);
            mProtocoder.app.startServers();
        }
    }
}
