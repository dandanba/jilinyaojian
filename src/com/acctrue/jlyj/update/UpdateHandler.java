package com.acctrue.jlyj.update;

import com.acctrue.jlyj.activity.NFCActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class UpdateHandler extends Handler{
	NFCActivity _nfcActivity;
	
	UpdateHandler(NFCActivity activity)
	{
		_nfcActivity = activity;
	}
	
	@Override
	public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
		super.handleMessage(msg);
		
		Object obj = msg.obj;
		if(obj != null)
		{
			UpdateResult result = (UpdateResult) msg.obj;
			_nfcActivity.CellUpdate(result,_nfcActivity);
		}
		else
		{
			Bundle bundle = msg.getData(); 
			String message = bundle.getString("error");
			_nfcActivity.CellResult(message);
		}
	}
}
