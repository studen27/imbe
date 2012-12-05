package com.example.imbedproject.v021.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.example.imbedproject.v021.BookEditor;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

// created by 60062446 박정실
// created date : 2012/12/05
// last modify : 2012/12/05
public class ImageItemizedOverlay extends ItemizedOverlay {
	private final String SERVER_ADDRESS = "http://schoolradio.ivyro.net/test";
	private Context mContext;
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();

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
		
		OverlayItem item = mOverlays.get(index);
		AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
		dialog.setTitle(item.getTitle());
		dialog.setMessage(item.getSnippet());
		
		InputStream inputStream;
		try {
			inputStream = new URL(SERVER_ADDRESS + "/" + item.getSnippet()).openStream();
			File file = new File(mContext.getFilesDir().getPath().toString() + "/" + item.getSnippet());
			OutputStream out = new FileOutputStream(file);
			writeFile(inputStream, out);
			out.close();
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
