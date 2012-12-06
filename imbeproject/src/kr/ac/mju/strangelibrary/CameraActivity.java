package kr.ac.mju.strangelibrary;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
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
	
	//핸들러. 카메라에서 찍은 그림 byte배열을 받음
	final Handler handler = new Handler(){
		public void handleMessage(Message msg){
			byte[] b = msg.getData().getByteArray("byte");
			
			if(b != null){			
				captured(b);	//찍은 사진을 넘겨주기 위한 메소드 호출
			}else{				//만약을 위한 종료. 카메라에서 byte배열을 null을 리턴받으면 종료시킴
				cv.surfacePreDestroy();	//종료
				finish();
			}
		}
	};
	
	//생성시
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PackageManager pm = getPackageManager();	//카메라 사용가능한가 체크용. 소스출처 : stackoverflow
        boolean frontCam, rearCam;
        //It would be safer to use the constant PackageManager.FEATURE_CAMERA_FRONT
        //but since it is not defined for Android 2.2, I substituted the literal value
        frontCam = pm.hasSystemFeature("android.hardware.camera.front");	//앞카메라 가능여부
        rearCam = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);		//뒷카메라 가능여부

        Log.i("Camera ",frontCam + " : " + rearCam);				//로그로 위 변수들을 출력해봄

        if(frontCam == true && rearCam == true){		//앞뒤 카메라 모두 사용가능하면
	        requestWindowFeature(Window.FEATURE_NO_TITLE);	//화면설정
	        cv = new CameraView(this, handler);        	//카메라뷰 생성
	        setContentView(cv); 						//화면 셋팅
        }else{
        	setContentView(R.layout.camera);			//만약 카메라가 사용이 안되면 종료. 에러방지위해 setContentView를 해놓음
        	finish();
        }
    }

    //사진찍은후. 사진찍으면 handler에 의해 호출됨
    protected void captured(byte[] b) {    	
    	Intent intent = new Intent();
		intent.putExtra("bytes", b);				//넘길 그림 byte[] 배열 (db에 박는 blob과 유사)
		setResult(Activity.RESULT_OK, intent);		//응답설정
		cv.surfacePreDestroy();						//카메라뷰 종료준비
		finish();									//후 리턴
	}

    //옵션메뉴생성 코드. 메뉴는 Exit 하나임.
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_camera, menu);
        return true;
    }
	
    //옵션메뉴의 exit버튼이 눌릴경우 액티비티 종료
	public boolean onOptionsItemSelected(MenuItem item) {
		cv.surfacePreDestroy();	//카메라뷰 종료준비
		finish();				//자신 종료
		return false;
	}
	
	//그냥 뒤로가기 버튼 눌렀을때 리소스반환후 종료
	public void onPause() {	
		Log.i("msg","Camera onPause");
		super.onPause();
		if(cv.isCaptured() == false){//사진찍은상태 아니면 종료		
			cv.surfacePreDestroy();
			finish();
		}
	}
}
