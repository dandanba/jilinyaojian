package com.acctrue.jlyj.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Hashtable;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import android.annotation.SuppressLint;
import android.app.ActivityGroup;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Matrix;
import android.util.Base64;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

@SuppressLint("SimpleDateFormat")
@SuppressWarnings("deprecation")
public class Util {
	
	/**
	 * 切换activity
	 * @param view 要切换的activity
	 * @param intent 
	 * @param ag 父布局
	 */
	
	public static int width = 200;
	public static void switchActivity(LinearLayout view,Intent intent,ActivityGroup ag){
		if (view!=null) {
			view.removeAllViews();
		}
		if (intent != null) {
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			Window subActivity = ag.getLocalActivityManager().startActivity(
					"subActivity", intent);
			view.addView(subActivity.getDecorView(),
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//			ag.getLocalActivityManager().getActivity()
//			view.removeView(view);
		}
		
	}
	
	//有日期申城出库单号
	public static String getDocumentNumFromDate()
	{			
		SimpleDateFormat formatter = new SimpleDateFormat("yyyymmddhhmmss");
				
		Date nowDate = new Date(System.currentTimeMillis());
				
		return formatter.format(nowDate);
	}

	//图片转化为base64字符串
	public static String transImageToBase64(Bitmap map)
	{
		double maxSize =10.00;
        //将bitmap放至数组中，意在bitmap的大小（与实际读取的原文件要大）  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        map.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        //将字节换成KB
        double mid = b.length/1024;
        //判断bitmap占用空间是否大于允许最大空间  如果大于则压缩 小于则不压缩
        if (mid > maxSize) {
                //获取bitmap大小 是允许最大大小的多少倍
                double i = mid / maxSize;
                //开始压缩  此处用到平方根 将宽带和高度压缩掉对应的平方根倍 （1.保持刻度和高度和原bitmap比率一致，压缩后也达到了最大大小占用空间的大小）
                map = zoomImage(map, map.getWidth() / Math.sqrt(i),
                		map.getHeight() / Math.sqrt(i));
        }
		
                //图片base64处理
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
		map.compress(CompressFormat.PNG, 100, bStream);
		byte[] bytes = bStream.toByteArray();
		String imStr = Base64.encodeToString(bytes, Base64.DEFAULT);
		
		System.out.println(imStr);
		
		return imStr;
		
		
	}
	
	public static Bitmap zoomImage(Bitmap bgimage, double newWidth,
            double newHeight) {
    // 获取这个图片的宽和高
    float width = bgimage.getWidth();
    float height = bgimage.getHeight();
    // 创建操作图片用的matrix对象
    Matrix matrix = new Matrix();
    // 计算宽高缩放率
    float scaleWidth = ((float) newWidth) / width;
    float scaleHeight = ((float) newHeight) / height;
    // 缩放图片动作
    matrix.postScale(scaleWidth, scaleHeight);
    Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                    (int) height, matrix, true);
    return bitmap;
	}

	
	//判断路径文件是否存在
	public static boolean isExist(String path) {
		
		File file = new File(path);
		// 判断文件夹是否存在,如果不存在则创建文件夹
		if (file.exists()) {
			return true;
		} else {
			return false;
		}
	}
	
	public static Bitmap createQRBitMap(String Num)
	{
		try {
            
            QRCodeWriter writer = new QRCodeWriter();

            String text = Num;

            if (text == null || "".equals(text) || text.length() < 1) {
                return null;
            }

            
            BitMatrix martix = writer.encode(text, BarcodeFormat.QR_CODE,
            		width, width);

            System.out.println("w:" + martix.getWidth() + "h:"
                    + martix.getHeight());

            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            BitMatrix bitMatrix = new QRCodeWriter().encode(text,
                    BarcodeFormat.QR_CODE, width, width, hints);
            int[] pixels = new int[width * width];
            for (int y = 0; y < width; y++) {
                for (int x = 0; x < width; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * width + x] = 0xff000000;
                    } else {
                        pixels[y * width + x] = 0xffffffff;
                    }

                }
            }

            Bitmap bitmap = Bitmap.createBitmap(width, width,
                    Bitmap.Config.ARGB_8888);

            bitmap.setPixels(pixels, 0, width, 0, 0, width, width);
            
            return bitmap;
            
		} catch (WriterException e) {
            e.printStackTrace();
        }
		return null;
	}


	
}
