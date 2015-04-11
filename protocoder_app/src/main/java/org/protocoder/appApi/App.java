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
package org.protocoder.appApi;

import android.provider.Settings;
import android.view.View;
import android.view.animation.CycleInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.java_websocket.drafts.Draft_17;
import org.protocoder.network.ProtocoderFtpServer;
import org.protocoder.network.ProtocoderHttpServer;
import org.protocoder.views.Overlay;
import org.protocoderrunner.AppSettings;
import org.protocoderrunner.network.CustomWebsocketServer;
import org.protocoderrunner.network.IDEcommunication;

import java.io.ByteArrayOutputStream;
import java.net.UnknownHostException;

public class App {

    final String TAG = "App";
    private final Protocoder protocoder;

    //Servers
    private ProtocoderHttpServer httpServer;
    private CustomWebsocketServer ws;
    private ProtocoderFtpServer mFtpServer;

    //Views
    private RelativeLayout mainAppView;
    private TextView textIP;
    private LinearLayout mIpContainer;
    protected int textIPHeight;
    public Overlay overlay;

    public Editor editor;

    int usbEnabled = 0;

    App(Protocoder protocoder) {
        editor = new Editor(protocoder);
        this.protocoder = protocoder;

        init();
    }


    public void init() {



//        overlay = new Overlay(protocoder.mContext);
//        overlay.setLayoutParams(new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.MATCH_PARENT));
//        mainAppView.addView(overlay);
//
//        try {
//            overlay.setFrame();
//        } catch (Exception e) {
//            e.printStackTrace();
//            MLog.d("qq", e.getStackTrace().toString());
//        }
    }


    public void showNumberConections() {

    }

    public void showNetworkBottomInfo(boolean show) {
        if (show) {
            //textIP.setVisibility(View.VISIBLE);
        } else {
            //textIP.setVisibility(View.GONE);
        }
    }

    public void showNetworkDetails(boolean show) {
        if (show) {

        } else {

        }
    }

    //when there is mContext some data transfer
    public void showNetworkProgress(boolean show) {
        if (show) {

        } else {

        }
    }

    public void showLibrariesRepo(boolean show) {
        if (show) {

        } else {

        }
    }

    public void showLibaries(boolean show) {
        if (show) {

        } else {

        }
    }

    public void highlight(String color) {
        overlay.setFrame();
    }

    public void vibrate(int time) {
        //protocoder.pDevice.vibrate(time);
    }

    public void shake() {
        View v = (View) mainAppView.getParent().getParent();
        v.animate().rotation(10).translationX(100).setDuration(1000).setInterpolator(new CycleInterpolator(1)).start();
    }

    //"noise", "blipy", "hipster", "color"
    public void mode(String mode) {

    }

    public void close() {
//        protocoder.mCon.superMegaForceKill();
    }

    public void restart() {

    }

    public void setIp(String s) {
        textIP.setText(s);
    }


    /**
     * Starts the remote service connection
     */
    public boolean startServers() {

        // check if usb is enabled
        usbEnabled = Settings.Secure.getInt(protocoder.mContext.getContentResolver(), Settings.Secure.ADB_ENABLED, 0);

        // start webserver
        httpServer = ProtocoderHttpServer.getInstance(protocoder.mContext.getApplicationContext(), AppSettings.HTTP_PORT);

        // websocket
        try {
            ws = CustomWebsocketServer.getInstance(protocoder.mContext, AppSettings.WEBSOCKET_PORT, new Draft_17());
            IDEcommunication.getInstance(protocoder.mContext).ready(false);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }



        ByteArrayOutputStream out = new ByteArrayOutputStream();

        //MLog.d(TAG, "qq" + NetworkUtils.getLocalIpAddress(protocoder.mContext));

        if (httpServer != null) {// If no instance of HTTPServer, we set the IP
            // address view to gone.
            showNetworkBottomInfo(true);
        } else {
            showNetworkBottomInfo(false);
        }

        if (protocoder.settings.getFtpChecked()) {
            mFtpServer = ProtocoderFtpServer.getInstance(protocoder.mContext, AppSettings.FTP_PORT);
            if (!mFtpServer.isStarted()) {
                mFtpServer.startServer();
            }
        }

        return true;

    }

    /**
     * Unbinds service and stops the servers
     */
    // TODO add stop websocket
    public void killConnections() {
        if (httpServer != null) {
            httpServer.close();
            httpServer = null;
        }


        if (mFtpServer != null) {
            mFtpServer.stopServer();
        }
    }

    //showPopUp={true, false}
    public void checkNewVersion() {

    }

    //JSON
    public void sendDeviceStats() {

    }

    //JSON
    public void sendCrashStats() {

    }

    public void getListLibraries() {

    }

    public void getListCommunityLibraries() {

    }


}
