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
	
	//局部刷新view
	LinearLayout view;
	
	//菜单 标志
	int isMain;
	boolean flag=false;
	
	static boolean isCameraing = false;
	
	//拍照用surfaceView
	private SurfaceView mainSurfaceView;
	private android.hardware.Camera camera;
	private android.hardware.Camera.Parameters parameters;
		  
	//保存照片的Bundle
	Bundle bundle = null; // 声明一个Bundle对象，用来存储数据 
	
	private TextView menu;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//get请求允许设置
		if (android.os.Build.VERSION.SDK_INT > 9) {  
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build(); 
			StrictMode.setThreadPolicy(policy);
		}
		
		//Activty初始化
		init();
		
		//首先启动销售出库界面
		Intent intent=new Intent(this,SaleOutActivity.class);
		Util.switchActivity(view, intent, this);
		isMain=3;
		menu.setText("=.模式切换\n+.修改价格\n×.修改数量\n-.拍照\n÷.结算");
	}
	
	private void init() {
		
		menu=(TextView) findViewById(R.id.menu);
		
		view=(LinearLayout) findViewById(R.id.view);
		

		

			//实例化mainSurfaceView
			mainSurfaceView = (SurfaceView)this.findViewById(R.id.menu_sufaceView);
			mainSurfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			mainSurfaceView.getHolder().setFixedSize(176, 114);
			mainSurfaceView.getHolder().setKeepScreenOn(true);
			mainSurfaceView.getHolder().addCallback(new SurfaceCallback());

		
		
	}

	//设置，并且返回图像
	private final class SurfaceCallback implements android.view.SurfaceHolder.Callback
	{
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			// TODO Auto-generated method stub
			try {
			parameters = camera.getParameters(); // 获取各项参数  
			parameters.setPictureFormat(PixelFormat.JPEG); // 设置图片格式  
			parameters.setPreviewSize(width, height); // 设置预览大小  
			parameters.setPreviewFrameRate(10);  //设置每秒显示4帧  
			parameters.setPictureSize(width, height); // 设置保存的图片尺寸  
			parameters.setJpegQuality(80); // 设置照片质量
			} catch (Exception e) {  
			e.printStackTrace();  
			}
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			try {  
				camera = Camera.open(0); // 打开摄像头  
				camera.setPreviewDisplay(holder); // 设置用于显示拍照影像的SurfaceHolder对象  
				camera.setDisplayOrientation(90);  
				camera.startPreview(); // 开始预览  
				} catch (Exception e) {  
				e.printStackTrace();  
				}
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			if (camera != null) {  
				camera.release(); // 释放照相机  
				 camera = null;  
			} 

		}
		
		
	}
	
	//拍照回调
	private final class MyPictureCallback implements PictureCallback {    
	        @Override  
		    public void onPictureTaken(byte[] data, Camera camera) {  
            try {  
            	
            	if (isMain!=0) {
            		Bitmap map = BitmapFactory.decodeByteArray(data, 0, data.length);	
            		commonService.lastPhoto = Util.transImageToBase64(map); 
                    Toast.makeText(getApplicationContext(), "成功拍照",  
                            Toast.LENGTH_SHORT).show();
                    camera.startPreview(); // 拍完照后，重新开始预览  
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
		
				//如果keycode对应拍照按钮
				if (keyCode==156) {
					
					if (isMain==0) {
						 Toast.makeText(getApplicationContext(), "请前往功能界面拍摄对应图片",  
		                            Toast.LENGTH_SHORT).show();
						return true;
					}
					
					//播放拍照声音
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

					//拍照
					if (!isCameraing) {
						 camera.takePicture(null, null, new MyPictureCallback());  
						 isCameraing = true;
					}
					
					 return true;
				}
				if(keyCode==4){
					menu.setText("1.采购入库\n2.退货入库\n3.销售出库\n4.数据维护\n5.追溯上线\n6.库存管理\n0.配置");
					return false;
				}
				return super.onKeyUp(keyCode, event);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//通过不同的activity判断监听内容
		Intent intent;
		if(isMain==0){//主界面
			
		System.out.println("主界面监听 onkeydown方法");
			
		switch (keyCode) {
		case KeyEvent.KEYCODE_1:
			intent=new Intent(this,PurchaseInActivity.class);
			Util.switchActivity(view, intent, this);
			isMain=1;
			menu.setText("=.模式切换\n+.修改价格\n×.修改数量\n-.拍照\n÷.结算");
			break;
		case KeyEvent.KEYCODE_2:
			intent=new Intent(this,ReturnInActivity.class);
			Util.switchActivity(view, intent, this);
			isMain=2;
			menu.setText("=.模式切换\n+.修改价格\n×.修改数量\n-.拍照\n÷.结算");
			break;
		case KeyEvent.KEYCODE_3:
			intent=new Intent(this,SaleOutActivity.class);
			Util.switchActivity(view, intent, this);
			isMain=3;
			menu.setText("=.模式切换\n+.修改价格\n×.修改数量\n-.拍照\n÷.结算");
			break;
		case KeyEvent.KEYCODE_4:
			intent=new Intent(this,DataMaintenanceActivity.class);
			Util.switchActivity(view, intent, this);
			isMain=4;
			menu.setText("=.模式切换\n+.修改价格\n×.修改数量\n-.拍照\n÷.结算");
			break;
		case KeyEvent.KEYCODE_5:
			intent=new Intent(this,TraceBackActivity.class);
			Util.switchActivity(view, intent, this);
			isMain=5;
			menu.setText("=.模式切换\n+.修改价格\n×.修改数量\n-.拍照\n÷.结算");
			break;
		case KeyEvent.KEYCODE_6:
			intent=new Intent(this,StockInfoMatirials.class);
			Util.switchActivity(view, intent, this);
			isMain=6;
			menu.setText("=.模式切换\n+.修改价格\n×.修改数量\n-.拍照\n÷.结算");
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
			
			System.out.println("主界面监听采购入库界面方法监听 onkeydown方法");
			System.out.println(keyCode+"w"+ KeyEvent.KEYCODE_BACK);
			Log.i(Constants.msg, keyCode+"w"+ KeyEvent.KEYCODE_BACK);
			switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				Log.i(Constants.msg, keyCode+":"+ KeyEvent.KEYCODE_BACK);
				System.out.println(keyCode+":"+ KeyEvent.KEYCODE_BACK);
				intent=new Intent(this,MainActivity.class);
				Util.switchActivity(view, intent, this);
				//图片置空
		         Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.no_image); 
		         ByteArrayOutputStream bos=new ByteArrayOutputStream();
		         defaultBitmap.compress(CompressFormat.JPEG, 100, bos);//参数100表示不压缩  
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
			System.out.println("主界面监听采购入库界面方法监听 onkeydown方法");
			System.out.println(keyCode+"w"+ KeyEvent.KEYCODE_BACK);
			Log.i(Constants.msg, keyCode+"w"+ KeyEvent.KEYCODE_BACK);
			switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				Log.i(Constants.msg, keyCode+":"+ KeyEvent.KEYCODE_BACK);
				System.out.println(keyCode+":"+ KeyEvent.KEYCODE_BACK);
				intent=new Intent(this,MainActivity.class);
				Util.switchActivity(view, intent, this);
				//图片置空
		         Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.no_image); 
		         ByteArrayOutputStream bos=new ByteArrayOutputStream();
		         defaultBitmap.compress(CompressFormat.JPEG, 100, bos);//参数100表示不压缩  
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
			System.out.println("主界面监听采购入库界面方法监听 onkeydown方法");
			System.out.println(keyCode+"w"+ KeyEvent.KEYCODE_BACK);
			Log.i(Constants.msg, keyCode+"w"+ KeyEvent.KEYCODE_BACK);
			switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				Log.i(Constants.msg, keyCode+":"+ KeyEvent.KEYCODE_BACK);
				System.out.println(keyCode+":"+ KeyEvent.KEYCODE_BACK);
				intent=new Intent(this,MainActivity.class);
				Util.switchActivity(view, intent, this);
				//图片置空
		         Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.no_image); 
		         ByteArrayOutputStream bos=new ByteArrayOutputStream();
		         defaultBitmap.compress(CompressFormat.JPEG, 100, bos);//参数100表示不压缩  
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
			System.out.println("主界面监听采购入库界面方法监听 onkeydown方法");
			System.out.println(keyCode+"w"+ KeyEvent.KEYCODE_BACK);
			Log.i(Constants.msg, keyCode+"w"+ KeyEvent.KEYCODE_BACK);
			switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				Log.i(Constants.msg, keyCode+":"+ KeyEvent.KEYCODE_BACK);
				System.out.println(keyCode+":"+ KeyEvent.KEYCODE_BACK);
				intent=new Intent(this,MainActivity.class);
				Util.switchActivity(view, intent, this);
				//图片置空
		         Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.no_image); 
		         ByteArrayOutputStream bos=new ByteArrayOutputStream();
		         defaultBitmap.compress(CompressFormat.JPEG, 100, bos);//参数100表示不压缩  
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
			System.out.println("主界面监听采购入库界面方法监听 onkeydown方法");
			System.out.println(keyCode+"w"+ KeyEvent.KEYCODE_BACK);
			Log.i(Constants.msg, keyCode+"w"+ KeyEvent.KEYCODE_BACK);
			switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				Log.i(Constants.msg, keyCode+":"+ KeyEvent.KEYCODE_BACK);
				System.out.println(keyCode+":"+ KeyEvent.KEYCODE_BACK);
				intent=new Intent(this,MainActivity.class);
				Util.switchActivity(view, intent, this);
				//图片置空
		         Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.no_image); 
		         ByteArrayOutputStream bos=new ByteArrayOutputStream();
		         defaultBitmap.compress(CompressFormat.JPEG, 100, bos);//参数100表示不压缩  
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
			System.out.println("主界面监听采购入库界面方法监听 onkeydown方法");
			System.out.println(keyCode+"w"+ KeyEvent.KEYCODE_BACK);
			Log.i(Constants.msg, keyCode+"w"+ KeyEvent.KEYCODE_BACK);
			switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				Log.i(Constants.msg, keyCode+":"+ KeyEvent.KEYCODE_BACK);
				System.out.println(keyCode+":"+ KeyEvent.KEYCODE_BACK);
				intent=new Intent(this,MainActivity.class);
				Util.switchActivity(view, intent, this);
				//图片置空
		         Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.no_image); 
		         ByteArrayOutputStream bos=new ByteArrayOutputStream();
		         defaultBitmap.compress(CompressFormat.JPEG, 100, bos);//参数100表示不压缩  
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
			System.out.println("主界面监听采购入库界面方法监听 onkeydown方法");
			System.out.println(keyCode+"w"+ KeyEvent.KEYCODE_BACK);
			Log.i(Constants.msg, keyCode+"w"+ KeyEvent.KEYCODE_BACK);
			switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				Log.i(Constants.msg, keyCode+":"+ KeyEvent.KEYCODE_BACK);
				System.out.println(keyCode+":"+ KeyEvent.KEYCODE_BACK);
				intent=new Intent(this,MainActivity.class);
				Util.switchActivity(view, intent, this);
				//图片置空
		         Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.no_image); 
		         ByteArrayOutputStream bos=new ByteArrayOutputStream();
		         defaultBitmap.compress(CompressFormat.JPEG, 100, bos);//参数100表示不压缩  
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
