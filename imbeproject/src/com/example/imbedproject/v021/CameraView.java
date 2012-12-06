package com.example.imbedproject.v021;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
//created by 60022495 정민규
//created date : 2012/12/06
//last modify : 2012/12/06
//카메라로 사진찍는 객체의 표면객체.  * 참고 : 한승철 교수님 비주얼프로그래밍 강의자료
public class CameraView extends SurfaceView implements Callback,
		PictureCallback {

	private SurfaceHolder holder;	//홀더
	private Camera camera;		//카메라
	private Context mContext;
	static Handler handler;
	boolean captured = false;
		
	//getter & setter
	public boolean isCaptured() {
		return captured;
	}
	public void setCaptured(boolean captured) {
		this.captured = captured;
	}

	public CameraView(Context context, Handler handler) {
		super(context);
		this.handler = handler;
		mContext = context;
		holder = getHolder();	//홀더생성
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);	//푸쉬 버퍼 지정
	}
	
	//생성시
	public void surfaceCreated(SurfaceHolder arg0) {
		try {
			camera = Camera.open();	//카메라 초기화			
			camera.setPreviewDisplay(holder);
		} catch (IOException e) {	//카메라 오픈 오류시
			Message msg = handler.obtainMessage();
			Bundle b = new Bundle();
			b.putByteArray("byte", null);
			msg.setData(b);
			handler.sendMessage(msg);	
		}
	}

	//변경시
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		camera.startPreview();	//미리보기 시작
	}
	
	//끝내기 전
	public void surfacePreDestroy() {
		if(camera != null){
			camera.setPreviewCallback(null);	//미리보기 정지
			camera.stopPreview();
			camera.release();
			camera = null;
		}		
	}
	
	//사라질때
	public void surfaceDestroyed(SurfaceHolder arg0) {
//		camera.setPreviewCallback(null);	//미리보기 정지
//		camera.stopPreview();
//		camera.release();
//		camera = null;
	}
	
	//터치시 캡쳐
	public boolean onTouchEvent(MotionEvent e){
		if (e.getAction() == MotionEvent.ACTION_DOWN){
			camera.takePicture(null, null, this);	//스크린샷 구함
			captured = true;
		}
		return true;
	}
	
	//촬영완료시
	public void onPictureTaken(byte[] data, Camera camera) {
		try{
			BitmapFactory.Options options = new BitmapFactory.Options();
	    	options.inSampleSize = 8;		//  1/8로 줄이는 옵션. 2의 지수만큼 비례할때 가장빠르다고 함    	
	    	BitmapDrawable bd = new BitmapDrawable(BitmapFactory.decodeByteArray(data, 0, data.length, options));//사이즈줄임. 어쩔수없이 bitmapfactory사용
	    	Bitmap bitmap = bd.getBitmap();									//비트맵 얻음
	    	
	    	ByteArrayOutputStream stream = new ByteArrayOutputStream();
	    	bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
	    	byte[] bitmapdata = stream.toByteArray();
			
			data2sd(getContext(),bitmapdata,"test.jpg");	//메소드 호출로 넘김
		}catch(Exception e){
			Log.e("camera msg","사진저장실패");
		}
		camera.startPreview();	//미리보기 재개
		captured = false;
	}
	
	//바이트데이터(blob) 을 핸들러로 CameraActivity로 넘김
	private static void data2sd(Context context, byte[] bytes, String filename) throws Exception{		
		Message msg = handler.obtainMessage();
		Bundle b = new Bundle();
		b.putByteArray("byte", bytes);
		msg.setData(b);
		handler.sendMessage(msg);			
						
	}

}
