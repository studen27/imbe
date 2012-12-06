package kr.ac.mju.strangelibrary.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import kr.ac.mju.strangelibrary.BookInfo;
import kr.ac.mju.strangelibrary.Constants;
import kr.ac.mju.strangelibrary.LoadActivity;
import kr.ac.mju.strangelibrary.MyProvider;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

// created by 60062446 박정실
// created date : 2012/12/05
// last modify : 2012/12/05
public class ImageItemizedOverlay extends ItemizedOverlay {
	private final String SERVER_ADDRESS = "http://schoolradio.ivyro.net/test";
	private Context mContext;
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private FileTransportManager ftm; 	// 파일 전송 관리자
	private OverlayItem item;

	public ImageItemizedOverlay(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		// TODO Auto-generated constructor stub
		mContext = context;
	}

	@Override
	protected OverlayItem createItem(int i) {
		// TODO Auto-generated method stub
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return mOverlays.size();
	}

	public void addOverlay(OverlayItem o) {
		mOverlays.add(o);
		populate();
	}
	
	// 탭 되었을 때
	// 해당 오버레이의 책을 다운 받을 것인지 결정
	@Override
	protected boolean onTap(int index) {
		// TODO Auto-generated method stub
		ftm = new FileTransportManager();
		item = mOverlays.get(index);
		AlertDialog.Builder aDialog = new AlertDialog.Builder(mContext);
		aDialog.setTitle(item.getTitle().substring(0, item.getTitle().length() - 10) + "다운 받으시겠습니까??");
		aDialog.setPositiveButton("네!!", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				InputStream inputStream;
				try {
					// 해당 책을 다운 받는다.
					inputStream = new URL(SERVER_ADDRESS + "/" + item.getSnippet()).openStream();
					File file = new File(mContext.getFilesDir().getPath().toString() + "/" + item.getTitle());
					OutputStream out = new FileOutputStream(file);
					writeFile(inputStream, out);
					out.close();
					
					// 해당 책에 포함된 그림을 찾아 모두 다운 받는다.
					String path = item.getSnippet().subSequence(0, item.getSnippet().length() -4).toString();
					ArrayList<String> nameList = ftm.getBookImage(path);
					for(int i = 0; i < nameList.size(); i++) {
						inputStream = new URL(SERVER_ADDRESS + "/" + path + "/"+ nameList.get(i)).openStream();
						file = new File(mContext.getFilesDir().getPath().toString() + "/" + nameList.get(i));
						out = new FileOutputStream(file);
						writeFile(inputStream, out);
						out.close();
					}
					
					// 다운 받은 책을 북 인포 객체로 참조하도록 한다.
					FileInputStream fis = mContext.openFileInput(item.getTitle());
					ObjectInputStream ois = new ObjectInputStream(
							new BufferedInputStream(fis));
					
					BookInfo bookInfo = null;
					try {
						bookInfo = (BookInfo) ois.readObject();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					// 다운받은 책을 DB에 등록한다.
					ContentValues cv = new ContentValues();
					cv.put(Constants.NAME, bookInfo.getBookName());
					cv.put(Constants.AUTHOR, bookInfo.getAuthor());
					ContentResolver cr = mContext.getContentResolver();
					cr.insert(Uri.parse(MyProvider.URI), cv);
					
					Toast.makeText(mContext, "완료!", Toast.LENGTH_SHORT).show();
					
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Toast.makeText(mContext, "뭔가 잘못되었 습니다!", Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}				
			}
		});
		aDialog.setNegativeButton("아니요..", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {				
			}
		});
		AlertDialog ad = aDialog.create();
		ad.show();
		
		return true;
	}

	private void writeFile(InputStream is, OutputStream os) throws IOException {
		// TODO Auto-generated method stub
		 int c = 0;
	     while((c = is.read()) != -1)
	         os.write(c);
	     os.flush();
	}
	
	

}
