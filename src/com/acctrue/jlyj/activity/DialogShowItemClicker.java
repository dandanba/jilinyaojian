package com.acctrue.jlyj.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.acctrue.jlyj.R;
import com.acctrue.jlyj.adapter.FMAdapter;
import com.acctrue.jlyj.adapter.FMAdapter2;
import com.acctrue.jlyj.adapter.FMAdapter3;
import com.acctrue.jlyj.entity.lv_item;
import com.acctrue.jlyj.entity.lv_item2;
import com.acctrue.jlyj.entity.lv_item3;

public class DialogShowItemClicker implements OnClickListener {
	public DialogShowItemClicker(Context context) {
		this.activity = (Activity) context;
	}

	Activity activity;
	boolean number;
	boolean price;
	private BaseAdapter adapter;
	EditText priceEditText;
	EditText numberEditText;

	@Override
	public void onClick(View view) {
		final Object tag = view.getTag();

		showDialog(tag);
	}

	private void showDialog(final Object tag) {

		final AlertDialog alertDialog = new AlertDialog.Builder(activity)
				.create();

		final View view = activity.getLayoutInflater().inflate(
				R.layout.activity_edit_info, null);
		alertDialog.setView(view);
		priceEditText = (EditText) view.findViewById(R.id.price_input_et);
		numberEditText = (EditText) view.findViewById(R.id.number_input_et);
		view.findViewById(R.id.confirm_button).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (tag instanceof FMAdapter2.ViewHolder) {
							lv_item2 item2 = (lv_item2) ((FMAdapter2.ViewHolder) tag).tv_7
									.getTag();
							item2.item_7 = priceEditText.getText().toString();
							item2.item_8 = numberEditText.getText().toString();
						} else if (tag instanceof FMAdapter.ViewHolder) {
							lv_item item2 = (lv_item) ((FMAdapter.ViewHolder) tag).tv_7
									.getTag();
							item2.item_7 = priceEditText.getText().toString();
							item2.item_8 = numberEditText.getText().toString();
						} else if (tag instanceof FMAdapter3.ViewHolder) {
							lv_item3 item3 = (lv_item3) ((FMAdapter3.ViewHolder) tag).tv_7
									.getTag();
							item3.item_8 = numberEditText.getText().toString();
							item3.upload(v.getContext());
						}
						adapter.notifyDataSetChanged();
						alertDialog.dismiss();
					}
				});
		if (tag instanceof FMAdapter2.ViewHolder) {
			lv_item2 item2 = (lv_item2) ((FMAdapter2.ViewHolder) tag).tv_7
					.getTag();
			priceEditText.setText(item2.item_7);
			numberEditText.setText(item2.item_8);
		} else if (tag instanceof FMAdapter.ViewHolder) {
			lv_item item = (lv_item) ((FMAdapter.ViewHolder) tag).tv_7.getTag();
			priceEditText.setText(item.item_7);
			numberEditText.setText(item.item_8);
		} else if (tag instanceof FMAdapter3.ViewHolder) {
			lv_item3 item3 = (lv_item3) ((FMAdapter3.ViewHolder) tag).tv_7
					.getTag();
			priceEditText.setVisibility(View.GONE);
			numberEditText.setText(item3.item_8);
		}
		alertDialog.show();
	}

	public void setAdapter(BaseAdapter adapter) {
		this.adapter = adapter;
	}

}