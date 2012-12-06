package kr.ac.mju.strangelibrary;

import java.io.Serializable;

import android.graphics.Paint;
import android.graphics.Path;

//created by 60022495 정민규
//created date : 2012/11/21
//last modify : 2012/11/23
//직렬화 로드시 정보사라짐. 안그렇게 할려면 action배열을 쓰면 되나, vertex배열을 쓰는것과 같아서 현재 사용안함
public class MyPath extends Path implements Serializable {
	private static final long serialVersionUID = -3492058858222144466L;
	protected MyPaint pnt;	
	
	//constructor
	public MyPath() {
		super();
		pnt = new MyPaint();
	}
	//constructor2
	public MyPath(Path p) {
		super(p);
		pnt = new MyPaint();
	}

	//getter&setter
	public Paint getPaint(){
		return this.pnt;
	}	
	public void setPaint (Paint p){		
//		this.pnt = p.clone();	//깊은복사 현재안되어 아래로 함		
		pnt.setColor(p.getColor());
    	pnt.setStrokeWidth(p.getStrokeWidth());
        pnt.setStyle(p.getStyle());		
	}
}
