package com.acctrue.jlyj.activity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;

import com.acctrue.jlyj.Config;
import com.acctrue.jlyj.R;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

public class PopJSActivity extends UmengActivity {

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

	private double zl;

	private TextView jsTV;
	private EditText factInEditText;
	private TextView jsTV_1;

	private Bundle bundle;
	private int sureCount;

	private Switch methodSwitch;

	private boolean isYB;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pop_js);

		sureCount = 0;

		zl = 0;

		isYB = false;

		jsTV = (TextView) this.findViewById(R.id.js_tv);
		jsTV_1 = (TextView) this.findViewById(R.id.js_tv_1);

		factInEditText = (EditText) this.findViewById(R.id.fact_in_edit_text);
		factInEditText.setSingleLine(true);

		bundle = getIntent().getExtras();

		methodSwitch = (Switch) this.findViewById(R.id.method_switch);
		methodSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {

					System.out.println("¥Úø™◊¥Ã¨");

					isYB = true;
				} else {

					System.out.println("πÿ±’◊¥Ã¨");

					isYB = false;

				}

			}
		});

		String title = "µ•∫≈£∫" + bundle.getString("batchNum") + "\n\n";

		String money = "◊‹º∆£∫" + toFormatString(bundle.getString("Money")) + "\n";

		jsTV.setText(title + money);

		factInEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s != null && s.length() > 0) {
					zl = Double.parseDouble(s.toString())
							- Double.parseDouble(bundle.getString("Money"));
					jsTV_1.setText("’“¡„:" + toFormatString(zl));
				}

			}
		});

		findViewById(R.id.button1).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				factInEditText.append("1");
			}
		});
		findViewById(R.id.button2).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				factInEditText.append("2");
			}
		});
		findViewById(R.id.button3).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				factInEditText.append("3");
			}
		});
		findViewById(R.id.button4).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				factInEditText.append("4");
			}
		});
		findViewById(R.id.button5).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				factInEditText.append("5");
			}
		});
		findViewById(R.id.button6).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				factInEditText.append("6");
			}
		});
		findViewById(R.id.button7).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				factInEditText.append("7");
			}
		});
		findViewById(R.id.button8).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				factInEditText.append("8");
			}
		});
		findViewById(R.id.button9).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				factInEditText.append("9");
			}
		});
		findViewById(R.id.button0).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				factInEditText.append("0");
			}
		});

		findViewById(R.id.button_point).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						factInEditText.append(".");
					}
				});

		findViewById(R.id.button_del).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final String text = factInEditText.getText().toString();
				final int size = text.length() - 1;
				if (size > 0) {
					factInEditText.setText(text.substring(0, size));
				} else {
					factInEditText.setText("");
				}
			}
		});

		// if (!Config.sKeyIgnore) {
		// factInEditText
		// .setOnFocusChangeListener(new OnFocusChangeListener() {
		//
		// @Override
		// public void onFocusChange(View v, boolean hasFocus) {
		// // TODO Auto-generated method stub
		// if (hasFocus) {
		// factInEditText.setText("");
		// jsTV_1.setText("’“¡„:-----");
		//
		// } else {
		// zl = Double.parseDouble(factInEditText
		// .getText().toString())
		// - Double.parseDouble(bundle
		// .getString("Money"));
		// float number = (float) (Math.round(zl * 100)) / 100;
		// DecimalFormat decimalFormat = new DecimalFormat(
		// ".00");
		// jsTV_1.setText("’“¡„:"
		// + decimalFormat.format(number));
		//
		// }
		// }
		// });
		// // ∆¡±Œ»Ìº¸≈Ã
		// this.getWindow().setSoftInputMode(
		// WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		// Method setShowSoftInputOnFocus = null;
		// try {
		// setShowSoftInputOnFocus = factInEditText.getClass().getMethod(
		// "setShowSoftInputOnFocus", boolean.class);
		// } catch (NoSuchMethodException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// if (setShowSoftInputOnFocus != null) {
		// setShowSoftInputOnFocus.setAccessible(true);
		// }
		// try {
		// setShowSoftInputOnFocus.invoke(factInEditText, false);
		// } catch (IllegalArgumentException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (IllegalAccessException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (InvocationTargetException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// }

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pop_j, menu);
		return true;
	}

	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		//
		if (event.getKeyCode() == 76) {

			if (sureCount % 2 == 0) {

				this.inputOver();

				return true;
			} else {
				sureCount++;
				return true;
			}

		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			Intent intent = new Intent(this, SaleOutActivity.class);
			intent.putExtras(bundle);
			this.setResult(40, intent);
			this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	//  ‰»ÎΩ· ¯
	public void inputOver() {

		System.out.println("Ω· ¯≤Ÿ◊˜");

		// boudle∞Û∂®∑µªÿ–≈œ¢
		bundle.putString("infact", factInEditText.getText().toString());
		// bundle.putString("zhaolin", String.valueOf(zl));
		bundle.putString("zhaolin", String.format("%.2f", zl));

		bundle.putBoolean("isYB", isYB);

		Intent intent = new Intent(this, SaleOutActivity.class);
		intent.putExtras(bundle);
		this.setResult(30, intent);
		this.finish();
	}

	public void onConfirm(View view) {
		inputOver();
	}
}
