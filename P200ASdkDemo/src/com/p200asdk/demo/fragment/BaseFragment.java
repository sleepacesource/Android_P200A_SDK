package com.p200asdk.demo.fragment;

import com.p200asdk.demo.MainActivity;
import com.sleepace.sdk.manager.CallbackData;
import com.sleepace.sdk.p200a.P200AHelper;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public abstract class BaseFragment extends Fragment implements OnClickListener{
	
	protected String TAG = getClass().getSimpleName();
	protected MainActivity mActivity;
	private P200AHelper p200aHelper;
	private boolean isFragmentVisible;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mActivity = (MainActivity) getActivity();
		p200aHelper = P200AHelper.getInstance(mActivity);
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setFragmentVisible(true);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		setFragmentVisible(false);
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
	    super.onHiddenChanged(hidden);
		if(hidden){
			 setFragmentVisible(false);
		} else {
			setFragmentVisible(true);
		}
	}

	public P200AHelper getP200AHelper() {
		return p200aHelper;
	}

	protected void findView(View root) {
		// TODO Auto-generated method stub
	}


	protected void initListener() {
		// TODO Auto-generated method stub
	}


	protected void initUI() {
		// TODO Auto-generated method stub
	}
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

	public void printLog(int strRes){
		
	}
	
	public boolean checkStatus(CallbackData cd){
		return mActivity.checkStatus(cd);
	}

	public boolean isFragmentVisible() {
		if(isAdded()) {
			return isFragmentVisible;
		}
		return false;
	}

	public void setFragmentVisible(boolean isFragmentVisible) {
		this.isFragmentVisible = isFragmentVisible;
	}
	
	
	
}



