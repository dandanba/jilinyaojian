package com.acctrue.jlyj.activity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import android.app.ActivityGroup;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Bitmap.CompressFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.acctrue.jlyj.R;
import com.acctrue.jlyj.service.commonService;
import com.acctrue.jlyj.util.Constants;
import com.acctrue.jlyj.util.Util;



@SuppressWarnings("deprecation")
public class MenuActivity extends ActivityGroup {
	
	//�ֲ�ˢ��view
	LinearLayout view;
	
	//�˵� ��־
	int isMain;
	boolean flag=false;
	
	static boolean isCameraing = false;
	
	//������surfaceView
	private SurfaceView mainSurfaceView;
	private android.hardware.Camera camera;
	private android.hardware.Camera.Parameters parameters;
		  
	//������Ƭ��Bundle
	Bundle bundle = null; // ����һ��Bundle���������洢���� 
	
	private TextView menu;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//get������������
		if (android.os.Build.VERSION.SDK_INT > 9) {  
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build(); 
			StrictMode.setThreadPolicy(policy);
		}
		
		//Activty��ʼ��
		init();
		
		//�����������۳������
		Intent intent=new Intent(this,SaleOutActivity.class);
		Util.switchActivity(view, intent, this);
		isMain=3;
		menu.setText("=.ģʽ�л�\n+.�޸ļ۸�\n��.�޸�����\n-.����\n��.����");
	}
	
	private void init() {
		
		menu=(TextView) findViewById(R.id.menu);
		
		view=(LinearLayout) findViewById(R.id.view);
		

		

			//ʵ����mainSurfaceView
			mainSurfaceView = (SurfaceView)this.findViewById(R.id.menu_sufaceView);
			mainSurfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			mainSurfaceView.getHolder().setFixedSize(176, 114);
			mainSurfaceView.getHolder().setKeepScreenOn(true);
			mainSurfaceView.getHolder().addCallback(new SurfaceCallback());

		
		
	}

	//���ã����ҷ���ͼ��
	private final class SurfaceCallback implements android.view.SurfaceHolder.Callback
	{
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			// TODO Auto-generated method stub
			try {
			parameters = camera.getParameters(); // ��ȡ�������  
			parameters.setPictureFormat(PixelFormat.JPEG); // ����ͼƬ��ʽ  
			parameters.setPreviewSize(width, height); // ����Ԥ����С  
			parameters.setPreviewFrameRate(10);  //����ÿ����ʾ4֡  
			parameters.setPictureSize(width, height); // ���ñ����ͼƬ�ߴ�  
			parameters.setJpegQuality(80); // ������Ƭ����
			} catch (Exception e) {  
			e.printStackTrace();  
			}
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			try {  
				camera = Camera.open(0); // ������ͷ  
				camera.setPreviewDisplay(holder); // ����������ʾ����Ӱ���SurfaceHolder����  
				camera.setDisplayOrientation(90);  
				camera.startPreview(); // ��ʼԤ��  
				} catch (Exception e) {  
				e.printStackTrace();  
				}
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			if (camera != null) {  
				camera.release(); // �ͷ������  
				 camera = null;  
			} 

		}
		
		
	}
	
	//���ջص�
	private final class MyPictureCallback implements PictureCallback {    
	        @Override  
		    public void onPictureTaken(byte[] data, Camera camera) {  
            try {  
            	
            	if (isMain!=0) {
            		Bitmap map = BitmapFactory.decodeByteArray(data, 0, data.length);	
            		commonService.lastPhoto = Util.transImageToBase64(map); 
                    Toast.makeText(getApplicationContext(), "�ɹ�����",  
                            Toast.LENGTH_SHORT).show();
                    camera.startPreview(); // �����պ����¿�ʼԤ��  
                    isCameraing = false;
				}
  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }  
    }

	@Override
	protected void onResume() {
		super.onResume();
		
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		
		System.out.println("menuActivity onKeyUp == "+keyCode);
		
				//���keycode��Ӧ���հ�ť
				if (keyCode==156) {
					
					if (isMain==0) {
						 Toast.makeText(getApplicationContext(), "��ǰ�����ܽ��������ӦͼƬ",  
		                            Toast.LENGTH_SHORT).show();
						return true;
					}
					
					//������������
					MediaPlayer shootMP = null;
					AudioManager meng = (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);  
					int volume = meng.getStreamVolume( AudioManager.STREAM_NOTIFICATION);   
					if (volume != 0)  
					{  
						if (shootMP == null)  
					        shootMP = MediaPlayer.create(getBaseContext(), Uri.parse("file:///system/media/audio/ui/camera_click.ogg"));  
					        if (shootMP != null)  
					            shootMP.start();  
					 }

					//����
					if (!isCameraing) {
						 camera.takePicture(null, null, new MyPictureCallback());  
						 isCameraing = true;
					}
					
					 return true;
				}
				if(keyCode==4){
					menu.setText("1.�ɹ����\n2.�˻����\n3.���۳���\n4.����ά��\n5.׷������\n6.������\n0.����");
					return false;
				}
				return super.onKeyUp(keyCode, event);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//ͨ����ͬ��activity�жϼ�������
		Intent intent;
		if(isMain==0){//������
			
		System.out.println("��������� onkeydown����");
			
		switch (keyCode) {
		case KeyEvent.KEYCODE_1:
			intent=new Intent(this,PurchaseInActivity.class);
			Util.switchActivity(view, intent, this);
			isMain=1;
			menu.setText("=.ģʽ�л�\n+.�޸ļ۸�\n��.�޸�����\n-.����\n��.����");
			break;
		case KeyEvent.KEYCODE_2:
			intent=new Intent(this,ReturnInActivity.class);
			Util.switchActivity(view, intent, this);
			isMain=2;
			menu.setText("=.ģʽ�л�\n+.�޸ļ۸�\n��.�޸�����\n-.����\n��.����");
			break;
		case KeyEvent.KEYCODE_3:
			intent=new Intent(this,SaleOutActivity.class);
			Util.switchActivity(view, intent, this);
			isMain=3;
			menu.setText("=.ģʽ�л�\n+.�޸ļ۸�\n��.�޸�����\n-.����\n��.����");
			break;
		case KeyEvent.KEYCODE_4:
			intent=new Intent(this,DataMaintenanceActivity.class);
			Util.switchActivity(view, intent, this);
			isMain=4;
			menu.setText("=.ģʽ�л�\n+.�޸ļ۸�\n��.�޸�����\n-.����\n��.����");
			break;
		case KeyEvent.KEYCODE_5:
			intent=new Intent(this,TraceBackActivity.class);
			Util.switchActivity(view, intent, this);
			isMain=5;
			menu.setText("=.ģʽ�л�\n+.�޸ļ۸�\n��.�޸�����\n-.����\n��.����");
			break;
		case KeyEvent.KEYCODE_6:
			intent=new Intent(this,StockInfoMatirials.class);
			Util.switchActivity(view, intent, this);
			isMain=6;
			menu.setText("=.ģʽ�л�\n+.�޸ļ۸�\n��.�޸�����\n-.����\n��.����");
			break;
		case KeyEvent.KEYCODE_0:
			intent=new Intent(this,OpinionActivity.class);
			Util.switchActivity(view, intent, this);
			isMain=-1;
			break;
		case KeyEvent.KEYCODE_BACK:
			this.finish();
			break;
		default:
			break;
		}
		}
		else if(isMain==1){
			
			System.out.println("����������ɹ������淽������ onkeydown����");
			System.out.println(keyCode+"w"+ KeyEvent.KEYCODE_BACK);
			Log.i(Constants.msg, keyCode+"w"+ KeyEvent.KEYCODE_BACK);
			switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				Log.i(Constants.msg, keyCode+":"+ KeyEvent.KEYCODE_BACK);
				System.out.println(keyCode+":"+ KeyEvent.KEYCODE_BACK);
				intent=new Intent(this,MainActivity.class);
				Util.switchActivity(view, intent, this);
				//ͼƬ�ÿ�
		         Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.no_image); 
		         ByteArrayOutputStream bos=new ByteArrayOutputStream();
		         defaultBitmap.compress(CompressFormat.JPEG, 100, bos);//����100��ʾ��ѹ��  
		         byte[] bytes=bos.toByteArray();  
		         String defaultPhoto= Base64.encodeToString(bytes, Base64.DEFAULT); 
		         commonService.lastPhoto = defaultPhoto;
				isMain=0;
				break;

			default:
				break;
			}
		}
		else if (isMain==-1) {
			System.out.println("����������ɹ������淽������ onkeydown����");
			System.out.println(keyCode+"w"+ KeyEvent.KEYCODE_BACK);
			Log.i(Constants.msg, keyCode+"w"+ KeyEvent.KEYCODE_BACK);
			switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				Log.i(Constants.msg, keyCode+":"+ KeyEvent.KEYCODE_BACK);
				System.out.println(keyCode+":"+ KeyEvent.KEYCODE_BACK);
				intent=new Intent(this,MainActivity.class);
				Util.switchActivity(view, intent, this);
				//ͼƬ�ÿ�
		         Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.no_image); 
		         ByteArrayOutputStream bos=new ByteArrayOutputStream();
		         defaultBitmap.compress(CompressFormat.JPEG, 100, bos);//����100��ʾ��ѹ��  
		         byte[] bytes=bos.toByteArray();  
		         String defaultPhoto= Base64.encodeToString(bytes, Base64.DEFAULT); 
		         commonService.lastPhoto = defaultPhoto;
				isMain=0;
				break;

			default:
				break;
				}
		}
		else if (isMain==2) {
			System.out.println("����������ɹ������淽������ onkeydown����");
			System.out.println(keyCode+"w"+ KeyEvent.KEYCODE_BACK);
			Log.i(Constants.msg, keyCode+"w"+ KeyEvent.KEYCODE_BACK);
			switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				Log.i(Constants.msg, keyCode+":"+ KeyEvent.KEYCODE_BACK);
				System.out.println(keyCode+":"+ KeyEvent.KEYCODE_BACK);
				intent=new Intent(this,MainActivity.class);
				Util.switchActivity(view, intent, this);
				//ͼƬ�ÿ�
		         Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.no_image); 
		         ByteArrayOutputStream bos=new ByteArrayOutputStream();
		         defaultBitmap.compress(CompressFormat.JPEG, 100, bos);//����100��ʾ��ѹ��  
		         byte[] bytes=bos.toByteArray();  
		         String defaultPhoto= Base64.encodeToString(bytes, Base64.DEFAULT); 
		         commonService.lastPhoto = defaultPhoto; 
				isMain=0;
				break;

			default:
				break;
				}
		}
		else if (isMain==5) {
			System.out.println("����������ɹ������淽������ onkeydown����");
			System.out.println(keyCode+"w"+ KeyEvent.KEYCODE_BACK);
			Log.i(Constants.msg, keyCode+"w"+ KeyEvent.KEYCODE_BACK);
			switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				Log.i(Constants.msg, keyCode+":"+ KeyEvent.KEYCODE_BACK);
				System.out.println(keyCode+":"+ KeyEvent.KEYCODE_BACK);
				intent=new Intent(this,MainActivity.class);
				Util.switchActivity(view, intent, this);
				//ͼƬ�ÿ�
		         Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.no_image); 
		         ByteArrayOutputStream bos=new ByteArrayOutputStream();
		         defaultBitmap.compress(CompressFormat.JPEG, 100, bos);//����100��ʾ��ѹ��  
		         byte[] bytes=bos.toByteArray();  
		         String defaultPhoto= Base64.encodeToString(bytes, Base64.DEFAULT); 
		         commonService.lastPhoto = defaultPhoto;
				isMain=0;
				break;

			default:
				break;
				}
		}
		else if (isMain==3) {
			System.out.println("����������ɹ������淽������ onkeydown����");
			System.out.println(keyCode+"w"+ KeyEvent.KEYCODE_BACK);
			Log.i(Constants.msg, keyCode+"w"+ KeyEvent.KEYCODE_BACK);
			switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				Log.i(Constants.msg, keyCode+":"+ KeyEvent.KEYCODE_BACK);
				System.out.println(keyCode+":"+ KeyEvent.KEYCODE_BACK);
				intent=new Intent(this,MainActivity.class);
				Util.switchActivity(view, intent, this);
				//ͼƬ�ÿ�
		         Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.no_image); 
		         ByteArrayOutputStream bos=new ByteArrayOutputStream();
		         defaultBitmap.compress(CompressFormat.JPEG, 100, bos);//����100��ʾ��ѹ��  
		         byte[] bytes=bos.toByteArray();  
		         String defaultPhoto= Base64.encodeToString(bytes, Base64.DEFAULT); 
		         commonService.lastPhoto = defaultPhoto;
				isMain=0;
				break;
			default:
				break;
				}
		}
		else if (isMain==4) {
			System.out.println("����������ɹ������淽������ onkeydown����");
			System.out.println(keyCode+"w"+ KeyEvent.KEYCODE_BACK);
			Log.i(Constants.msg, keyCode+"w"+ KeyEvent.KEYCODE_BACK);
			switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				Log.i(Constants.msg, keyCode+":"+ KeyEvent.KEYCODE_BACK);
				System.out.println(keyCode+":"+ KeyEvent.KEYCODE_BACK);
				intent=new Intent(this,MainActivity.class);
				Util.switchActivity(view, intent, this);
				//ͼƬ�ÿ�
		         Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.no_image); 
		         ByteArrayOutputStream bos=new ByteArrayOutputStream();
		         defaultBitmap.compress(CompressFormat.JPEG, 100, bos);//����100��ʾ��ѹ��  
		         byte[] bytes=bos.toByteArray();  
		         String defaultPhoto= Base64.encodeToString(bytes, Base64.DEFAULT); 
		         commonService.lastPhoto = defaultPhoto; 
				isMain=0;
				break;

			default:
				break;
				}
		}
		else if (isMain==6) {
			System.out.println("����������ɹ������淽������ onkeydown����");
			System.out.println(keyCode+"w"+ KeyEvent.KEYCODE_BACK);
			Log.i(Constants.msg, keyCode+"w"+ KeyEvent.KEYCODE_BACK);
			switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				Log.i(Constants.msg, keyCode+":"+ KeyEvent.KEYCODE_BACK);
				System.out.println(keyCode+":"+ KeyEvent.KEYCODE_BACK);
				intent=new Intent(this,MainActivity.class);
				Util.switchActivity(view, intent, this);
				//ͼƬ�ÿ�
		         Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.no_image); 
		         ByteArrayOutputStream bos=new ByteArrayOutputStream();
		         defaultBitmap.compress(CompressFormat.JPEG, 100, bos);//����100��ʾ��ѹ��  
		         byte[] bytes=bos.toByteArray();  
		         String defaultPhoto= Base64.encodeToString(bytes, Base64.DEFAULT); 
		         commonService.lastPhoto = defaultPhoto; 
				isMain=0;
				break;

			default:
				break;
				}
		}
		return super.onKeyDown(keyCode, event);
	}

}
