package com.example.imbedproject.v021;

import java.io.Serializable;

import android.graphics.Paint;

//created by 60022495 정민규
//created date : 2012/11/21
//last modify : 2012/11/23
//정점 객체.
public class MyVertex implements Serializable{
	private static final long serialVersionUID = -7585507529930287998L;
	private float x;
	private float y;
	private boolean doDraw;	
	private MyPaint pnt;		//paint로 해도되나 일단 MyPaint로 사용
	private MyPaintInfo pInfo;	//페인트정보를 담은 객체(그냥 paint는 로드시 정보사라짐)

	//constructor
	MyVertex(float ax, float ay, boolean doDraw, Paint p) {			
		x = ax;
		y = ay;
		this.doDraw = doDraw;
		pnt = new MyPaint();
		setPaint(p);
	}
	
	//getter&setter
	public Paint getPaint(){
		return this.pnt;
	}	
	public void setPaint (Paint p){
		pnt.setColor(p.getColor());
    	pnt.setStrokeWidth(p.getStrokeWidth());
        pnt.setStyle(p.getStyle());		
	}
	public boolean isDraw() {
		return doDraw;
	}
	public void setDraw(boolean draw) {
		this.doDraw = draw;
	}
	public float getX() {
		return x;
	}
	public float getY() {
		return y;
	}	
	
	//paint정보생성
	public void createPaintInfo(){
		pInfo = new MyPaintInfo(pnt);
	}
	//paint정보에 의해 paint셋팅
	public void setByPaintInfo(){
		pnt.setColor(pInfo.getColor());
    	pnt.setStrokeWidth(pInfo.getStrokeWidth());
        pnt.setStyle(pInfo.getStyle());
        pnt.setAntiAlias(true);
	}	
}