package com.acctrue.jlyj.activity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.acctrue.jlyj.Config;
import com.acctrue.jlyj.R;
import com.acctrue.jlyj.R.layout;
import com.acctrue.jlyj.R.menu;
import com.acctrue.jlyj.util.Constants;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.WindowManager;
import android.widget.EditText;

public class PopAddInfoActivity extends Activity {

	
	private int sureCount;
	private Bundle bundle;
	
	private EditText batchEditText;
	private EditText dateEditText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pop_add_info);

		sureCount=0;
		
		batchEditText = (EditText)this.findViewById(R.id.batch_input_et);
		dateEditText = (EditText)this.findViewById(R.id.date_input_et);

		//屏蔽软键盘
		if(!Config.sKeyIgnore){
				this.getWindow().setSoftInputMode(
						WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
				Method setShowSoftInputOnFocus = null;
				try {
					setShowSoftInputOnFocus = batchEditText.getClass().getMethod(
							"setShowSoftInputOnFocus", boolean.class);
					setShowSoftInputOnFocus = dateEditText.getClass().getMethod(
							"setShowSoftInputOnFocus", boolean.class);
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(setShowSoftInputOnFocus!=null){
					setShowSoftInputOnFocus.setAccessible(true);
				}
				try {
					setShowSoftInputOnFocus.invoke(batchEditText, false);
					setShowSoftInputOnFocus.invoke(dateEditText, false);
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
		bundle=getIntent().getExtras();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pop_add_info, menu);
		return true;
	}
	
	
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		// 点击+号，修改产品价格
		if (event.getKeyCode() == 76) {
			
			if (sureCount%2==1) {
				this.inputOver();
				return true;
			}
			else {
				sureCount++;
				return true;
			}			
		}
		// 点击*号键
				if (event.getKeyCode() == 158) {
					Log.i(Constants.msg, "KEYCODE158");
					event = new KeyEvent(event.getDownTime(), event.getEventTime(),
							event.getAction(), KeyEvent.KEYCODE_PERIOD,
							event.getRepeatCount(), event.getMetaState(),
							event.getDeviceId(), event.getScanCode(), event.getFlags());
				}
				// 点击-号键,删除键
				if (event.getKeyCode() == 156) {
					event = new KeyEvent(event.getDownTime(), event.getEventTime(),
							event.getAction(), KeyEvent.KEYCODE_DEL,
							event.getRepeatCount(), event.getMetaState(),
							event.getDeviceId(), event.getScanCode(), event.getFlags());

				}
		return super.dispatchKeyEvent(event);
	}

	//输入结束
	public void inputOver()
	{
		System.out.println("执行上传出库单操作");
		bundle.putString("productBatchNum", batchEditText.getText().toString());
		bundle.putString("productProduceDate", dateEditText.getText().toString());
		Intent intent=new Intent(this,PurchaseInActivity.class);
		intent.putExtras(bundle);
		this.setResult(20, intent);
		
		this.finish();
	}
	
}
