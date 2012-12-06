package kr.ac.mju.strangelibrary;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
//created by 60022495 정민규
//created date : 2012/12/04
//last modify : 2012/12/04
//불러올 파일선택 화면
public class LoadActivity extends Activity {
	protected BookInfo bookInfo; // 책정보 객체(로드/삭제용)
	private ArrayList<PageView> pages; // 각 페이지들 객체배열 (로드/삭제용. 여기에서 각 페이지의 이미지파일의 수를 가져와서 로드/삭제를 함)
	SimpleCursorAdapter adapter;		//목록에서 선택한 라인의 정보를 갖고오기 위한 어댑터. 때문에 여기에 선언함.
	LocationManager locationManager;	//GPS 사용위함
	LocationListener locationListener;	//GPS 사용위함

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_load);
	    
	    pages = new ArrayList<PageView>();//초기화

	    locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);//GPS사용을 위한 코드
	    locationListener = new LocationListener() {

			public void onLocationChanged(Location location) {
				Log.d("Location", location.toString());
			}

			public void onProviderDisabled(String arg0) {
				// TODO Auto-generated method stub
			}

			public void onProviderEnabled(String arg0) {
				// TODO Auto-generated method stub
			}

			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
				
			}
	    };
	     
	    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);//위치리스너 붙임
	    
	    Button findBook = (Button) findViewById(R.id.find_book);//XML에서 버튼연결하고
	    findBook.setOnClickListener(new Button.OnClickListener() {//리스너 붙임
	    	public void onClick(View arg0) {
	    		/*
				ConnectivityManager connect = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
				if (connect.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() 
						== NetworkInfo.State.CONNECTED) {
					Intent intent = new Intent("com.example.imbedproject.v021.findIntent");
					Location l = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
					if (l != null) {
						int latitude = (int) (l.getLatitude() * 1000000);
						int longitude = (int) (l.getLongitude() * 1000000);
						intent.putExtra("latitude", latitude);
						intent.putExtra("longitude", longitude);
					}
					startActivity(intent);
				} else {
					Toast.makeText(getApplicationContext(), "인터넷이 연결되어있지 않습니다.", Toast.LENGTH_LONG).show();
				}
				*/				
				
				Intent intent = new Intent("com.example.imbedproject.v021.findIntent");	//BookFinder로 보낼 인텐트
				Location l = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);	//마지막 위치 가져옴
				if (l != null) {
					int latitude = (int) (l.getLatitude() * 1000000);	//GPS위치를 MAP위치로 바꾸기 위해 1000000을 곱함
					int longitude = (int) (l.getLongitude() * 1000000);
					intent.putExtra("latitude", latitude);				//인텐트에 위도, 경도 값 넣음
					intent.putExtra("longitude", longitude);
				}
				startActivity(intent);			//BookFinder액티비티 호출
				
			}
	    });
	    
		ListView lv = (ListView) findViewById(R.id.loadList);//xml에서 리스트뷰 연결(갖고있는 책목록을 보여줄)
		
	    ContentResolver cr = getContentResolver();	//컨텐트 프로바이더에 쿼리를 날리기 위한 컨텐트 리졸버
		Cursor cursor = cr.query(MyProvider.CONTENT_URI, new String[] { MyProvider.ID, MyProvider.NAME, MyProvider.AUTHOR },
				null, null, null);			//모든 책목록을 가져옴

		String[] from = new String[] { MyProvider.ID, MyProvider.NAME, MyProvider.AUTHOR };	//가져올 항목들
		int[] to = new int[] { R.id.textId, R.id.textName, R.id.textAuthor };				//붙일 곳
        adapter = new SimpleCursorAdapter(this, R.layout.row, cursor, from, to);			//셋팅
        lv.setAdapter(adapter);        
        
        lv.setOnItemLongClickListener(new OnItemLongClickListener() {		//롱클릭 리스너
        	int pos;        //선택한 줄이 리스트뷰의 몇변재 줄인지 알기위함
        
        	//롱클릭시 
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {	
				pos = position;
				AlertDialog.Builder aDialog = new AlertDialog.Builder(LoadActivity.this);//삭제확인창 띄움
				aDialog.setTitle("삭제하시겠습니까?");
				aDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {//yes클릭시
					public void onClick(DialogInterface dialog, int which) {
				        Cursor c = (Cursor) adapter.getItem(pos);	//쿼리결과의 해당 줄 가져옴 
				        int sId = c.getInt(0);			//해당 줄의 0번재 컬럼("_id") 가져옴
				        String sName = c.getString(1);			//해당 줄의 1번재 컬럼 ("NAME")가져옴
				        String author = c.getString(2);			//해당 줄의 2 컬럼 ("author") 가져옴

						deleteWork(sId, sName, author);			//삭제작업						
					}
				});
				aDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {//no클릭시
					public void onClick(DialogInterface dialog, int which) {				
					}
				});
				AlertDialog ad = aDialog.create();
				ad.show();	//확인창 보여주기
				
				return false;
			}
		});

        //그냥 클릭(터치)시
		lv.setOnItemClickListener(new OnItemClickListener() {			
			
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		        Cursor c = (Cursor) adapter.getItem(position);	//쿼리결과의 해당 줄 가져옴 
		        int sId = c.getInt(0);			//해당 줄의 0번재 컬럼("_id") 가져옴
		        String sName = c.getString(1);			//해당 줄의 1번재 컬럼 ("NAME")가져옴
		        String sAuthor = c.getString(2);			//해당 줄의 2 컬럼 ("author") 가져옴
		        
		        Log.i("msg",sId+"");
		        
				Intent intent = new Intent("com.example.imbedproject.v021.readIntent");//나를 호출한 액티비티에 반환할 인텐트
				intent.putExtra("SelectedId", sId);			//id와
				intent.putExtra("SelectedName", sName);		//책이름 넘겨줌
				intent.putExtra("SelectedAuthor", sAuthor);	//작자명 넘겨줌
				setResult(Activity.RESULT_OK, intent);		//응답설정	    		
				finish();									//후 리턴
			}
		});        
	}

	//삭제작업_DB에서도 삭제
	private void deleteWork(int sId, String sName, String sAuthor) {	//선택된줄 id, 책이름, 작자명 받음
		try {
			Log.i("LoadActivity msg","sid : " + sId + " sName : " + sName + " sAuthor : " + sAuthor);
			
			String prefix = sName + "_" + sAuthor + "_"; // 파일명 앞에붙을이름
			
			String[] filter_word = { "\\p{Space} ", " ", "\\?", "\\/",
					"\\*", "\\+", "\\|", "\\\\", "\\\"", "\\:", "\\<",
					"\\>", "\\?", "\\/" };
			for (int i = 0; i < filter_word.length; i++) { // 파일명으로 쓸수없는 특수문자를 "_"로 교체
				prefix = prefix.replaceAll(filter_word[i], "_");
			}

			Log.i("LoadActivity msg",prefix	+ Constants.SAVE_FILENAME);	//삭제할 파일 로그로 찍어봄
			
			FileInputStream fis = openFileInput(prefix + Constants.SAVE_FILENAME);	//삭제를 위해 정보객체를 읽기위한 스트림
			// ObjectInputStream ois = new ObjectInputStream(fis);
			ObjectInputStream ois = new ObjectInputStream(
					new BufferedInputStream(fis));

			bookInfo = (BookInfo) ois.readObject();	//정보객체 읽어옴

			if (ois != null) { // 스트림 닫기
				try {
					ois.close();
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			// 불러온정보 셋팅(각 페이지당 이미지 수 셋팅)
			int maxPageNumber = bookInfo.getPageInfos().size();
			//		pages.get(currentPageNumber - 1).stopThread();	//스레드 중지
			for (int i = 0; i < maxPageNumber; i++) { // 불러온 페이지정보대로 셋팅
				PageView pv = new PageView(this);
				pv.setPageViewInfo(bookInfo.getPageInfos().get(i)); // 페이지정보 줌
				pv.setByPageViewInfo();								// 페이지정보대로 페이지 셋
//				pv.createTextView(pv.getPageType(), inflater);		//각페이지 text 셋
				pages.add(pv);
			}
			
			// 이미지들 삭제			
			String bmName; // 파일명
			File tempFile;			//임시로 쓸 파일객체
			for (int i = 0; i < maxPageNumber; i++) { // 각 페이지에
				for (int j = 0; j < pages.get(i).getImages().size(); j++) { // 각 이미지를
					bmName = prefix + i + "_" + j + "" + ".PNG"; // 파일명 : 책이름+페이지번호+이미지번호
					
					tempFile = new File(getApplicationContext().getFilesDir().getPath().toString() + "/" + bmName);
					Log.i("bookeditor msg",getApplicationContext().getFilesDir().getPath().toString() + "/" + bmName);
					if(tempFile.exists()){	//존재하면
						tempFile.delete();	//삭제시도
					}
				}
			}
			
			//정보파일 삭제시도
			File datFile = new File(getApplicationContext().getFilesDir().getPath().toString() + "/" + prefix + Constants.SAVE_FILENAME);			
			if(datFile.exists()){
				datFile.delete();	
			}

			Toast.makeText(this, "삭제성공", 0).show();	//성공여부 메세지 띄움
		}catch(Exception e){
			Toast.makeText(this, "이미 삭제됨", 0).show();
		}finally{
			ContentResolver cr = getContentResolver();
			cr.delete(MyProvider.CONTENT_URI, "_id=?", new String[]{sId+""});	//db에서 해당 id의 투플 삭제
		}
	}
	
	//종료시
	public void onDestroy() {
		Log.i("msg", "BookEditor onDestroy");
		super.onDestroy();
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.removeUpdates(locationListener);
	}
}
