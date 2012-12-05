package com.example.imbedproject.v021;

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
//last modify : 2012/12/04
public class MyProvider extends ContentProvider {
	public static final String URI = "content://" + Constants.PAKAGE_NAME + "/" + Constants.TABLE_NAME;
	static final Uri CONTENT_URI = Uri.parse(URI);
	static final String DB_NAME = Constants.DB_FILENAME;
	static final int DB_VERTION = 1;
	static final int MYDB_DATA = 1;
	static final UriMatcher uriMatcher;	
	
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(Constants.PAKAGE_NAME, Constants.TABLE_NAME, MYDB_DATA);
	}
	
	static final String ID = "_id";
	static final String NAME = "name";
	static final String AUTHOR = "author";
	
	private SQLiteDatabase db;	
	/////////////////////////////////////////////////////////////////////////	
	@Override
	public boolean onCreate() {
		Context context = getContext();
		
		SQLiteOpenHelper helper = new SQLiteOpenHelper(context, DB_NAME, null, DB_VERTION) {
			
		static final String DB_CREATE_QUERY = "Create Table " + "books" + "("
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

			@Override
			public void onCreate(SQLiteDatabase db) {
				// TODO Auto-generated method stub
				db.execSQL(DB_CREATE_QUERY);
			}
		};
		db = helper.getWritableDatabase();
		return (db == null)? false : true;
	}
	
	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case MYDB_DATA:
			return "type";
		default:
			throw new IllegalArgumentException("Unsupport URI:" + uri);
		}
	}

	//Insert
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		Uri curi;
		switch (uriMatcher.match(uri)) {
		case MYDB_DATA:
			long id = db.insert(Constants.TABLE_NAME, ID, values);
			if (id > 0) {
				curi = ContentUris.withAppendedId(CONTENT_URI, id);
				Log.i("msg",curi.toString());
				getContext().getContentResolver().notifyChange(curi, null);
			} else {
				throw new SQLException("Failed to add new item into " + uri);
			}
			break;

		default:
			throw new IllegalArgumentException("Unsupport URI:" + uri);
		}
		return curi;
	}

	//Query
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(Constants.TABLE_NAME);

		String orderBy;
		if (TextUtils.isEmpty(sortOrder)) {
			orderBy = ID;
		} else {
			orderBy = sortOrder;
		}

		Cursor c = qb.query(db, projection, selection, selectionArgs, null,
				null, sortOrder);
		c.setNotificationUri(getContext().getContentResolver(), uri);

		return c;
	}

	//Update
	@Override
	public int update(Uri uri, ContentValues values, String selection, 
			String[] selectionArgs) {
		int count;

		switch (uriMatcher.match(uri)) {
		case MYDB_DATA:
			count = db.update(Constants.TABLE_NAME, values, selection, selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unsupport URI : " + uri );
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
	
	//Delete
	@Override
	public int delete(Uri uri, String where, String[] selectionArgs) {	
		int count;

		switch (uriMatcher.match(uri)) {
		case MYDB_DATA:
			count = db.delete(Constants.TABLE_NAME, where, selectionArgs);			
			break;
		default:
			throw new IllegalArgumentException("Unsupport URI : " + uri );
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

}
