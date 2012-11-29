package com.example.imbedproject.v021;

import java.io.Serializable;

import android.graphics.Paint.Style;

//created by 60022495 정민규
//created date : 2012/11/21
//last modify : 2012/11/23
public class MyPaintInfo implements Serializable{	
	private static final long serialVersionUID = -7528355835616002014L;
	int color;
	float strokeWidth;
	Style style;
	
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
