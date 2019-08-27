package com.p200asdk.demo.fragment;

import com.p200asdk.demo.MainActivity;
import com.p200asdk.demo.R;
import com.p200asdk.demo.RawDataActivity;
import com.sleepace.sdk.interfs.IConnectionStateCallback;
import com.sleepace.sdk.interfs.IDeviceManager;
import com.sleepace.sdk.interfs.IMonitorManager;
import com.sleepace.sdk.interfs.IResultCallback;
import com.sleepace.sdk.manager.CONNECTION_STATE;
import com.sleepace.sdk.manager.CallbackData;
import com.sleepace.sdk.manager.DeviceType;
import com.sleepace.sdk.p200a.domain.RealTimeData;
import com.sleepace.sdk.p200a.util.SleepStatusType;
import com.sleepace.sdk.util.SdkLog;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class ControlFragment extends BaseFragment {
	
	private Button btnStartRealtimeData, btnStopRealtimeData, btnSignal;
	private TextView tvSleepStatus, tvHeartRate, tvBreathRate, tvTemp, tvHum;
	private View envirView;
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_control, null);
//		SdkLog.log(TAG+" onCreateView-----------");
		findView(view);
		initListener();
		initUI();
		return view;
	}
	
	
	protected void findView(View root) {
		// TODO Auto-generated method stub
		super.findView(root);
		btnStartRealtimeData = (Button) root.findViewById(R.id.btn_realtime_data);
		btnStopRealtimeData = (Button) root.findViewById(R.id.btn_stop_realtime_data);
		btnSignal = (Button) root.findViewById(R.id.btn_signal_strength);
		
		tvSleepStatus = (TextView) root.findViewById(R.id.tv_sleep_status);
		tvHeartRate = (TextView) root.findViewById(R.id.tv_heartrate);
		tvBreathRate = (TextView) root.findViewById(R.id.tv_breathrate);
		envirView = root.findViewById(R.id.layout_envir_data);
		tvTemp = (TextView) root.findViewById(R.id.tv_temp);
		tvHum = (TextView) root.findViewById(R.id.tv_hum);
	}


	protected void initListener() {
		// TODO Auto-generated method stub
		super.initListener();
		getP200AHelper().addConnectionStateCallback(stateCallback);
		btnStartRealtimeData.setOnClickListener(this);
		btnStopRealtimeData.setOnClickListener(this);
		btnSignal.setOnClickListener(this);
	}


	protected void initUI() {
		// TODO Auto-generated method stub
		mActivity.setTitle(R.string.control_);
		if(mActivity.getDevice() != null) {
			if(DeviceType.isP3(mActivity.getDevice().getDeviceType())) {
				envirView.setVisibility(View.VISIBLE);
			}else {
				envirView.setVisibility(View.GONE);
			}
		}else {
			envirView.setVisibility(View.GONE);
		}
	}
	
	private void initBtnState(boolean connect) {
		if(connect) {
			if(MainActivity.realtimeDataOpen) {
				btnStartRealtimeData.setEnabled(false);
				btnStopRealtimeData.setEnabled(true);
			}else {
				btnStartRealtimeData.setEnabled(true);
				btnStopRealtimeData.setEnabled(false);
			}
			btnSignal.setEnabled(true);
		}else {
			btnStartRealtimeData.setEnabled(false);
			btnStopRealtimeData.setEnabled(false);
			btnSignal.setEnabled(false);
		}
	}
	
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initBtnState(getP200AHelper().isConnected());
		SdkLog.log(TAG+" onResume realtimeDataOpen:" + MainActivity.realtimeDataOpen);
	}
	
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		SdkLog.log(TAG+" onDestroyView----------------");
		getP200AHelper().removeConnectionStateCallback(stateCallback);
	}
	
	
	private IConnectionStateCallback stateCallback = new IConnectionStateCallback() {
		@Override
		public void onStateChanged(IDeviceManager manager, final CONNECTION_STATE state) {
			// TODO Auto-generated method stub
			mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					
					if(!isFragmentVisible()){
						return;
					}
					
					if(state == CONNECTION_STATE.DISCONNECT){
						initBtnState(false);
						tvSleepStatus.setText(null);
						tvHeartRate.setText(null);
						tvBreathRate.setText(null);
						tvTemp.setText(null);
						tvHum.setText(null);
					}else if(state == CONNECTION_STATE.CONNECTED){
						initBtnState(true);
					}
				}
			});
		}
	};
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		if(v == btnStartRealtimeData){
			SdkLog.log(TAG+" startRealtime realtimeDataOpen:" + MainActivity.realtimeDataOpen);
			printLog(R.string.view_data);
			getP200AHelper().startRealTimeData(1000, realtimeCB);
		}else if(v == btnStopRealtimeData){
			printLog(R.string.off_data);
			getP200AHelper().stopRealTimeData(1000, new IResultCallback<Void>() {
				@Override
				public void onResultCallback(final CallbackData<Void> cd) {
					// TODO Auto-generated method stub
					mActivity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							if(!isFragmentVisible()){
								return;
							}
							
							if(checkStatus(cd)){
								//printLog(R.string.stop_data_successfully);
								MainActivity.realtimeDataOpen = false;
								initBtnState(true);
								tvSleepStatus.setText("--");
								tvHeartRate.setText("--");
								tvBreathRate.setText("--");
								tvTemp.setText("--");
								tvHum.setText("--");
							}
						}
					});
				}
			});
		}else if(v == btnSignal){
			Intent intent = new Intent(mActivity, RawDataActivity.class);
        	startActivity(intent);
		}
	}
	

	 private IResultCallback<RealTimeData> realtimeCB = new IResultCallback<RealTimeData>() {
			@Override
			public void onResultCallback(final CallbackData<RealTimeData> cd) {
				// TODO Auto-generated method stub
//				SdkLog.log(TAG+" realtimeCB cd:" + cd +",isAdd:" + isAdded());
				mActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if(!isFragmentVisible()){
							return;
						}
						
						if(checkStatus(cd)){
							if(cd.getCallbackType() == IMonitorManager.METHOD_REALTIME_DATA_OPEN){
								MainActivity.realtimeDataOpen = true;
								initBtnState(true);
							}else if(cd.getCallbackType() == IMonitorManager.METHOD_REALTIME_DATA){//实时数据
								btnStartRealtimeData.setEnabled(false);
								btnStopRealtimeData.setEnabled(true);
								btnSignal.setEnabled(true);
								
								RealTimeData realTimeData = cd.getResult();
								int sleepStatus = realTimeData.getStatus();
								int statusRes = getSleepStatus(sleepStatus);
								if(statusRes > 0){
									tvSleepStatus.setText(statusRes);
								}else{
									tvSleepStatus.setText(null);
								}
								
								if(sleepStatus == SleepStatusType.SLEEP_INIT || sleepStatus == SleepStatusType.SLEEP_LEAVE){//离床
									tvHeartRate.setText("--");
									tvBreathRate.setText("--");
								}else{
									tvHeartRate.setText(realTimeData.getHeartRate() + getString(R.string.unit_heart));
									tvBreathRate.setText(realTimeData.getBreathRate() + getString(R.string.unit_respiration));
								}
								
								if(sleepStatus == SleepStatusType.SLEEP_INIT) {
									tvTemp.setText("--");
									tvHum.setText("--");
								}else {
									tvTemp.setText(realTimeData.getTemp()/100 + "℃");
									tvHum.setText(realTimeData.getHumidity()+"%");
								}
							}
						}
					}
				});
			}
		};
		
		private int getSleepStatus(int status){
			int statusRes = 0;
			switch(status){
			case SleepStatusType.SLEEP_OK:
				statusRes = R.string.normal;
				break;
			case SleepStatusType.SLEEP_INIT:
				statusRes = R.string.initialization;
				break;
			case SleepStatusType.SLEEP_B_STOP:
				statusRes = R.string.respiration_pause;
				break;
			case SleepStatusType.SLEEP_H_STOP:
				statusRes = R.string.heartbeat_pause;
				break;
			case SleepStatusType.SLEEP_BODYMOVE:
				statusRes = R.string.body_movement;
				break;
			case SleepStatusType.SLEEP_LEAVE:
				statusRes = R.string.out_bed;
				break;
			case SleepStatusType.SLEEP_TURN_OVER:
				statusRes = R.string.label_turn_over;
				break;
			}
			
			return statusRes;
		}
}



