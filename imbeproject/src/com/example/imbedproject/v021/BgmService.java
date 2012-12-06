package com.example.imbedproject.v021;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.IBinder;

//created by 60022495 정민규
//created date : 2012/11/28
//last modify : 2012/11/28
//BGM재생 서비스
public class BgmService extends Service {
	private MediaPlayer mp;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void onStart(Intent intent, int startId){
		super.onStart(intent, startId);
		mp = MediaPlayer.create(this, R.raw.bgm1);
		mp.start();
		
	    //after playing
        mp.setOnCompletionListener(new OnCompletionListener() {			
			public void onCompletion(MediaPlayer arg0) {
				mp.start();				
			}
		});
	}
	
	public void onDestroy(){//can onStop
		super.onDestroy();
		mp.stop();
	}
}
