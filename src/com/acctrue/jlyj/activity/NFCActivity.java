package com.acctrue.jlyj.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.acctrue.jlyj.Config;
import com.acctrue.jlyj.R;
import com.acctrue.jlyj.entity.SelectServerResult;
import com.acctrue.jlyj.entity.UserInfo;
import com.acctrue.jlyj.service.commonService;

import com.acctrue.jlyj.update.UpdateResult;
import com.acctrue.jlyj.update.UpdateThread;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Base64;
import android.widget.Toast;

public class NFCActivity extends Activity {

	ProgressDialog updateProgress;
	ProgressDialog downProgress;

	ProgressDialog selectProgress;

	private Intent intent; // ������ͼ

	PendingIntent pendingIntent; // �ȴ���ͼ
	NfcAdapter nfcAdapter; // nfc������
	String[][] techListsArray; // ֧�ּ����б�
	IntentFilter[] ia; // ��ͼ�������б�

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_load);
		// �������߳��н����������
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		if (Config.sTest) {
			checkTheCardNo("0000000000910");
		} else {

			// ��ʼ��NFC
			initNFC();

			this.selectServer();
		}

	}

	public void selectServer() {
		// �������������Ƿ��и���
		selectProgress = new ProgressDialog(this);
		selectProgress.setMessage("�����Զ�ѡ�����繩Ӧ��");
		selectProgress.setCancelable(false);
		selectProgress.show();

		SelectServerThread sThread = new SelectServerThread(this);
		sThread.start();

	}

	private void initNFC() {
		// ���adapter
		nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		if (nfcAdapter == null) {
			Toast.makeText(this, "�豸��֧��NFC��", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
		if (!nfcAdapter.isEnabled()) {
			Toast.makeText(this, "����ϵͳ������������NFC���ܣ�", Toast.LENGTH_SHORT).show();
			finish();
			return;
		} else {
			Toast.makeText(this, "NFC�ѿ���", Toast.LENGTH_SHORT).show();
		}

		pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);

		ia = new IntentFilter[] { ndef, };

		techListsArray = new String[][] { new String[] { MifareClassic.class
				.getName() } };
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		processIntent(intent);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (!Config.sTest) {
			nfcAdapter.enableForegroundDispatch(this, pendingIntent, ia,
					techListsArray);
		}
	}

	private void processIntent(Intent intent) {
		// ȡ����װ��intent�е�TAG
		Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

		Ndef ndef = Ndef.get(tagFromIntent);
		try {
			ndef.connect();
			NdefMessage message = ndef.getNdefMessage();

			System.out.println(message.toString());

			NdefRecord[] records = message.getRecords();

			byte[] payload = records[0].getPayload();
			String textEncoding = ((payload[0] & 0x80) == 0) ? "UTF-8"
					: "UTF-16";

			String cardNo = new String(payload, textEncoding);

			// У�鿨��
			this.checkTheCardNo(cardNo);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			Toast.makeText(this, "δʶ������", Toast.LENGTH_SHORT).show();

		}

	}

	@SuppressLint("ShowToast")
	public void checkTheCardNo(String CardNo) {

		UserInfo userInfo = null;

		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(commonService.serverUrl
				+ "/Portal/ScanServices/CodeService.svc/GetUser/" + CardNo);
		HttpResponse response;

		try {
			response = client.execute(request);

			int connectStatus = response.getStatusLine().getStatusCode();
			if (connectStatus == HttpStatus.SC_OK) {

				HttpEntity entity = response.getEntity();
				JSONObject object = new JSONObject(EntityUtils.toString(entity,
						"utf-8"));
				userInfo = new UserInfo();
				userInfo.UserName = object.getString("UserName");
				userInfo.UserDisplayName = object.getString("UserDisplayName");
				userInfo.CorpCode = object.getString("CorpCode");
				userInfo.CorpName = object.getString("CorpName");
				userInfo.CropType = object.getInt("CorpType");

				userInfo.Belong = commonService.serverUrl
						.equals("http://211.137.215.54:8080/TTS") ? "�ƶ�ͨ��"
						: "���Ӵ�ý";
				// userInfo.Belong =
				// commonService.serverUrl.equals("http://192.168.40.90:9090/TTS")?"�ƶ�ͨ��":"���Ӵ�ý";
				// userInfo.Belong =
				// commonService.serverUrl.equals("http://test.51lixin.com/jlyj")?"�ƶ�ͨ��":"���Ӵ�ý";

				if (object.getInt("CorpType") == 2) {
					userInfo.CropType = 0;
					this.goIntoOterMenuActiviy(userInfo);
				} else if (object.getInt("CorpType") == 3) {
					if (object.getString("CorpRankText").equals("ҩ���ƶ�")
							|| object.getString("CorpRankText")
									.equals("ҩ�꼪�Ӵ�ý") // add by wanggeng
									|| object.getString("CorpRankText")
									.equals("���Ӵ�ý")) {
						userInfo.CropType = 2;
						this.goIntoMenuActivity(userInfo);

					} else {
						userInfo.CropType = 1;
						this.goIntoOterMenuActiviy(userInfo);
					}

				}
			} else {
				Toast.makeText(this, "��¼ʧ�ܣ�", Toast.LENGTH_SHORT).show();

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(this, "��¼ʧ�ܣ�", Toast.LENGTH_SHORT).show();

		}

		return;
	}

	public void checkSystemUpdate() {
		// �������������Ƿ��и���

	}

	public void goIntoMenuActivity(UserInfo userInfo) {
		// �洢��Ƭ,��ʼ��ͼƬΪnoimage
		SharedPreferences settings = getSharedPreferences("JL_Setting",
				Context.MODE_PRIVATE);
		Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.no_image);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		defaultBitmap.compress(CompressFormat.JPEG, 100, bos);// ����100��ʾ��ѹ��
		byte[] bytes = bos.toByteArray();
		String defaultPhoto = Base64.encodeToString(bytes, Base64.DEFAULT);
		commonService.lastPhoto = defaultPhoto;

		// ��ʼ����������ַ
		// settings.edit().putString("severUrl", userInfo.url).commit();
		// ��ʼ���ŵ��
		settings.edit().putString("cropCode", userInfo.CorpCode).commit();
		// ��ʼ���ŵ�����
		settings.edit().putString("cropName", userInfo.CorpName).commit();

		settings.edit().putString("userName", userInfo.UserName).commit();

		settings.edit().putString("cropBelongs", userInfo.Belong).commit();

		// ����ʼ����ֵ����service
		// commonService.serverUrl=settings.getString("severUrl", "");
		//
		commonService.cropCode = settings.getString("cropCode", "");
		commonService.cropType = userInfo.CropType;
		// ����������
		intent = new Intent(this, MenuActivity.class);
		this.startActivity(intent);
	}

	public void goIntoOterMenuActiviy(UserInfo userInfo) {

		commonService.cropCode = userInfo.CorpCode;
		commonService.cropType = userInfo.CropType;

		intent = new Intent(this, OtherMenuActivity.class);
		this.startActivity(intent);
	}

	public void CellUpdate(final UpdateResult result,
			final NFCActivity _nfcActivity) {
		// TODO Auto-generated method stub
		updateProgress.dismiss();
		AlertDialog.Builder alert = new Builder(this);
		alert.setTitle("������ʾ");
		alert.setMessage(result.getContext());
		alert.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO ��������汾
				downProgress = new ProgressDialog(NFCActivity.this);
				downProgress.setTitle("������ʾ");
				downProgress.setMessage("���������°汾...");
				downProgress.setCancelable(false);
				downProgress.setCanceledOnTouchOutside(false);
				downProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				downProgress.setMax(100);
				downProgress.show();

				try {
					downLoadApp(result.getDownUrl());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
		alert.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				System.out.println("ȡ�������°汾���");
			}
		});
		alert.show();
	}

	private void downLoadApp(String url) throws Exception {
		System.out.println("app���ص�ַ��" + url);
		// ����app�洢·��
		String savePath = "";

		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			String root = Environment.getExternalStorageDirectory().getPath();
			savePath = root;
		} else
			throw new Exception("��׼��SD��");

		savePath += "/"
				+ getResources().getString(com.acctrue.jlyj.R.string.app_name)
				+ ".apk";

		System.out.println("App�洢·�� �� " + savePath);

		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);
		HttpResponse response;
		try {
			response = client.execute(get);

			int status = response.getStatusLine().getStatusCode();
			// If the status is equal to 200 ��that is OK
			if (status != HttpStatus.SC_OK) {
				Toast.makeText(this, "�������ʧ��", Toast.LENGTH_SHORT).show();
				downProgress.cancel();
				return;
			}

			HttpEntity entity = response.getEntity();

			long length = entity.getContentLength();
			int fileSize = (int) length;// �ļ���С
			InputStream is = entity.getContent();
			FileOutputStream fileOutputStream = null;
			if (is == null) {
				throw new RuntimeException("isStream is null");
			}
			File file = new File(savePath);
			fileOutputStream = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int ch = -1;
			int downLoadFileSize = 0;
			do {
				ch = is.read(buf);
				if (ch <= 0)
					break;
				fileOutputStream.write(buf, 0, ch);
				downLoadFileSize += ch;
				downProgress.setProgress(downLoadFileSize / fileSize * 100);
			} while (downLoadFileSize != fileSize);
			is.close();
			fileOutputStream.close();
			InstallApp(savePath);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}

	}

	public void CellResult(String message) {
		// TODO Auto-generated method stub

		if (updateProgress != null) {
			updateProgress.dismiss();
			updateProgress = null;
		}

		Toast.makeText(this, "��ǰ����Ѿ�������汾", Toast.LENGTH_SHORT);

	}

	public void InstallApp(final String string) {
		// TODO Auto-generated method stub
		downProgress.dismiss();
		System.out.println("�Զ���װapp:" + string);

		// ��ʾ�Ƿ�װ�Ի���
		AlertDialog.Builder alert = new Builder(this)
				.setTitle("��װ��ʾ")
				.setMessage("��װ��������ɣ��Ƿ�װ��")
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setDataAndType(Uri.fromFile(new File(string)),
								"application/vnd.android.package-archive");
						startActivity(intent);
						NFCActivity.this.finish();
					}
				})
				.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});
		alert.show();

	}

	public void haveSelectTheServer(SelectServerResult result) {
		// TODO Auto-generated method stub
		if (selectProgress != null) {
			selectProgress.dismiss();
			selectProgress = null;
		}
		if (result.isSelected) {
			SharedPreferences settings = getSharedPreferences("JL_Setting",
					Context.MODE_PRIVATE);
			// ��ʼ����������ַ
			settings.edit().putString("severUrl", result.Server).commit();
			// ����ʼ����ֵ����service
			commonService.serverUrl = settings.getString("severUrl", "");

			Toast.makeText(this, "��ǰ���繩Ӧ��Ϊ" + result.belongTo,
					Toast.LENGTH_SHORT).show();

			this.checkSystemUpdate();

		} else {
			Toast.makeText(this, "�Զ�ѡ�����繩Ӧ��ʧ��", Toast.LENGTH_SHORT).show();
		}
	}

}
