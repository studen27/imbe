package com.example.imbedproject.v021;

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
	private MediaPlayer mp;
	
	private final BgmService.Stub binder = new BgmService.Stub() {

		public void bgmStart() throws RemoteException {
			Log.i("bgmService msg", "bgmStart");
			mp = MediaPlayer.create(BgmService2.this, R.raw.bgm1);
			mp.start();	
			
		    //after playing
	        mp.setOnCompletionListener(new OnCompletionListener() {			
				public void onCompletion(MediaPlayer arg0) {
					mp.start();				
				}
			});
		}

		public void bgmStop() throws RemoteException {
			Log.i("bgmService msg", "bgmStop");
			mp.stop();
		}
	};

	@Override
    public IBinder onBind(Intent p_intent) {
	    Log.i("bgmService msg", "onBind");
	    return binder;
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