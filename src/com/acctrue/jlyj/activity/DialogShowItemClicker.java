package com.acctrue.jlyj.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;

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
	boolean focusedNumber;

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
		final ViewGroup v = (ViewGroup) view
				.findViewById(R.id.price_input_layout);
		priceEditText = (EditText) view.findViewById(R.id.price_input_et);
		numberEditText = (EditText) view.findViewById(R.id.number_input_et);
		numberEditText.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					focusedNumber = true;
					InputMethodManager inputMethodManager = (InputMethodManager) activity
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					inputMethodManager.toggleSoftInput(0,
							InputMethodManager.SHOW_FORCED);
				}
			}
		});
		priceEditText.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					focusedNumber = false;
					InputMethodManager inputMethodManager = (InputMethodManager) activity
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					inputMethodManager.toggleSoftInput(0,
							InputMethodManager.SHOW_FORCED);
				}
			}
		});

		view.findViewById(R.id.button1).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (focusedNumber) {
							numberEditText.append("1");
						} else {
							priceEditText.append("1");
						}
					}
				});
		view.findViewById(R.id.button2).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (focusedNumber) {
							numberEditText.append("2");
						} else {
							priceEditText.append("2");
						}
					}
				});
		view.findViewById(R.id.button3).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (focusedNumber) {
							numberEditText.append("3");
						} else {
							priceEditText.append("3");
						}
					}
				});
		view.findViewById(R.id.button4).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (focusedNumber) {
							numberEditText.append("4");
						} else {
							priceEditText.append("4");
						}
					}
				});
		view.findViewById(R.id.button5).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (focusedNumber) {
							numberEditText.append("5");
						} else {
							priceEditText.append("5");
						}
					}
				});
		view.findViewById(R.id.button6).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (focusedNumber) {
							numberEditText.append("6");
						} else {
							priceEditText.append("6");
						}
					}
				});
		view.findViewById(R.id.button7).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (focusedNumber) {
							numberEditText.append("7");
						} else {
							priceEditText.append("7");
						}
					}
				});
		view.findViewById(R.id.button8).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (focusedNumber) {
							numberEditText.append("8");
						} else {
							priceEditText.append("8");
						}
					}
				});
		view.findViewById(R.id.button9).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (focusedNumber) {
							numberEditText.append("9");
						} else {
							priceEditText.append("9");
						}
					}
				});
		view.findViewById(R.id.button0).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (focusedNumber) {
							numberEditText.append("0");
						} else {
							priceEditText.append("0");
						}
					}
				});

		view.findViewById(R.id.button_point).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (focusedNumber) {
							numberEditText.append(".");
						} else {
							priceEditText.append(".");
						}
					}
				});
		view.findViewById(R.id.button_del).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (focusedNumber) {
							final String text = numberEditText.getText()
									.toString();
							final int size = text.length() - 1;
							if (size > 0) {
								numberEditText.setText(text.substring(0, size));
							} else {
								numberEditText.setText("");
							}
						} else {
							final String text = priceEditText.getText()
									.toString();
							final int size = text.length() - 1;
							if (size > 0) {
								priceEditText.setText(text.substring(0, size));
							} else {
								priceEditText.setText("");
							}
						}
					}
				});

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

			priceEditText.setSelection(item2.item_7.length());
			numberEditText.setSelection(item2.item_8.length());

		} else if (tag instanceof FMAdapter.ViewHolder) {
			lv_item item = (lv_item) ((FMAdapter.ViewHolder) tag).tv_7.getTag();
			priceEditText.setText(item.item_7);
			numberEditText.setText(item.item_8);
			priceEditText.setSelection(item.item_7.length());
			numberEditText.setSelection(item.item_8.length());
		} else if (tag instanceof FMAdapter3.ViewHolder) {
			lv_item3 item3 = (lv_item3) ((FMAdapter3.ViewHolder) tag).tv_7
					.getTag();
			v.setVisibility(View.GONE);
			numberEditText.setText(item3.item_8);
			numberEditText.setSelection(item3.item_8.length());
		}

		alertDialog.show();

	}

	public void setAdapter(BaseAdapter adapter) {
		this.adapter = adapter;
	}

}
