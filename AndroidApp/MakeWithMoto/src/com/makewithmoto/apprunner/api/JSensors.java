package com.makewithmoto.apprunner.api;

import android.util.Log;
import android.webkit.JavascriptInterface;

import com.makewithmoto.apprunner.AppRunnerActivity;
import com.makewithmoto.sensors.AccelerometerManager;
import com.makewithmoto.sensors.AccelerometerManager.AccelerometerListener;
import com.makewithmoto.sensors.GPSManager;
import com.makewithmoto.sensors.OrientationManager;
import com.makewithmoto.sensors.OrientationManager.OrientationListener;
import com.makewithmoto.sensors.WhatIsRunning;

public class JSensors extends JInterface {

	private AccelerometerManager accelerometerManager;
	private AccelerometerListener accelerometerListener;
	private OrientationManager orientationManager;
	private OrientationListener orientationListener;
	private boolean AccelerometerStarted = false;
	private GPSManager gps;
	private boolean gpsStarted=false;


	public JSensors(AppRunnerActivity mwmActivity) {
		super(mwmActivity);
	}

	@JavascriptInterface

	public void startAccelerometer(final String callbackfn) {
		if(!AccelerometerStarted){
		    accelerometerManager = new AccelerometerManager(a.get());
		    accelerometerListener = new AccelerometerListener() {

			    @Override
			    public void onShake(float force) {

			    }

			    @Override
			    public void onAccelerometerChanged(float x, float y, float z) {
				    	callback(callbackfn,x,y,z);	
			    }
		    };
		    accelerometerManager.addListener(accelerometerListener);
		    accelerometerManager.start();
		    WhatIsRunning.getInstance().add(accelerometerManager);
		    
		    AccelerometerStarted = true;
		}
	}

	@JavascriptInterface
	public void stopAccelerometer() {
		Log.d(TAG, "Called stopAccelerometer");
		if(AccelerometerStarted){
		    accelerometerManager.removeListener(accelerometerListener);
		    accelerometerManager.stop();
		    AccelerometerStarted = false;
		}
	}

	
	
	@JavascriptInterface
	public double getLatitude() {
		
		if(!gpsStarted){
			gpsStarted = true;
	        gps = new GPSManager(a.get());   
		}
	    
	    
	 // check if GPS enabled     
        if(gps.canGetLocation()){
            return gps.getLatitude();
          }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert(); 
        }
        
        return 0;
	}
	    
	@JavascriptInterface
	public double getLongitude() {
		
		if(!gpsStarted){
			gpsStarted = true;
	        gps = new GPSManager(a.get());   
		}
	    
	    
	 // check if GPS enabled     
        if(gps.canGetLocation()){
            return gps.getLongitude();
          }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert(); 
        }
        
        return 0;
	}
	    	
	
	@JavascriptInterface
	public String getCity() {
		
		if(!gpsStarted){
			gpsStarted = true;
	        gps = new GPSManager(a.get());   
		}
	    
	    
	 // check if GPS enabled     
        if(gps.canGetLocation()){
            return gps.getCity();
          }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert(); 
        }
        
        return "";
	}
	       
	
	@JavascriptInterface
	public void stopGPS() {
		if(gpsStarted){
		    gps.stopUsingGPS(); 
		    gpsStarted = false;
		}
	}
	

	
	
	
	
	@JavascriptInterface
	public void startOrientation(final String callback) {
		orientationManager = new OrientationManager(a.get());

		orientationListener = new OrientationListener() {

			@Override
			public void onOrientation(float pitch, float roll, float z) {
				// TODO Auto-generated method stub

			}
		};
		orientationManager.addListener(orientationListener);
		orientationManager.start();
		WhatIsRunning.getInstance().add(orientationManager);

	}

	@JavascriptInterface
	public void stopOrientation() {
		orientationManager.removeListener(orientationListener);
		orientationManager.stop();
	}



	@Override
	public void destroy() {
		
	}

}