package kr.ac.mju.strangelibrary;

import java.io.Serializable;

import kr.ac.mju.strangelibrary.Constants.ANCHOR_TYPE;

import android.graphics.RectF;
// created by 60022495 정민규
// created date : 2012/11/17
// last modify : 2012/11/21
//그림에 표시할 앵커(리사이즈, 회전용) 들을 갖고있는 클래스
public class MyAnchor extends RectF implements Serializable{
	private static final long serialVersionUID = -1582315659954345536L;	//직렬화시 써주는게 권장되어 자동생성시킴

	//constructor
	public MyAnchor() {
		super();
	}

	//타입에 따라 자기의 위치를 결정하는 함수
	public void setLocation(int x, int y, int right, int bottom, ANCHOR_TYPE anchorType){
		switch(anchorType){
		case NW:
			super.set(x - Constants.ANCHOR_SIZE, y - Constants.ANCHOR_SIZE,
					x + Constants.ANCHOR_SIZE, y + Constants.ANCHOR_SIZE);
			break;
		case NN:
			super.set((right + x)/2 - Constants.ANCHOR_SIZE, y - Constants.ANCHOR_SIZE,
					(right + x)/2 + Constants.ANCHOR_SIZE, y + Constants.ANCHOR_SIZE);
			break;
		case NE:
			super.set(right - Constants.ANCHOR_SIZE, y - Constants.ANCHOR_SIZE,
					right + Constants.ANCHOR_SIZE, y + Constants.ANCHOR_SIZE);
			break;
		case WW:
			super.set(x - Constants.ANCHOR_SIZE, (bottom + y)/2 - Constants.ANCHOR_SIZE,
					x + Constants.ANCHOR_SIZE, (bottom + y)/2 + Constants.ANCHOR_SIZE);
			break;
		case EE:
			super.set(right - Constants.ANCHOR_SIZE, (bottom + y)/2 - Constants.ANCHOR_SIZE,
					right + Constants.ANCHOR_SIZE, (bottom + y)/2 + Constants.ANCHOR_SIZE);
			break;
		case SW:
			super.set(x - Constants.ANCHOR_SIZE, bottom - Constants.ANCHOR_SIZE,
					x + Constants.ANCHOR_SIZE, bottom + Constants.ANCHOR_SIZE);
			break;
		case SS:
			super.set((right + x)/2 - Constants.ANCHOR_SIZE, bottom - Constants.ANCHOR_SIZE,
					(right + x)/2 + Constants.ANCHOR_SIZE, bottom + Constants.ANCHOR_SIZE);
			break;
		case SE:
			super.set(right - Constants.ANCHOR_SIZE, bottom - Constants.ANCHOR_SIZE,
					right + Constants.ANCHOR_SIZE, bottom + Constants.ANCHOR_SIZE);
			break;
		case ROTATE:
			super.set((right + x)/2 - Constants.ANCHOR_SIZE, y - Constants.ANCHOR_SIZE*3 - Constants.ANCHOR_SIZE,
					(right + x)/2 + Constants.ANCHOR_SIZE, y - Constants.ANCHOR_SIZE*3 + Constants.ANCHOR_SIZE);
			break;
		default:
			super.set(0,0,0,0);				
		}
	}
}
