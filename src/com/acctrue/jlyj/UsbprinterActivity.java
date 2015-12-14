package com.acctrue.jlyj;

import java.util.ArrayList;
import java.util.HashMap;

import org.yrc.print.PrintCode;
import org.yrc.print.UsbPrintHelper;

import android.content.BroadcastReceiver;
import android.graphics.Bitmap;
import android.hardware.usb.UsbDevice;
import android.os.Handler;
import android.os.Message;

import com.acctrue.jlyj.activity.UmengActivity;

public class UsbprinterActivity extends UmengActivity implements PrintCode {
	protected static final String TAG = UsbprinterActivity.class
			.getSimpleName();
	BroadcastReceiver mUsbReceiver;
	/** arg1 current count, arg2 max count */
	private static final int MSG_SCAN_USB_PORT = 80;

	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SCAN_USB_PORT:
				scanUsbPort();
				break;

			default:
				break;
			}

		}
	};

	public void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (ThisApplication.mUsbDevice == null) {
			mUsbReceiver = ThisApplication.mUsbPrint.registerReceiver();
			scanUsbPort(true);
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mUsbReceiver != null) {
			unregisterReceiver(mUsbReceiver);
		}
		mHandler.removeCallbacksAndMessages(null);
	}

	private void scanUsbPort() {
		HashMap<String, UsbDevice> usbDevices = ThisApplication.mUsbPrint
				.getUsbManager().getDeviceList();

		ArrayList<String> al = new ArrayList<String>(usbDevices.size());

		for (UsbDevice localDevice : usbDevices.values()) {
			if (UsbPrintHelper.isUsbPrinterDevice(localDevice)) {
				if (ThisApplication.mUsbDevice == null) {
					ThisApplication.mUsbDevice = localDevice;
				}
				boolean reqPer = UsbPrintHelper.requestPermission(
						ThisApplication.mUsbPrint.getContext(),
						ThisApplication.mUsbPrint.getUsbManager(), localDevice);
				al.add(localDevice.getDeviceName());
			}
		}
	}

	private void scanUsbPort(boolean resetAll) {
		ThisApplication.mUsbDevice = null;
		int delay = 500;
		if (resetAll) { // 全部复位一次
			ThisApplication.mUsbPrint.scanUsbPrinterDevicesWithReset();
			delay = 1000;
		}
		mHandler.removeMessages(MSG_SCAN_USB_PORT);
		mHandler.sendEmptyMessageDelayed(MSG_SCAN_USB_PORT, delay);
	}

	public void PRN_PrintText(String text) {
		ThisApplication.mUsbPrint.printText(ThisApplication.mUsbDevice, text,
				ALIGNMENT_CENTER, FT_BOLD, 0x10 | 0x01);
	}

	public void PRN_PrintBarcode(Bitmap bmp1) {
		ThisApplication.mUsbPrint.printBitmap(ThisApplication.mUsbDevice, bmp1,
				IMAGE_NORMAL);
	}

	public void PRN_PrintBitmap(Bitmap qrBitmap, int imageNormal) {
		ThisApplication.mUsbPrint.printBitmap(ThisApplication.mUsbDevice,
				qrBitmap, imageNormal);
	}

	public void PRN_PrintBarcode(byte[] data, int symbology, int width,
			int height, int alignment, int textPosition) {
		ThisApplication.mUsbPrint.printBarcode(ThisApplication.mUsbDevice,
				data, symbology, height, width, alignment, textPosition);
	}

	public void PRN_PrintText(String txt, int alignment, int attribute,
			int textSize) {
		ThisApplication.mUsbPrint.printText(ThisApplication.mUsbDevice, txt,
				alignment, attribute, textSize);
	}

	public void PRN_LineFeed(int i) {

	}

}
