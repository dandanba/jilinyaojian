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
	 * �л�activity
	 * @param view Ҫ�л���activity
	 * @param intent 
	 * @param ag ������
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
	
	//��������ǳ��ⵥ��
	public static String getDocumentNumFromDate()
	{			
		SimpleDateFormat formatter = new SimpleDateFormat("yyyymmddhhmmss");
				
		Date nowDate = new Date(System.currentTimeMillis());
				
		return formatter.format(nowDate);
	}

	//ͼƬת��Ϊbase64�ַ���
	public static String transImageToBase64(Bitmap map)
	{
		double maxSize =10.00;
        //��bitmap���������У�����bitmap�Ĵ�С����ʵ�ʶ�ȡ��ԭ�ļ�Ҫ��  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        map.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        //���ֽڻ���KB
        double mid = b.length/1024;
        //�ж�bitmapռ�ÿռ��Ƿ�����������ռ�  ���������ѹ�� С����ѹ��
        if (mid > maxSize) {
                //��ȡbitmap��С ����������С�Ķ��ٱ�
                double i = mid / maxSize;
                //��ʼѹ��  �˴��õ�ƽ���� ������͸߶�ѹ������Ӧ��ƽ������ ��1.���̶ֿȺ͸߶Ⱥ�ԭbitmap����һ�£�ѹ����Ҳ�ﵽ������Сռ�ÿռ�Ĵ�С��
                map = zoomImage(map, map.getWidth() / Math.sqrt(i),
                		map.getHeight() / Math.sqrt(i));
        }
		
                //ͼƬbase64����
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
		map.compress(CompressFormat.PNG, 100, bStream);
		byte[] bytes = bStream.toByteArray();
		String imStr = Base64.encodeToString(bytes, Base64.DEFAULT);
		
		System.out.println(imStr);
		
		return imStr;
		
		
	}
	
	public static Bitmap zoomImage(Bitmap bgimage, double newWidth,
            double newHeight) {
    // ��ȡ���ͼƬ�Ŀ�͸�
    float width = bgimage.getWidth();
    float height = bgimage.getHeight();
    // ��������ͼƬ�õ�matrix����
    Matrix matrix = new Matrix();
    // ������������
    float scaleWidth = ((float) newWidth) / width;
    float scaleHeight = ((float) newHeight) / height;
    // ����ͼƬ����
    matrix.postScale(scaleWidth, scaleHeight);
    Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                    (int) height, matrix, true);
    return bitmap;
	}

	
	//�ж�·���ļ��Ƿ����
	public static boolean isExist(String path) {
		
		File file = new File(path);
		// �ж��ļ����Ƿ����,����������򴴽��ļ���
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
