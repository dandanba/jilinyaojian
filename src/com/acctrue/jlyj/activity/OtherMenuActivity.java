package com.acctrue.jlyj.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EncodingUtils;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.acctrue.jlyj.R;
import com.acctrue.jlyj.event.ClickEvent;
import com.acctrue.jlyj.service.commonService;
import com.acctrue.jlyj.util.Util;

import de.greenrobot.event.EventBus;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class OtherMenuActivity extends ActivityGroup {

	private static final String SDA_PATH = "/storage/external_storage/sda";

	private LinearLayout view;

	private String uPadFilePath;

	private Handler handler;

	private Handler synchronousHandler;
	ProgressDialog dialog;

	ProgressDialog downloadDialog;

	ProgressDialog synchronousDialog;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
		setContentView(R.layout.activity_other_menu);

		view = (LinearLayout) findViewById(R.id.other_view);

		Intent intent = new Intent(this, OtherMainActivity.class);

		Bundle bundle = new Bundle();
		bundle.putInt("CorpType", commonService.cropType);

		intent.putExtras(bundle);

		Util.switchActivity(view, intent, this);

		uPadFilePath = null;

		handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);

				OtherMenuActivity.this.updatedProgress((String) msg.obj);
			}

		};

		synchronousHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);

				OtherMenuActivity.this.haveDownloadTheModel((String) msg.obj);
			}

		};

		checkTheUsbPad();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

	public void onEvent(Object event) {
		if (event instanceof ClickEvent) {
			final ClickEvent clickEvent = (ClickEvent) event;
			onKeyDown(clickEvent.keyCode, clickEvent.event);
		}
	}

	protected void haveDownloadTheModel(String obj) {
		// TODO Auto-generated method stub
		synchronousDialog.cancel();
		Toast.makeText(this, obj, Toast.LENGTH_LONG).show();
	}

	@SuppressWarnings("unused")
	public void checkTheUsbPad() {

		File theExternalFile = new File(SDA_PATH);
		String[] fileList = theExternalFile.list();
		if (fileList == null/* ||fileList.length == 1 */) {
			AlertDialog dialog = new AlertDialog.Builder(OtherMenuActivity.this)
					.setTitle("提示")
					.setMessage("未检测到U盘，请先插入一个U盘后点击确定")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									checkTheUsbPad();
								}
							}).show();
		} else {
			for (int i = 0; i < fileList.length; i++) {
				String file = fileList[i];
				if (!file.equals("sdcard1")) {
					uPadFilePath = SDA_PATH + file;

					// 检测到U盘的存在在U盘上创建文件夹
					File modelDir = new File(uPadFilePath + "/药品监管_模版_文件");
					File iodataDir = new File(uPadFilePath + "/药品监管_生产批次_文件");
					File producedataDir = new File(uPadFilePath
							+ "/药品监管_出入库台帐_文件");

					if (!modelDir.exists()) {
						modelDir.mkdir();
					}
					if (!iodataDir.exists()) {
						iodataDir.mkdir();
					}
					if (!producedataDir.exists()) {
						producedataDir.mkdir();
					}
					break;
				}
			}
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (keyCode) {
		case KeyEvent.KEYCODE_1:
			downloadModel();
			break;
		case KeyEvent.KEYCODE_2:
			uploadIOData();
			break;
		case KeyEvent.KEYCODE_3:
			// if (commonService.cropType!=1) {
			// uploadProduceData();
			// }
			uploadProduceData();
			break;

		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void downloadModel() {
		System.out.println("点击了功能1");
		synchronousDialog = new ProgressDialog(this);
		synchronousDialog.setMessage("正在同步往来企业");
		synchronousDialog.setCancelable(false);
		synchronousDialog.show();

		final String theProduceDataPath1 = uPadFilePath + "/药品监管_模版_文件";

		// 下载模版

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String downloadResult = "下载结果：\n";
				try {

					// if(commonService.cropType == 0){
					HttpGet request0 = new HttpGet(
							commonService.serverUrl
									+ "/Portal/ScanServices/CodeService.svc/DownExcel/0");
					HttpResponse response0 = new DefaultHttpClient()
							.execute(request0);
					int connectStatus0 = response0.getStatusLine()
							.getStatusCode();
					if (connectStatus0 == HttpStatus.SC_OK) {
						// 判断如果有旧模版则删除

						File modeProduceFile = new File(theProduceDataPath1
								+ "/生产批次模版.xls");

						if (modeProduceFile.exists()) {
							modeProduceFile.delete();
						}

						byte[] buffer = EntityUtils.toByteArray(response0
								.getEntity());
						FileOutputStream fout = new FileOutputStream(
								theProduceDataPath1 + "/生产批次模版.xls");
						fout.write(buffer);
						fout.close();

						downloadResult += "生产批次模版下载成功\n";
					} else {

						downloadResult += "出入库台帐模版下载失败\n";
					}

					// }
					HttpGet request = new HttpGet(
							commonService.serverUrl
									+ "/Portal/ScanServices/CodeService.svc/DownExcel/1");
					HttpResponse response = new DefaultHttpClient()
							.execute(request);
					int connectStatus = response.getStatusLine()
							.getStatusCode();
					if (connectStatus == HttpStatus.SC_OK) {
						// 判断如果有旧模版则删除

						File modeIODataFile = new File(theProduceDataPath1
								+ "/出入库台帐模版.xls");

						if (modeIODataFile.exists()) {
							modeIODataFile.delete();
						}
						byte[] buffer = EntityUtils.toByteArray(response
								.getEntity());
						FileOutputStream fout = new FileOutputStream(
								theProduceDataPath1 + "/出入库台帐模版.xls");
						fout.write(buffer);
						fout.close();

						downloadResult += "出入库台帐模版下载成功\n";
					} else {

						downloadResult += "出入库台帐模版下载失败\n";
					}

				} catch (Exception e) {
					// TODO: handle exception
					Message msg = new Message();
					msg.obj = "下载结果：因网络连接问题下载失败";
					synchronousHandler.sendMessage(msg);
				}

				Message msg = new Message();
				msg.obj = downloadResult;
				synchronousHandler.sendMessage(msg);

			}
		}).start();

	}

	public void uploadIOData() {
		System.out.println("点击了功能2");

		final String theProduceDataPath = uPadFilePath + "//药品监管_出入库台帐_文件";

		File ProduceDataFile = new File(theProduceDataPath);

		final String theProduceDataFileList[] = ProduceDataFile.list();

		if (theProduceDataFileList.length == 0) {
			Toast.makeText(this, "/药品监管_出入库台帐_文件", Toast.LENGTH_LONG).show();
			return;
		}
		dialog = new ProgressDialog(this);
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置水平进度条
		dialog.setCancelable(true);// 设置是否可以通过点击Back键取消
		dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
		dialog.setIcon(R.drawable.ic_launcher);// 设置提示的title的图标，默认是没有的
		dialog.setTitle("提示");
		dialog.setMax(theProduceDataFileList.length);
		dialog.setMessage("");
		dialog.show();
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				for (int i = 0; i < theProduceDataFileList.length; i++) {

					if (theProduceDataFileList[i].indexOf("上传成功") != -1) {
						continue;

					}
					if (theProduceDataFileList[i].indexOf("上传失败") != -1) {
						continue;
					}
					if (theProduceDataFileList[i].indexOf("部分上传失败") != -1) {
						continue;
					}
					if (theProduceDataFileList[i].indexOf("Excel读取错误") != -1) {
						continue;
					}
					if (theProduceDataFileList[i].indexOf("已经上传") != -1) {
						continue;
					}

					String xlsStr = "";
					// 文件读取
					try {
						FileInputStream fin = new FileInputStream(
								theProduceDataPath + "/"
										+ theProduceDataFileList[i]);
						int length = fin.available();
						byte[] buffer = new byte[length];
						fin.read(buffer);
						xlsStr = EncodingUtils.getString(buffer, "UTF-8");
						fin.close();
						// 上传操作
						Thread.sleep(5000 / theProduceDataFileList.length);
						String fileName = theProduceDataFileList[i];
						String[] tmp = fileName.split("\\.");
						String fileNamePre = tmp[0];
						String fileNameLast = tmp[1];

						// 上传
						HttpPost request = new HttpPost(
								commonService.serverUrl
										+ "/Portal/ScanServices/CodeService.svc/UploadExcel/1/"
										+ fileNamePre + "/"
										+ commonService.cropCode);
						ByteArrayEntity entity = new ByteArrayEntity(buffer);
						entity.setContentType("application/x-www-form-urlencoded");
						request.setEntity(entity);
						HttpResponse response = new DefaultHttpClient()
								.execute(request);
						int connectStatus = response.getStatusLine()
								.getStatusCode();
						if (connectStatus == HttpStatus.SC_OK) {

							String result = EntityUtils.toString(response
									.getEntity());

							JSONObject object = new JSONObject(result);

							String errMsg = object.getString("ErrMes");

							File file = new File(theProduceDataPath + "/"
									+ theProduceDataFileList[i]);
							if (errMsg.equals("null")) {

								String byteStr = object.getString("Bytes");

								byte[] returnBuffer = Base64.decode(byteStr,
										Base64.DEFAULT);

								if (byteStr.equals("null")) {
									file.renameTo(new File(theProduceDataPath
											+ "/" + fileNamePre + "_上传成功" + "."
											+ fileNameLast));
								} else {
									file.delete();

									File errFile = new File(theProduceDataPath
											+ "/" + fileNamePre + "_部分上传失败"
											+ "." + fileNameLast);

									FileOutputStream fOut = new FileOutputStream(
											errFile);

									fOut.write(returnBuffer);

									fOut.close();

								}

							} else {

								file.renameTo(new File(theProduceDataPath + "/"
										+ fileNamePre + "_" + errMsg + "."
										+ fileNameLast));

							}

							Message msg = new Message();
							if (errMsg.equals("null")) {
								String byteStr = object.getString("Bytes");

								if (byteStr.equals("null")) {
									msg.obj = theProduceDataFileList[i]
											+ "\n上传结果：上传成功";
								} else {
									msg.obj = theProduceDataFileList[i]
											+ "\n上传结果：部分上传成功";
								}
							} else {
								msg.obj = theProduceDataFileList[i] + "\n上传结果："
										+ errMsg;
							}
							handler.sendMessage(msg);

						} else {
							Message msg = new Message();
							msg.obj = theProduceDataFileList[i]
									+ "\n上传结果：因服务端问题上传失败";
							handler.sendMessage(msg);
						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						Message msg = new Message();
						msg.obj = theProduceDataFileList[i]
								+ "\n上传结果：因网络连接问题上传失败";
						handler.sendMessage(msg);
					}
				}
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				dialog.dismiss();

			}
		}).start();
	}

	protected void updatedProgress(String Message) {
		// TODO Auto-generated method stub
		dialog.setMessage(Message);

		dialog.incrementProgressBy(1);
	}

	public void uploadProduceData() {
		System.out.println("点击了功能3");
		final String theProduceDataPath = uPadFilePath + "/药品监管_生产批次_文件";

		File ProduceDataFile = new File(theProduceDataPath);

		final String theProduceDataFileList[] = ProduceDataFile.list();

		if (theProduceDataFileList.length == 0) {
			Toast.makeText(this, "药品监管_生产批次_文件 中没有待上传文件", Toast.LENGTH_LONG)
					.show();
			return;
		}
		dialog = new ProgressDialog(this);
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置水平进度条
		dialog.setCancelable(true);// 设置是否可以通过点击Back键取消
		dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
		dialog.setIcon(R.drawable.ic_launcher);// 设置提示的title的图标，默认是没有的
		dialog.setTitle("提示");
		dialog.setMax(theProduceDataFileList.length);
		dialog.setMessage("");
		dialog.show();
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				for (int i = 0; i < theProduceDataFileList.length; i++) {

					if (theProduceDataFileList[i].indexOf("上传成功") != -1) {
						continue;
					}
					if (theProduceDataFileList[i].indexOf("上传失败") != -1) {
						continue;
					}
					if (theProduceDataFileList[i].indexOf("Excel读取错误") != -1) {
						continue;
					}
					if (theProduceDataFileList[i].indexOf("已经上传") != -1) {
						continue;
					}

					String xlsStr = "";
					// 文件读取
					try {
						FileInputStream fin = new FileInputStream(
								theProduceDataPath + "/"
										+ theProduceDataFileList[i]);
						int length = fin.available();
						byte[] buffer = new byte[length];
						fin.read(buffer);
						xlsStr = EncodingUtils.getString(buffer, "UTF-8");
						fin.close();
						// 上传操作
						Thread.sleep(5000 / theProduceDataFileList.length);
						String fileName = theProduceDataFileList[i];
						String[] tmp = fileName.split("\\.");
						String fileNamePre = tmp[0];
						String fileNameLast = tmp[1];

						// 上传
						HttpPost request = new HttpPost(
								commonService.serverUrl
										+ "/Portal/ScanServices/CodeService.svc/UploadExcel/0/"
										+ fileNamePre + "/"
										+ commonService.cropCode);
						ByteArrayEntity entity = new ByteArrayEntity(buffer);
						entity.setContentType("application/x-www-form-urlencoded");
						request.setEntity(entity);
						HttpResponse response = new DefaultHttpClient()
								.execute(request);
						int connectStatus = response.getStatusLine()
								.getStatusCode();
						if (connectStatus == HttpStatus.SC_OK) {

							String result = EntityUtils.toString(response
									.getEntity());

							JSONObject object = new JSONObject(result);

							String errMsg = object.getString("ErrMes");

							File file = new File(theProduceDataPath + "/"
									+ theProduceDataFileList[i]);
							if (errMsg.equals("null")) {

								String byteStr = object.getString("Bytes");

								byte[] returnBuffer = Base64.decode(byteStr,
										Base64.DEFAULT);

								if (byteStr.equals("null")) {
									file.renameTo(new File(theProduceDataPath
											+ "/" + fileNamePre + "_上传成功" + "."
											+ fileNameLast));
								} else {
									file.delete();

									File errFile = new File(theProduceDataPath
											+ "/" + fileNamePre + "_部分上传失败"
											+ "." + fileNameLast);

									FileOutputStream fOut = new FileOutputStream(
											errFile);

									fOut.write(returnBuffer);

									fOut.flush();
									fOut.close();

								}

							} else {

								file.renameTo(new File(theProduceDataPath + "/"
										+ fileNamePre + "_" + errMsg + "."
										+ fileNameLast));

							}

							Message msg = new Message();
							if (errMsg.equals("null")) {
								String byteStr = object.getString("Bytes");

								if (byteStr.equals("null")) {
									msg.obj = theProduceDataFileList[i]
											+ "\n上传结果：上传成功";
								} else {
									msg.obj = theProduceDataFileList[i]
											+ "\n上传结果：部分上传成功";
								}
							} else {
								msg.obj = theProduceDataFileList[i] + "\n上传结果："
										+ errMsg;
							}
							handler.sendMessage(msg);

						} else {
							Message msg = new Message();
							msg.obj = theProduceDataFileList[i]
									+ "\n上传结果：因服务端问题上传失败";
							handler.sendMessage(msg);
						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						Message msg = new Message();
						msg.obj = theProduceDataFileList[i]
								+ "\n上传结果：因网络连接问题上传失败";
						handler.sendMessage(msg);
					}
				}
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				dialog.dismiss();
			}
		}).start();
	}
	public void onPhotoClick(View view){
		final ClickEvent event = new ClickEvent();
		event.keyCode = 156;
		event.keyEvent = "onKeyUp";
		EventBus.getDefault().post(event);
	}
}
