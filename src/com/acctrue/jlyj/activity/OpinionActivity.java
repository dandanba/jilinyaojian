package com.acctrue.jlyj.activity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.acctrue.jlyj.Config;
import com.acctrue.jlyj.R;
import com.acctrue.jlyj.service.commonService;
import com.acctrue.jlyj.util.Constants;

public class OpinionActivity extends Activity {

	private TextView settingTTSTextView;
	private TextView settingCropTextView;
	private TextView userNameTextView;
	private int sureCount;

	// private ImageView personImage;
	// private TextView personInfo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		sureCount = 0;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_opinion);

		// personImage = (ImageView)this.findViewById(R.id.person_image);
		// personImage.setVisibility(View.GONE);
		//
		// personInfo = (TextView)this.findViewById(R.id.person_info);
		//
		//
		//
		// if (commonService.personflag==0) {
		// personImage.setImageResource(R.drawable.no_image);
		// personInfo.setText("姓名：未识别\n"+"单位：未识别\n"+"职称：未识别\n"+"职责：未识别\n");
		// }
		// else{
		// personImage.setImageResource(R.drawable.no_image);
		// personInfo.setText("姓名：未识别\n"+"单位：未识别\n"+"职称：未识别\n"+"职责：未识别\n");
		// }

		settingTTSTextView = (TextView) this
				.findViewById(R.id.setting_tts_editview);
		//settingTTSTextView.setEnabled(false);
		settingCropTextView = (TextView) this
				.findViewById(R.id.setting_cropid_editview);
		//settingCropTextView.setEnabled(false);
		userNameTextView = (TextView) this
				.findViewById(R.id.setting_username_editview);
		//userNameTextView.setEnabled(false);
		SharedPreferences settings = getSharedPreferences("JL_Setting",
				Context.MODE_PRIVATE);
		String ttsServer = settings.getString("cropBelongs", "");
		String crop = settings.getString("cropName", "");
		String userName = settings.getString("userName", "");

		settingTTSTextView.setText(ttsServer);
		settingCropTextView.setText(crop);
		userNameTextView.setText(userName);
		if (!Config.sKeyIgnore) {
			settingTTSTextView.requestFocus();
			// 屏蔽软键盘
			this.getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			Method setShowSoftInputOnFocus = null;
			try {
				setShowSoftInputOnFocus = settingTTSTextView.getClass()
						.getMethod("setShowSoftInputOnFocus", boolean.class);
				setShowSoftInputOnFocus = settingCropTextView.getClass()
						.getMethod("setShowSoftInputOnFocus", boolean.class);
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (setShowSoftInputOnFocus != null) {
				setShowSoftInputOnFocus.setAccessible(true);
			}
			try {
				setShowSoftInputOnFocus.invoke(settingTTSTextView, false);
				setShowSoftInputOnFocus.invoke(settingCropTextView, false);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (!Config.sKeyIgnore) {
			// TODO Auto-generated method stub
			if (event.getKeyCode() == 76) {
				Toast.makeText(this, "配置信息已经修改", Toast.LENGTH_LONG).show();
				commonService.serverUrl = settingTTSTextView.getText()
						.toString();
				commonService.cropCode = settingCropTextView.getText()
						.toString();
				return true;
			}
			// 点击*号键
			if (event.getKeyCode() == 158) {
				Log.i(Constants.msg, "KEYCODE158");
				event = new KeyEvent(event.getDownTime(), event.getEventTime(),
						event.getAction(), KeyEvent.KEYCODE_PERIOD,
						event.getRepeatCount(), event.getMetaState(),
						event.getDeviceId(), event.getScanCode(),
						event.getFlags());
			}
			// // 点击-号键,删除键
			// if (event.getKeyCode() == 156) {
			// event = new KeyEvent(event.getDownTime(), event.getEventTime(),
			// event.getAction(), KeyEvent.KEYCODE_DEL,
			// event.getRepeatCount(), event.getMetaState(),
			// event.getDeviceId(), event.getScanCode(), event.getFlags());
			//
			// }
			if (event.getKeyCode() == 156) {
				// event = new KeyEvent(event.getDownTime(),
				// event.getEventTime(),
				// event.getAction(), KeyEvent.KEYCODE_DEL,
				// event.getRepeatCount(), event.getMetaState(),
				// event.getDeviceId(), event.getScanCode(), event.getFlags());
				return false;

			}

		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
}
