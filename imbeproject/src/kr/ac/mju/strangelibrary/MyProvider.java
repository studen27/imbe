package kr.ac.mju.strangelibrary;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
//created by 60022495 정민규
//created date : 2012/12/04
//last modify : 2012/12/07
//컨텐트 프로바이더
public class MyProvider extends ContentProvider {
	public static final String URI = "content://" + Constants.PAKAGE_NAME + "/" + Constants.TABLE_NAME;//이 프로바이더로 정보를 얻기위한 URI설정
	static final Uri CONTENT_URI = Uri.parse(URI);		//파싱된 URI셋팅
	static final String DB_NAME = Constants.DB_FILENAME;	//DB파일명 설정
	static final int DB_VERTION = 1;					//DB버전
	static final int MYDB_DATA = 1;						//아래의 매처에서, 매칭시 반환할 값
	static final UriMatcher uriMatcher;					//매쳐
	
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(Constants.PAKAGE_NAME, Constants.TABLE_NAME, MYDB_DATA);	//매쳐 설정
	}
	
	static final String ID = "_id";		//테이블의 칼럼들. id = pk
	static final String NAME = "name";	//책이름
	static final String AUTHOR = "author";	//작자명
	
	private SQLiteDatabase db;	
	/////////////////////////////////////////////////////////////////////////	
	@Override
	public boolean onCreate() {
		Context context = getContext();
		
		//SQLITE사용위한 객체
		SQLiteOpenHelper helper = new SQLiteOpenHelper(context, DB_NAME, null, DB_VERTION) {
			
		static final String DB_CREATE_QUERY = "Create Table " + "books" + "("	//테이블 생성 sql문장 설정
				+ ID + " INTEGER PRIMARY KEY AUTOINCREMENT," 
				+ NAME + " TEXT,"
				+ AUTHOR + " TEXT"
				+ ");";	

			@Override
			public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
				// TODO Auto-generated method stub
				db.execSQL("drop table if exist " + Constants.TABLE_NAME);
				onCreate(db);
			}

			
			//생성시
			public void onCreate(SQLiteDatabase db) {
				// TODO Auto-generated method stub
				db.execSQL(DB_CREATE_QUERY);	//테이블 생성쿼리 날림. 테이블이 이미 있으면 생성이 안됨. 리셋하고 싶으면 ddms나 어플관리에서 삭제해야함
			}
		};
		db = helper.getWritableDatabase();
		return (db == null)? false : true;
	}
	
	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case MYDB_DATA:	//URI일치시 수행. 이하 동일
			return "type";
		default:
			throw new IllegalArgumentException("Unsupport URI:" + uri);
		}
	}

	//Insert시
	public Uri insert(Uri uri, ContentValues values) {
		Uri curi;
		switch (uriMatcher.match(uri)) {
		case MYDB_DATA:
			long id = db.insert(Constants.TABLE_NAME, ID, values);
			if (id > 0) {
				curi = ContentUris.withAppendedId(CONTENT_URI, id);//URI뒤에 ID붙여 새로 URI를 만듬
				Log.i("msg",curi.toString());
				getContext().getContentResolver().notifyChange(curi, null);
			} else {
				throw new SQLException("Failed to add new item into " + uri);
			}
			break;

		default:
			throw new IllegalArgumentException("Unsupport URI:" + uri);
		}
		return curi;	//새로만든 URI리턴
	}

	//Query(Select문과 동일)
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(Constants.TABLE_NAME);	//테이블 설정

		String orderBy;
		if (TextUtils.isEmpty(sortOrder)) {
			orderBy = ID;		//기본은 pk로 정렬
		} else {
			orderBy = sortOrder;	//아니면 해당 칼럼으로 정렬
		}

		Cursor c = qb.query(db, projection, selection, selectionArgs, null,		//실제 쿼리 수행
				null, sortOrder);
		c.setNotificationUri(getContext().getContentResolver(), uri);	//변경사항 통지

		return c;
	}

	//Update시
	public int update(Uri uri, ContentValues values, String selection, 
			String[] selectionArgs) {
		int count;

		switch (uriMatcher.match(uri)) {
		case MYDB_DATA:	
			count = db.update(Constants.TABLE_NAME, values, selection, selectionArgs);//실제 UPDATE수행
			break;
		default:
			throw new IllegalArgumentException("Unsupport URI : " + uri );
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
	
	//Delete시
	public int delete(Uri uri, String where, String[] selectionArgs) {	
		int count;

		switch (uriMatcher.match(uri)) {
		case MYDB_DATA:
			count = db.delete(Constants.TABLE_NAME, where, selectionArgs);	//실제 DELETE수행
			break;
		default:
			throw new IllegalArgumentException("Unsupport URI : " + uri );
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

}
