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

import android.content.Context;

public class Protocoder {

    public static Context mContext;
    private static Protocoder instance;

    public App app;
    public ProtoScripts protoScripts;
    //public WebEditor webEditor;
    //public Editor editor;
    public Settings settings;


    //instances of some Protocoder AppRunner objects
    //public PUtil mPUtil = new PUtil(mActivityContext);
    //PUI mPUi = new PUI(mActivityContext);
    //PNetwork mPNetwork = new PNetwork(mActivityContext);
    //PFileIO mPFileIO = new PFileIO(mActivityContext);
    //PMedia mPMedia = new PMedia(mActivityContext);
    //PDevice mPDevice = new PDevice(mActivityContext);
    //PProtocoder mProtocoder = new PProtocoder(mActivityContext);

    //public AppRunnerInterpreter interp;

    private boolean debugApp = false;


    String remoteFile = "";
    String versionName;
    int versionCode;


    Protocoder() {
        settings = new Settings(mContext);

    }

    public void init() {
        app = new App(this);
        protoScripts = new ProtoScripts(this);

    }


    public static Protocoder getInstance(Context context) {
        mContext = context;
        if (instance == null) {
            instance = new Protocoder();
        }

        return instance;
    }


}
