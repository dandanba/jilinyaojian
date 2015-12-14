package com.acctrue.jlyj.activity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.acctrue.jlyj.Config;
import com.acctrue.jlyj.R;
import com.acctrue.jlyj.adapter.FMAdapter3;
import com.acctrue.jlyj.entity.StoreItem;
import com.acctrue.jlyj.entity.Stores;
import com.acctrue.jlyj.entity.lv_item3;
import com.acctrue.jlyj.service.commonService;
import com.acctrue.jlyj.util.Constants;
import com.acctrue.jlyj.util.Util;

import de.greenrobot.event.EventBus;

public class StockInfoMatirials extends UmengActivity implements
		OnFocusChangeListener {

	private ListView purchaseInListView; // �ɹ�������б�

	private EditText codeInputEditText; // ���������

	private EditText batchInputEditText;

	private TextView blankTextView; // �հ׽���TextView

	private TextView batchTitle;

	private ArrayList<lv_item3> list; // ������ݵ�list

	private FMAdapter3 adapter; // ����������

	private TextView titleTV;

	private String matiralCode;

	private String batchNo;

	private String state;

	private int dipatchChangeFocusCount;

	private int dipatchPriceCount;

	private int lastKeyCode;

	private int time = 0; // ��������ʱʱ��

	private StringBuffer postion = new StringBuffer(); // ���������

	private int page = 1;

	private boolean timeFlag = false;

	private TextView pageMenu;// ��ʾҳ��

	private int total, totalpage;// ��������ҳ��

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
				// if (time == 0) {
				// index = Integer.parseInt(postion.toString());
				// adapter.selectIndex = index;
				// Log.i(Constants.msg, index + "");
				// purchaseInListView.setAdapter(adapter);
				// purchaseInListView.setSelection(index - 1);
				// purchaseInListView.requestFocus();
				// postion = new StringBuffer();
				// }
				break;
			case Constants.beginTimePrice:
				time += 1;
				Log.i(Constants.msg, "beginPrice" + postion.toString());
				break;
			case Constants.endTimePrice:
				time -= 1;
				// if (time == 0) {
				// list.get(index - 1).item_7 = postion.toString();
				// purchaseInListView.setAdapter(adapter);
				// purchaseInListView.setSelection(index - 1);
				// purchaseInListView.requestFocus();
				// lastKeyCode=70;
				// Log.i(Constants.msg, "price"+postion.toString());
				// postion = new StringBuffer();
				// }
				break;
			case Constants.beginTimeNumber:
				time += 1;
				Log.i(Constants.msg, "begin" + postion.toString());
				break;
			case Constants.endTimeNumber:
				time -= 1;
				if (time == 0) {
					list.get(0).item_8 = postion.toString();
					purchaseInListView.setAdapter(adapter);
					// purchaseInListView.setSelection(index - 1);
					// purchaseInListView.requestFocus();
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

	private int sureCount;

	private int dipatchRight;

	private int dipatchLeft;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
		setContentView(R.layout.layout_order3);
		System.setProperty("file.encoding", "gb2312");
		// ���ݡ��ؼ���ʼ��
		this.init();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

	public void onEvent(Object event) {
		if (event instanceof String) {
			if (event.equals("����")) {
				searchData();
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!Config.sKeyIgnore) {
			codeInputEditText.requestFocus();
		}
	}

	private void init() {
		titleTV = (TextView) this.findViewById(R.id.title);
		titleTV.setText("������");
		batchTitle = (TextView) this.findViewById(R.id.batchTitle);

		batchTitle.setVisibility(View.VISIBLE);
		list = new ArrayList<lv_item3>();

		pageMenu = (TextView) findViewById(R.id.pageMenu);

		getPageData();// ��ȡҳ����Ϣ

		purchaseInListView = (ListView) this.findViewById(R.id.lv);

		codeInputEditText = (EditText) this.findViewById(R.id.data);

		batchInputEditText = (EditText) this.findViewById(R.id.batch);

		batchInputEditText.setVisibility(View.VISIBLE);

		blankTextView = (TextView) this.findViewById(R.id.blank);

		if (!Config.sKeyIgnore) {
			codeInputEditText.setOnFocusChangeListener(this);

			batchInputEditText.setOnFocusChangeListener(this);

			blankTextView.setOnFocusChangeListener(this);
		}

		getData(page);// ��ȡ���

		// adapter = new FMAdapter3(list, this);
		//
		// purchaseInListView.setAdapter(adapter);
		if (!Config.sKeyIgnore) {
			// ���������
			this.getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			Method setShowSoftInputOnFocus = null;
			try {
				setShowSoftInputOnFocus = codeInputEditText.getClass()
						.getMethod("setShowSoftInputOnFocus", boolean.class);
				setShowSoftInputOnFocus = batchInputEditText.getClass()
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
				setShowSoftInputOnFocus.invoke(batchInputEditText, false);
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

	private void getPageData() {
		total = commonService.getTotal();
		totalpage = total / 10;
		if (total % 10 == 0) {
			totalpage = total / 10;
		} else {
			totalpage = (total / 10) + 1;
		}
		pageMenu.setText("��" + page + "ҳ,��" + totalpage + "ҳ");
	}

	private void getData(int page) {
		// for (int i = 0; i < 12; i++) {
		// lv_item3 item3 = new lv_item3();
		// item3.item_1 = i + 1 + "";
		// item3.item_2 = "6523" + i;
		// item3.item_3 = i + "name";
		// item3.item_5 = "2014" + 1 * 10;
		// item3.item_8 = (int) Math.random() * 100 + "";
		// list.add(item3);
		//
		// }
		list.clear();
		new AsyncTask<Integer, Void, ArrayList<lv_item3>>() {

			@Override
			protected ArrayList<lv_item3> doInBackground(Integer... params) {
				final java.util.ArrayList<lv_item3> list = commonService
						.getStockInfo("empty", "empty", params[0]);
				return list;
			}

			protected void onPostExecute(java.util.ArrayList<lv_item3> result) {
				final java.util.ArrayList<lv_item3> list = result;
				if (list.size() == 0) {
					Toast.makeText(StockInfoMatirials.this, "û�ж�Ӧ����",
							Toast.LENGTH_LONG).show();
				} else {
					StockInfoMatirials.this.list.addAll(list);
					adapter = new FMAdapter3(StockInfoMatirials.this.list,
							StockInfoMatirials.this);
					purchaseInListView.setAdapter(adapter);
				}

			};

		}.execute(page);

	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (!Config.sKeyIgnore) {
			if (event.getKeyCode() == 19 || event.getKeyCode() == 20
					|| event.getKeyCode() == 21 || event.getKeyCode() == 22) {
				System.out.println("������������Ҽ�");
				return true;
			}

			if (event.getKeyCode() == 70) {
				if (dipatchChangeFocusCount % 2 == 0) {
					if (getCurrentFocus().equals(codeInputEditText)) {
						batchInputEditText.requestFocus();
					} else if (getCurrentFocus().equals(batchInputEditText)) {
						batchInputEditText.clearFocus();
						searchData();
					} else {
						codeInputEditText.requestFocus();
						getData(page);
					}
				}
				dipatchChangeFocusCount++;
				return false;
			}
			// ���*�ţ��޸Ĳ�Ʒ����
			if (event.getKeyCode() == 155) {

				if (dipatchPriceCount % 2 == 0) {
					lastKeyCode = 155;
					timeFlag = true;
					// if (timeFlag == true) {
					//
					// }
				}
				dipatchPriceCount++;

				return false;
			}

			// ���/�ż�,���㣬�ϴ����ݹ��ܰ�ť���������
			if (event.getKeyCode() == 76) {

				System.out.println("������㰴ť");

				if (sureCount % 2 == 0) {
					submitNumber();// �ύ�޸�
				}
				sureCount++;
				return false;
			}
			// ����ң���һҳ
			if (event.getKeyCode() == 22) {

				if (dipatchRight % 2 == 0) {
					page++;
					if (page > totalpage) {
						Toast.makeText(this, "�������һҳ", Toast.LENGTH_LONG)
								.show();
						page = totalpage;
					} else {
						getData(page);
						// adapter=new FMAdapter3(list, this);
						// purchaseInListView.setAdapter(adapter);
						pageMenu.setText("��" + page + "ҳ,��" + totalpage + "ҳ");
					}
				}
				dipatchRight++;

				return true;
			}
			// �������һҳ
			if (event.getKeyCode() == 21) {

				if (dipatchLeft % 2 == 0) {
					page--;
					if (page < 1) {
						Toast.makeText(this, "���ǵ�һҳ", Toast.LENGTH_LONG).show();
						page = 1;
					} else {
						getData(page);
						// adapter=new FMAdapter3(list, this);
						// purchaseInListView.setAdapter(adapter);
						pageMenu.setText("��" + page + "ҳ,��" + totalpage + "ҳ");
					}
				}
				dipatchLeft++;

				return true;
			}
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

	private void submitNumber(lv_item3 item) {
		StoreItem storeItem = new StoreItem(item.item_2,
				Integer.parseInt(item.item_8), item.item_5, "", false);
		ArrayList<StoreItem> slist = new ArrayList<StoreItem>();
		slist.add(storeItem);
		Stores stores = new Stores(Util.getDocumentNumFromDate(), "CheckIn",
				commonService.cropCode, "", slist, false);
		boolean flag = commonService.updateItem(stores);
		if (flag) {
			Toast.makeText(this, "�޸ĳɹ�", Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(this, "�޸�ʧ��", Toast.LENGTH_LONG).show();
		}
	}

	private void submitNumber() {
		if (list.size() == 1) {
			StoreItem storeItem = new StoreItem(list.get(0).item_2,
					Integer.parseInt(list.get(0).item_8), list.get(0).item_5,
					"", false);
			ArrayList<StoreItem> slist = new ArrayList<StoreItem>();
			slist.add(storeItem);
			Stores stores = new Stores(Util.getDocumentNumFromDate(),
					"CheckIn", commonService.cropCode, "", slist, false);
			boolean flag = commonService.updateItem(stores);
			if (flag) {
				Toast.makeText(this, "�޸ĳɹ�", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(this, "�޸�ʧ��", Toast.LENGTH_LONG).show();
			}
		} else {
			Toast.makeText(this, "����ѡ��Ҫ�޸ĵĲ�Ʒ���������", Toast.LENGTH_LONG).show();
		}
	}

	private void searchData() {

		list.clear();
		list = commonService.getStockInfo(codeInputEditText.getText()
				.toString(), batchInputEditText.getText().toString(), 1);
		// TODO:
		if (list.size() == 0) {
			Toast.makeText(this, "û�ж�Ӧ����", Toast.LENGTH_LONG).show();
		} else {
			adapter = new FMAdapter3(list, this);
			purchaseInListView.setAdapter(adapter);
		}
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
			if (event.getKeyCode() == KeyEvent.KEYCODE_PERIOD) {
				postion.append(".");
			}
			if (lastKeyCode == 155 && keyCode != 23) {
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

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		switch (v.getId()) {
		case R.id.batch:
			if (hasFocus) {

			} else {
				state = "batch";
			}
			break;
		case R.id.blank:
			if (hasFocus) {
				if (state.equals("code")) {
					batchInputEditText.requestFocus();
				} else if (state.equals("batch")) {
					codeInputEditText.requestFocus();
				}
			}
			break;
		case R.id.data:
			if (hasFocus) {

			} else {
				state = "code";
			}
			break;

		default:
			break;
		}
	}

	private void beginTimerNumber() {
		handler.post(beginTimeRunNumber);
		handler.postDelayed(endTimeRunNumber, 500);
	}

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

	public void onReset(View view) {
		page = 1;
		getData(page);
		codeInputEditText.setText("");
		batchInputEditText.setText("");
		pageMenu.setText("��" + page + "ҳ,��" + totalpage + "ҳ");
	}

	public void onLeft(View view) {
		page--;
		if (page < 1) {
			Toast.makeText(this, "���ǵ�һҳ", Toast.LENGTH_LONG).show();
			page = 1;
		} else {
			getData(page);
			// adapter=new FMAdapter3(list, this);
			// purchaseInListView.setAdapter(adapter);
			pageMenu.setText("��" + page + "ҳ,��" + totalpage + "ҳ");
		}

	}

	public void onRight(View view) {
		page++;
		if (page > totalpage) {
			Toast.makeText(this, "�������һҳ", Toast.LENGTH_LONG).show();
			page = totalpage;
		} else {
			getData(page);
			// adapter=new FMAdapter3(list, this);
			// purchaseInListView.setAdapter(adapter);
			pageMenu.setText("��" + page + "ҳ,��" + totalpage + "ҳ");
		}

	}
}
