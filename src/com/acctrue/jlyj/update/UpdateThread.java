package com.acctrue.jlyj.update;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.acctrue.jlyj.activity.NFCActivity;
import com.acctrue.jlyj.service.commonService;

public class UpdateThread extends Thread{
	NFCActivity _nfcActivity;
	UpdateHandler updateHandler;
	
	public UpdateThread(NFCActivity activity)
	{
		_nfcActivity = activity;
		updateHandler = new UpdateHandler(_nfcActivity);
	}
	
	private String getVersionName() throws Exception {
		// ��ȡpackagemanager��ʵ��
		PackageManager packageManager = _nfcActivity.getPackageManager();
		// getPackageName()���㵱ǰ��İ�����0�����ǻ�ȡ�汾��Ϣ
		PackageInfo packInfo = packageManager.getPackageInfo(
				_nfcActivity.getPackageName(), 0);
		String version = packInfo.versionName;
		return version;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		
		String version;   //�������
		Message msg = updateHandler.obtainMessage();  //hangler�е�һ��json
		Bundle bundle = new Bundle();         //����һ��������
		try {
			version = getVersionName();
			UpdateResult result = check(version);
			if (result.Check()) {
				msg.obj = result;
			} else {
				if (result.getMsg().length() != 0)
					bundle.putString("error", result.getMsg());
			}
		} catch (Exception e) {
			String m =  e.getMessage();
			m = (m == null || m.length() ==0) ? "������ʧ��" : m;
			bundle.putString("error",m);
		}
		msg.setData(bundle);
		updateHandler.sendMessage(msg);
	}
	private UpdateResult check(String version) throws JSONException
	{
		
		UpdateResult result = new UpdateResult();
		result.setCurrentVersion(version);
//		String url = "http://211.137.215.54/TTS/PhoneServices/QyjcService.svc/CheckVersion"+"/token/android" + "/" + version;
		String url = commonService.serverUrl+"/PhoneServices/QyjcService.svc/CheckVersion"+"/token/android" + "/" + version;

		System.out.println(url);
		String json = GetContentFromUrl(url);
		
		if (json.equals(""))
			return result;
		JSONObject jsonObject = new JSONObject(json); 
		if (jsonObject != null) {  
			result.setDownUrl(jsonObject.getString("DownUrl"));  
			result.setContext(jsonObject.getString("UpdateContent"));
			result.setServerVersion(jsonObject.getString("VersionID"));
		}
		
		return result;
	}
	
	private String GetContentFromUrl(String url) {
		
		
		
		String result = "";
		try {
			DefaultHttpClient client = new DefaultHttpClient();
			HttpUriRequest req = new HttpGet(url);
			HttpResponse resp = client.execute(req);
			HttpEntity ent = resp.getEntity();
			int status = resp.getStatusLine().getStatusCode();
			// If the status is equal to 200 ��that is OK
			if (status == HttpStatus.SC_OK) {
				result = EntityUtils.toString(ent);
			}
			client.getConnectionManager().shutdown();
			return result;
		} catch (Exception e) {
			Log.e("NetHelper", "______________��ȡ����ʧ��" + e.toString()
					+ "_____________");
			return "";
		}
	}
}
