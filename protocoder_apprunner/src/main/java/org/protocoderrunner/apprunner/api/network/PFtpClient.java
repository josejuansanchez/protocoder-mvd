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

package org.protocoderrunner.apprunner.api.network;

import android.content.Context;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.apprunner.PInterface;
import org.protocoderrunner.apprunner.api.other.WhatIsRunning;
import org.protocoderrunner.project.ProjectManager;
import org.protocoderrunner.utils.MLog;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

//current source :http://androiddev.orkitra.com/?p=28

public class PFtpClient extends PInterface {

    public static String workDir;

    private FTPClient mFTPClient;
    String TAG = "PFtpClient";
    Boolean isConnected = false;

    public PFtpClient(Context c) {
        super(c);

        WhatIsRunning.getInstance().add(this);
    }

    public interface FtpConnectedCb {
        public void event(boolean connected);
    }


    @ProtoMethod(description = "Connect to a ftp server", example = "")
    @ProtoMethodParam(params = {"host", "port", "username", "password", "function(connected)"})
    public void connect(final String host, final int port, final String username, final String password, final FtpConnectedCb callback) {
        mFTPClient = new FTPClient();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mFTPClient.connect(host, port);

                    MLog.d(TAG, "1");

                    if (FTPReply.isPositiveCompletion(mFTPClient.getReplyCode())) {
                        boolean logged = mFTPClient.login(username, password);
                        mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
                        mFTPClient.enterLocalPassiveMode();
                        isConnected = logged;

                        callback.event(logged);
                    }
                    MLog.d(TAG, "" + isConnected);

                } catch (Exception e) {
                    MLog.d(TAG, "connection failed error:" + e);
                }
            }
        });
        t.start();
    }

    public interface GetCurrentDirCb {
        public void event(String msg);
    }


    @ProtoMethod(description = "Get the current directory", example = "")
    @ProtoMethodParam(params = {""})
    public void getCurrentDir(final GetCurrentDirCb callback) {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MLog.d(TAG, "" + mFTPClient);
                    String workingDir = mFTPClient.printWorkingDirectory();
                    callback.event(workingDir);
                } catch (Exception e) {
                    MLog.d(TAG, "Error: could not get current working directory. " + e);
                }

            }
        });
        t.start();

    }

    public interface ChangeDirectoryCb {
        public void event(boolean msg);
    }


    @ProtoMethod(description = "Change the directory", example = "")
    @ProtoMethodParam(params = {"dirname"})
    public void changeDir(final String directory_path, final ChangeDirectoryCb callback) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    callback.event(mFTPClient.changeWorkingDirectory(directory_path));
                } catch (Exception e) {
                    MLog.d(TAG, "Error:" + e);
                }
            }
        });
        t.start();
    }


    public interface GetFileListCb {
        public void event(ArrayList<ListDir> msg);
    }

    class ListDir {
        public String type;
        public String name;
    }


    @ProtoMethod(description = "Get list of files in the given dir", example = "")
    @ProtoMethodParam(params = {"dirname"})
    public void getFileList(final String dir_path, final GetFileListCb callback) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<ListDir> list = new ArrayList<ListDir>();
                try {
                    FTPFile[] ftpFiles = mFTPClient.listFiles(dir_path);
                    int length = ftpFiles.length;

                    for (int i = 0; i < length; i++) {
                        String name = ftpFiles[i].getName();
                        boolean isFile = ftpFiles[i].isFile();

                        ListDir listDir = new ListDir();
                        if (isFile) {
                            listDir.type = "file";
                        } else {
                            listDir.type = "dir";
                        }
                        listDir.name = name;
                        list.add(listDir);
                    }
                    callback.event(list);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    public interface DownloadFiletCb {
        public void event(boolean msg);
    }


    @ProtoMethod(description = "Download the file", example = "")
    @ProtoMethodParam(params = {"sourceFilePath", "destinyFilePath"})
    public void download(final String srcFilePath, final String destiny, final DownloadFiletCb callback) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean status = false;
                String desFilePath = ProjectManager.getInstance().getCurrentProject().getStoragePath() + "/" + destiny;

                try {
                    FileOutputStream desFileStream = new FileOutputStream(desFilePath);

                    status = mFTPClient.retrieveFile(srcFilePath, desFileStream);
                    callback.event(status);

                    desFileStream.close();
                } catch (Exception e) {
                    MLog.d(TAG, "download failed error:" + e);
                }
            }
        });
        t.start();
    }

    public interface UploadCb {
        public void event(boolean msg);
    }


    @ProtoMethod(description = "Upload a file", example = "")
    @ProtoMethodParam(params = {"sourceFilePath", "fileName", "destinyPath"})
    public void upload(final String source, final String desFileName, String desDirectory, final UploadCb callback) {
        boolean status = false;

        final String srcFilePath = ProjectManager.getInstance().getCurrentProject().getStoragePath() + "/" + source;

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isConnected) {
                    try {
                        MLog.d(TAG, "waiting for connection");
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    FileInputStream srcFileStream = new FileInputStream(srcFilePath);
                    boolean status = mFTPClient.storeFile(desFileName, srcFileStream);
                    srcFileStream.close();
                    callback.event(status);
                } catch (Exception e) {
                    MLog.d(TAG, "upload failed" + e);
                }
            }
        });
        t.start();

    }

    public interface DeleteFileCb {
        public void event(boolean msg);
    }


    @ProtoMethod(description = "Delete a file", example = "")
    @ProtoMethodParam(params = {"filename", "function(boolean)"})
    public void deleteFile(final String filename, final DeleteFileCb callback) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                if (isConnected) {
                    try {
                        boolean deleted = mFTPClient.deleteFile(filename);
                        callback.event(deleted);
                    } catch (IOException e) {
                        e.printStackTrace();
                        callback.event(false);
                    }
                }
            }
        });
        t.start();
    }

    public interface DisconnectCb {
        public void event(Boolean msg);
    }


    @ProtoMethod(description = "Disconnect from server", example = "")
    @ProtoMethodParam(params = {""})
    public void disconnect(final DisconnectCb callback) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                MLog.d(TAG, "" + isConnected);
                if (isConnected) {
                    try {
                        mFTPClient.logout();
                        mFTPClient.disconnect();
                    } catch (Exception e) {

                    }
                    isConnected = false;

                    if (callback != null)
                        callback.event(isConnected);
                }
            }
        });
        t.start();
    }

    public void stop() {
        disconnect(null);
    }
}
