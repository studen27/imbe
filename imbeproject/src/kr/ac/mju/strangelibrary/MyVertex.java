package kr.ac.mju.strangelibrary;

import java.io.Serializable;

import android.graphics.Paint;

//created by 60022495 정민규
//created date : 2012/11/21
//last modify : 2012/12/07
//정점 객체.
public class MyVertex implements Serializable{
	private static final long serialVersionUID = -7585507529930287998L;//직렬화시 써주는게 권장되어 자동생성시킴
	private float x;	//x좌표
	private float y;	//y좌표
	private boolean doDraw;	//이게 true이면 이전 점과 자신을 연결함
	private MyPaint pnt;		//그릴때 쓴 페인트. paint로 해도되나 일단 MyPaint로 사용
	private MyPaintInfo pInfo;	//페인트정보를 담은 객체(그냥 paint는 로드시 정보사라져버리고 clone도 안됨)

	//constructor (생성자. 넘어온 값으로 셋팅)
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
	public void setPaint (Paint p){//페인트 셋팅
		pnt.setColor(p.getColor());
    	pnt.setStrokeWidth(p.getStrokeWidth());
        pnt.setStyle(p.getStyle());
        pnt.setAntiAlias(true);		//부드럽게 라고 하나 별차이를 못느낌
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