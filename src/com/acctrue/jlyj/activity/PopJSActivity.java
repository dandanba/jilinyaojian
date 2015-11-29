package com.acctrue.jlyj.activity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.acctrue.jlyj.R;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

public class PopJSActivity extends Activity {

	private double zl;
	
	private TextView jsTV;
	private EditText factInEditText;
	private TextView jsTV_1;
	
	private Bundle bundle;
	private int sureCount;
	
	private Switch methodSwitch;
	
	private boolean isYB;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pop_js);
		
		sureCount = 0;
		
		zl=0;
		
		isYB = false;
		
		jsTV = (TextView)this.findViewById(R.id.js_tv);
		jsTV_1 = (TextView)this.findViewById(R.id.js_tv_1);
		
		factInEditText = (EditText)this.findViewById(R.id.fact_in_edit_text);
		factInEditText.setSingleLine(true);
		
		bundle=getIntent().getExtras();
		
		
		methodSwitch = (Switch)this.findViewById(R.id.method_switch);
		methodSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					
					System.out.println("打开状态");
					
					isYB = true;
				}
				else{
					
					System.out.println("关闭状态");
					
					isYB = false;
					
					
				}
				
			}
		});
		
		
		String title = "单号："+bundle.getString("batchNum")+"\n\n";
		
		String money = "总计："+bundle.getString("Money")+"\n";
		
		
		jsTV.setText(title+money);
		
		factInEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					factInEditText.setText("");
					jsTV_1.setText("找零:-----");
					
				}
				else
				{
					zl = Double.parseDouble(factInEditText.getText().toString())-Double.parseDouble(bundle.getString("Money"));
					jsTV_1.setText("找零:"+ String.valueOf(zl));
					
				}
			}
		});
		
		//屏蔽软键盘
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		Method setShowSoftInputOnFocus = null;
		try {
			setShowSoftInputOnFocus = factInEditText.getClass().getMethod(
					"setShowSoftInputOnFocus", boolean.class);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(setShowSoftInputOnFocus!=null){
			setShowSoftInputOnFocus.setAccessible(true);
		}
		try {
			setShowSoftInputOnFocus.invoke(factInEditText, false);
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pop_j, menu);
		return true;
	}

	
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		// 
		if (event.getKeyCode() == 76) {
			
			if (sureCount%2==0) {
				
				this.inputOver();
				
				return true;
			}
			else {
				sureCount++;
				return true;
			}
			
			
		}
		return super.dispatchKeyEvent(event);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode==KeyEvent.KEYCODE_BACK) {
			
			Intent intent=new Intent(this,SaleOutActivity.class);
			intent.putExtras(bundle);
			this.setResult(40, intent);
			this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	//输入结束
	public void inputOver()
	{
		
		System.out.println("结束操作");
		
				
		//boudle绑定返回信息
		bundle.putString("infact", factInEditText.getText().toString());
//		bundle.putString("zhaolin", String.valueOf(zl));
		bundle.putString("zhaolin", String.format("%.2f", zl));

		bundle.putBoolean("isYB", isYB);
		
		Intent intent=new Intent(this,SaleOutActivity.class);
		intent.putExtras(bundle);
		this.setResult(30, intent);
		this.finish();
	}
	
}
