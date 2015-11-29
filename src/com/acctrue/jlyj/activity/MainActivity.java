package com.acctrue.jlyj.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.acctrue.jlyj.R;
import com.acctrue.jlyj.event.ClickEvent;

import de.greenrobot.event.EventBus;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_main);
		// TODO …Ë÷√º‡Ã˝ ≤¢«“∑¢ÀÕEvent

	}

	public void key7(View view) {
		final ClickEvent event = new ClickEvent();
		event.keyCode = KeyEvent.KEYCODE_0;
		EventBus.getDefault().post(event);
	}

	public void key1(View view) {
		final ClickEvent event = new ClickEvent();
		event.keyCode = KeyEvent.KEYCODE_1;
		EventBus.getDefault().post(event);
	}

	public void key2(View view) {
		final ClickEvent event = new ClickEvent();
		event.keyCode = KeyEvent.KEYCODE_2;
		EventBus.getDefault().post(event);
	}

	public void key3(View view) {
		final ClickEvent event = new ClickEvent();
		event.keyCode = KeyEvent.KEYCODE_3;
		EventBus.getDefault().post(event);
	}

	public void key4(View view) {
		final ClickEvent event = new ClickEvent();
		event.keyCode = KeyEvent.KEYCODE_4;
		EventBus.getDefault().post(event);
	}

	public void key5(View view) {
		final ClickEvent event = new ClickEvent();
		event.keyCode = KeyEvent.KEYCODE_5;
		EventBus.getDefault().post(event);
	}

	public void key6(View view) {
		final ClickEvent event = new ClickEvent();
		event.keyCode = KeyEvent.KEYCODE_6;
		EventBus.getDefault().post(event);
	}

}
