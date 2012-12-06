package kr.ac.mju.strangelibrary;

import java.io.Serializable;

import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;
//created by 60022495 정민규
//created date : 2012/11/21
//last modify : 2012/11/23
//직렬화가능 paint클래스. 이나 로드시 정보가 사라지므로, 사실상 사용안하는것과 마찬가지. 점의 색정보는  MyPaintInfo클래스로 따로저장
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
