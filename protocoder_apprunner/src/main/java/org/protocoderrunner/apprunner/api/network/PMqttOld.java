///*
//* Part of Protocoder http://www.protocoder.org
//* A prototyping platform for Android devices
//*
//* Copyright (C) 2013 Victor Diaz Barrales victormdb@gmail.com
//*
//* Protocoder is free software: you can redistribute it and/or modify
//* it under the terms of the GNU Lesser General Public License as published by
//* the Free Software Foundation, either version 3 of the License, or
//* (at your option) any later version.
//*
//* Protocoder is distributed in the hope that it will be useful,
//* but WITHOUT ANY WARRANTY; without even the implied warranty of
//* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//* GNU General Public License for more details.
//*
//* You should have received a copy of the GNU Lesser General Public License
//* along with Protocoder. If not, see <http://www.gnu.org/licenses/>.
//*/
//
//
//package org.protocoderrunner.apprunner.api.network;
//
//
//import android.content.Context;
//
//import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
//import org.eclipse.paho.client.mqttv3.MqttCallback;
//import org.eclipse.paho.client.mqttv3.MqttClient;
//import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
//import org.eclipse.paho.client.mqttv3.MqttException;
//import org.eclipse.paho.client.mqttv3.MqttMessage;
//import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
//import org.protocoderrunner.apprunner.PInterface;
//import org.protocoderrunner.apprunner.api.other.WhatIsRunning;
//import org.protocoderrunner.utils.MLog;
//
//public class PMqttOld extends PInterface {
//
//    private final String TAG = PMqttOld.class.getSimpleName();
//
//    String topic        = "MQTT Examples";
//    String content      = "Message from MqttPublishSample";
//    int qos             = 0;
//    String broker       = "tcp://messagesight.demos.ibm.com:1883";
//    String clientId     = "ProtoClient";
//    MemoryPersistence persistence = new MemoryPersistence();
//    private MqttClient mSampleClient;
//
//    public PMqttOld(Context c) {
//        super(c);
//
//        WhatIsRunning.getInstance().add(this);
//    }
//
//    public PMqttOld connect() {
//        mSampleClient = null;
//        try {
//            mSampleClient = new MqttClient(broker, clientId, persistence);
//        } catch (MqttException e) {
//            e.printStackTrace();
//            MLog.d(TAG, "Cannot create instance");
//        }
//        MqttConnectOptions connOpts = new MqttConnectOptions();
//        connOpts.setCleanSession(true);
//        MLog.d(TAG, "Connecting to broker: " + broker);
//        try {
//            mSampleClient.connect(connOpts);
//        } catch (MqttException e) {
//            e.printStackTrace();
//            MLog.d(TAG, "Cannot connect");
//        }
//        MLog.d(TAG, "Connected");
//
//        return this;
//    }
//
//    public PMqttOld subscribe() {
//        String subscription = "planets/earth";
//      //  mSampleClient.subscribe(subscription, qos);
//
//        return this;
//    }
//
//
//    public interface OnNewDataCallback {
//        void event(String data);
//    }
//    public PMqttOld onNewData(OnNewDataCallback onNewDataCallback) {
//        onNewDataCallback.event("qq");
//        mSampleClient.setCallback(new MqttCallback() {
//            @Override
//            public void connectionLost(Throwable throwable) {
//                MLog.d(TAG, "Connection lost");
//            }
//
//            @Override
//            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
//                MLog.d(TAG, "Message arrived");
//            }
//
//            @Override
//            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
//                MLog.d(TAG, "Delivery complete");
//            }
//        });
//
//        return this;
//    }
//
//    public PMqttOld send() {
//        MLog.d(TAG, "Publishing message: " + content);
//        MqttMessage message = new MqttMessage(content.getBytes());
//        message.setQos(qos);
//        try {
//            mSampleClient.publish(topic, message);
//        } catch (MqttException e) {
//            e.printStackTrace();
//            MLog.d(TAG, "publish problem");
//
//        }
//        MLog.d(TAG, "Message published");
//
//        return this;
//    }
//
//    public PMqttOld disconnect() {
//        try {
//            mSampleClient.disconnect();
//        } catch (MqttException e) {
//            e.printStackTrace();
//            MLog.d(TAG, "disconnect problem");
//        }
//
//        return this;
//    }
//
//}
