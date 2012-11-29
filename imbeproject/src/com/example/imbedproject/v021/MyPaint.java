package com.example.imbedproject.v021;

import java.io.Serializable;

import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;
//created by 60022495 정민규
//created date : 2012/11/21
//last modify : 2012/11/23
public class MyPaint extends Paint implements Serializable, Parcelable//,Cloneable
{	//깊은복사가능시도(현재안됨)
	private static final long serialVersionUID = -6898667682756259935L;
	//constructor
	public MyPaint() {
		super();
	}
	public MyPaint(int flag) {
		super(flag);
	}
	public MyPaint(Paint paint) {
		super(paint);
	}
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		
	}
	
	//클론가능시도(현재안됨)
//	public Paint clone()throws CloneNotSupportedException {
//		return (Paint) super.clone();		
//	}
}
