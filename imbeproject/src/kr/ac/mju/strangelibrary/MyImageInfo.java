package kr.ac.mju.strangelibrary;

import java.io.Serializable;

import android.graphics.drawable.BitmapDrawable;
//created by 60022495 정민규
//created date : 2012/11/21
//last modify : 2012/11/23
//이미지의 저장정보를 담은 객체
public class MyImageInfo implements Serializable{
	private static final long serialVersionUID = -986076128360470580L;
	private String filename;
//	private BitmapDrawable bd;		//원본그림
	private int x;		//비트맵 위치정보
	private int y;
	private int right;
	private int bottom;
	private int width;
	private int height;
	private int transX;	//회전시 중심점차이
	private int transY;	
	private float[] matrixInfo;		

	//constructor
	public MyImageInfo(MyImage img) {
		filename = img.getFilename();
//		bd = img.getBd();
		x = img.getX();
		y = img.getY();
		right = img.getRight();
		bottom = img.getBottom();
		width = img.getWidth();
		height = img.getHeight();
		transX = img.getTransX();
		transY = img.getTransY();
		
		matrixInfo = new float[9];
		img.getMatrix().getValues(matrixInfo);
	}

	//getter & setter
	public String getFilename() {
		return filename;
	}
	public int getX() {
		return x;
	}
//	public BitmapDrawable getBd() {
//		return bd;
//	}
	public int getY() {
		return y;
	}
	public int getRight() {
		return right;
	}
	public int getBottom() {
		return bottom;
	}
	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}
	public int getTransX() {
		return transX;
	}
	public int getTransY() {
		return transY;
	}
	public float[] getMatrixInfo() {
		return matrixInfo;
	}
}
