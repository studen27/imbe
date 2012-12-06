package com.example.imbedproject.v021;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
//created by 60022495 정민규
//created date : 2012/12/06
//last modify : 2012/12/06
//카메라로 사진찍는 객체. * 참고 : 한승철 교수님 비주얼프로그래밍 강의자료
public class CameraActivity extends Activity {
	CameraView cv;
	
	//핸들러
	final Handler handler = new Handler(){
		public void handleMessage(Message msg){
			byte[] b = msg.getData().getByteArray("byte");
			
			if(b != null){			
				captured(b);
			}else{
				cv.surfacePreDestroy();	//종료
				finish();
			}
		}
	};
	
	//생성시
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        final PackageManager packageManager = this.getPackageManager();
        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        PackageManager pm = getPackageManager();
        boolean frontCam, rearCam;
        //It would be safer to use the constant PackageManager.FEATURE_CAMERA_FRONT
        //but since it is not defined for Android 2.2, I substituted the literal value
        frontCam = pm.hasSystemFeature("android.hardware.camera.front");
        rearCam = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);

        Log.i("Camera ",frontCam + " : " + rearCam);

        if(frontCam == true && rearCam == true){
	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        cv = new CameraView(this, handler);        
	        setContentView(cv); 
        }else{
        	setContentView(R.layout.camera);
        	finish();
        }
    }

    //사진찍은후
    protected void captured(byte[] b) {    	
    	Intent intent = new Intent();
		intent.putExtra("bytes", b);				//넘길 그림 byte[] 배열 (db에 박는 blob과 유사)
		setResult(Activity.RESULT_OK, intent);		//응답설정
		cv.surfacePreDestroy();
		finish();									//후 리턴
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_camera, menu);
        return true;
    }
	
	public boolean onOptionsItemSelected(MenuItem item) {
		cv.surfacePreDestroy();
		finish();
		onPause();
		return false;
	}
	
	public void onPause() {	//그냥 백버튼 눌렀을때 리소스반환후 종료
		Log.i("msg","Camera onPause");
		super.onPause();
		if(cv.isCaptured() == false){//사진찍은상태 아니면 종료		
			cv.surfacePreDestroy();
			finish();
		}
	}
}
