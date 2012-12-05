package com.example.imbedproject.v021.util;

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

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.Toast;

import com.example.imbedproject.v021.BookInfo;
import com.example.imbedproject.v021.Constants;
import com.example.imbedproject.v021.MyProvider;
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

	@Override
	protected boolean onTap(int index) {
		// TODO Auto-generated method stub
		ftm = new FileTransportManager();
		OverlayItem item = mOverlays.get(index);
		AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
		dialog.setTitle(item.getTitle());
		dialog.setMessage(item.getSnippet());
		
		InputStream inputStream;
		try {
			inputStream = new URL(SERVER_ADDRESS + "/" + item.getSnippet()).openStream();
			File file = new File(mContext.getFilesDir().getPath().toString() + "/" + item.getTitle());
			OutputStream out = new FileOutputStream(file);
			writeFile(inputStream, out);
			out.close();
			
			String path = item.getSnippet().subSequence(0, item.getSnippet().length() -4).toString();
			ArrayList<String> nameList = ftm.getBookImage(path);
			for(int i = 0; i < nameList.size(); i++) {
				inputStream = new URL(SERVER_ADDRESS + "/" + path + "/"+ nameList.get(i)).openStream();
				file = new File(mContext.getFilesDir().getPath().toString() + "/" + nameList.get(i));
				out = new FileOutputStream(file);
				writeFile(inputStream, out);
				out.close();
			}
			
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
			
			ContentValues cv = new ContentValues();
			cv.put(Constants.NAME, bookInfo.getBookName());
			cv.put(Constants.AUTHOR, "-");
			ContentResolver cr = mContext.getContentResolver();
			cr.insert(Uri.parse(MyProvider.URI), cv);
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
		dialog.show();

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
