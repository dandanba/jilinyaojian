package com.acctrue.jlyj.activity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import zq5850usbsdk.UsbprinterActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.acctrue.jlyj.Config;
import com.acctrue.jlyj.R;
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

public class SaleOutActivity extends UsbprinterActivity implements
		OnFocusChangeListener {

	public String toFormatString(String d) {
		final double zj = Double.parseDouble(d);
		float number = (float) (Math.round(zj * 100)) / 100;
		DecimalFormat decimalFormat = new DecimalFormat(".00");
		return decimalFormat.format(number);
	}

	public String toFormatString(double zj) {

		float number = (float) (Math.round(zj * 100)) / 100;
		DecimalFormat decimalFormat = new DecimalFormat(".00");
		return decimalFormat.format(number);
	}

	private boolean isInEditModel;

	private Boolean isFromPop;

	private String allMoney;

	private int jsCount;

	private int dipatchChangeFocusCount; // �ַ��л�ģʽ����

	private int dipatchChangeNumCount; // �ַ�������������

	private int dipatchPriceCount; // �ַ����ļ۸����

	private ListView purchaseInListView; // �ɹ�������б�

	private EditText codeInputEditText; // ���������

	private TextView blankTextView; // �հ׽���TextView

	private String purchaseInDocumentNum; // ��ⵥ��

	private ArrayList<lv_item2> list; // ������ݵ�list

	private FMAdapter2 adapter; // ����������

	private int time = 0; // ��������ʱʱ��

	private boolean timeFlag = false; // �Ƿ�ʼ��ʱ

	private StringBuffer postion = new StringBuffer(); // ���������

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
		// ���ݡ��ؼ���ʼ��
		this.init();
	}

	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(this);

		try {
			super.onDestroy();
		} catch (Exception e) {

		}

	}

	public void onEvent(Object event) {
		if (event instanceof String) {
			if (event.equals("����")) {
				saleOut();
			}
		}
	}

	private void init() {

		isFromPop = false;

		isInEditModel = false;

		// ���ɳ��ⵥ��
		allMoney = "";
		jsCount = 0;
		dipatchChangeFocusCount = 0;
		dipatchChangeNumCount = 0;
		dipatchPriceCount = 0;

		purchaseInDocumentNum = Util.getDocumentNumFromDate();

		titleTV = (TextView) this.findViewById(R.id.title);
		titleTV.setText("���۳���");

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
							if ((event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) { // actionId
																									// ==
																									// EditorInfo.IME_ACTION_GO
																									// ||
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

		purchaseInListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

			}
		});

		if (!Config.sKeyIgnore) {
			// ���������
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

	private int keyindex;

	protected void addData() {
		keyindex++;
		if (keyindex % 2 == 1) {
			this.haveGetCode(this.codeInputEditText.getText().toString());
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		codeInputEditText.requestFocus();
	}

	private void haveGetCode(String code) {

		// ����
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

			@Override
			protected void onPostExecute(CodeInfo result) {
				super.onPostExecute(result);

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
					Toast.makeText(SaleOutActivity.this, "û�в鵽������Ĳ�Ʒ��Ϣ",
							Toast.LENGTH_LONG).show();
				}

				codeInputEditText.setText("");
				codeInputEditText.requestFocus();

			}
		}.execute(code);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		// if (requestCode==20&&requestCode==20) {
		// System.out.println("�ص�����1");
		// Bundle bundle=data.getExtras();
		//
		// lv_item2 item = new lv_item2();
		// item.item_1 = String.valueOf(list.size() + 1);
		// item.item_2 = bundle.getString("produtCode","");
		// item.item_3 = bundle.getString("productName", "");
		// // item.item_4 = bundle.getString("productProduceDate", "");
		// // item.item_5 = bundle.getString("productBatchNum", "");
		// item.item_6 = bundle.getString("produceCropName", "");
		// item.item_7 = bundle.getString("productPrice", "");
		// item.item_8 = String.valueOf(1);
		// item.item_9=false;
		// list.add(item);
		// purchaseInListView.setAdapter(adapter);
		// purchaseInListView.setSelection(list.size() - 1);
		// jsCount=0;
		//
		//
		// }
		if (requestCode == 30 && resultCode == 30) {
			//
			Bundle bundle = data.getExtras();

			System.out.println("=================" + jsCount);

			SharedPreferences settings = getSharedPreferences("JL_Setting",
					Context.MODE_PRIVATE);
			String crop = settings.getString("cropName", "");

			String qrStr = "http://211.137.215.54:8080/tts"
					+ "/Portal/PortalUI/MobileProList.aspx?ID="
					+ commonService.cropCode + "," + purchaseInDocumentNum;
			// ��ӡ���ⵥ����
			Bitmap qrBitmap = Util.createQRBitMap(qrStr);

			String strBarcode = purchaseInDocumentNum;
			byte dataBar3[] = strBarcode.getBytes();
			PRN_PrintBarcode(dataBar3, dataBar3.length, BCS_Code128_ZQ, 50, 2,
					ALIGNMENT_LEFT, BC_TEXT_BELOW);

			PRN_PrintText(strBarcode + "\n", ALIGNMENT_LEFT, FT_DEFAULT,
					TS_0WIDTH | TS_0HEIGHT);

			PRN_LineFeed(1);

			PRN_PrintText(crop + "\r\n", ALIGNMENT_LEFT, FT_BOLD, TS_0WIDTH
					| TS_0HEIGHT);

			// ��ͷ
			PRN_PrintText(" \r\n", ALIGNMENT_RIGHT, FT_DEFAULT, TS_0WIDTH
					| TS_0HEIGHT);
			PRN_PrintText("���㵥��\r\n", ALIGNMENT_LEFT, FT_DEFAULT, TS_0WIDTH
					| TS_0HEIGHT);

			PRN_PrintText("--------------------------------\r\n",
					ALIGNMENT_LEFT, FT_DEFAULT, TS_0WIDTH | TS_0HEIGHT);

			for (int i = 0; i < list.size(); i++) {
				PRN_PrintText(list.get(i).item_8 + "*" + list.get(i).item_3
						+ "\r\n", ALIGNMENT_LEFT, FT_DEFAULT, TS_0WIDTH
						| TS_0HEIGHT);
				PRN_PrintText(list.get(i).item_7 + "\r\n", ALIGNMENT_RIGHT,
						FT_DEFAULT, TS_0WIDTH | TS_0HEIGHT);
			}

			PRN_PrintText("--------------------------------\r\n",
					ALIGNMENT_LEFT, FT_DEFAULT, TS_0WIDTH | TS_0HEIGHT);
			PRN_PrintText("�ܶ�: " + toFormatString(allMoney) + "\r\n", ALIGNMENT_RIGHT,
					FT_DEFAULT, TS_0WIDTH | TS_0HEIGHT);
			PRN_PrintText("ʵ��: " + bundle.getString("infact") + "\r\n",
					ALIGNMENT_RIGHT, FT_DEFAULT, TS_0WIDTH | TS_0HEIGHT);
			PRN_PrintText("����: " + bundle.getString("zhaolin") + "\r\n",
					ALIGNMENT_RIGHT, FT_DEFAULT, TS_0WIDTH | TS_0HEIGHT);

			PRN_PrintText("--------------------------------\r\n",
					ALIGNMENT_LEFT, FT_DEFAULT, TS_0WIDTH | TS_0HEIGHT);
			PRN_PrintText("  ɨ��׷�ݲ�Ʒ\n", ALIGNMENT_LEFT, FT_BOLD, TS_0WIDTH
					| TS_0HEIGHT);
			PRN_PrintText("      ", ALIGNMENT_LEFT, FT_BOLD, TS_0WIDTH
					| TS_0HEIGHT);
			PRN_PrintBitmap(qrBitmap, IMAGE_NORMAL);

			PRN_LineFeed(2);

			PRN_PrintText("��ܰ��ʾ�������Ʊ�����������ƾ֤�Ա���Դ���ٱ��绰12331\r\n", ALIGNMENT_LEFT,
					FT_BOLD, TS_0WIDTH | TS_0HEIGHT);

			PRN_PrintText("����ʡʳƷҩƷ�ල�����\r\n", ALIGNMENT_CENTER, FT_BOLD,
					TS_0WIDTH | TS_0HEIGHT);

			PRN_LineFeed(7);

			List<StoreItem> slist = new ArrayList<StoreItem>();
			for (int i = 0; i < list.size(); i++) {
				StoreItem item = new StoreItem(list.get(i).item_2,
						Integer.parseInt(list.get(i).item_8), "", "",
						list.get(i).item_9);
				slist.add(item);
			}
			Stores stores = new Stores(purchaseInDocumentNum, "SaleOut",
					commonService.cropCode, commonService.lastPhoto, slist,
					bundle.getBoolean("isYB"));

			boolean isSuccess = commonService.uploadDocumentByHttp(stores);
			if (isSuccess) {
				list.clear();

				purchaseInListView.setAdapter(adapter);
				purchaseInDocumentNum = Util.getDocumentNumFromDate();

				Toast.makeText(this, "�ϴ��ɹ�", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(this, "�ϴ�ʧ��", Toast.LENGTH_LONG).show();
			}
			System.out.println("�ص�����2");

			isFromPop = true;
		}
		jsCount = 0;

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
			// �����������Ҽ�
			if (event.getKeyCode() == 19 || event.getKeyCode() == 20
					|| event.getKeyCode() == 21 || event.getKeyCode() == 22) {
				System.out.println("������������Ҽ�");
				return true;
			}
			if (event.getKeyCode() == 66) {
				if (isInEditModel) {
					System.out.println("����ȷ����");
					return true;
				}
			}

			// ���+�ţ��޸Ĳ�Ʒ�۸�
			if (event.getKeyCode() == 157) {

				if (dipatchPriceCount % 2 == 0) {
					lastKeyCode = 157;
					if (timeFlag == true) {
						// beginTimerPrice();
					}
				}
				dipatchPriceCount++;

				return true;
			}
			// ���*�ţ��޸Ĳ�Ʒ����
			if (event.getKeyCode() == 155) {

				if (dipatchPriceCount % 2 == 0) {
					lastKeyCode = 155;
					if (timeFlag == true) {

					}
				}
				dipatchPriceCount++;

				return true;
			}
			// ���#�ż�,�����ƿ�����λ����λ��
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
						isInEditModel = true;
					} else {
						timeFlag = false;
						isInEditModel = false;
						codeInputEditText.requestFocus();

						index = -1;
						adapter.selectIndex = index;
						purchaseInListView.setAdapter(adapter);
						purchaseInListView.setSelection(index);
					}
				}
				dipatchChangeFocusCount++;
				Log.i(Constants.msg, "KEYCODE70");

				// ȷ��ѡ���index
				// int index=1;
				// adapter.selectIndex=index;
				//
				// purchaseInListView.setAdapter(adapter);
				// purchaseInListView.setSelection(index);

				return true;
			}

			// ���+�ż�,�۸��޸Ĺ��ܰ�ť
			// if (event.getKeyCode() == 157) {
			//
			// return false;
			// }

			// ���X�ż�,�����޸Ĺ��ܰ�ť
			// if (event.getKeyCode() == 155) {
			//
			// return false;
			// }
			// ���/�ż�,���㣬�ϴ����ݹ��ܰ�ť���������
			if (event.getKeyCode() == 76) {

				if (isInEditModel) {
					System.out.println("�޸Ľ�������ȷ�ϼ�");
					return true;
				}

				System.out.println("���۳������" + "jscount" + jsCount);

				if (jsCount % 2 == 1) {
					System.out.println("/haojian");
					saleOut();
				} else {

					if (isFromPop) {
						jsCount = 0;
						isFromPop = false;
					} else {
						jsCount++;
					}
					System.out.println("���۳������" + "jscount++");

					return true;
				}

				codeInputEditText.requestFocus();
				index = -1;
				adapter.selectIndex = index;
				purchaseInListView.setAdapter(adapter);
				purchaseInListView.setSelection(index);

				return true;
			}
			// ���*�ż�
			if (event.getKeyCode() == 158) {
				Log.i(Constants.msg, "KEYCODE158");
				event = new KeyEvent(event.getDownTime(), event.getEventTime(),
						event.getAction(), KeyEvent.KEYCODE_PERIOD,
						event.getRepeatCount(), event.getMetaState(),
						event.getDeviceId(), event.getScanCode(),
						event.getFlags());
			}
			// ���-�ż�,ɾ����
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

	private void saleOut() {
		// TODO Auto-generated method stub
		Intent addInfoIntent = new Intent(this, PopJSActivity.class);
		Bundle bundle = new Bundle();

		double MOey = 0;

		for (int i = 0; i < list.size(); i++) {
			MOey += (Double.parseDouble(list.get(i).item_7) * Integer
					.parseInt(list.get(i).item_8));

		}
		allMoney = String.valueOf(MOey);
		bundle.putString("Money", String.valueOf(MOey));
		bundle.putString("batchNum", purchaseInDocumentNum);
		addInfoIntent.putExtras(bundle);
		startActivityForResult(addInfoIntent, 30);
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
	 * ��ʱ���������position
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
