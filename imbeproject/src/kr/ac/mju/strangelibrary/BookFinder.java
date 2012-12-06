package kr.ac.mju.strangelibrary;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import kr.ac.mju.strangelibrary.util.FileTransportManager;
import kr.ac.mju.strangelibrary.util.ImageItemizedOverlay;
import kr.ac.mju.strangelibrary.util.QueryResult;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

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
	TextView tv1;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.book_finder);
	    
	    Intent intent = getIntent();
	    int latitude = intent.getIntExtra("latitude", 37222281);
	    int longitude = intent.getIntExtra("longitude", 127187283);
	    
        ftm = new FileTransportManager();
        mv = (MapView) findViewById(R.id.mapview);
        mv.setBuiltInZoomControls(true);
        mv.setSatellite(true);
        mv.setTraffic(true);
        tv1 = (TextView) findViewById(R.id.textAddress);
        
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
        
        //현재주소 표시
		Geocoder geo = new Geocoder(BookFinder.this, Locale.KOREAN);
		List<Address> addr;
		try {
			addr = geo.getFromLocation((double)latitude/1000000.0,(double)longitude/1000000.0, 2);
			
			String country = addr.get(0).getCountryName();
			String addrline = addr.get(0).getAddressLine(0);
			String phone = addr.get(0).getPhone();
			String post = addr.get(0).getPostalCode();
			String s = new String(country + " , " + addrline + " , "
					+ phone + " , " + post);
			tv1.setText("현재주소 : " + s);
		} catch (IOException e) {			
			Toast.makeText(this, "현재주소를 가져오지 못했습니다", Toast.LENGTH_SHORT).show();
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
