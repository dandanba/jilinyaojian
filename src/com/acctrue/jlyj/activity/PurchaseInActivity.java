package com.acctrue.jlyj.activity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.acctrue.jlyj.Config;
import com.acctrue.jlyj.R;
import com.acctrue.jlyj.adapter.FMAdapter;
import com.acctrue.jlyj.entity.CodeInfo;
import com.acctrue.jlyj.entity.StoreItem;
import com.acctrue.jlyj.entity.Stores;
import com.acctrue.jlyj.entity.lv_item;
import com.acctrue.jlyj.service.commonService;
import com.acctrue.jlyj.util.Constants;
import com.acctrue.jlyj.util.Util;

public class PurchaseInActivity extends Activity implements
		OnFocusChangeListener {
	
	private boolean isInEditModel;
	
	private int sureCount;

	private int dipatchChangeFocusCount; // �ַ��л�ģʽ����

	private int dipatchChangeNumCount; // �ַ�������������

	private int dipatchPriceCount; // �ַ����ļ۸����

	private ListView purchaseInListView; // �ɹ�������б�

	private EditText codeInputEditText; // ���������

	private TextView blankTextView; // �հ׽���TextView

	private String purchaseInDocumentNum; // ��ⵥ��

	private ArrayList<lv_item> list; // ������ݵ�list

	private FMAdapter adapter; // ����������

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
						index = Integer.parseInt(postion.toString().replace(".", ""));
					} catch (Exception e) {
						postion=new StringBuffer();
						return;
					}
					if(index>list.size()){
						index=list.size();
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
					lastKeyCode=70;
					Log.i(Constants.msg, "price"+postion.toString());
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
					lastKeyCode=70;
					Log.i(Constants.msg, "num"+postion.toString());
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
		setContentView(R.layout.layout_order);
		// ���ݡ��ؼ���ʼ��
		this.init();

	}

	private void init() {
		
		isInEditModel = false;
		
		//��ʼ���������˱�־
		sureCount = 0;
		dipatchChangeFocusCount = 0;
		dipatchChangeNumCount = 0;
		dipatchPriceCount = 0;
		
		// ���ɳ��ⵥ��
		purchaseInDocumentNum = Util.getDocumentNumFromDate();
		
		
		// UI�ؼ���ʼ��
		titleTV = (TextView)this.findViewById(R.id.title);
		titleTV.setText("�ɹ����");
		
		list = new ArrayList<lv_item>();

		purchaseInListView = (ListView) this.findViewById(R.id.lv);

		codeInputEditText = (EditText) this.findViewById(R.id.data);

		blankTextView = (TextView) this.findViewById(R.id.blank);

		codeInputEditText.setOnFocusChangeListener(this);

		blankTextView.setOnFocusChangeListener(this);

		adapter = new FMAdapter(list, this);

		purchaseInListView.setAdapter(adapter);

		if(!Config.sKeyIgnore){
		//���������
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		Method setShowSoftInputOnFocus = null;
		try {
			setShowSoftInputOnFocus = codeInputEditText.getClass().getMethod(
					"setShowSoftInputOnFocus", boolean.class);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(setShowSoftInputOnFocus!=null){
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
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		codeInputEditText.requestFocus();
	}

	private void haveGetCode(String code) {

		CodeInfo codeInfo = commonService.getCodeInfoByHttp(code);
		if(codeInfo!=null){
			
			if (codeInfo.isYJCode) {
				lv_item item = new lv_item();
				item.item_1 = String.valueOf(list.size() + 1);
				item.item_2 = codeInfo.produtCode;
				item.item_3 = codeInfo.productName;
				item.item_4 = codeInfo.productProduceDate;
				item.item_5 = codeInfo.productBatchNum;
				item.item_6 = codeInfo.produceCropName;
				item.item_7 = codeInfo.productPrice;
				item.item_8 = String.valueOf(1);
				item.item_9 = true;
				list.add(item);
				purchaseInListView.setAdapter(adapter);
				purchaseInListView.setSelection(list.size() - 1);
			}
			else{
				Intent addInfoIntent = new Intent(this, PopAddInfoActivity.class);
				Bundle bundle=new Bundle();
				bundle.putString("produtCode",codeInfo.produtCode);
				bundle.putString("productName", codeInfo.productName);
				bundle.putString("produceCropName", codeInfo.produceCropName);
				bundle.putString("productPrice", codeInfo.productPrice);
				addInfoIntent.putExtras(bundle);
				startActivityForResult(addInfoIntent, 20);

			}
			
		}else {
			Toast.makeText(this, "û�в鵽������Ĳ�Ʒ��Ϣ", Toast.LENGTH_LONG).show();
		}
		
		
			
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		//�˴��������κź���������
		if (requestCode==20&&requestCode==20) {
			if(data==null)
			{
				return;
			}
			Bundle bundle=data.getExtras();
			
			lv_item item = new lv_item();
			item.item_1 = String.valueOf(list.size() + 1);
			item.item_2 = bundle.getString("produtCode","");
			item.item_3 = bundle.getString("productName", "");
			item.item_4 = bundle.getString("productProduceDate", "");
			item.item_5 = bundle.getString("productBatchNum", "");
			item.item_6 = bundle.getString("produceCropName", "");
			item.item_7 = bundle.getString("productPrice", "");
			item.item_8 = String.valueOf(1);
			item.item_9 = false;
			list.add(item);
			purchaseInListView.setAdapter(adapter);
			purchaseInListView.setSelection(list.size() - 1);
			
			dipatchChangeFocusCount=0;
			
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.data:
			if (hasFocus) {

			} else {
				
			}
			break;
		case R.id.blank:
			if (hasFocus) {

				this.haveGetCode(codeInputEditText.getText().toString());
				codeInputEditText.setText("");
				codeInputEditText.requestFocus();
			} else {
			}
			break;
		default:
			break;
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		
		
		
		// �����������Ҽ�
		if (event.getKeyCode() == 19 || event.getKeyCode() == 20 || event.getKeyCode() == 21 || event.getKeyCode() == 22) {
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

			return false;
		}
		// ���*�ţ��޸Ĳ�Ʒ����
		if (event.getKeyCode() == 155) {

			if (dipatchPriceCount % 2 == 0) {
				lastKeyCode = 155;
				if (timeFlag == true) {

				}
			}
			dipatchPriceCount++;

			return false;
		}
		// ���#�ż�,�����ƿ�����λ����λ��
		if (event.getKeyCode() == 70) {
			
			if (dipatchChangeFocusCount % 2 == 0) {
				lastKeyCode=70;
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
			return false;
		}
		// ���/�ż�,���㣬�ϴ����ݹ��ܰ�ť���������
		if (event.getKeyCode() == 76) {
			if (isInEditModel) {
				System.out.println("�޸Ľ�������ȷ�ϼ�");
				return true;
			}
			System.out.println("������㰴ť");
			
			if (sureCount % 2 == 1) {				
				System.out.println("����ύ����");
				
				if (list.size()==0) {
					Toast.makeText(this, "�ɹ���ⵥΪ��!", Toast.LENGTH_LONG).show();
					sureCount=0;
					return true;
				}
				
				List<StoreItem> slist=new ArrayList<StoreItem>();
				for (int i = 0; i < list.size(); i++) {
					commonService.uploadPriceByHttp(list.get(i).item_2, list.get(i).item_7);
					StoreItem item=new StoreItem(list.get(i).item_2, Integer.parseInt(list.get(i).item_8), list.get(i).item_5, list.get(i).item_4, list.get(i).item_9);
					slist.add(item);
				}
				Stores stores=new Stores(purchaseInDocumentNum, "PurchaseIn", commonService.cropCode, commonService.lastPhoto, slist,false);
				boolean isSuccess=commonService.uploadDocumentByHttp(stores);
				if (!isSuccess) {
					Toast.makeText(this, "�ϴ�ʧ��", Toast.LENGTH_LONG).show();
					codeInputEditText.requestFocus();
					index = -1;
					adapter.selectIndex = index;
					purchaseInListView.setAdapter(adapter);
					purchaseInListView.setSelection(index);
				}
				else
				{
					Toast.makeText(this, "�ϴ��ɹ�", Toast.LENGTH_LONG).show();
					codeInputEditText.requestFocus();
					index = -1;
					adapter.selectIndex = index;
					list.clear();
					purchaseInListView.setAdapter(adapter);
					purchaseInDocumentNum=Util.getDocumentNumFromDate();
				}
				sureCount=0;
			}
			else
			{
				sureCount++;
				return true;
			}
			
			

			return true;
		}
		// ���*�ż�
		if (event.getKeyCode() == 158) {
			Log.i(Constants.msg, "KEYCODE158");
			event = new KeyEvent(event.getDownTime(), event.getEventTime(),
					event.getAction(), KeyEvent.KEYCODE_PERIOD,
					event.getRepeatCount(), event.getMetaState(),
					event.getDeviceId(), event.getScanCode(), event.getFlags());
		}
		// ���-�ż�,ɾ����
		if (event.getKeyCode() == 156) {
//			event = new KeyEvent(event.getDownTime(), event.getEventTime(),
//					event.getAction(), KeyEvent.KEYCODE_DEL,
//					event.getRepeatCount(), event.getMetaState(),
//					event.getDeviceId(), event.getScanCode(), event.getFlags());
			return false;

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
			if(event.getKeyCode()==56){
				postion.append(".");
			}
			if (event.getKeyCode() == 158) {
				postion.append(".");
			}
			if (event.getKeyCode() == 112) {
				postion.append(".");
			}			
			if (lastKeyCode == 70&&keyCode!=23) {
				beginTimer();
			} else if (lastKeyCode == 157&&keyCode!=23) {
				beginTimerPrice();
			} else if (lastKeyCode == 155&&keyCode!=23) {
				beginTimerNumber();
			}
			
		}
		if(keyCode==KeyEvent.KEYCODE_BACK){
		}
		return super.onKeyUp(keyCode, event);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
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
