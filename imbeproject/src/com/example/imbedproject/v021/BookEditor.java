package com.example.imbedproject.v021;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
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
	private ImageInsertListener insertListner;
	private Dialog humanImageDialog;
	private Dialog animalImageDialog;
	private AlertDialog pageTypeDialog;
	private String preBgFileName;
	private LayoutInflater inflater;

	// Buttons
	private Button prev_butten;
	private Button next_button;
	private Button delete_button;
	private Button insert_button;
	private Button clear;
	private Button black;
	private Button red;
	private Button green;
	private Button blue;
	private Button yellow;
	private Button bgmBtn;

	final CharSequence[] category = { "인물", "동물" };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.book_editor);

		// Attributes
		currentPageNumber = 1;
		maxPageNumber = 1;

		// Components
		backgroundListner = new BackgroundSelectListener();
		insertListner = new ImageInsertListener();
		pageTypeListner = new PageTypeSelectListener();
		ScrollView sv;
		TableLayout tl;

		// backgroundDialog 생성
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

		// humanImageDialog 생성
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

		alertBuilder = new AlertDialog.Builder(this);
		alertBuilder.setTitle("Select img");
		alertBuilder.setItems(category, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				imgInsertDialog.dismiss();
				switch (item) {
				case 0:
					humanImageDialog.show();
					break;
				case 1:
					animalImageDialog.show();
					break;
				case 2:
				}
			}
		});
		imgInsertDialog = alertBuilder.create();

		// 책장 레이아웃 다이얼로그
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
		prev_butten = (Button) findViewById(R.id.prev_button);
		next_button = (Button) findViewById(R.id.next_button);
		delete_button = (Button) findViewById(R.id.delete_button);
		insert_button = (Button) findViewById(R.id.insert_button);
		clear = (Button) findViewById(R.id.clear);
		black = (Button) findViewById(R.id.black);
		red = (Button) findViewById(R.id.red);
		green = (Button) findViewById(R.id.green);
		blue = (Button) findViewById(R.id.blue);
		yellow = (Button) findViewById(R.id.yellow);
		bgmBtn = (Button) findViewById(R.id.bgmBtn);

		pageNumberView = (TextView) findViewById(R.id.page_number);
		pageViewer = (FrameLayout) findViewById(R.id.farme);
		pages = new ArrayList<PageView>();

		bookInfo = new BookInfo();

		init();
	}

	// Initialization
	public void init() {
		// 버튼에 리스너 부착
		prev_butten.setOnClickListener(this);
		next_button.setOnClickListener(this);
		delete_button.setOnClickListener(this);
		insert_button.setOnClickListener(this);
		clear.setOnClickListener(this);
		black.setOnClickListener(this);
		red.setOnClickListener(this);
		green.setOnClickListener(this);
		blue.setOnClickListener(this);
		yellow.setOnClickListener(this);
		bgmBtn.setOnClickListener(this);

		// 첫 page를 생성하고 pageViewer에 add
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.book_title,
				(ViewGroup) findViewById(R.id.book_title_root));
		pages.add(new PageView(this));
		pages.get(0).setTextView(view);
		pageViewer.addView(pages.get(0));
		pageViewer.addView(pages.get(0).getTextView());

		// 페이지번호 설정
		pageNumberView.setText(currentPageNumber.toString() + "/"
				+ maxPageNumber.toString());

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
	
	//메뉴아이템 선택시
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.menu_save:
			saveWork();
			break;
		case R.id.menu_load:
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
				pageViewer.removeAllViews();
				currentPageNumber--;
				pageNumberView.setText(currentPageNumber.toString() + "/"
						+ maxPageNumber.toString());
				pageViewer.addView(pages.get(currentPageNumber - 1));
				pageViewer.addView(pages.get(currentPageNumber - 1).getTextView());
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
				// 마지막 페이지일경우 최대페이지를 증가
				maxPageNumber++;
//				pages.get(currentPageNumber - 1).stopThread();	//그리기 중지시킴(스레드 사용시)
				pages.add(new PageView(this));
				pageTypeDialog.show();
				pageViewer.removeAllViews();
				currentPageNumber++;
				pageNumberView.setText(currentPageNumber.toString() + "/"
						+ maxPageNumber.toString());
				pageViewer.addView(pages.get(currentPageNumber - 1));
			} else {
				pageViewer.removeAllViews();
				currentPageNumber++;
				pageNumberView.setText(currentPageNumber.toString() + "/"
						+ maxPageNumber.toString());
				pageViewer.addView(pages.get(currentPageNumber - 1));
				pageViewer.addView(pages.get(currentPageNumber - 1).getTextView());
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
				pages.remove(currentPageNumber - 1);
				maxPageNumber--;
				currentPageNumber--;
				pageNumberView.setText(currentPageNumber.toString() + "/"
						+ maxPageNumber.toString());
				pageViewer.removeAllViews();
				pageViewer.addView(pages.get(currentPageNumber - 1));
			} else {
				pages.remove(currentPageNumber - 1);
				maxPageNumber--;
				pageNumberView.setText(currentPageNumber.toString() + "/"
						+ maxPageNumber.toString());
				pageViewer.removeAllViews();
				pageViewer.addView(pages.get(currentPageNumber - 1));
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
		case R.id.black:
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
		case R.id.bgmBtn:
			// created 2012/11/22 정민규
			// bgm 켜기/끄기 (preference)
			SharedPreferences pref = getSharedPreferences("Bgm Toggle",
					MODE_PRIVATE);
			SharedPreferences.Editor editor = pref.edit(); // 수정용 에디터

			String valueStr = pref.getString("Bgm On/Off", ""); // 값 읽기. 없을시  ""리턴
			if (valueStr.equals("") || valueStr.equals("0")) {
				editor.putString("Bgm On/Off", "1"); // 1로 씀

				Intent intent = new Intent("com.example.pagemanager.BgmService"); // Bgm서비스 켬
				startService(intent);
			} else {
				editor.putString("Bgm On/Off", "0"); // 0으로 씀

				Intent intent = new Intent("com.example.pagemanager.BgmService"); // Bgm서비스  끔
				stopService(intent);
			}
			editor.commit(); // 변경사항 적용

			Log.i("msg", valueStr);
			break;
		}
	}

	// created 2012/11/22 정민규
	// 세이브
	public void saveWork() {
		try {
			String prefix; // 파일명 앞에붙을이름 (디렉토리생성은 현재 안됨)
			if (!bookInfo.getBookName().equals("")) { // 책이름 "" 아니면 책이름으로
				prefix = bookInfo.getBookName();
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

				prefix = prefix + "_";
			} else {
				prefix = ""; //책이름 ""이면 디폴트 파일명
			}

			// File file = new
			// File(getDir(getApplicationContext().getPackageName(),MODE_PRIVATE)
			// + "/" + Constants.SAVE_FILENAME);
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
				for (int j = 0; j < pages.get(i).getImages().size(); j++) { // 각 이미지를
					bmName = prefix + i + "_" + j + "" + ".PNG"; // 파일명 : 책이름+페이지번호+이미지번호
					out = openFileOutput(bmName, Context.MODE_PRIVATE);

					bm = pages.get(i).getImages().get(j).getBd().getBitmap(); // 원본그림 가져옴

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
	// 로드
	public void loadWork() {
		try {
			String prefix; // 파일명 앞에붙을이름 (디렉토리생성은 현재 안됨)
			if (!bookInfo.getBookName().equals("")) { // 책이름 "" 아니면 책이름으로
				prefix = bookInfo.getBookName();
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

				prefix = prefix + "_";
			} else {
				prefix = ""; // 책이름 ""이면 디폴트 파일명
			}

			// File file = new
			// File(getDir(getApplicationContext().getPackageName(),MODE_PRIVATE)
			// + "/" + Constants.SAVE_FILENAME);
			// FileInputStream fis = new FileInputStream(file);
			FileInputStream fis = openFileInput(prefix
					+ Constants.SAVE_FILENAME);
			// ObjectInputStream ois = new ObjectInputStream(fis);
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
			pages.clear(); // 모든 페이지 지움
			for (int i = 0; i < maxPageNumber; i++) { // 불러온 페이지정보대로 셋팅
				PageView pv = new PageView(this);
				pv.setPageViewInfo(bookInfo.getPageInfos().get(i)); // 페이지정보 줌
				pv.setByPageViewInfo(); // 페이지정보대로 페이지 셋
				pages.add(pv);
			}
			currentPageNumber = 1;

			// 이미지 로드해 셋 data/data/패키지명/files/ 에서 불러옴
			String bmName; // 파일명
			FileInputStream in;
			BitmapDrawable bd;
			for (int i = 0; i < maxPageNumber; i++) { // 페이지 수 만큼
				for (int j = 0; j < pages.get(i).getImages().size(); j++) { // 각 이미지만큼
					bmName = prefix + i + "_" + j + "" + ".PNG"; //파일명 : 책이름+페이지번호+이미지번호
					in = openFileInput(bmName);

					// bm = BitmapFactory.decodeFile(bmName); //메모리누수 우려
					bd = new BitmapDrawable(in); //파일에서 그림 가져옴
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
			Toast.makeText(this, "불러왔습니다", 0).show();
		}catch(Exception e){
			Log.e("불러오기실패:", e.getMessage());
			Toast.makeText(this, "불러오기실패!", 0).show();
		}
	}

	private class BackgroundSelectListener implements OnClickListener {

		public void onClick(View v) {
			preBgFileName = (Constants.Background.getS(v.getId()));
			pages.get(maxPageNumber - 1).setBgFileName(preBgFileName);
			// pages.get(maxPageNumber - 1).setBgImg(v.getId());
			pages.get(maxPageNumber - 1).setBgImg();
			backgroundDialog.dismiss();
		}

	}

	private class ImageInsertListener implements OnClickListener {

		public void onClick(View v) {
			humanImageDialog.dismiss();
			animalImageDialog.dismiss();
			pages.get(currentPageNumber - 1).insertImage(v.getId());
		}

	}

	private class PageTypeSelectListener implements OnClickListener {

		public void onClick(View v) {
			View view;

			switch (v.getId()) {
			case R.id.book_type1:
				view = inflater.inflate(R.layout.page_text_left,
						(ViewGroup) findViewById(R.id.page_text_Left_root));
				pages.get(maxPageNumber - 1).setTextView(view);
				break;
			case R.id.book_type2:
				view = inflater.inflate(R.layout.page_text_right,
						(ViewGroup) findViewById(R.id.page_text_right_root));
				pages.get(maxPageNumber - 1).setTextView(view);
				break;
			}

			pageViewer.addView(pages.get(maxPageNumber - 1).getTextView());
			pageTypeDialog.dismiss();
			backgroundDialog.show();
		}

	}

}
