package kr.ac.mju.strangelibrary;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import kr.ac.mju.strangelibrary.util.FileTransportManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

//created by 60062446 박정실
//created date : 2012/11/29
//last modify : 2012/12/06
public class BookReader extends Activity implements OnClickListener {
	final Context context = this;

	private FrameLayout pageViewer; // main panel 역할을함
	private Integer currentPageNumber; // 현제 페이지
	private Integer maxPageNumber; // 최대 페이지
	private TextView pageNumberView; // 페이지 번호를 보여주는 TextView
	private ArrayList<PageView> pages; // ImageView Vector 객체
	protected BookInfo bookInfo; // 책정보 객체(세이브/로드용)
	private String preBgFileName;
	private LayoutInflater inflater;
	private boolean isStart = true; // 처음 시작하나. (onResume때문)
	SimpleCursorAdapter adapter; // 리스트 항목 정보얻기용
	private FileTransportManager ftm; // 파일 전송 관리자
	private LocationManager locationManager; // GPS관련 참조변수
	private LocationListener locationListener; // GPS관련 참조변수

	static final int DO_SQL = 0;
	static final int NO_SQL = 1;
	static final String KEY = "_ID";
	static final String NAME = "NAME";
	static final String AUTHOR = "AUTHOR";

	// Buttons
	private ImageButton prev_butten;
	private ImageButton next_button;
	private Button bgmBtn;
	private Button uploadButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.book_reader);

		// Attributes
		currentPageNumber = 1;
		maxPageNumber = 1;

		// Components
		// Button 참조변수
		ftm = new FileTransportManager();
		prev_butten = (ImageButton) findViewById(R.id.prev_button);
		next_button = (ImageButton) findViewById(R.id.next_button);
		bgmBtn = (Button) findViewById(R.id.reader_bgmBtn);
		uploadButton = (Button) findViewById(R.id.upload_button);

		pageNumberView = (TextView) findViewById(R.id.page_number);
		pageViewer = (FrameLayout) findViewById(R.id.farme);

		pages = new ArrayList<PageView>();

		bookInfo = new BookInfo();
		
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
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
	     
	    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);

		init();
	}

	// Initialization
	public void init() {
		// 버튼에 리스너 부착
		prev_butten.setOnClickListener(this);
		next_button.setOnClickListener(this);
		bgmBtn.setOnClickListener(this);
		uploadButton.setOnClickListener(this);

		// 첫 page를 생성하고 pageViewer에 add
		inflater = (LayoutInflater) context
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		// View view = inflater.inflate(R.layout.book_title,
		// (ViewGroup) findViewById(R.id.book_title_root));
		PageView pv = new PageView(this);

		pv.createTextView(Constants.PAGE_TYPE.Title, inflater);// 밑에써도됨
		pages.add(pv);

		// pages.get(0).setTextView(view);
		pageViewer.addView(pages.get(0));
		if (pages.get(0).getTextView() != null) {
			pageViewer.addView(pages.get(0).getTextView());
		}

		// 페이지번호 설정
		pageNumberView.setText(currentPageNumber.toString() + "/"
				+ maxPageNumber.toString());

		loadWork();

		// GPS를 이용하기위한 객체 생성후 설정
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);		 
		locationListener = new MyLocationListener();		 
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
	}

	public void onStart() {
		super.onStart();
		pages.get(0).invalidate();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.bookreader_menu, menu);
		return true;
	}

	// 메뉴아이템 선택시
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_Qload: // 퀵로드
			loadWorkQ();
			break;
		case R.id.menu_load: // 로드
			loadWork();
			break;
		}
		return true;
	}

	// Button Event
	public void onClick(View v) {
		switch (v.getId()) {
		// 전 페이지로 이동 버튼
		// currentpageNumber가 1인경우 경고메세지 출력
		// 아니면 currentpageNumber를 1 감소시킨후 재출력
		case R.id.prev_button:
			if (!(currentPageNumber == 1)) {
				// pages.get(currentPageNumber - 1).stopThread(); //스레드 중지
				pageViewer.removeAllViews();
				currentPageNumber--;
				pageNumberView.setText(currentPageNumber.toString() + "/"
						+ maxPageNumber.toString());
				pageViewer.addView(pages.get(currentPageNumber - 1));
				if (pages.get(currentPageNumber - 1).getTextView() != null) {
					pageViewer.addView(pages.get(currentPageNumber - 1)
							.getTextView());
				}

			} else {
				// 첫페이지일 경우 경고메세지 출력
				Toast.makeText(this, "첫 페이지 입니다.", Toast.LENGTH_SHORT).show();
			}
			break;

		// 다음 페이지로 이동 버튼
		// currentPageNumber가 maxPageNumber와 같으면
		// maxPageNumber를 1 증가시킨후 images에에 view를 하나 더 추가
		// 아니면 이동후 재출력
		case R.id.next_button:
			if (currentPageNumber == maxPageNumber) {
				// pages.get(currentPageNumber - 1).stopThread(); //스레드 중지
				// 마지막 페이지일 경우 경고메세지 출력
				Toast.makeText(this, "마지막 페이지 입니다.", Toast.LENGTH_SHORT).show();
			} else {
				// pages.get(currentPageNumber - 1).stopThread(); //스레드 중지
				pageViewer.removeAllViews();
				currentPageNumber++;
				pageNumberView.setText(currentPageNumber.toString() + "/"
						+ maxPageNumber.toString());
				pageViewer.addView(pages.get(currentPageNumber - 1));
				if (pages.get(currentPageNumber - 1).getTextView() != null) {
					pageViewer.addView(pages.get(currentPageNumber - 1)
							.getTextView());
				}
			}

			break;

		case R.id.bgmBtn:
			// created 2012/11/22 정민규
			// bgm 켜기/끄기 (preference)
			SharedPreferences pref = getSharedPreferences("Bgm Toggle",
					MODE_PRIVATE);
			SharedPreferences.Editor editor = pref.edit(); // 수정용 에디터

			String valueStr = pref.getString("Bgm On/Off", ""); // 값 읽기. 없을시
																// ""리턴
			if (valueStr.equals("") || valueStr.equals("0")) {
				editor.putString("Bgm On/Off", "1"); // 1로 씀

				Intent intent = new Intent("com.example.pagemanager.BgmService"); // Bgm서비스
																					// 켬
				startService(intent);
			} else {
				editor.putString("Bgm On/Off", "0"); // 0으로 씀

				Intent intent = new Intent("com.example.pagemanager.BgmService"); // Bgm서비스
																					// 끔
				stopService(intent);
			}
			editor.commit(); // 변경사항 적용

			Log.i("msg", valueStr);
			break;

		// 책 업로드
		// GPS로부터 좌표를 읽어들여 서버로 전송한다.
		case R.id.upload_button:
			// wifi가 연결되었을때만 이용 가능하도록 함
			ConnectivityManager connect = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
			if (connect.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
				Location l = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				int latitude;
				int longitude;
				// GPS로부터 수집된 값이 있으면 해당 값을 이용하고 없으면 명지대를 기본값으로 함
				if (l != null) {
					latitude = (int) (l.getLatitude() * 1000000);
					longitude = (int) (l.getLongitude() * 1000000);
				} else {
					latitude = 37222281;
					longitude = 127187283;
					Toast.makeText(getApplicationContext(),
							"GPS 정보를 받지 못하여 기본좌표를 사용합니다.", Toast.LENGTH_LONG).show();
				}
				
				// FileTransportManager을 이용한 파일전송 실행
				// 관련된 이미지도 같이 전송한다.
				String path = ftm.upload(getFilesDir().getPath().toString()
						+ "/", bookInfo.getBookFileName(), latitude,
						longitude);
				for (int i = 0; i < bookInfo.getUploadFileNames().size(); i++) {
					try {
						ftm.DoImageUpload(path, getFilesDir().getPath()
								.toString()
								+ "/"
								+ bookInfo.getUploadFileNames().get(i));
					} catch (IOException e) {
						Toast.makeText(getApplicationContext(),
								"뭔가 잘못되었어요!", Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					}
				}
				Toast.makeText(getApplicationContext(), "업로드 성공!",
						Toast.LENGTH_SHORT).show();
				
			} else {
				Toast.makeText(getApplicationContext(), "인터넷이 연결되어있지 않습니다.",
						Toast.LENGTH_LONG).show();
			}
			
			break;
		}
	}

	// created 2012/11/22 정민규
	// 퀵세이브
	public void saveWorkQ() {
		try {
			String prefix; // 파일명 앞에붙을이름 (디렉토리생성은 현재 안됨)
			prefix = ""; // ------------ 그냥 세이브와 여기정도만 다름

			FileOutputStream fos = openFileOutput(prefix
					+ Constants.SAVE_FILENAME, Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(
					new BufferedOutputStream(fos));

			// 이미지를 기기에 저장. DDMS에 data/data/패키지명/files/ 에 저장됨
			Bitmap bm;
			String bmName; // 파일명
			FileOutputStream out;
			for (int i = 0; i < maxPageNumber; i++) { // 각 페이지에
				for (int j = 0; j < pages.get(i).getImages().size(); j++) { // 각
																			// 이미지를
					bmName = prefix + i + "_" + j + "" + ".PNG"; // 파일명 :
																	// 책이름+페이지번호+이미지번호
					out = openFileOutput(bmName, Context.MODE_PRIVATE);

					bm = pages.get(i).getImages().get(j).getBd().getBitmap(); // 원본그림
																				// 가져옴

					try {
						bm.compress(CompressFormat.PNG, 100, out); // 파일로 저장
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						try {
							out.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}

			// 페이지들 정보 셋팅
			ArrayList<PageViewInfo> pageInfos = new ArrayList<PageViewInfo>();
			for (int i = 0; i < maxPageNumber; i++) {
				pages.get(i).createPageViewInfo();
				pageInfos.add(pages.get(i).getPageViewInfo());
			}
			bookInfo.setPageViewInfos(pageInfos); // bookInfo에 넣음

			oos.writeObject(bookInfo); // bookInfo객체를 저장

			if (oos != null) { // 스트림 닫기
				try {
					oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			Toast.makeText(this, "저장되었습니다!", 0).show();
		} catch (Exception e) {
			Log.e("저장실패:", e.getMessage());
			Toast.makeText(this, "저장실패!", 0).show();
		}

	}

	// created 2012/11/22 정민규
	// 퀵로드
	public void loadWorkQ() {
		try {
			String prefix; // 파일명 앞에붙을이름 (디렉토리생성은 현재 안됨)
			prefix = "";

			FileInputStream fis = openFileInput(prefix + Constants.SAVE_FILENAME);		//로드위한 파일스트림
			ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(fis));//버퍼스트림으로도 감쌈(안써도 되긴함)

			bookInfo = (BookInfo) ois.readObject();		//파일에서 불러와 책정보 객체에 저장함

			if (ois != null) { // 스트림 닫기
				try {
					ois.close();
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			// 불러온정보 셋팅(bookInfo의 정보를 이용해 셋팅)
			maxPageNumber = bookInfo.getPageInfos().size();	//최대페이지 수 설정
//			pages.get(currentPageNumber - 1).stopThread();	//스레드 중지
			pages.clear(); // 모든 페이지 지움
			for (int i = 0; i < maxPageNumber; i++) { // 불러온 페이지정보대로 셋팅
				PageView pv = new PageView(this);		//추가할 임시 페이지(PageView) 생성
				pv.setPageViewInfo(bookInfo.getPageInfos().get(i)); // 페이지정보 줌
				pv.setByPageViewInfo();								// 페이지정보대로 페이지 셋
				pv.createTextView(pv.getPageType(), inflater);		//각페이지 text 셋
				Log.i("bookeditor msg",""+pv.getPageType());
				pages.add(pv);										//페이지들 배열에 추가
			}
			currentPageNumber = 1;					//불러온후 현재페이지는 1페이지로 설정함

			// 이미지 로드해 셋 data/data/패키지명/files/ 에서 불러옴
			String bmName; // 파일명
			FileInputStream in;	//불러오기 위한 파일스트림
			BitmapDrawable bd;	//이미지를 임시저장할 객체
			for (int i = 0; i < maxPageNumber; i++) { // 페이지 수 만큼
				for (int j = 0; j < pages.get(i).getImages().size(); j++) { // 각 이미지만큼
					bmName = prefix + i + "_" + j + "" + ".PNG"; //파일명 : 책이름+작자명+페이지번호+이미지번호
					in = openFileInput(bmName);					//파일 열기

					// bm = BitmapFactory.decodeFile(bmName); //메모리누수 우려로 안씀
					bd = new BitmapDrawable(in); //파일에서 그림 가져옴
					pages.get(i).getImages().get(j).setBitmapByBd(bd);//해당 페이지의 해당 이미지에 그림을 셋팅시킴

					Log.i("msg", pages.get(i).getImages().size() + "");

					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
	
			//로그로 파일목록 제대로 로드되나 확인
			Log.i("Book Editor msg", bookInfo.getBookFileName());
			for(String s : bookInfo.getUploadFileNames()){
				Log.i("Book Editor msg", s);
			}

			pageViewer.removeAllViews();	//프레임뷰에 쌓인 뷰들 없앰
			pageNumberView.setText(currentPageNumber.toString() + "/"	//페이지번호 셋팅
					+ maxPageNumber.toString());
			pageViewer.addView(pages.get(currentPageNumber - 1));		//프레임뷰에 페이지뷰 쌓음
			pageViewer.addView(pages.get(currentPageNumber - 1).getTextView());	//프레임뷰에 해당페이지의 텍스트뷰를 쌓음
			Toast.makeText(this, "불러왔습니다", 0).show();	//성공여부 출력
		}catch(Exception e){
			Log.e("불러오기실패:", e.getMessage());
			Toast.makeText(this, "불러오기실패!", 0).show();
		}
	}

	// created 2012/11/29 정민규
	// 로드 (불러올 파일을 선택하는 액티비티 호출)
	public void loadWork() {
		Intent intent = new Intent(this, LoadActivity.class);
		startActivityForResult(intent, 0);		
	}
	
	//로드 (선택된 id, 선택된 책이름, 선택된 작자명 받음)     sId는 현재안씀
	public void loadWork2(int sId, String sName, String sAuthor) {		
		try {
			String prefix = sName + "_" + sAuthor + "_"; // 파일명 앞에붙을이름
			
			String[] filter_word = { "\\p{Space} ", " ", "\\?", "\\/",
					"\\*", "\\+", "\\|", "\\\\", "\\\"", "\\:", "\\<",
					"\\>", "\\?", "\\/" };
			for (int i = 0; i < filter_word.length; i++) { // 파일명으로 쓸수없는 특수문자를 "_"로 교체
				prefix = prefix.replaceAll(filter_word[i], "_");
			}

			FileInputStream fis = openFileInput(prefix + Constants.SAVE_FILENAME);		//로드위한 파일스트림
			ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(fis));//버퍼스트림으로도 감쌈(안써도 되긴함)

			bookInfo = (BookInfo) ois.readObject();		//파일에서 불러와 책정보 객체에 저장함

			if (ois != null) { // 스트림 닫기
				try {
					ois.close();
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			// 불러온정보 셋팅(bookInfo의 정보를 이용해 셋팅)
			maxPageNumber = bookInfo.getPageInfos().size();	//최대페이지 수 설정
//			pages.get(currentPageNumber - 1).stopThread();	//스레드 중지
			pages.clear(); // 모든 페이지 지움
			for (int i = 0; i < maxPageNumber; i++) { // 불러온 페이지정보대로 셋팅
				PageView pv = new PageView(this);		//추가할 임시 페이지(PageView) 생성
				pv.setPageViewInfo(bookInfo.getPageInfos().get(i)); // 페이지정보 줌
				pv.setByPageViewInfo();								// 페이지정보대로 페이지 셋
				pv.createTextView(pv.getPageType(), inflater);		//각페이지 text 셋
				Log.i("bookeditor msg",""+pv.getPageType());
				pages.add(pv);										//페이지들 배열에 추가
			}
			currentPageNumber = 1;					//불러온후 현재페이지는 1페이지로 설정함

			// 이미지 로드해 셋 data/data/패키지명/files/ 에서 불러옴
			String bmName; // 파일명
			FileInputStream in;	//불러오기 위한 파일스트림
			BitmapDrawable bd;	//이미지를 임시저장할 객체
			for (int i = 0; i < maxPageNumber; i++) { // 페이지 수 만큼
				for (int j = 0; j < pages.get(i).getImages().size(); j++) { // 각 이미지만큼
					bmName = prefix + i + "_" + j + "" + ".PNG"; //파일명 : 책이름+작자명+페이지번호+이미지번호
					in = openFileInput(bmName);					//파일 열기

					// bm = BitmapFactory.decodeFile(bmName); //메모리누수 우려로 안씀
					bd = new BitmapDrawable(in); //파일에서 그림 가져옴
					pages.get(i).getImages().get(j).setBitmapByBd(bd);//해당 페이지의 해당 이미지에 그림을 셋팅시킴

					Log.i("msg", pages.get(i).getImages().size() + "");

					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
	
			//로그로 파일목록 제대로 로드되나 확인
			Log.i("Book Editor msg", bookInfo.getBookFileName());
			for(String s : bookInfo.getUploadFileNames()){
				Log.i("Book Editor msg", s);
			}

			pageViewer.removeAllViews();	//프레임뷰에 쌓인 뷰들 없앰
			pageNumberView.setText(currentPageNumber.toString() + "/"	//페이지번호 셋팅
					+ maxPageNumber.toString());
			pageViewer.addView(pages.get(currentPageNumber - 1));		//프레임뷰에 페이지뷰 쌓음
			pageViewer.addView(pages.get(currentPageNumber - 1).getTextView());	//프레임뷰에 해당페이지의 텍스트뷰를 쌓음
			
			for(int i = 0; i < pages.size(); i++) {
				pages.get(i).setEnabled(false);
			}
			
			
			Toast.makeText(this, "불러왔습니다", 0).show();	//성공여부 출력
		}catch(Exception e){
			Log.e("불러오기실패:", e.getMessage());
			Toast.makeText(this, "불러오기실패!", 0).show();
		}
	}

	//부른 액티비티(로드)의 응답 받음
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
    	super.onActivityResult(requestCode, resultCode, intent);
    	if(requestCode == 0){
    		if(resultCode == Activity.RESULT_OK){
    			int sId = intent.getIntExtra("SelectedId", 0);
    			String sName = intent.getStringExtra("SelectedName");			//선택된 줄의 id, 책이름 설정
    			String sAuthor = intent.getStringExtra("SelectedAuthor");			//선택된 줄의 id, 책이름 설정

    			loadWork2(sId, sName, sAuthor);
    		}
    	} else if(requestCode == 1){//사진찍은후 byte[]로 돌아올때
    		if(resultCode == Activity.RESULT_OK){
	    		byte[] b = intent.getByteArrayExtra("bytes");	//byte[] 받음
	    		pages.get(currentPageNumber - 1).insertImage(b);
    		}
    	}  	
    }

	public class MyLocationListener implements LocationListener {

		public void onLocationChanged(Location l) {
			Log.d("Location", l.toString());
		}

		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
		}

		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
		}

	}

	public void onResume() {
		Log.i("msg", "BookEditor onResume");
		super.onResume();
		// if(isStart == false){
		// pages.get(currentPageNumber - 1).startThread(); //스레드 시작
		// }
		// isStart = false;
	}

	public void onPause() {
		Log.i("msg", "BookEditor onPause");
		super.onPause();
		// pages.get(currentPageNumber - 1).stopThread(); //스레드 중지
	}

	public void onStop() {
		Log.i("msg", "BookEditor onStop");
		super.onStop();
	}

	public void onRestart() {
		Log.i("msg", "BookEditor onRestart");
		super.onRestart();
	}

	public void onDestroy() {
		Log.i("msg", "BookEditor onDestroy");
		super.onDestroy();
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.removeUpdates(locationListener);
	}

}