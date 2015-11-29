package com.acctrue.jlyj.activity;

import com.acctrue.jlyj.R;
import com.acctrue.jlyj.event.ClickEvent;

import de.greenrobot.event.EventBus;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class OtherMainActivity extends Activity {

	// public ImageButton thirdBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_othermain);

		// thirdBtn = (ImageButton) this.findViewById(R.id.third_btn);

		// Bundle bundle = this.getIntent().getExtras();
		// int CorpType = bundle.getInt("CorpType");
		// if (CorpType==1) {
		// thirdBtn.setVisibility(View.GONE);
		// }
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
}
