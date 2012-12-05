package com.example.imbedproject.v021;

import java.util.List;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;

import com.example.imbedproject.v021.util.FileTransportManager;
import com.example.imbedproject.v021.util.ImageItemizedOverlay;
import com.example.imbedproject.v021.util.QueryResult;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

//created by 60062446 박정실
//created date : 2012/12/05
//last modify : 2012/12/05
public class BookFinder extends MapActivity {
	MapView mv;
	List<Overlay> mapOverlays;
	Drawable d;
	ImageItemizedOverlay itemizedOverlay;
	FileTransportManager ftm; // 파일 전송 관리자

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.book_finder);
	    
	    // 좌표, GPS를 이용하여 설정하여야함
	    int latitude = 37222281;
	    int longitude = 127187283;
	    
        ftm = new FileTransportManager();
        mv = (MapView) findViewById(R.id.mapview);
        mv.setBuiltInZoomControls(true);
        mv.setSatellite(true);
        mv.setTraffic(true);
        
        // 검색 결과를 받아오는 QueryResult 객체
        QueryResult qr = ftm.getBookList(latitude, longitude);
        
        // 지도 설정
        GeoPoint viewGp = new GeoPoint(latitude, longitude);
        MapController mc = mv.getController();
        mc.animateTo(viewGp);
        mc.setZoom(16);
        d = this.getResources().getDrawable(R.drawable.mark);
        itemizedOverlay = new ImageItemizedOverlay(d, this);
        
        // 검색 결과를 이용하여 맵에 데이터를 표시한다.
        for(int i = 0; i < qr.size(); i++) {
        	GeoPoint gp = new GeoPoint(qr.getLatitudeAt(i), qr.getLongitudeAt(i));
        	OverlayItem overlayitem = new OverlayItem(gp, qr.getOriginNameAt(i), qr.getActualNameAt(i));
        	itemizedOverlay.addOverlay(overlayitem);
        }
        
        if(qr.size() > 0) {
        	mapOverlays = mv.getOverlays();
        	mapOverlays.add(itemizedOverlay);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}
