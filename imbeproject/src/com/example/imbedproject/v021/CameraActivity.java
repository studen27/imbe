package com.example.imbedproject.v021;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
	
	final Handler handler = new Handler(){
		public void handleMessage(Message msg){
			byte[] b = msg.getData().getByteArray("byte");
			captured(b);
		}
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        cv = new CameraView(this, handler);        
        setContentView(cv);
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
