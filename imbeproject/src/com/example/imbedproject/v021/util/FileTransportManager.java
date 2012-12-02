package com.example.imbedproject.v021.util;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;
import android.widget.EditText;


//created by 60062446 박정실
//created date : 2012/12/02
//last modify : 2012/12/02
public class FileTransportManager {
	private final String SERVER_ADDRESS = "http://schoolradio.ivyro.net/test";
	private FileInputStream mFileInputStream = null;
	private URL connectUrl = null;
	private EditText mEdityEntry;

	String lineEnd = "\r\n";
	String twoHyphens = "--";
	String boundary = "*****";
	
	ArrayList<String> data;
    
    public FileTransportManager() {
    	data = new ArrayList<String>();
    	
	}
    
    // File Upload Method
    // String filePath : 업로드될 대상의 실제 Path
    public String upload(String filePath) {
    	String fileName = "";
    	// 파일 업로드
    	try {
    		fileName = DoFileUpload(filePath);
		} catch (Exception e) {
			// TODO: handle exception
		}
    	
    	// 파일 업로드 성공 후 database에 기록을 위한 부분
    	// get을 이용하여 php파일에 접근하여 query를 날린다.
    	try {
            URL url = new URL(SERVER_ADDRESS + "/insert.php?"
                    + "name=" + URLEncoder.encode(fileName, "UTF-8")); //변수값을 UTF-8로 인코딩하기 위해 URLEncoder를 이용하여 인코딩함
            url.openStream(); //서버의 DB에 입력하기 위해 웹서버의 insert.php파일에 입력된 이름과 가격을 넘김
             
        } catch(Exception e) {
            Log.e("Error", e.getMessage());
        }
    	
    	return getXmlData("insertresult.xml", "result"); //입력 성공여부 확인
    }
    
    // 근처 책 목록을 받아오는 함수
    // 후에 맵과 융합되야함
    // 미완성
    public ArrayList<String> getBookList(String str) {
    	try {
            data.clear(); //반복적으로 누를경우 똑같은 값이 나오는 것을 방지하기 위해 data를 클리어함
            URL url = new URL(SERVER_ADDRESS + "/search.php");
            url.openStream(); //서버의 serarch.php파일을 실행함
 
            ArrayList<String> namelist = getXmlDataList("searchresult.xml", "name");//name 태그값을 읽어 namelist 리스트에 저장
            ArrayList<String> pricelist = getXmlDataList("searchresult.xml", "price"); //price 태그값을 읽어 prica 리스트에 저장
             
            if(namelist.isEmpty())
                data.add("아무것도 검색되지 않았습니다.");
            else {
                for(int i = 0; i < namelist.size(); i++) {
                    String s = namelist.get(i) + " - " + pricelist.get(i);
                    data.add(s);
                }
            }
        } catch(Exception e) {
            Log.e("Error", e.getMessage());
        } finally{
            
        }
    	
    	return data;
    }
    
    // 파일 업로드 함수
    private String DoFileUpload(String filePath) throws IOException {
		Log.d("Test", "file path = " + filePath);
		return HttpFileUpload(SERVER_ADDRESS + "/upload.php", "", filePath);
	}
    
    // 실제로 파일을 업로드하는 함수
    // 업로드된 파일이름 반환
    private String HttpFileUpload(String urlString, String params, String fileName) {
		try {

			mFileInputStream = new FileInputStream(fileName);
			connectUrl = new URL(urlString);
			Log.d("Test", "mFileInputStream  is " + mFileInputStream);

			// open connection
			HttpURLConnection conn = (HttpURLConnection) connectUrl
					.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);

			// write data
			DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\""
					+ fileName + "\"" + lineEnd);
			dos.writeBytes(lineEnd);

			int bytesAvailable = mFileInputStream.available();
			int maxBufferSize = 1024;
			int bufferSize = Math.min(bytesAvailable, maxBufferSize);

			byte[] buffer = new byte[bufferSize];
			int bytesRead = mFileInputStream.read(buffer, 0, bufferSize);

			Log.d("Test", "image byte is " + bytesRead);

			// read image
			while (bytesRead > 0) {
				dos.write(buffer, 0, bufferSize);
				bytesAvailable = mFileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = mFileInputStream.read(buffer, 0, bufferSize);
			}

			dos.writeBytes(lineEnd);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

			// close streams
			Log.e("Test", "File is written");
			mFileInputStream.close();
			dos.flush(); // finish upload...

			// get response
			int ch;
			InputStream is = conn.getInputStream();
			StringBuffer b = new StringBuffer();
			while ((ch = is.read()) != -1) {
				b.append((char) ch);
			}
			String s = b.toString();
			Log.e("Test", "result = " + s);
			mEdityEntry.setText(s);
			dos.close();
			
			return s;
		} catch (Exception e) {
			Log.d("Test", "exception " + e.getMessage());
			return "";
		}
	}
    
    // 쿼리 insert완료 확인 메소드
    // String filename : 확인할 대상이되는 xml file 이름
    // String str : 확인할 대상이되는 xml tag name
    private String getXmlData(String filename, String tagName) { 
        String rss = SERVER_ADDRESS + "/";
        String ret = "";
        
        //XML 파싱을 위한 과정
        try { 
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            URL server = new URL(rss + filename);
            InputStream is = server.openStream();
            xpp.setInput(is, "UTF-8");
             
            int eventType = xpp.getEventType();
            
            while(eventType != XmlPullParser.END_DOCUMENT) {
                if(eventType == XmlPullParser.START_TAG) {
                    if(xpp.getName().equals(tagName)) { //태그 이름이 str 인자값과 같은 경우
                        ret = xpp.nextText();
                    }
                }
                eventType = xpp.next();
            }
        } catch(Exception e) {
            Log.e("Error", e.getMessage());
        }
        return ret;
    }
    
    // 데이터베이스의 내용을 받아오는 함수로 수정 요함
    // 태그값 여러개를 받아오기위한 ArrayList<string>형 변수
    private ArrayList<String> getXmlDataList(String filename, String str) { 
        String rss = SERVER_ADDRESS + "/";
        ArrayList<String> ret = new ArrayList<String>();
         
        try { //XML 파싱을 위한 과정
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            URL server = new URL(rss + filename);
            InputStream is = server.openStream();
            xpp.setInput(is, "UTF-8");
             
            int eventType = xpp.getEventType();
             
            while(eventType != XmlPullParser.END_DOCUMENT) {
                if(eventType == XmlPullParser.START_TAG) {
                    if(xpp.getName().equals(str)) { //태그 이름이 str 인자값과 같은 경우
                        ret.add(xpp.nextText());
                    }
                }
                eventType = xpp.next();
            }
        } catch(Exception e) {
            Log.e("Error", e.getMessage());
        }
         
        return ret;
    }
}
