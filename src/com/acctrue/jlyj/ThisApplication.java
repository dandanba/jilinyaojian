/* NFCard is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
(at your option) any later version.

NFCard is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Wget.  If not, see <http://www.gnu.org/licenses/>.

Additional permission under GNU GPL version 3 section 7 */

package com.acctrue.jlyj;

import org.yrc.print.UsbPrint;

import android.app.Application;
import android.hardware.usb.UsbDevice;

//import org.yrc.print.UsbPrint;

public final class ThisApplication extends Application {
	private static ThisApplication instance;
	public static UsbDevice mUsbDevice;
	public static UsbPrint mUsbPrint;

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
	}

}
