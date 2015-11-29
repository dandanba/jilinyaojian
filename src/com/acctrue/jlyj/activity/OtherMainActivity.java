package com.acctrue.jlyj.activity;

import com.acctrue.jlyj.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class OtherMainActivity extends Activity{
	
	public ImageButton thirdBtn;
	
@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.layout_othermain);
	
	thirdBtn = (ImageButton)this.findViewById(R.id.third_btn);
	
//	Bundle bundle = this.getIntent().getExtras();
//	int CorpType = bundle.getInt("CorpType");
//	if (CorpType==1) {
//		thirdBtn.setVisibility(View.GONE);
//	}
}
}
