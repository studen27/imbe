package kr.ac.mju.strangelibrary;

import java.io.Serializable;

import android.graphics.Paint.Style;
//created by 60022495 정민규
//created date : 2012/11/21
//last modify : 2012/12/07
//페인트의 저장정보를 담은 객체(paint는 직렬화 로드시 정보가 사라짐)
public class MyPaintInfo implements Serializable{	
	private static final long serialVersionUID = -7528355835616002014L;//직렬화시 써주는게 권장되어 자동생성시킴
	int color;			//색
	float strokeWidth;	//두께
	Style style;		//선 스타일
	
	//constructor
	public MyPaintInfo(MyPaint p) {
		this.color = p.getColor();
		this.strokeWidth = p.getStrokeWidth();
		this.style = p.getStyle();
	}
	
	//getter&setter
	public int getColor() {
		return color;
	}
	public void setColor(int color) {
		this.color = color;
	}
	public float getStrokeWidth() {
		return strokeWidth;
	}
	public void setStrokeWidth(float strokeWidth) {
		this.strokeWidth = strokeWidth;
	}
	public Style getStyle() {
		return style;
	}
	public void setStyle(Style style) {
		this.style = style;
	}

	
}
