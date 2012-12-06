package kr.ac.mju.strangelibrary;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import kr.ac.mju.strangelibrary.util.FileTransportManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
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
	private LocationManager locationManager;
	private LocationListener locationListener;

	static final int DO_SQL = 0;
	static final int NO_SQL = 1;
	static final String KEY = "_ID";
	static final String NAME = "NAME";
	static final String AUTHOR = "AUTHOR";

	// Buttons
	private Button prev_butten;
	private Button next_button;
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
		prev_butten = (Button) findViewById(R.id.prev_button);
		next_button = (Button) findViewById(R.id.next_button);
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
		getMenuInflater().inflate(R.menu.bookeditor_menu, menu);
		return true;
	}

	// 메뉴아이템 선택시
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_Qsave: // 퀵세이브
			saveWorkQ();
			break;
		case R.id.menu_Qload: // 퀵로드
			loadWorkQ();
			break;
		case R.id.menu_save: // 세이브
			saveWork();
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
			/*
			ConnectivityManager connect = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
			if (connect.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
				Location l = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

				if (l != null) {
					int latitude = (int) (l.getLatitude() * 1000000);
					int longitude = (int) (l.getLongitude() * 1000000);
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
					Toast.makeText(getApplicationContext(),
							"GPS 정보를 받지 못하였습니다.", Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(getApplicationContext(), "인터넷이 연결되어있지 않습니다.",
						Toast.LENGTH_LONG).show();
			}
			*/
			
			Location l = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

			if (l != null) {
				int latitude = (int) (l.getLatitude() * 1000000);
				int longitude = (int) (l.getLongitude() * 1000000);
				String path = ftm.upload(getFilesDir().getPath().toString()
						+ "/", bookInfo.getBookFileName(), latitude, longitude);
				for (int i = 0; i < bookInfo.getUploadFileNames().size(); i++) {
					try {
						ftm.DoImageUpload(path, getFilesDir().getPath()
								.toString()
								+ "/"
								+ bookInfo.getUploadFileNames().get(i));
					} catch (IOException e) {
						Toast.makeText(getApplicationContext(), "뭔가 잘못되었어요!",
								Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					}
				}
				Toast.makeText(getApplicationContext(), "업로드 성공!",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getApplicationContext(), "GPS 정보를 받지 못하였습니다.",
						Toast.LENGTH_LONG).show();
			}
			
			String path = ftm.upload(getFilesDir().getPath().toString()
					+ "/", bookInfo.getBookFileName(), 37222281, 127187283);
			for (int i = 0; i < bookInfo.getUploadFileNames().size(); i++) {
				try {
					ftm.DoImageUpload(path, getFilesDir().getPath()
							.toString()
							+ "/"
							+ bookInfo.getUploadFileNames().get(i));
				} catch (IOException e) {
					Toast.makeText(getApplicationContext(), "뭔가 잘못되었어요!",
							Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
			}
			Toast.makeText(getApplicationContext(), "업로드 성공!",
					Toast.LENGTH_SHORT).show();
			
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

			FileInputStream fis = openFileInput(prefix
					+ Constants.SAVE_FILENAME);
			ObjectInputStream ois = new ObjectInputStream(
					new BufferedInputStream(fis));

			bookInfo = (BookInfo) ois.readObject();

			if (ois != null) { // 스트림 닫기
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			// 불러온정보 셋팅
			maxPageNumber = bookInfo.getPageInfos().size();
			// pages.get(currentPageNumber - 1).stopThread(); //스레드 중지
			pages.clear(); // 모든 페이지 지움
			for (int i = 0; i < maxPageNumber; i++) { // 불러온 페이지정보대로 셋팅
				PageView pv = new PageView(this);
				pv.setPageViewInfo(bookInfo.getPageInfos().get(i)); // 페이지정보 줌
				pv.setByPageViewInfo(); // 페이지정보대로 페이지 셋
				pv.createTextView(pv.getPageType(), inflater); // 각페이지 text 셋
				Log.i("bookeditor msg", "" + pv.getPageType());
				pages.add(pv);
			}
			currentPageNumber = 1;

			// 이미지 로드해 셋 data/data/패키지명/files/ 에서 불러옴
			String bmName; // 파일명
			FileInputStream in;
			BitmapDrawable bd;
			for (int i = 0; i < maxPageNumber; i++) { // 페이지 수 만큼
				for (int j = 0; j < pages.get(i).getImages().size(); j++) { // 각
																			// 이미지만큼
					bmName = prefix + i + "_" + j + "" + ".PNG"; // 파일명 :
																	// 책이름+페이지번호+이미지번호
					in = openFileInput(bmName);

					// bm = BitmapFactory.decodeFile(bmName); //메모리누수 우려
					bd = new BitmapDrawable(in); // 파일에서 그림 가져옴
					pages.get(i).getImages().get(j).setBitmapByBd(bd);

					Log.i("msg", pages.get(i).getImages().size() + "");

					try {
						bd = new BitmapDrawable(in);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						try {
							in.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}

			pageViewer.removeAllViews();
			pageNumberView.setText(currentPageNumber.toString() + "/"
					+ maxPageNumber.toString());
			pageViewer.addView(pages.get(currentPageNumber - 1));
			pageViewer.addView(pages.get(currentPageNumber - 1).getTextView());
			Toast.makeText(this, "불러왔습니다", 0).show();
		} catch (Exception e) {
			Log.e("불러오기실패:", e.getMessage());
			Toast.makeText(this, "불러오기실패!", 0).show();
		}
	}

	// created 2012/11/29 정민규
	// 세이브 (파일명 앞에 책이름 붙여 저장. ~_pages.dat + ?_?.PNG 들로 저장)
	public void saveWork() {
		String prefix; // 파일명 앞에붙을이름 (디렉토리생성은 현재 안됨)
		bookInfo.setBookName((pages.get(0).getEditText().getText().toString())); // 책이름
																					// 셋

		// pages.get(currentPageNumber -
		// 1).getRootView().setDrawingCacheEnabled(true); //캡쳐소스이나 현재는 제대로안됨.
		// 스레드로 바꿔도 동일
		// Bitmap bm = pages.get(currentPageNumber -
		// 1).getRootView().getDrawingCache(); //PageView는 검은화면으로 캡쳐됨
		// String bmName; // 파일명
		// FileOutputStream out;
		// bmName = "1234.PNG"; // 파일명 : 책이름+페이지번호+이미지번호
		// try {
		// out = openFileOutput(bmName, Context.MODE_PRIVATE);
		// bm.compress(CompressFormat.PNG, 100, out); // 파일로 저장
		// } catch (FileNotFoundException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		if (!bookInfo.getBookName().equals("")) { // 책이름 "" 아니면 책이름으로
			prefix = bookInfo.getBookName();
			// String[] filter_word =
			// {"","\\.","\\?","\\/","\\~","\\!","\\@","\\#","\\$","\\%","\\^",
			// "\\&","\\*","\\(","\\)","\\_","\\+","\\=","\\|","\\\\","\\}","\\]","\\{","\\[",
			// "\\\"","\\'","\\:","\\;","\\<","\\,","\\>","\\.","\\?","\\/"};
			String[] filter_word = { "\\p{Space} ", " ", "\\?", "\\/", "\\*",
					"\\+", "\\|", "\\\\", "\\\"", "\\:", "\\<", "\\>", "\\?",
					"\\/" };
			for (int i = 0; i < filter_word.length; i++) { // 파일명으로 쓸수없는 특수문자를
															// "_"로 교체
				prefix = prefix.replaceAll(filter_word[i], "_");
			}

			prefix = prefix + "_";
		} else {
			prefix = ""; // 책이름 ""이면 디폴트 파일명
		}

		// File file = new
		// File(getApplicationContext().getFilesDir().getPath().toString() + "/"
		// + prefix + Constants.SAVE_FILENAME);

		ContentResolver cr = getContentResolver();
		Cursor cursor = cr.query(MyProvider.CONTENT_URI, new String[] {
				MyProvider.ID, MyProvider.NAME, MyProvider.AUTHOR },
				MyProvider.NAME + "=?",
				new String[] { bookInfo.getBookName() }, null);

		if (cursor.getCount() > 0) { // 이미 있으면
			AlertDialog.Builder aDialog = new AlertDialog.Builder(this);
			aDialog.setTitle("같은 제목이 이미 있습니다. 덮어씌우시겠습니까?");
			aDialog.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							saveWork2(NO_SQL); // 변수넘기기 어려워서
						}
					});
			aDialog.setNegativeButton("No",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						}
					});
			aDialog.setOnCancelListener(new OnCancelListener() { // 취소시
				public void onCancel(DialogInterface arg0) {
				}
			});
			AlertDialog ad = aDialog.create();
			ad.show();
		} else {
			saveWork2(DO_SQL);
		}
	}

	// 세이브작업2 (책이름 받아서 그걸로 저장)
	public void saveWork2(int isDoSql) {
		try {
			String prefix; // 파일명 앞에붙을이름 (디렉토리생성은 현재 안됨)
			bookInfo.setBookName((pages.get(0).getEditText().getText()
					.toString())); // 책이름 셋

			if (isDoSql == NO_SQL) { // 덮어씌우기일경우
				deleteWork(bookInfo.getBookName()); // 기존 저장된 이미지들 삭제
			}

			if (!bookInfo.getBookName().equals("")) { // 책이름 "" 아니면 책이름으로
				prefix = bookInfo.getBookName();
				String[] filter_word = { "\\p{Space} ", " ", "\\?", "\\/",
						"\\*", "\\+", "\\|", "\\\\", "\\\"", "\\:", "\\<",
						"\\>", "\\?", "\\/" };
				for (int i = 0; i < filter_word.length; i++) { // 파일명으로 쓸수없는
																// 특수문자를 "_"로 교체
					prefix = prefix.replaceAll(filter_word[i], "_");
				}

				prefix = prefix + "_";
			} else {
				prefix = ""; // 책이름 ""이면 디폴트 파일명
			}

			File file = new File(getApplicationContext().getFilesDir()
					.getPath().toString()
					+ "/" + prefix + Constants.SAVE_FILENAME);

			// FileOutputStream fos = new FileOutputStream(file);
			FileOutputStream fos = openFileOutput(prefix
					+ Constants.SAVE_FILENAME, Context.MODE_PRIVATE);
			// ObjectOutputStream oos = new ObjectOutputStream(fos);
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
				Log.i("bookeditor msg", "" + pages.get(i).getPageType());
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

			if (isDoSql != NO_SQL) { // 덮어씌우기 아닐경우
				ContentValues cv = new ContentValues(); // SQLITE로 DB에 추가
				cv.put(NAME, bookInfo.getBookName());
				cv.put(AUTHOR, "-");
				ContentResolver cr = getContentResolver();
				cr.insert(Uri.parse(MyProvider.URI), cv);
			}

			Toast.makeText(this, "저장되었습니다!", 0).show();
		} catch (Exception e) {
			Log.e("저장실패:", e.getMessage());
			Toast.makeText(this, "저장실패!", 0).show();
		}
	}

	// created 2012/11/29 정민규
	// 로드 (로드 액티비티 호출)
	public void loadWork() {
		Intent intent = new Intent(this, LoadActivity.class);
		startActivityForResult(intent, 0);
	}

	// 로드 (선택된 id, 선택된 name 받음. 이름으로 로드함) sId는 현재안씀
	public void loadWork(int sId, String sName) {
		try {
			String prefix = sName; // 파일명 앞에붙을이름

			if (!prefix.equals("")) { // 책이름 "" 아니면 책이름으로
				String[] filter_word = { "\\p{Space} ", " ", "\\?", "\\/",
						"\\*", "\\+", "\\|", "\\\\", "\\\"", "\\:", "\\<",
						"\\>", "\\?", "\\/" };
				for (int i = 0; i < filter_word.length; i++) { // 파일명으로 쓸수없는
																// 특수문자를 "_"로 교체
					prefix = prefix.replaceAll(filter_word[i], "_");
				}

				prefix = prefix + "_";
			} else {
				prefix = ""; // 책이름 ""이면 디폴트 파일명
			}

			FileInputStream fis = openFileInput(prefix
					+ Constants.SAVE_FILENAME);
			ObjectInputStream ois = new ObjectInputStream(
					new BufferedInputStream(fis));

			bookInfo = (BookInfo) ois.readObject();

			if (ois != null) { // 스트림 닫기
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			// 불러온정보 셋팅
			maxPageNumber = bookInfo.getPageInfos().size();
			// pages.get(currentPageNumber - 1).stopThread(); //스레드 중지
			pages.clear(); // 모든 페이지 지움
			for (int i = 0; i < maxPageNumber; i++) { // 불러온 페이지정보대로 셋팅
				PageView pv = new PageView(this);
				pv.setPageViewInfo(bookInfo.getPageInfos().get(i)); // 페이지정보 줌
				pv.setByPageViewInfo(); // 페이지정보대로 페이지 셋
				pv.createTextView(pv.getPageType(), inflater); // 각페이지 text 셋
				Log.i("bookeditor msg", "" + pv.getPageType());
				pages.add(pv);
			}
			currentPageNumber = 1;

			// 이미지 로드해 셋 data/data/패키지명/files/ 에서 불러옴
			String bmName; // 파일명
			FileInputStream in;
			BitmapDrawable bd;
			for (int i = 0; i < maxPageNumber; i++) { // 페이지 수 만큼
				for (int j = 0; j < pages.get(i).getImages().size(); j++) { // 각
																			// 이미지만큼
					bmName = prefix + i + "_" + j + "" + ".PNG"; // 파일명 :
																	// 책이름+페이지번호+이미지번호
					in = openFileInput(bmName);

					// bm = BitmapFactory.decodeFile(bmName); //메모리누수 우려
					bd = new BitmapDrawable(in); // 파일에서 그림 가져옴
					pages.get(i).getImages().get(j).setBitmapByBd(bd);

					Log.i("msg", pages.get(i).getImages().size() + "");

					try {
						bd = new BitmapDrawable(in);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						try {
							in.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}

			pageViewer.removeAllViews();
			pageNumberView.setText(currentPageNumber.toString() + "/"
					+ maxPageNumber.toString());
			pageViewer.addView(pages.get(currentPageNumber - 1));
			pageViewer.addView(pages.get(currentPageNumber - 1).getTextView());
			Toast.makeText(this, "불러왔습니다", 0).show();
		} catch (Exception e) {
			Log.e("불러오기실패:", e.getMessage());
			Toast.makeText(this, "불러오기실패!", 0).show();
		}
	}

	// 삭제작업. DB는 놔둠 (덮어씌우기시)
	private void deleteWork(String sName) { // 책이름 받음
		try {
			String prefix = sName; // 파일명 앞에붙을이름 (디렉토리생성은 현재 안됨)

			if (!prefix.equals("")) { // 책이름 "" 아니면 책이름으로
				String[] filter_word = { "\\p{Space} ", " ", "\\?", "\\/",
						"\\*", "\\+", "\\|", "\\\\", "\\\"", "\\:", "\\<",
						"\\>", "\\?", "\\/" };
				for (int i = 0; i < filter_word.length; i++) { // 파일명으로 쓸수없는
																// 특수문자를 "_"로 교체
					prefix = prefix.replaceAll(filter_word[i], "_");
				}

				prefix = prefix + "_";
			} else {
				prefix = ""; // 책이름 ""이면 디폴트 파일명
			}

			FileInputStream fis = openFileInput(prefix
					+ Constants.SAVE_FILENAME);
			// ObjectInputStream ois = new ObjectInputStream(fis);
			ObjectInputStream ois = new ObjectInputStream(
					new BufferedInputStream(fis));

			BookInfo bookInfo = (BookInfo) ois.readObject(); // 임시변수 bookInfo따로씀
			ArrayList<PageView> pages = new ArrayList<PageView>(); // 임시변수 pages
																	// 따로씀

			if (ois != null) { // 스트림 닫기
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			// 불러온정보 셋팅(각 페이지당 이미지 수 셋팅)
			int maxPageNumber = bookInfo.getPageInfos().size();
			// pages.get(currentPageNumber - 1).stopThread(); //스레드 중지
			for (int i = 0; i < maxPageNumber; i++) { // 불러온 페이지정보대로 셋팅
				PageView pv = new PageView(this);
				pv.setPageViewInfo(bookInfo.getPageInfos().get(i)); // 페이지정보 줌
				pv.setByPageViewInfo(); // 페이지정보대로 페이지 셋
				// pv.createTextView(pv.getPageType(), inflater); //각페이지 text 셋
				pages.add(pv);
			}

			// 이미지들 삭제
			String bmName; // 파일명
			File tempFile;
			for (int i = 0; i < maxPageNumber; i++) { // 각 페이지에
				for (int j = 0; j < pages.get(i).getImages().size(); j++) { // 각
																			// 이미지를
					bmName = prefix + i + "_" + j + "" + ".PNG"; // 파일명 :
																	// 책이름+페이지번호+이미지번호

					tempFile = new File(getApplicationContext().getFilesDir()
							.getPath().toString()
							+ "/" + bmName);
					Log.i("bookeditor msg", getApplicationContext()
							.getFilesDir().getPath().toString()
							+ "/" + bmName);
					if (tempFile.exists()) {
						tempFile.delete(); // 삭제시도
					}
				}
			}

			Toast.makeText(this, "기존 데이터 삭제성공", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Toast.makeText(this, "기존 데이터 이미 삭제됨", Toast.LENGTH_SHORT).show();
		}
	}

	// 부른 액티비티(로드)의 응답 받음
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (requestCode == 0) {
			if (resultCode == Activity.RESULT_OK) {
				int sId = intent.getIntExtra("SelectedId", 0);
				String sName = intent.getStringExtra("SelectedName"); // 선택된 줄의
																		// id,
																		// 책이름
																		// 설정
				loadWork(sId, sName);
				
				for(int i = 0; i < pages.size(); i++) {
					pages.get(i).setEnabled(false);
				}
			}else{
				finish();
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