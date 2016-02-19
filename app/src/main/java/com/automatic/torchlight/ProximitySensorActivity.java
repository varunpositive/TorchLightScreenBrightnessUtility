package com.automatic.torchlight;


import android.app.Activity;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

@SuppressWarnings("deprecation")
public class ProximitySensorActivity extends Activity implements SensorEventListener{

    private String TAG = ProximitySensorActivity.class.getSimpleName();
    private SensorManager mSensorManager;
	private Sensor mSensor;
	private boolean isSensorPresent;
	private float distanceFromPhone;
	private Camera mCamera;
	private SurfaceTexture mPreviewTexture;
	private Camera.Parameters mParameters;
	private boolean isFlashLightOn = false;

	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.proximitysensor_layout);
		
		mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
		if(mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null) {
			mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
			isSensorPresent = true;
		} else {
			isSensorPresent = false;
		}

		initCameraFlashLight();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(isSensorPresent) {
			mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if(isSensorPresent) {
			mSensorManager.unregisterListener(this);
		}
	}

	public void initCameraFlashLight()
	{
		mCamera = Camera.open();
		mParameters = mCamera.getParameters();
		mPreviewTexture = new SurfaceTexture(0);
		try {
			mCamera.setPreviewTexture(mPreviewTexture);
		} catch (IOException ex) {
            Log.e(TAG, ex.getLocalizedMessage());
            Toast.makeText(getApplicationContext(),getResources().getText(R.string.error_message), Toast.LENGTH_SHORT).show();
        }
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();

		mSensorManager = null;
		mSensor = null;
		mCamera.release();
		mCamera = null;
	}

	public void turnTorchLightOn()
	{
		mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
		mCamera.setParameters(mParameters);
		mCamera.startPreview();
		isFlashLightOn = true;
	}

	public void turnTorchLightOff()
	{
		mParameters.setFlashMode(mParameters.FLASH_MODE_OFF);
		mCamera.setParameters(mParameters);
		mCamera.stopPreview();
		isFlashLightOn = false;
	}
	
	public void onSensorChanged(SensorEvent event) {

		distanceFromPhone = event.values[0];
		if(distanceFromPhone < mSensor.getMaximumRange()) {
            if(!isFlashLightOn) {
                turnTorchLightOn();
            }
		} else {
            if(isFlashLightOn) {
                turnTorchLightOff();
            }
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

}
