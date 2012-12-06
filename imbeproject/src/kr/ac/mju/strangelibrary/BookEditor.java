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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SimpleCursorAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

//created by 60062446 박정실
//created date : 2012/11/17
//last modify : 2012/11/29
public class BookEditor extends Activity implements OnClickListener {
	final Context context = this;

	private FrameLayout pageViewer; // main panel 역할을함
	private Integer currentPageNumber; // 현제 페이지
	private Integer maxPageNumber; // 최대 페이지
	private TextView pageNumberView; // 페이지 번호를 보여주는 TextView
	private AlertDialog.Builder alertBuilder; // AlertDialog Builder
	private AlertDialog imgInsertDialog; // 그림 추가 다이얼로그 객체
	private ArrayList<PageView> pages; // ImageView Vector 객체
	protected BookInfo bookInfo; // 책정보 객체(세이브/로드용)
	private BackgroundSelectListener backgroundListner; // background 선택을 위한 리스너
	private PageTypeSelectListener pageTypeListner;
	private Dialog backgroundDialog; // background 선택을 위한 다이얼로그
	private ImageInsertListener insertListner; // Image를 삽입하기위한 다이얼로그
	private Dialog illustrationDialog; // 삽화 삽입 다이얼로그
	private Dialog humanImageDialog; // 사람 그림 삽입 다이얼로그
	private Dialog animalImageDialog; // 동물 그림 삽입 다이얼로그
	private Dialog objectImageDialog; // 사물 그림 삽입 다이얼로그
	private AlertDialog pageTypeDialog; // 페이지타입 선택 다이얼로그
	private String preBgFileName;
	private LayoutInflater inflater;
	private boolean isStart = true;		//처음 시작하나. (onResume때문)
	SimpleCursorAdapter adapter;		//리스트 항목 정보얻기용
	
	static final int DO_SQL = 0;
	static final int NO_SQL = 1;

	// Buttons
	private ImageButton prevButten;
	private ImageButton nextButton;
	private Button deleteButton;
	private Button insertButton;
	private Button clear;
	private Button black;
	private Button red;
	private Button green;
	private Button blue;
	private Button yellow;
	private Button bgmBtn;
	private Button setBackground;
	private ImageButton cameraButton;

	// 그림 삽입 메뉴에 사용될 CharSequence 배열
	final CharSequence[] category = { "삽화", "인물", "동물", "사물" };

	private BgmService bgmService = null;
	
	//원격서비스 연결용 객체(AIDL)
	private ServiceConnection serviceConn = new ServiceConnection() {

		public void onServiceDisconnected(ComponentName p_name) {//서비스 끊을때
			Log.i("bookeditor", "onServiceDisConnected");	//디버깅 위한 로그출력
			try {
				if (bgmService != null){
					bgmService.bgmStop();	//bgm종료
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			bgmService = null;
		}

		public void onServiceConnected(ComponentName name, IBinder service) {//서비스 연결시
			Log.i("bookeditor", "onServiceConnected");		//디버깅 위한 로그출력
			bgmService = BgmService.Stub.asInterface(service);
			try {
				bgmService.bgmStart();	//bgm시작
			} catch (RemoteException e) {				
				e.printStackTrace();
			}
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.book_editor);

		// Attributes
		currentPageNumber = 1;
		maxPageNumber = 1;

		// Components
		// 리스너 인스턴스화
		backgroundListner = new BackgroundSelectListener();
		insertListner = new ImageInsertListener();
		pageTypeListner = new PageTypeSelectListener();
		
		// 다이얼로그 생성에 사용될 Scrollview와 TableLayout 참조변수
		ScrollView sv;
		TableLayout tl;

		// backgroundDialog 생성
		// Constants에서 관련된 이미지를 받아와 다이얼로그에 포함시킨다.
		backgroundDialog = new Dialog(this);
		backgroundDialog.setTitle("배경은 선택해 주세요");
		sv = new ScrollView(backgroundDialog.getContext());
		tl = new TableLayout(sv.getContext());
		for (int i = 0; i < Constants.Background.getLength(); i++) {

			ImageView image = new ImageView(backgroundDialog.getContext());
			image.setImageResource(Constants.Background.get(i));
			// image.setId(Constants.Background.get(i));
			image.setId(i);
			image.setMaxHeight(220);
			image.setMaxWidth(400);
			image.setPadding(50, 10, 50, 10);
			image.setAdjustViewBounds(true);
			image.setOnClickListener(backgroundListner);
			TableRow tr = new TableRow(tl.getContext());
			tr.addView(image);
			tl.addView(tr);
		}
		sv.addView(tl);
		backgroundDialog.setContentView(sv);
		
		// illustrationDialog 생성
		// Constants에서 관련된 이미지를 받아와 다이얼로그에 포함시킨다.
		illustrationDialog = new Dialog(this);
		illustrationDialog.setTitle("삽화를 선택해 주세요.");
		sv = new ScrollView(illustrationDialog.getContext());
		tl = new TableLayout(sv.getContext());
		for (int i = 0; i < Constants.Illustration.getLength(); i++) {

			ImageView image = new ImageView(illustrationDialog.getContext());
			image.setImageResource(Constants.Illustration.get(i));
			image.setId(Constants.Illustration.get(i));
			image.setMaxHeight(220);
			image.setMaxWidth(400);
			image.setPadding(50, 10, 50, 10);
			image.setAdjustViewBounds(true);
			image.setOnClickListener(insertListner);
			TableRow tr = new TableRow(tl.getContext());
			tr.addView(image);
			tl.addView(tr);
		}
		sv.addView(tl);
		illustrationDialog.setContentView(sv);

		// humanImageDialog 생성
		// Constants에서 관련된 이미지를 받아와 다이얼로그에 포함시킨다.
		humanImageDialog = new Dialog(this);
		humanImageDialog.setTitle("이미지를 선택해 주세요");
		sv = new ScrollView(humanImageDialog.getContext());
		tl = new TableLayout(sv.getContext());
		for (int i = 0; i < Constants.Human.getLength(); i++) {

			ImageView image = new ImageView(humanImageDialog.getContext());
			image.setImageResource(Constants.Human.get(i));
			image.setId(Constants.Human.get(i));
			image.setMaxHeight(220);
			image.setMaxWidth(400);
			image.setPadding(50, 10, 50, 10);
			image.setAdjustViewBounds(true);
			image.setOnClickListener(insertListner);
			TableRow tr = new TableRow(tl.getContext());
			tr.addView(image);
			tl.addView(tr);
		}
		sv.addView(tl);
		humanImageDialog.setContentView(sv);

		// animalImageDialog 생성
		// Constants에서 관련된 이미지를 받아와 다이얼로그에 포함시킨다.
		animalImageDialog = new Dialog(this);
		animalImageDialog.setTitle("이미지를 선택해 주세요");
		sv = new ScrollView(animalImageDialog.getContext());
		tl = new TableLayout(sv.getContext());
		for (int i = 0; i < Constants.Animal.getLength(); i++) {

			ImageView image = new ImageView(animalImageDialog.getContext());
			image.setImageResource(Constants.Animal.get(i));
			image.setId(Constants.Animal.get(i));
			image.setMaxHeight(220);
			image.setMaxWidth(400);
			image.setPadding(50, 10, 50, 10);
			image.setAdjustViewBounds(true);
			image.setOnClickListener(insertListner);
			TableRow tr = new TableRow(tl.getContext());
			tr.addView(image);
			tl.addView(tr);
		}
		sv.addView(tl);
		animalImageDialog.setContentView(sv);

		// objectImageDialog 생성
		// Constants에서 관련된 이미지를 받아와 다이얼로그에 포함시킨다.
		objectImageDialog = new Dialog(this);
		objectImageDialog.setTitle("이미지를 선택해 주세요");
		sv = new ScrollView(objectImageDialog.getContext());
		tl = new TableLayout(sv.getContext());
		for (int i = 0; i < Constants.Other.getLength(); i++) {

			ImageView image = new ImageView(objectImageDialog.getContext());
			image.setImageResource(Constants.Other.get(i));
			image.setId(Constants.Other.get(i));
			image.setMaxHeight(220);
			image.setMaxWidth(400);
			image.setPadding(50, 10, 50, 10);
			image.setAdjustViewBounds(true);
			image.setOnClickListener(insertListner);
			TableRow tr = new TableRow(tl.getContext());
			tr.addView(image);
			tl.addView(tr);
		}
		sv.addView(tl);
		objectImageDialog.setContentView(sv);
		
		// 삽입 다이얼로그의 첫번째 화면
		alertBuilder = new AlertDialog.Builder(this);
		alertBuilder.setTitle("Select img");
		alertBuilder.setItems(category, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				imgInsertDialog.dismiss();
				switch (item) {
				case 0:
					illustrationDialog.show();
					break;
				case 1:
					humanImageDialog.show();
					break;
				case 2:
					animalImageDialog.show();
					break;
				case 3:
					objectImageDialog.show();
					break;
				}
			}
		});
		imgInsertDialog = alertBuilder.create();

		// 책장 레이아웃 다이얼로그
		// 정의된 레이아웃을 이용해 생성한다.
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.page_type_select_dialog,
				(ViewGroup) findViewById(R.id.page_type_layout_root));
		alertBuilder = new AlertDialog.Builder(context);
		alertBuilder.setView(layout);
		pageTypeDialog = alertBuilder.create();
		ImageView type1 = (ImageView) layout.findViewById(R.id.book_type1);
		ImageView type2 = (ImageView) layout.findViewById(R.id.book_type2);
		type1.setOnClickListener(pageTypeListner);
		type2.setOnClickListener(pageTypeListner);

		// Button 참조변수
		prevButten = (ImageButton) findViewById(R.id.prev_button);
		nextButton = (ImageButton) findViewById(R.id.next_button);
		deleteButton = (Button) findViewById(R.id.delete_button);
		insertButton = (Button) findViewById(R.id.insert_button);
		clear = (Button) findViewById(R.id.clear);
		black = (Button) findViewById(R.id.black);
		red = (Button) findViewById(R.id.red);
		green = (Button) findViewById(R.id.green);
		blue = (Button) findViewById(R.id.blue);
		yellow = (Button) findViewById(R.id.yellow);
		bgmBtn = (Button) findViewById(R.id.bgmBtn);
		setBackground = (Button) findViewById(R.id.setBg);
		cameraButton = (ImageButton) findViewById(R.id.cameraBtn);

		pageNumberView = (TextView) findViewById(R.id.page_number);
		pageViewer = (FrameLayout) findViewById(R.id.farme);

		pages = new ArrayList<PageView>();
		bookInfo = new BookInfo();

		init();
	}

	// Initialization
	public void init() {
		// 버튼에 리스너 부착
		prevButten.setOnClickListener(this);
		nextButton.setOnClickListener(this);
		deleteButton.setOnClickListener(this);
		insertButton.setOnClickListener(this);
		clear.setOnClickListener(this);
		black.setOnClickListener(this);
		red.setOnClickListener(this);
		green.setOnClickListener(this);
		blue.setOnClickListener(this);
		yellow.setOnClickListener(this);
		bgmBtn.setOnClickListener(this);
		setBackground.setOnClickListener(this);
		cameraButton.setOnClickListener(this);
		
		// 첫 page를 생성하고 pageViewer에 add
		inflater = (LayoutInflater) context
				.getSystemService(LAYOUT_INFLATER_SERVICE);
//		View view = inflater.inflate(R.layout.book_title,
//				(ViewGroup) findViewById(R.id.book_title_root));
		PageView pv = new PageView(this);		
		
		pv.createTextView(Constants.PAGE_TYPE.Title, inflater);//밑에써도됨
		pages.add(pv);
		
//		pages.get(0).setTextView(view);
		pageViewer.addView(pages.get(0));
		if(pages.get(0).getTextView() != null){
			pageViewer.addView(pages.get(0).getTextView());
			pages.get(0).getEditText().setBackgroundColor(Color.WHITE);
		}

		// 페이지번호 설정
		pageNumberView.setText(currentPageNumber.toString() + "/"
				+ maxPageNumber.toString());
		
//		Intent i = getIntent();							//인텐트를 메인에서 받았을때, 넘겨받은 책이름으로 로드함.
//		int type = i.getIntExtra(Constants.CALL_TYPE, 0);
//		if (type == Constants.MAIN_EDIT_LOAD){		
//			int sId = i.getIntExtra("SelectedId", 0);			//선택된 줄의 id, 책이름 설정
//			String sName = i.getStringExtra("SelectedName");
//			loadWork(sId, sName);
//		}
	}

	public void onStart() {
		super.onStart();
		pages.get(0).invalidate();
	}

	//옵션메뉴 표시
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.bookeditor_menu, menu);
		return true;
	}
	
	//옵션메뉴아이템 선택시
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.menu_Qsave:	//퀵세이브
			saveWorkQ();
			break;
		case R.id.menu_Qload:	//퀵로드
			loadWorkQ();
			break;
		case R.id.menu_save:	//세이브
			saveWork();
			break;
		case R.id.menu_load:	//로드
			loadWork();
			break;		
		}		
		return true;
	}	

	// Button Event ---------------------버튼작업-------------------------------
	public void onClick(View v) {
		switch (v.getId()) {
		// 전 페이지로 이동 버튼
		// currentpageNumber가 1인경우 경고메세지 출력
		// 아니면 currentpageNumber를 1 감소시킨후 재출력
		case R.id.prev_button:
			if (!(currentPageNumber == 1)) {				
//				pages.get(currentPageNumber - 1).stopThread();	//스레드 중지
				pageViewer.removeAllViews();
				currentPageNumber--;
				pageNumberView.setText(currentPageNumber.toString() + "/"
						+ maxPageNumber.toString());
				pageViewer.addView(pages.get(currentPageNumber - 1));
				if(pages.get(currentPageNumber - 1).getTextView() != null){
					pageViewer.addView(pages.get(currentPageNumber - 1).getTextView());
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
//				pages.get(currentPageNumber - 1).stopThread();	//스레드 중지
				// 마지막 페이지일경우 최대페이지를 증가
				pageTypeDialog.show();
				maxPageNumber++;
				pages.add(new PageView(this));
				
				pageViewer.removeAllViews();
				currentPageNumber++;
				pageNumberView.setText(currentPageNumber.toString() + "/"
						+ maxPageNumber.toString());
				pageViewer.addView(pages.get(currentPageNumber - 1));
			} else {
//				pages.get(currentPageNumber - 1).stopThread();	//스레드 중지
				pageViewer.removeAllViews();
				currentPageNumber++;
				pageNumberView.setText(currentPageNumber.toString() + "/"
						+ maxPageNumber.toString());
				pageViewer.addView(pages.get(currentPageNumber - 1));
				if(pages.get(currentPageNumber - 1).getTextView() != null){
					pageViewer.addView(pages.get(currentPageNumber - 1).getTextView());
				}
			}
			
			break;

		// maxPageNumber가 1이면 경고메세지 출력
		// currentPageNumber가 maxPageNumber와 같으면
		// 해당인덱스와 관련된 images의 요소를 하나 삭제후
		// maxPageNumber와 currnetPageNumber둘다 1 감소시킨후 재출력
		// 아니면 해당인덱스와 관련된 images의 요소를 하나 삭제후 maxPageNumber를 1 감소시킨후 재출력
		case R.id.delete_button:
			if (maxPageNumber == 1) {
				Toast.makeText(this, "첫페이지는 지울수 없습니다.",
						Toast.LENGTH_SHORT).show();
			} else if (currentPageNumber == maxPageNumber) {
//				pages.get(currentPageNumber - 1).stopThread();	//스레드 중지
				pages.remove(currentPageNumber - 1);
				maxPageNumber--;
				currentPageNumber--;
				pageNumberView.setText(currentPageNumber.toString() + "/"
						+ maxPageNumber.toString());				
				pageViewer.removeAllViews();
				pageViewer.addView(pages.get(currentPageNumber - 1));
				if(pages.get(currentPageNumber - 1).getTextView() != null){
					pageViewer.addView(pages.get(currentPageNumber - 1).getTextView());
				}

			} else {
//				pages.get(currentPageNumber - 1).stopThread();	//스레드 중지
				pages.remove(currentPageNumber - 1);
				maxPageNumber--;
				pageNumberView.setText(currentPageNumber.toString() + "/"
						+ maxPageNumber.toString());				
				pageViewer.removeAllViews();
				pageViewer.addView(pages.get(currentPageNumber - 1));
				if(pages.get(currentPageNumber - 1).getTextView() != null){
					pageViewer.addView(pages.get(currentPageNumber - 1).getTextView());
				}
			}
			break;

		// 삽입 버튼
		// imgInsertDialog 객체를 이용하여 그림을 추가한다.
		case R.id.insert_button:
			imgInsertDialog.show();
			break;
		case R.id.clear: // 모든 선 삭제
			synchronized (pages.get(currentPageNumber - 1).getVertexes()) {
				pages.get(currentPageNumber - 1).removeVertexes();
				pages.get(currentPageNumber - 1).callOnDraw();
			}
			break;
		case R.id.black:	//각 컬러설정 버튼. 컬러추서로 바꿀 예정
			pages.get(currentPageNumber - 1).setPaintColor(Color.BLACK);
			break;
		case R.id.red:
			pages.get(currentPageNumber - 1).setPaintColor(Color.RED);
			break;
		case R.id.green:
			pages.get(currentPageNumber - 1).setPaintColor(Color.GREEN);
			break;
		case R.id.blue:
			pages.get(currentPageNumber - 1).setPaintColor(Color.BLUE);
			break;
		case R.id.yellow:
			pages.get(currentPageNumber - 1).setPaintColor(Color.YELLOW);
			break;
		// bgm 켜기/끄기 (preference에 값 쓰고 변경, 원격서비스, aidl사용)
		case R.id.bgmBtn:
			SharedPreferences pref = getSharedPreferences(Constants.PREF_BGM, MODE_PRIVATE);//bgm파라미터 값 엶
			SharedPreferences.Editor editor = pref.edit(); // 수정용 에디터 설정

			String valueStr = pref.getString(Constants.PREF_BGM, ""); // 값 읽기. 없을시  ""리턴
			if (valueStr.equals("") || valueStr.equals("0")) {
				editor.putString(Constants.PREF_BGM, "1"); // 값을 1로 씀

				Intent intent = new Intent("com.example.pagemanager.BgmService"); // Bgm서비스 켜기위한 인텐트
//				startService(intent);
				bindService(intent, serviceConn, BIND_AUTO_CREATE);		//Bgm 서비스 켬				
			} else {
				editor.putString(Constants.PREF_BGM, "0"); // 값을 0으로 씀

				Intent intent = new Intent("com.example.pagemanager.BgmService"); // Bgm서비스  끄기위한 인텐트
//				stopService(intent);
				try{
					unbindService(serviceConn);			//Bgm서비스 끔
				}catch(IllegalArgumentException e){					
				}
			}
			editor.commit(); // 변경사항 적용

			Log.i("msg", valueStr);
			break;
		case R.id.setBg :			//배경선택 버튼
			backgroundDialog.show();
			break;
		case R.id.cameraBtn:		//카메라로 찍어 이미지넣는 버튼
			Intent intent = new Intent(this, CameraActivity.class);
			startActivityForResult(intent, 1);	//요청코드 1로 셋팅
			break;
		}
	}//-----------------------------버튼작업 끝--------------------------------

	// created 2012/11/22 정민규
	// 퀵세이브
	public void saveWorkQ() {
		try {
			String prefix; // 파일명 앞에붙을이름 (디렉토리생성은 현재 안됨)
			prefix = "";					//------------ 그냥 세이브와 여기정도만 다름

			SharedPreferences pref = getSharedPreferences(Constants.PREF_USERNAME, MODE_PRIVATE);//작성자명 preprence에서 읽기
			String author = pref.getString(Constants.PREF_USERNAME, ""); // 값 읽기. 없을시  ""리턴
			bookInfo.setAuthor(author);										//작성자명 셋		

			bookInfo.setBookFileName(prefix + Constants.SAVE_FILENAME);		//저장될 정보파일 이름 셋 . pages.dat 임

			FileOutputStream fos = openFileOutput(prefix					//저장을 위한 스트림 열기
					+ Constants.SAVE_FILENAME, Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(fos));	//버퍼도 연결(안해도 되는듯)

			ArrayList<String> uploadFileNames = new ArrayList<String>();	//bookInfo에 저장할 업로드파일배열
			
			// 이미지를 기기에 저장. DDMS에 data/data/패키지명/files/ 에 저장됨
			Bitmap bm;
			String bmName; // 파일명
			FileOutputStream out;
			for (int i = 0; i < maxPageNumber; i++) { // 각 페이지에
				for (int j = 0; j < pages.get(i).getImages().size(); j++) { // 각 이미지를
					bmName = prefix + i + "_" + j + "" + ".PNG"; // 파일명 : 책이름+페이지번호+이미지번호
					out = openFileOutput(bmName, Context.MODE_PRIVATE);

					bm = pages.get(i).getImages().get(j).getBd().getBitmap(); // 원본그림 가져옴

					try {
						bm.compress(CompressFormat.PNG, 100, out); // 파일로 저장
						uploadFileNames.add(bmName);						//bookInfo에 저장할 업로드파일배열에 추가
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

			bookInfo.setUploadFileNames(uploadFileNames);						//bookInfo에 저장할 업로드파일배열 저장

			// 페이지들 정보 셋팅
			ArrayList<PageViewInfo> pageInfos = new ArrayList<PageViewInfo>();
			for (int i = 0; i < maxPageNumber; i++) {
				pages.get(i).createPageViewInfo();					//각 페이지마다 페이지정보객체를 생성히고
				pageInfos.add(pages.get(i).getPageViewInfo());		//페이지정보객체들의 배열에 추가
			}
			bookInfo.setPageViewInfos(pageInfos); // bookInfo에 넣음

			oos.writeObject(bookInfo); // bookInfo객체를 저장

			if (oos != null) { // 스트림 닫기
				try {
					oos.close();
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			Toast.makeText(this, "저장되었습니다!", 0).show();//퀵세이브 성공여부 출력
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
	// 세이브  (파일명 앞에 책이름 붙여 저장.     ~_pages.dat + ?_?.PNG 들로 저장)
	public void saveWork() {
		String prefix; // 파일명 앞에붙을이름 (디렉토리생성은 현재 안됨)
		bookInfo.setBookName((pages.get(0).getEditText().getText().toString()));	//책이름 셋

		SharedPreferences pref = getSharedPreferences(Constants.PREF_USERNAME, MODE_PRIVATE);//작성자명 preprence에서 읽기
		String author = pref.getString(Constants.PREF_USERNAME, ""); // 값 읽기. 없을시  ""리턴
		bookInfo.setAuthor(author);										//작성자명 셋		

//		pages.get(currentPageNumber - 1).getRootView().setDrawingCacheEnabled(true);	//캡쳐소스이나 현재는 제대로안됨. 스레드로 바꿔도 동일
//		Bitmap bm = pages.get(currentPageNumber - 1).getRootView().getDrawingCache();	//PageView는 검은화면으로 캡쳐됨
//		String bmName; // 파일명
//		FileOutputStream out;
//		bmName = "1234.PNG"; // 파일명 : 책이름+페이지번호+이미지번호
//		try {
//			out = openFileOutput(bmName, Context.MODE_PRIVATE);
//			bm.compress(CompressFormat.PNG, 100, out); // 파일로 저장
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		prefix = bookInfo.getBookName() + "_" + bookInfo.getAuthor() + "_";
		// String[] filter_word =
		// {"","\\.","\\?","\\/","\\~","\\!","\\@","\\#","\\$","\\%","\\^",
		// "\\&","\\*","\\(","\\)","\\_","\\+","\\=","\\|","\\\\","\\}","\\]","\\{","\\[",
		// "\\\"","\\'","\\:","\\;","\\<","\\,","\\>","\\.","\\?","\\/"};
		String[] filter_word = { "\\p{Space} ", " ", "\\?", "\\/",
				"\\*", "\\+", "\\|", "\\\\", "\\\"", "\\:", "\\<",
				"\\>", "\\?", "\\/" };
		for (int i = 0; i < filter_word.length; i++) { // 파일명으로 쓸수없는 특수문자를 "_"로 교체
			prefix = prefix.replaceAll(filter_word[i], "_");
		}
		
		bookInfo.setBookFileName(prefix + Constants.SAVE_FILENAME);		//저장될 정보파일 이름 셋 ex) ~_pages.dat

	    ContentResolver cr = getContentResolver();//컨텐트 리졸버로, 책이름, 작가명이 같은 책이 있나 쿼리
		Cursor cursor = cr.query(MyProvider.CONTENT_URI, new String[] { MyProvider.ID, MyProvider.NAME, MyProvider.AUTHOR },
				MyProvider.NAME + "=?" + " and " + MyProvider.AUTHOR + "=?", new String[]{bookInfo.getBookName(), bookInfo.getAuthor()}, null);		
		
		if(cursor.getCount() > 0){	//이미 있으면
			AlertDialog.Builder aDialog = new AlertDialog.Builder(this);
			aDialog.setTitle("같은 책이름이 이미 있습니다. 덮어씌우시겠습니까?");
			aDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {//yes 누를시
				public void onClick(DialogInterface dialog, int which) {
					saveWork2(NO_SQL);	//db엔 변화없이 기존파일들 삭제후 저장하는 함수호출 
				}
			});
			aDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {//no누르면 아무것도 안함
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			aDialog.setOnCancelListener(new OnCancelListener() {	//취소시			
				public void onCancel(DialogInterface arg0) {
				}
			});				
			AlertDialog ad = aDialog.create();
			ad.show();		//확인창 띄우기
		}else{
			saveWork2(DO_SQL);//db에 삽입하며 파일들 저장하는 함수호출
		}
	}

	//세이브작업2 (bookInfo에서 책이름을 구해서 그걸로 저장). saveWork1에서 호출
	public void saveWork2(int isDoSql) {
		try {
			String prefix; // 파일명 앞에붙을이름 (디렉토리생성은 현재 안됨)

			if(isDoSql == NO_SQL){	//덮어씌우기일경우
				deleteWork(bookInfo.getBookName(), bookInfo.getAuthor());	//기존 저장된 이미지들 삭제
			}

			prefix = bookInfo.getBookName() + "_" + bookInfo.getAuthor() + "_";
			String[] filter_word = { "\\p{Space} ", " ", "\\?", "\\/",
					"\\*", "\\+", "\\|", "\\\\", "\\\"", "\\:", "\\<",
					"\\>", "\\?", "\\/" };
			for (int i = 0; i < filter_word.length; i++) { // 파일명으로 쓸수없는 특수문자를 "_"로 교체
				prefix = prefix.replaceAll(filter_word[i], "_");
			}
			
//			File file = new File(getApplicationContext().getFilesDir().getPath().toString() + "/" + bookInfo.getBookFileName());
//			FileOutputStream fos = new FileOutputStream(file);			
			FileOutputStream fos = openFileOutput(bookInfo.getBookFileName(), Context.MODE_PRIVATE);//저장을 위한 스트림 열기
			// ObjectOutputStream oos = new ObjectOutputStream(fos);
			ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(fos));//버퍼도 연결

			ArrayList<String> uploadFileNames = new ArrayList<String>();	//bookInfo에 저장할 업로드파일배열
			
			// 이미지를 기기에 저장. DDMS에 data/data/패키지명/files/ 에 저장됨
			Bitmap bm;
			String bmName; // 파일명
			FileOutputStream out;
			for (int i = 0; i < maxPageNumber; i++) { // 각 페이지에
				for (int j = 0; j < pages.get(i).getImages().size(); j++) { // 각 이미지를
					bmName = prefix + i + "_" + j + "" + ".PNG"; // 파일명 : 책이름+페이지번호+이미지번호
					out = openFileOutput(bmName, Context.MODE_PRIVATE);

					bm = pages.get(i).getImages().get(j).getBd().getBitmap(); // 원본그림 가져옴

					try {
						bm.compress(CompressFormat.PNG, 100, out); // 파일로 저장
						uploadFileNames.add(bmName);						//bookInfo에 저장할 업로드파일배열에 추가
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

			bookInfo.setUploadFileNames(uploadFileNames);						//bookInfo에 저장할 업로드파일배열 저장

			// 페이지들 정보 셋팅
			ArrayList<PageViewInfo> pageInfos = new ArrayList<PageViewInfo>();
			for (int i = 0; i < maxPageNumber; i++) {
				pages.get(i).createPageViewInfo();					//각 페이지마다 페이지정보객체를 생성히고
				pageInfos.add(pages.get(i).getPageViewInfo());		//페이지정보객체들의 배열에 추가
			}
			bookInfo.setPageViewInfos(pageInfos); // bookInfo에 넣음

			oos.writeObject(bookInfo); // bookInfo객체를 저장

			if (oos != null) { // 스트림 닫기
				try {
					oos.close();
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			SharedPreferences pref = getSharedPreferences(Constants.PREF_USERNAME, MODE_PRIVATE);//작성자명 preprence에서 읽기
			String author = pref.getString(Constants.PREF_USERNAME, ""); // 값 읽기. 없을시  ""리턴
			
			if(isDoSql != NO_SQL){	//덮어씌우기 아닐경우
				ContentValues cv = new ContentValues();	//SQLITE로 DB에 추가
				cv.put(Constants.NAME, bookInfo.getBookName());
				cv.put(Constants.AUTHOR, author);
				ContentResolver cr = getContentResolver();//컨텐트 리졸버로 insert
				cr.insert(Uri.parse(MyProvider.URI), cv);
			}
			
			Toast.makeText(this, "저장되었습니다!", 0).show();
		} catch (Exception e) {
			Log.e("저장실패:", e.getMessage());
			Toast.makeText(this, "저장실패!", 0).show();
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
			Toast.makeText(this, "불러왔습니다", 0).show();	//성공여부 출력
		}catch(Exception e){
			Log.e("불러오기실패:", e.getMessage());
			Toast.makeText(this, "불러오기실패!", 0).show();
		}
	}
	
	//삭제작업. DB는 놔둠 (덮어씌우기시)
	private void deleteWork(String sName, String sAuthor) {	//책이름, 작자명 받음
		try {
			String prefix = sName + "_" + sAuthor + "_"; // 파일명 앞에붙을이름
			
			String[] filter_word = { "\\p{Space} ", " ", "\\?", "\\/",
					"\\*", "\\+", "\\|", "\\\\", "\\\"", "\\:", "\\<",
					"\\>", "\\?", "\\/" };
			for (int i = 0; i < filter_word.length; i++) { // 파일명으로 쓸수없는 특수문자를 "_"로 교체
				prefix = prefix.replaceAll(filter_word[i], "_");
			}
			
			FileInputStream fis = openFileInput(prefix
					+ Constants.SAVE_FILENAME);
			// ObjectInputStream ois = new ObjectInputStream(fis);
			ObjectInputStream ois = new ObjectInputStream(
					new BufferedInputStream(fis));

			BookInfo bookInfo = (BookInfo) ois.readObject();	//임시변수 bookInfo따로씀
			ArrayList<PageView> pages = new ArrayList<PageView>();					//임시변수 pages 따로씀

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
			File tempFile;			
			for (int i = 0; i < maxPageNumber; i++) { // 각 페이지에
				for (int j = 0; j < pages.get(i).getImages().size(); j++) { // 각 이미지를
					bmName = prefix + i + "_" + j + "" + ".PNG"; // 파일명 : 책이름+페이지번호+이미지번호
					
					tempFile = new File(getApplicationContext().getFilesDir().getPath().toString() + "/" + bmName);
					Log.i("bookeditor msg",getApplicationContext().getFilesDir().getPath().toString() + "/" + bmName);
					if(tempFile.exists()){//존재하면
						tempFile.delete();	//삭제시도
					}
				}
			}			

			Toast.makeText(this, "기존 데이터 삭제성공", Toast.LENGTH_SHORT).show();
		}catch(Exception e){
			Toast.makeText(this, "기존 데이터 이미 삭제됨", Toast.LENGTH_SHORT).show();
		}
	}

	//배경선택 리스너
	// 선택된 배경의 이름을 Constants로부터 받아와 해당 PageView에 넘겨준다.
	private class BackgroundSelectListener implements OnClickListener {

		public void onClick(View v) {
			preBgFileName = (Constants.Background.getS(v.getId()));
			pages.get(currentPageNumber - 1).setBgFileName(preBgFileName);
			// pages.get(maxPageNumber - 1).setBgImg(v.getId());
			pages.get(currentPageNumber - 1).setBgImg();
			backgroundDialog.dismiss();
		}
	}
	
	// 이미지선택 리스너
	// 선택되었을 시 이미지 삽입에대한 모든 다이얼로그를 닫고
	// 해당 PageView 이미지를 삽입한다.
	private class ImageInsertListener implements OnClickListener {

		public void onClick(View v) {
			humanImageDialog.dismiss();
			animalImageDialog.dismiss();
			objectImageDialog.dismiss();
			illustrationDialog.dismiss();
			pages.get(currentPageNumber - 1).insertImage(v.getId());
		}
	}

	// 페이지타입선택 리스너
	// 선택시 PageView의 TextView를 생성한다.
	// 편집 화면에서 투명 TextView는 혼란을 주기때문에 하얀색으로 설정한다.
	// pages로부터 TextView를 꺼내와 pageViewer에 붙여준다.
	private class PageTypeSelectListener implements OnClickListener {

		public void onClick(View v) {
//			View view;

			switch (v.getId()) {
			case R.id.book_type1:
//				view = inflater.inflate(R.layout.page_text_left,
//						(ViewGroup) findViewById(R.id.page_text_Left_root));
//				pages.get(maxPageNumber - 1).setTextView(view);
				pages.get(maxPageNumber - 1).createTextView(Constants.PAGE_TYPE.LeftText, inflater);
				break;
			case R.id.book_type2:
//				view = inflater.inflate(R.layout.page_text_right,
//						(ViewGroup) findViewById(R.id.page_text_right_root));
//				pages.get(maxPageNumber - 1).setTextView(view);
				pages.get(maxPageNumber - 1).createTextView(Constants.PAGE_TYPE.RightText, inflater);
				break;
			}

			pages.get(maxPageNumber - 1).getEditText().setBackgroundColor(Color.WHITE);
			pageViewer.addView(pages.get(maxPageNumber - 1).getTextView());
			pageTypeDialog.dismiss();
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
	
	public void onResume() {
		Log.i("msg","BookEditor onResume");
		super.onResume();
//		if(isStart == false){		
//			pages.get(currentPageNumber - 1).startThread();	//스레드 시작
//		}
//		isStart = false;
	}

	public void onPause() {
		Log.i("msg","BookEditor onPause");
		super.onPause();
//		pages.get(currentPageNumber - 1).stopThread();	//스레드 중지		
	}

	public void onStop() {
		Log.i("msg","BookEditor onStop");
		super.onStop();
	}

	public void onRestart() {
		Log.i("msg","BookEditor onRestart");
		super.onRestart();
	}
	
	public void onDestroy() {
		Log.i("msg","BookEditor onDestroy");
		super.onDestroy();
	}	
}
