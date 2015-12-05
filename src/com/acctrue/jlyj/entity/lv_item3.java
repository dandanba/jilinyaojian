package com.acctrue.jlyj.entity;

import java.util.ArrayList;

import android.content.Context;
import android.widget.Toast;

import com.acctrue.jlyj.service.commonService;
import com.acctrue.jlyj.util.Util;

public class lv_item3 {
	public String item_1;
	public String item_2;
	public String item_3;
	public String item_5;
	public String item_8;
	public boolean item_9;

	public void upload(Context context) {
		StoreItem storeItem = new StoreItem(this.item_2,
				Integer.parseInt(this.item_8), this.item_5, "", false);
		ArrayList<StoreItem> slist = new ArrayList<StoreItem>();
		slist.add(storeItem);
		Stores stores = new Stores(Util.getDocumentNumFromDate(), "CheckIn",
				commonService.cropCode, "", slist, false);
		boolean flag = commonService.updateItem(stores);
		if (flag) {
			Toast.makeText(context, "修改成功", Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(context, "修改失败", Toast.LENGTH_LONG).show();
		}
	}
}
