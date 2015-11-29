package com.acctrue.jlyj.activity;

import com.acctrue.jlyj.entity.SelectServerResult;

import android.os.Handler;
import android.os.Message;

public class SelectServerHandler extends Handler{
	
	private NFCActivity nfcActivity;
	
	public SelectServerHandler(NFCActivity nfcActivity) {
		// TODO Auto-generated constructor stub
		this.nfcActivity = nfcActivity;
	}
	
	@Override
	public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
		super.handleMessage(msg);
		
		nfcActivity.haveSelectTheServer((SelectServerResult) msg.obj);
		
		
	}

}
