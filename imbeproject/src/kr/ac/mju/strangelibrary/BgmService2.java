package kr.ac.mju.strangelibrary;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
//created by 60022495 정민규
//created date : 2012/12/06
//last modify : 2012/12/06
//Bgm재생 서비스 클래스 (aidl로 연결. 액티비티 종료시 종료됨)
public class BgmService2 extends Service {
	private MediaPlayer mp;			//음악재생용 플레이어
	
	//원격제어를 위한 바인더 클래스
	private final BgmService.Stub binder = new BgmService.Stub() {

		//bgm재생 메소드
		public void bgmStart() throws RemoteException {
			Log.i("bgmService msg", "bgmStart");		//신호용 로그출력
			mp = MediaPlayer.create(BgmService2.this, R.raw.bgm1);	//미디어플레이어 생성
			mp.start();	
			
		    //after playing
	        mp.setOnCompletionListener(new OnCompletionListener() {			
				public void onCompletion(MediaPlayer arg0) {
					mp.start();				//반복재생
				}
			});
		}

		//bgm종료 메소드
		public void bgmStop() throws RemoteException {
			Log.i("bgmService msg", "bgmStop");
			mp.stop();
		}
	};

	@Override
    public IBinder onBind(Intent p_intent) {
	    Log.i("bgmService msg", "onBind");
	    return binder;	//aidl 원격컨트롤을 위해 위에서 만든 클래스인 binder를 반환
    }

	@Override
    public void onCreate() {
	    super.onCreate();	    
	    Log.i("bgmService msg", "onCreate");
    }

	@Override
    public void onDestroy() {
	    super.onDestroy();
	    Log.i("bgmService msg", "onDestroy");
	    mp.stop();		//종료시도 bgm 끔
    }

	@Override
    public void onStart(Intent p_intent, int p_startId) {
	    super.onStart(p_intent, p_startId);	    
	    Log.i("bgmService msg", "onStart");
	}
}