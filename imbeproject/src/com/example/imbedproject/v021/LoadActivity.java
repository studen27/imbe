package com.example.imbedproject.v021;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
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
public class LoadActivity extends Activity {
	protected BookInfo bookInfo; // 책정보 객체(로드/삭제용)
	private ArrayList<PageView> pages; // ImageView Vector 객체 (로드/삭제용)
	SimpleCursorAdapter adapter;	

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_load);
	    
	    pages = new ArrayList<PageView>();//초기화

	    Button findBook = (Button) findViewById(R.id.find_book);
	    findBook.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View arg0) {
				Intent intent = new Intent("com.example.imbedproject.v021.findIntent");
				startActivity(intent);
			}
	    });
	    
		ListView lv = (ListView) findViewById(R.id.loadList);
		
	    ContentResolver cr = getContentResolver();
		Cursor cursor = cr.query(MyProvider.CONTENT_URI, new String[] { MyProvider.ID, MyProvider.NAME, MyProvider.AUTHOR },
				null, null, null);

		String[] from = new String[] { MyProvider.ID, MyProvider.NAME, MyProvider.AUTHOR };	//가져올 항목들
		int[] to = new int[] { R.id.textId, R.id.textName, R.id.textAuthor };				//붙일 곳
        adapter = new SimpleCursorAdapter(this, R.layout.row, cursor, from, to);			//셋팅
        lv.setAdapter(adapter);        
        
        lv.setOnItemLongClickListener(new OnItemLongClickListener() {		//롱클릭 리스너
        	int pos;        
        
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {	//롱클릭시 삭제확인창
				pos = position;
				AlertDialog.Builder aDialog = new AlertDialog.Builder(LoadActivity.this);
				aDialog.setTitle("삭제하시겠습니까?");
				aDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
				        Cursor c = (Cursor) adapter.getItem(pos);	//쿼리결과의 해당 줄 가져옴 
				        int sId = c.getInt(0);			//해당 줄의 해당 번재 컬럼 가져옴
				        String sName = c.getString(1);			//해당 줄의 해당 번재 컬럼 가져옴
				        Log.i("msg",sId+"");						
						deleteWork(sId, sName);			//삭제작업						
					}
				});
				aDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {				
					}
				});
				AlertDialog ad = aDialog.create();
				ad.show();
				
				return false;
			}
		});

		lv.setOnItemClickListener(new OnItemClickListener() {				//클릭리스너			
			
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		        Cursor c = (Cursor) adapter.getItem(position);	//쿼리결과의 해당 줄 가져옴 
		        int sId = c.getInt(0);			//해당 줄의 해당 번재 컬럼 가져옴
		        String sName = c.getString(1);			//해당 줄의 해당 번재 컬럼 가져옴
		        Log.i("msg",sId+"");
		        
				Intent intent = new Intent("com.example.imbedproject.v021.readIntent");
				intent.putExtra("SelectedId", sId);
				intent.putExtra("SelectedName", sName);
				setResult(Activity.RESULT_OK, intent);		//응답설정	    		
				finish();									//후 리턴
			}
		});        
	}

	//삭제작업_DB에서도 삭제
	private void deleteWork(int sId, String sName) {	//선택된줄 id, 책이름 받음
		try {
			Log.i("LoadActivity msg","sid : " + sId + " sName : " + sName);
			
			String prefix = sName; // 파일명 앞에붙을이름 (디렉토리생성은 현재 안됨)

			if (!prefix.equals("")) { // 책이름 "" 아니면 책이름으로				
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
			Log.i("LoadActivity msg",prefix	+ Constants.SAVE_FILENAME);
			
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
					if(tempFile.exists()){
						tempFile.delete();	//삭제시도
					}
				}
			}
			
			//정보파일 삭제시도
			File datFile = new File(getApplicationContext().getFilesDir().getPath().toString() + "/" + prefix + Constants.SAVE_FILENAME);			
			if(datFile.exists()){
				datFile.delete();	
			}

			Toast.makeText(this, "삭제성공", 0).show();
		}catch(Exception e){
			Toast.makeText(this, "이미 삭제됨", 0).show();
		}finally{
			ContentResolver cr = getContentResolver();
			cr.delete(MyProvider.CONTENT_URI, "_id=?", new String[]{sId+""});	//db에서 해당 id의 투플 삭제
		}
	}
}
