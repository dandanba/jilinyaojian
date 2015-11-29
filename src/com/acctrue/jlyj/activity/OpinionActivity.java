package com.acctrue.jlyj.activity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.acctrue.jlyj.Config;
import com.acctrue.jlyj.R;
import com.acctrue.jlyj.service.commonService;
import com.acctrue.jlyj.util.Constants;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class OpinionActivity extends Activity {
	
	private EditText settingTTSEditText;
	private EditText settingCropEditText;
	private EditText userNameEditText;
	private int sureCount;
	
//	private ImageView personImage;
//	private TextView  personInfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		sureCount=0;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_opinion);
		
//		personImage = (ImageView)this.findViewById(R.id.person_image);
//		personImage.setVisibility(View.GONE);
//		
//		personInfo = (TextView)this.findViewById(R.id.person_info);
//		
//		
//		
//		if (commonService.personflag==0) {
//			personImage.setImageResource(R.drawable.no_image);
//			personInfo.setText("������δʶ��\n"+"��λ��δʶ��\n"+"ְ�ƣ�δʶ��\n"+"ְ��δʶ��\n");
//		}
//		else{
//			personImage.setImageResource(R.drawable.no_image);
//			personInfo.setText("������δʶ��\n"+"��λ��δʶ��\n"+"ְ�ƣ�δʶ��\n"+"ְ��δʶ��\n");
//		}
		
		settingTTSEditText = (EditText)this.findViewById(R.id.setting_tts_editview);
		settingTTSEditText.setEnabled(false);
		settingCropEditText = (EditText)this.findViewById(R.id.setting_cropid_editview);
		settingCropEditText.setEnabled(false);
		userNameEditText = (EditText)this.findViewById(R.id.setting_username_editview);
		userNameEditText.setEnabled(false);
		SharedPreferences settings = getSharedPreferences("JL_Setting", Context.MODE_PRIVATE); 
		String ttsServer = settings.getString("cropBelongs", "");
		String crop = settings.getString("cropName", "");
		String userName = settings.getString("userName", "");
		
		settingTTSEditText.setText(ttsServer);
		settingCropEditText.setText(crop);
		userNameEditText.setText(userName);
		settingTTSEditText.requestFocus();
		if(!Config.sKeyIgnore){
		//���������
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		Method setShowSoftInputOnFocus = null;
		try {
			setShowSoftInputOnFocus = settingTTSEditText.getClass().getMethod(
					"setShowSoftInputOnFocus", boolean.class);
			setShowSoftInputOnFocus = settingCropEditText.getClass().getMethod(
					"setShowSoftInputOnFocus", boolean.class);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(setShowSoftInputOnFocus!=null){
			setShowSoftInputOnFocus.setAccessible(true);
		}
		try {
			setShowSoftInputOnFocus.invoke(settingTTSEditText, false);
			setShowSoftInputOnFocus.invoke(settingCropEditText, false);
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
		// TODO Auto-generated method stub
		if (event.getKeyCode() == 76) {
				Toast.makeText(this, "������Ϣ�Ѿ��޸�", Toast.LENGTH_LONG).show();
				commonService.serverUrl = settingTTSEditText.getText().toString();
				commonService.cropCode = settingCropEditText.getText().toString();
				return true;		
		}
		// ���*�ż�
				if (event.getKeyCode() == 158) {
					Log.i(Constants.msg, "KEYCODE158");
					event = new KeyEvent(event.getDownTime(), event.getEventTime(),
							event.getAction(), KeyEvent.KEYCODE_PERIOD,
							event.getRepeatCount(), event.getMetaState(),
							event.getDeviceId(), event.getScanCode(), event.getFlags());
				}
//				// ���-�ż�,ɾ����
//				if (event.getKeyCode() == 156) {
//					event = new KeyEvent(event.getDownTime(), event.getEventTime(),
//							event.getAction(), KeyEvent.KEYCODE_DEL,
//							event.getRepeatCount(), event.getMetaState(),
//							event.getDeviceId(), event.getScanCode(), event.getFlags());
//
//				}
				if (event.getKeyCode() == 156) {
//					event = new KeyEvent(event.getDownTime(), event.getEventTime(),
//							event.getAction(), KeyEvent.KEYCODE_DEL,
//							event.getRepeatCount(), event.getMetaState(),
//							event.getDeviceId(), event.getScanCode(), event.getFlags());
					return false;

				}

		
		return super.dispatchKeyEvent(event);
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
}
