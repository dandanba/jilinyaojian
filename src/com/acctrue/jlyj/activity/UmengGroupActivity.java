package com.acctrue.jlyj.activity;

import android.app.ActivityGroup;
import android.os.Bundle;

import com.umeng.analytics.MobclickAgent;

public class UmengGroupActivity extends ActivityGroup {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MobclickAgent.setDebugMode(true);
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
