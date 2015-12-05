package com.acctrue.jlyj.activity;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import zq5850usbsdk.UsbprinterActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.acctrue.jlyj.Config;
import com.acctrue.jlyj.R;
import com.acctrue.jlyj.adapter.FMAdapter;
import com.acctrue.jlyj.adapter.FMAdapter2;
import com.acctrue.jlyj.entity.CodeInfo;
import com.acctrue.jlyj.entity.StoreItem;
import com.acctrue.jlyj.entity.Stores;
import com.acctrue.jlyj.entity.lv_item;
import com.acctrue.jlyj.entity.lv_item2;
import com.acctrue.jlyj.service.commonService;
import com.acctrue.jlyj.util.Constants;
import com.acctrue.jlyj.util.Util;

import de.greenrobot.event.EventBus;

public class TraceBackActivity extends UsbprinterActivity implements
		OnFocusChangeListener {

	private int sureCount;

	private int dipatchChangeFocusCount; // 分发切换模式计数

	private int dipatchChangeNumCount; // 分发更改数量计数

	private int dipatchPriceCount; // 分发更改价格计数

	private ListView purchaseInListView; // 采购入库码列表

	private EditText codeInputEditText; // 条码输入框

	private TextView blankTextView; // 空白焦点TextView

	private String purchaseInDocumentNum; // 入库单号

	private ArrayList<lv_item2> list; // 存放数据的list

	private FMAdapter2 adapter; // 数据适配器

	private int time = 0; // 输入间隔计时时间

	private boolean timeFlag = false; // 是否开始计时

	private StringBuffer postion = new StringBuffer(); // 输入的数字

	private int lastKeyCode;

	private int index;

	private int lastSelection;

	private TextView titleTV;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constants.beginTime:
				time += 1;
				Log.i(Constants.msg, "begin" + postion.toString());
				break;
			case Constants.endTime:
				time -= 1;
				if (time == 0) {
					try {
						index = Integer.parseInt(postion.toString().replace(
								".", ""));
					} catch (Exception e) {
						postion = new StringBuffer();
						return;
					}
					if (index > list.size()) {
						index = list.size();
					}
					adapter.selectIndex = index;
					Log.i(Constants.msg, index + "");
					purchaseInListView.setAdapter(adapter);
					purchaseInListView.setSelection(index - 1);
					purchaseInListView.requestFocus();
					postion = new StringBuffer();
				}
				break;
			case Constants.beginTimePrice:
				time += 1;
				Log.i(Constants.msg, "beginPrice" + postion.toString());
				break;
			case Constants.endTimePrice:
				time -= 1;
				if (time == 0) {
					list.get(index - 1).item_7 = postion.toString();
					purchaseInListView.setAdapter(adapter);
					purchaseInListView.setSelection(index - 1);
					purchaseInListView.requestFocus();
					lastKeyCode = 70;
					Log.i(Constants.msg, "price" + postion.toString());
					postion = new StringBuffer();
				}
				break;
			case Constants.beginTimeNumber:
				time += 1;
				Log.i(Constants.msg, "begin" + postion.toString());
				break;
			case Constants.endTimeNumber:
				time -= 1;
				if (time == 0) {
					list.get(index - 1).item_8 = postion.toString();
					purchaseInListView.setAdapter(adapter);
					purchaseInListView.setSelection(index - 1);
					purchaseInListView.requestFocus();
					lastKeyCode = 70;
					Log.i(Constants.msg, "num" + postion.toString());
					postion = new StringBuffer();
				}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
		setContentView(R.layout.layout_order2);
		System.setProperty("file.encoding", "gb2312");
		// 数据、控件初始化
		this.init();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

	public void onEvent(Object event) {
		if (event instanceof String) {
			if (event.equals("上传")) {
				haveGetCode("123456789");
			}
		}
	}

	private void init() {
		// 生成出库单号
		sureCount = 0;
		dipatchChangeFocusCount = 0;
		dipatchChangeNumCount = 0;
		dipatchPriceCount = 0;

		purchaseInDocumentNum = Util.getDocumentNumFromDate();

		titleTV = (TextView) this.findViewById(R.id.title);
		titleTV.setText("追溯上传");

		list = new ArrayList<lv_item2>();

		purchaseInListView = (ListView) this.findViewById(R.id.lv);

		codeInputEditText = (EditText) this.findViewById(R.id.data);

		blankTextView = (TextView) this.findViewById(R.id.blank);

		if (!Config.sKeyIgnore) {

			codeInputEditText.setOnFocusChangeListener(this);

			blankTextView.setOnFocusChangeListener(this);
		} else {
			codeInputEditText
					.setOnEditorActionListener(new TextView.OnEditorActionListener() {
						public boolean onEditorAction(TextView v, int actionId,
								KeyEvent event) {
							if (actionId == EditorInfo.IME_ACTION_GO
									|| (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
								// do something;
								addData();

								return true;
							}
							return false;
						}
					});
		}

		adapter = new FMAdapter2(list, this);

		purchaseInListView.setAdapter(adapter);

		if (!Config.sKeyIgnore) {

			// 屏蔽软键盘
			this.getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			Method setShowSoftInputOnFocus = null;
			try {
				setShowSoftInputOnFocus = codeInputEditText.getClass()
						.getMethod("setShowSoftInputOnFocus", boolean.class);
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (setShowSoftInputOnFocus != null) {
				setShowSoftInputOnFocus.setAccessible(true);
			}
			try {
				setShowSoftInputOnFocus.invoke(codeInputEditText, false);
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
	}

	protected void addData() {
		this.haveGetCode(this.codeInputEditText.getText().toString());
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (!Config.sKeyIgnore) {
			codeInputEditText.requestFocus();
		}
	}

	private void printDocument(ArrayList<lv_item2> list, double allMoney) {
		String qrStr = "http://211.137.215.54:8080/tts"
				+ "/Portal/PortalUI/MobileProList.aspx?ID="
				+ commonService.cropCode + "," + purchaseInDocumentNum;
		// String qrStr =
		// commonService.serverUrl+"/Portal/PortalUI/MobileProList.aspx?ID="+commonService.cropCode+","+"20144501124555";
		Bitmap qrBitmap = Util.createQRBitMap(qrStr);

		SharedPreferences settings = getSharedPreferences("JL_Setting",
				Context.MODE_PRIVATE);
		String crop = settings.getString("cropName", "");

		String strBarcode = purchaseInDocumentNum;
		byte dataBar3[] = strBarcode.getBytes();
		PRN_PrintBarcode(dataBar3, dataBar3.length, BCS_Code128_ZQ, 50, 2,
				ALIGNMENT_LEFT, BC_TEXT_BELOW);

		PRN_PrintText(strBarcode + "\n", ALIGNMENT_LEFT, FT_DEFAULT, TS_0WIDTH
				| TS_0HEIGHT);

		PRN_LineFeed(1);

		PRN_PrintText(crop + "\r\n", ALIGNMENT_CENTER, FT_DEFAULT, TS_0WIDTH
				| TS_0HEIGHT);

		// 表单头
		PRN_PrintText(" \r\n", ALIGNMENT_RIGHT, FT_DEFAULT, TS_0WIDTH
				| TS_0HEIGHT);
		PRN_PrintText("结算单：\r\n", ALIGNMENT_LEFT, FT_DEFAULT, TS_0WIDTH
				| TS_0HEIGHT);

		PRN_PrintText("--------------------------------\r\n", ALIGNMENT_LEFT,
				FT_DEFAULT, TS_0WIDTH | TS_0HEIGHT);

		for (int i = 0; i < list.size(); i++) {
			PRN_PrintText(list.get(i).item_8 + "*" + list.get(i).item_3
					+ "\r\n", ALIGNMENT_LEFT, FT_DEFAULT, TS_0WIDTH
					| TS_0HEIGHT);
			PRN_PrintText(list.get(i).item_7 + "\r\n", ALIGNMENT_RIGHT,
					FT_DEFAULT, TS_0WIDTH | TS_0HEIGHT);
		}

		PRN_PrintText("--------------------------------\r\n", ALIGNMENT_LEFT,
				FT_DEFAULT, TS_0WIDTH | TS_0HEIGHT);
		PRN_PrintText("总额: " + String.valueOf(allMoney) + " RMB\r\n",
				ALIGNMENT_LEFT, FT_DEFAULT, TS_0WIDTH | TS_0HEIGHT);

		PRN_PrintText("--------------------------------\r\n", ALIGNMENT_LEFT,
				FT_DEFAULT, TS_0WIDTH | TS_0HEIGHT);
		PRN_PrintText("  扫码追溯产品\n", ALIGNMENT_LEFT, FT_BOLD, TS_0WIDTH
				| TS_0HEIGHT);
		PRN_PrintText("      ", ALIGNMENT_LEFT, FT_BOLD, TS_0WIDTH | TS_0HEIGHT);
		PRN_PrintBitmap(qrBitmap, IMAGE_NORMAL);

		PRN_LineFeed(2);

		PRN_PrintText("温馨提示，请妥善保存您的消费凭证以便溯源，举报电话12331\r\n", ALIGNMENT_LEFT,
				FT_BOLD, TS_0WIDTH | TS_0HEIGHT);

		PRN_PrintText("吉林省食品药品监督管理局\r\n", ALIGNMENT_CENTER, FT_BOLD, TS_0WIDTH
				| TS_0HEIGHT);

		PRN_LineFeed(7);
	}

	private void haveGetCode(String code) {

		// 判重
		for (int i = 0; i < list.size(); i++) {
			lv_item2 it = list.get(i);
			if (it.item_2.equals(code)) {
				int count = Integer.parseInt(it.item_8) + 1;
				it.item_8 = String.valueOf(count);
				purchaseInListView.setAdapter(adapter);
				purchaseInListView.setSelection(list.size() - 1);

				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

				codeInputEditText.setText("");
				codeInputEditText.requestFocus();
				return;
			}
		}

		if (code.equals("123456789")) {
			// 非医保结算
			double MOey = 0;

			for (int i = 0; i < list.size(); i++) {
				MOey += (Double.parseDouble(list.get(i).item_7) * Integer
						.parseInt(list.get(i).item_8));

			}

			this.printDocument(list, MOey);

			List<StoreItem> slist = new ArrayList<StoreItem>();
			for (int i = 0; i < list.size(); i++) {
				StoreItem item = new StoreItem(list.get(i).item_2,
						Integer.parseInt(list.get(i).item_8), "", "",
						list.get(i).item_9);
				slist.add(item);
			}
			Stores stores = new Stores(purchaseInDocumentNum, "SaleOut",
					commonService.cropCode, commonService.lastPhoto, slist,
					false);
			new AsyncTask<Stores, Void, Boolean>() {
				@Override
				protected void onPreExecute() {
					super.onPreExecute();

					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				}

				@Override
				protected Boolean doInBackground(Stores... params) {
					boolean isSuccess = commonService
							.uploadDocumentByHttp(params[0]);
					return isSuccess;
				}

				protected void onPostExecute(Boolean result) {

					boolean isSuccess = result;
					if (!isSuccess) {
						Toast.makeText(TraceBackActivity.this, "上传失败",
								Toast.LENGTH_LONG).show();
						codeInputEditText.requestFocus();
						index = -1;
						adapter.selectIndex = index;
						purchaseInListView.setAdapter(adapter);
						purchaseInListView.setSelection(index);
					} else {
						Toast.makeText(TraceBackActivity.this, "上传成功",
								Toast.LENGTH_LONG).show();
						codeInputEditText.requestFocus();
						index = -1;
						adapter.selectIndex = index;
						list.clear();
						purchaseInListView.setAdapter(adapter);
						purchaseInDocumentNum = Util.getDocumentNumFromDate();
					}

					codeInputEditText.setText("");
					codeInputEditText.requestFocus();
				};
			}.execute(stores);

			return;
		}

		if (code.equals("12345678")) {
			// 医保结算
			double MOey = 0;

			for (int i = 0; i < list.size(); i++) {
				MOey += (Double.parseDouble(list.get(i).item_7) * Integer
						.parseInt(list.get(i).item_8));

			}

			this.printDocument(list, MOey);

			List<StoreItem> slist = new ArrayList<StoreItem>();
			for (int i = 0; i < list.size(); i++) {
				StoreItem item = new StoreItem(list.get(i).item_2,
						Integer.parseInt(list.get(i).item_8), "", "",
						list.get(i).item_9);
				slist.add(item);
			}
			Stores stores = new Stores(purchaseInDocumentNum, "SaleOut",
					commonService.cropCode, commonService.lastPhoto, slist,
					true);
			new AsyncTask<Stores, Void, Boolean>() {

				@Override
				protected void onPreExecute() {
					super.onPreExecute();

					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				}

				@Override
				protected Boolean doInBackground(Stores... params) {
					boolean isSuccess = commonService
							.uploadDocumentByHttp(params[0]);
					return isSuccess;
				}

				protected void onPostExecute(Boolean result) {

					boolean isSuccess = result;
					if (!isSuccess) {
						Toast.makeText(TraceBackActivity.this, "上传失败",
								Toast.LENGTH_LONG).show();
						codeInputEditText.requestFocus();
						index = -1;
						adapter.selectIndex = index;
						purchaseInListView.setAdapter(adapter);
						purchaseInListView.setSelection(index);
					} else {
						Toast.makeText(TraceBackActivity.this, "上传成功",
								Toast.LENGTH_LONG).show();
						codeInputEditText.requestFocus();
						index = -1;
						adapter.selectIndex = index;
						list.clear();
						purchaseInListView.setAdapter(adapter);
						purchaseInDocumentNum = Util.getDocumentNumFromDate();
					}

					codeInputEditText.setText("");
					codeInputEditText.requestFocus();
				};

			}.execute(stores);

			return;
		}

		new AsyncTask<String, Void, CodeInfo>() {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();

				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			}

			@Override
			protected CodeInfo doInBackground(String... params) {
				CodeInfo codeInfo = commonService.getCodeInfoByHttp(params[0]);
				return codeInfo;
			}

			protected void onPostExecute(CodeInfo result) {

				CodeInfo codeInfo = result;
				if (codeInfo != null) {
					lv_item2 item = new lv_item2();
					item.item_1 = String.valueOf(list.size() + 1);
					item.item_2 = codeInfo.produtCode;
					item.item_3 = codeInfo.productName;
					item.item_6 = codeInfo.produceCropName;
					item.item_7 = codeInfo.productPrice;
					item.item_8 = String.valueOf(1);
					item.item_9 = codeInfo.isYJCode;
					list.add(item);
					purchaseInListView.setAdapter(adapter);
					purchaseInListView.setSelection(list.size() - 1);
				} else {
					Toast.makeText(TraceBackActivity.this, "没有查到该条码的产品信息",
							Toast.LENGTH_LONG).show();

				}

				codeInputEditText.setText("");
				codeInputEditText.requestFocus();
			};

		}.execute(code);

	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.data:
			if (hasFocus) {

			} else {
				// Log.i(Constants.msg, "lost_data");
			}
			break;
		case R.id.blank:
			if (hasFocus) {

				this.haveGetCode(codeInputEditText.getText().toString());

				codeInputEditText.setText("");
				// Log.i(Constants.msg, "get_blank");
				codeInputEditText.requestFocus();
			} else {
				// Log.i(Constants.msg, "lost_blank");
			}
			break;
		default:
			break;
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		if (!Config.sKeyIgnore) {
			// 屏蔽上下左右键
			if (event.getKeyCode() == 19 || event.getKeyCode() == 20
					|| event.getKeyCode() == 21 || event.getKeyCode() == 22) {
				System.out.println("点击了上下左右键");
				return true;
			}

			// 点击+号，修改产品价格
			if (event.getKeyCode() == 157) {

				if (dipatchPriceCount % 2 == 0) {
					lastKeyCode = 157;
					if (timeFlag == true) {
						// beginTimerPrice();
					}
				}
				dipatchPriceCount++;

				return false;
			}
			// 点击*号，修改产品数量
			if (event.getKeyCode() == 155) {

				if (dipatchPriceCount % 2 == 0) {
					lastKeyCode = 155;
					if (timeFlag == true) {

					}
				}
				dipatchPriceCount++;

				return false;
			}
			// 点击#号键,焦点移开，定位数据位置
			if (event.getKeyCode() == 70) {

				if (dipatchChangeFocusCount % 2 == 0) {
					lastKeyCode = 70;
					if (timeFlag == false) {
						codeInputEditText.clearFocus();
						index = list.size();
						adapter.selectIndex = index;
						Log.i(Constants.msg, index + ":first");
						purchaseInListView.setAdapter(adapter);
						purchaseInListView.setSelection(index - 1);
						purchaseInListView.requestFocus();
						timeFlag = true;
					} else {
						timeFlag = false;
						codeInputEditText.requestFocus();

						index = -1;
						adapter.selectIndex = index;
						purchaseInListView.setAdapter(adapter);
						purchaseInListView.setSelection(index);
					}
				}
				dipatchChangeFocusCount++;
				Log.i(Constants.msg, "KEYCODE70");

				return false;
			}
			// 点击/号键,结算，上传数据功能按钮，焦点回移
			if (event.getKeyCode() == 76) {
				// if (sureCount % 2 == 0) {
				// System.out.println("点击提交操作");
				//
				// }
				// sureCount++;

				return true;
			}
			// 点击*号键
			if (event.getKeyCode() == 158) {
				Log.i(Constants.msg, "KEYCODE158");
				event = new KeyEvent(event.getDownTime(), event.getEventTime(),
						event.getAction(), KeyEvent.KEYCODE_PERIOD,
						event.getRepeatCount(), event.getMetaState(),
						event.getDeviceId(), event.getScanCode(),
						event.getFlags());
			}
			// 点击-号键,删除键
			if (event.getKeyCode() == 156) {
				// event = new KeyEvent(event.getDownTime(),
				// event.getEventTime(),
				// event.getAction(), KeyEvent.KEYCODE_DEL,
				// event.getRepeatCount(), event.getMetaState(),
				// event.getDeviceId(), event.getScanCode(), event.getFlags());
				return false;

			}
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (timeFlag) {

			if (event.getKeyCode() == 7) {
				postion.append("0");
			}
			if (event.getKeyCode() == 8) {
				postion.append("1");
			}
			if (event.getKeyCode() == 9) {
				postion.append("2");
			}
			if (event.getKeyCode() == 10) {
				postion.append("3");
			}
			if (event.getKeyCode() == 11) {
				postion.append("4");
			}
			if (event.getKeyCode() == 12) {
				postion.append("5");
			}
			if (event.getKeyCode() == 13) {
				postion.append("6");
			}
			if (event.getKeyCode() == 14) {
				postion.append("7");
			}
			if (event.getKeyCode() == 15) {
				postion.append("8");
			}
			if (event.getKeyCode() == 16) {
				postion.append("9");
			}
			if (event.getKeyCode() == 56) {
				postion.append(".");
			}
			if (event.getKeyCode() == 158) {
				postion.append(".");
			}
			if (event.getKeyCode() == 112) {
				postion.append(".");
			}
			if (lastKeyCode == 70 && keyCode != 23) {
				beginTimer();
			} else if (lastKeyCode == 157 && keyCode != 23) {
				beginTimerPrice();
			} else if (lastKeyCode == 155 && keyCode != 23) {
				beginTimerNumber();
			}

		}
		if (keyCode == KeyEvent.KEYCODE_BACK) {
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 计时决定输入的position
	 */
	private void beginTimer() {
		handler.post(beginTimeRun);
		handler.postDelayed(endTimeRun, 500);
	}

	private void beginTimerPrice() {
		handler.post(beginTimeRunPrice);
		handler.postDelayed(endTimeRunPrice, 500);
	}

	private void beginTimerNumber() {
		handler.post(beginTimeRunNumber);
		handler.postDelayed(endTimeRunNumber, 500);
	}

	private Runnable beginTimeRun = new Runnable() {

		@Override
		public void run() {
			Message message = Message.obtain();
			message.what = Constants.beginTime;
			handler.sendMessage(message);

		}
	};

	private Runnable endTimeRun = new Runnable() {

		@Override
		public void run() {
			Message message = Message.obtain();
			message.what = Constants.endTime;
			handler.sendMessage(message);

		}
	};

	private Runnable beginTimeRunPrice = new Runnable() {

		@Override
		public void run() {
			Message message = Message.obtain();
			message.what = Constants.beginTimePrice;
			handler.sendMessage(message);

		}
	};

	private Runnable endTimeRunPrice = new Runnable() {

		@Override
		public void run() {
			Message message = Message.obtain();
			message.what = Constants.endTimePrice;
			handler.sendMessage(message);

		}
	};

	private Runnable beginTimeRunNumber = new Runnable() {

		@Override
		public void run() {
			Message message = Message.obtain();
			message.what = Constants.beginTimeNumber;
			handler.sendMessage(message);

		}
	};

	private Runnable endTimeRunNumber = new Runnable() {

		@Override
		public void run() {
			Message message = Message.obtain();
			message.what = Constants.endTimeNumber;
			handler.sendMessage(message);

		}
	};

}