package com.acctrue.jlyj.activity;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.os.Looper;
import android.os.Message;
import com.acctrue.jlyj.entity.SelectServerResult;

public class SelectServerThread extends Thread{
	
	private NFCActivity nfcActivity;
	private SelectServerHandler selectServerHandler;
	
	public SelectServerThread(NFCActivity activity) {
		// TODO Auto-generated constructor stub
		nfcActivity = activity;
		selectServerHandler = new SelectServerHandler(nfcActivity);
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		Looper.prepare();
		//检测网络联通状况，自动选择网络，弹出提示
		String YDUrl = "http://211.137.215.54:8080/tts";			//移动服务地址
//		String YDUrl = "http://192.168.40.90:9090/TTS";			//移动服务地址
//		String YDUrl = "http://test.51lixin.com/jlyj";			//移动服务地址

		String JSCMUrl = "http://10.2.34.3:8080/tts";	//吉视传媒服务地址
				
		boolean isSelected= false;
		
		SelectServerResult reult = new SelectServerResult();
				
		for (int i = 0; i < 2; i++) {
			String url = null;
			if (i==0) {
				url = YDUrl;
			}
			else{
				url=JSCMUrl;
			}
			try
			{
				HttpGet request = new HttpGet(url);

				HttpParams httpParameters = new BasicHttpParams(); 

				HttpConnectionParams.setConnectionTimeout(httpParameters, 3000);
				HttpClient httpClient = new DefaultHttpClient(httpParameters);
			    HttpResponse response = httpClient.execute(request);

				int status = response.getStatusLine().getStatusCode();

				if (status == HttpStatus.SC_OK) 
				{
					isSelected = true;

					if (i==0) {
						
						reult.Server = "http://211.137.215.54:8080/tts";
//						reult.Server = "http://192.168.40.90:9090/TTS";
//						reult.Server = "http://test.51lixin.com/jlyj";			//移动服务地址
						reult.belongTo = "移动通信";
						
						
						break;
					}else{
						reult.Server = "http://10.2.34.3:8080/tts";
						reult.belongTo = "吉视传媒";
						
					}


				}
					 
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		
		reult.isSelected = isSelected;
		
		Message message = new Message();
		message.obj = reult;
		
		selectServerHandler.handleMessage(message);
		Looper.loop();
	}

}
