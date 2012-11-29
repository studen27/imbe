package com.example.imbedproject.v021;

import java.io.Serializable;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.EditText;

import com.example.imbedproject.v021.Constants.ANCHOR_TYPE;
import com.example.imbedproject.v021.Constants.DRAWING_STATE;
 
//created by 60022495 정민규setBgImg
//created date : 2012/11/17
//last modify : 2012/11/23
public class PageView extends SurfaceView implements Callback, Serializable {
	private static final long serialVersionUID = 5454058848627226403L;
	private String bgFileName = "";		//배경그림 이름
	private BitmapDrawable bgBd;			//배경그림(원본)
	private Bitmap bgBitmap;			//배경그림(늘린것)
    private float x = 0;
    private float y = 0;    
    private float prevX = -1;    
    private float prevY = -1;    
    private float intervalX = 0;	//이미지 클릭시 이미지위치와 마우스위치 간격 (moving시 사용)
    private float intervalY = 0;
    private int width;	//뷰(자신) 크기
    private int height;
    private DRAWING_STATE drawingState;	//그리기상태
    private MyImage selectedImage;		//선택된이미지
    private SurfaceHolder holder;		//서피스뷰 홀더
    private MySimpleGestureListener sg;	//제스처리스너
    private MyGestureDetector gd;		//제스처디텍터
    protected Paint pnt;					//그리기 색
//    protected MyPath path;				//현재 그릴 선        
//    protected ArrayList<MyPath> paths;	//선들
    protected ArrayList<MyVertex> vertexes;	//점들
    protected ArrayList<MyImage> images;	//이미지들
//    protected ArrayList<MyImageInfo> imgInfos;	//이미지정보들
    protected PageViewInfo pageViewInfo;	//자기 페이지 정보. 생성시 안만들어지고 takePageViewInfo시 만들어짐
    private PageViewThread pageViewThread;	//그리는 스레드
    
    private boolean isEnable;
    
    //constructor1 코드로 생성시 호출됨.
    public PageView(Context context) {
        super(context);
        
//		bd = (BitmapDrawable)getResources().getDrawable(R.drawable.ab1);
//        bitmap = bd.getBitmap();
		
        pnt = new Paint();
        setPaintColor(Color.BLUE);
        
//       paths = new ArrayList<MyPath>();
        drawingState = DRAWING_STATE.idle;
        selectedImage = null;

        holder = getHolder();		//surfaceView에 필수인 홀더,콜백설정
        holder.addCallback(this);
        setFocusable(true);        

        sg = new MySimpleGestureListener();	//마우스 움직임 상세인식용 제스처디텍터&리스너
        gd = new MyGestureDetector(context, sg);
        
        vertexes = new ArrayList<MyVertex>();
        images = new ArrayList<MyImage>();
        
        isEnable = true;
    }
    
    //constructor2. xml껄로 만들시 호출됨. 보통 생성자3으로 넘김
    public PageView(Context context,AttributeSet attrs) {
        this(context,attrs,0);

    }    
    //constructor3 xml 에서 넘어온 속성을 멤버변수로 셋팅하는 역할을 함
    public PageView(Context context,AttributeSet attrs,int defStyle) {
        super(context,attrs,defStyle);
        
    }
    
	//surface 생성시(@override)
	public void surfaceCreated(SurfaceHolder holder) {		
		width = getWidth();
		height = getHeight();
    	setBgImg();									//배경설정    	
		Log.i("msg",this.getWidth() + " " + this.getHeight() );
		
//		pageViewThread = new PageViewThread(holder, this);	//스레드사용시, 페이지추가할때 에뮬에서의 속도문제 때문에 일단 사용안함 
//		pageViewThread.setRunning(true);					//실기에서는 빠름.
//		pageViewThread.start();
	}
	
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {		
		// TODO Auto-generated method stub		
	}
	
	//suface종료시
	public void surfaceDestroyed(SurfaceHolder holder) {
//		boolean retry = true;
//		pageViewThread.setRunning(false);
//		
//		while(retry){
//			try {
//				pageViewThread.join();	//스레드 종료 기다림
//				retry = false;
//			} catch (InterruptedException e) {				
//			}
//		}
	};


    //getter&setter
    public void setPaintColor(int color){
    	pnt.setColor(color);
//        pnt.setARGB(255, 255, 0, 0);
    	pnt.setStrokeWidth(3);
        pnt.setStyle(Paint.Style.STROKE);	//이거안하면 색채워짐
        pnt.setAntiAlias(true);
    }
    public String getBgFileName() {
		return bgFileName;
	}
	public void setBgFileName(String bgFileName) {
		this.bgFileName = bgFileName;
	}
	public ArrayList<MyVertex> getVertexes() {
		return vertexes;
	}
	public void setVertexes(ArrayList<MyVertex> vertexes) {
		this.vertexes = vertexes;
	}	
//	public ArrayList<MyImageInfo> getImgInfos() {
//		return imgInfos;
//	}	
	public PageViewInfo getPageViewInfo() {
		return pageViewInfo;
	}	
	public void setPageViewInfo(PageViewInfo pageViewInfo) {
		this.pageViewInfo = pageViewInfo;
	}	
	public ArrayList<MyImage> getImages() {
		return images;
	}
	
	//페이지정보 생성
    public void createPageViewInfo(){
    	pageViewInfo = new PageViewInfo(this);	//정보객체 생성
    }
    //로드시 페이지정보 셋팅(커스텀 생성자가  안됨)
    public void setByPageViewInfo(){
    	bgFileName = pageViewInfo.getBgFileName();
    	
//    	this.paths = pageViewInfo.getPaths();
    	this.vertexes = pageViewInfo.getVertexes();
		for (MyVertex mv : vertexes){	//정점의 paint정보 셋
			mv.setByPaintInfo();
		}
		
		images.clear();
		for(int i=0; i < pageViewInfo.getImgInfos().size(); i++){	//이미지들 생성
			MyImage mi = new MyImage();
			mi.setImgInfo(pageViewInfo.getImgInfos().get(i));	//이미지정보 줌
			mi.setByImgInfo(getResources());					//정보대로 이미지 셋시킴
			images.add(mi);
		}
    }
    
    //배경그림설정
	public void setBgImg() {
		int id = getResources().getIdentifier(bgFileName, "drawable", getResources().getString(R.string.pakage_name));

		try { // 파일이 없는경우 대비
			bgBd = (BitmapDrawable) getResources().getDrawable(id);
			Bitmap bitmapTemp = bgBd.getBitmap();
//			bgBitmap = Bitmap.createScaledBitmap(bgBd.getBitmap(), width,
//					height, true);
			bgBitmap = Bitmap.createScaledBitmap(bitmapTemp, width,
			height, true);
		} catch (NotFoundException e) {
			bgBd = null;
			bgBitmap = null;
		}
		
		callOnDraw();
	}
    //배경그림설정2 (배경파일명을 받음)(현재 미사용이나 만들어놓음)
	public void setBgImg(String bgFileName) {
		this.bgFileName = bgFileName;
		int id = getResources().getIdentifier(bgFileName, "drawable", getResources().getString(R.string.pakage_name));

		try { // 파일이 없는경우 대비
			bgBd = (BitmapDrawable) getResources().getDrawable(id);
			Bitmap bitmapTemp = bgBd.getBitmap();
//			bgBitmap = Bitmap.createScaledBitmap(bgBd.getBitmap(), width,
//					height, true);
			bgBitmap = Bitmap.createScaledBitmap(bitmapTemp, width,
			height, true);
		} catch (NotFoundException e) {
			bgBd = null;
			bgBitmap = null;
		}
		
		callOnDraw();
	}
    
	// modify by 60062446 박정실
	public void setBgImg(int bgId) {
		int id = bgId;
		try { // 파일이 없는경우 대비
			bgBd = (BitmapDrawable) getResources().getDrawable(id);
			bgBitmap = Bitmap.createScaledBitmap(bgBd.getBitmap(), width,
					height, true);
		} catch (NotFoundException e) {
			bgBd = null;
			bgBitmap = null;
		}
		
		callOnDraw();
	}
    
    // 이미지 넣기
	// created by 60062446 박정실
    public void insertImage(int id) {
    	images.add(new MyImage(getResources(), id, 100, 100));
    	callOnDraw();
    }
    
    //해당 이미지 삭제(MyImage에서 호출)
    public synchronized void removeImage() {
    	for(int i=0 ; i < images.size() ; i++){
//    		if(hashCode == images.get(i).hashCode()){
    		if(selectedImage == images.get(i)){
    			images.remove(i);
    		}
    	}
    }
    
    //선들 모두 삭제
    public void removeVertexes(){
//    	paths.clear();
    	vertexes.clear();
    	callOnDraw();    	
    }
    
    //Draw
    protected void onDraw(Canvas canvas) {
        // 현재의 x, y좌표를 기준으로 이미지를 그려준다.    	
    	if(bgBitmap != null){
    		canvas.drawBitmap(bgBitmap, 0, 0, null);	//배경출력    		
    	}else{
    		canvas.drawColor(Color.WHITE);	//흰색으로 칠해 화면지움. setBackground를 안해야 이걸로 지우기 가능    		
    	}
    	
    	
    	
    	for(MyImage mi : images){	//draw image
    		if(mi.bitmap != null){
    			mi.draw(canvas);      
    		}
        }
    	
//    	for(MyPath mp : paths){	//draw paths
//    		canvas.drawPath(mp, mp.getPaint());    //배열의 path들 그리기
//        }    	
//    	if(path != null){
//    		canvas.drawPath(path, pnt);    //현재path 그리기
//    	}
    	
		for (int i=0;i<vertexes.size();i++) {
			if (vertexes.get(i).isDraw() == true) {
//				pnt.setColor(vertexes.get(i).getPaint().getColor());
				canvas.drawLine(vertexes.get(i-1).getX(), vertexes.get(i-1).getY(),
						vertexes.get(i).getX(), vertexes.get(i).getY(), vertexes.get(i).getPaint());
			}
		}
    }    
    
    //onDraw() 호출
    protected void callOnDraw(){
        Canvas c = null;
		try {
			c = holder.lockCanvas(null);
			synchronized (holder) {
				onDraw(c);
			}
		} finally {
			if (c != null){
				holder.unlockCanvasAndPost(c);
			}
		}    	
    }

    
    //모든 이미지 셀렉트해제
    protected void deSelect(){
    	for(MyImage mi : images){
    		mi.setSelected(false);
    	}    	
    }
    
    //터치이벤트 처리
    public boolean onTouchEvent(MotionEvent event) {
    	return gd.onTouchEvent(event);
//        return true;
    }
	
	//--------------------------제스처디텍터-----------------------------------
	private final class MyGestureDetector extends GestureDetector {

		public MyGestureDetector(Context context, OnGestureListener listener) {
			super(context, listener);			
		}
		
		//마우스up이벤트감지
		public boolean onTouchEvent(MotionEvent e) {
			if(e.getAction() == MotionEvent.ACTION_UP){
				sg.onUp(e);
		    }
			
			return super.onTouchEvent(e);		    
		}
	}
	
	//--------------------제스처리스너----------------------------
	private final class MySimpleGestureListener extends GestureDetector.SimpleOnGestureListener{

		public boolean onDown(MotionEvent event){
			Log.i("msg","onDown");
			
			prevX = (int)x;
			prevY = (int)y;  
			x = (int)event.getX();  
			y = (int)event.getY();

//			path = new MyPath();	//패스 생성
//			path.setPaint(pnt);
//			path.moveTo(x,y);
			
			vertexes.add(new MyVertex(x, y, false, pnt));//정점추가

			boolean anchorSelected = false;
			if(selectedImage != null){	//선택된 그림이 있을시
				if(selectedImage.isOnAnchor(x, y) == true && selectedImage.selectedAnchor != ANCHOR_TYPE.MOVE){//앵커위에있나 검사
					if(selectedImage.selectedAnchor == ANCHOR_TYPE.ROTATE){
						drawingState =  DRAWING_STATE.rotating;				//회전앵커 위에 있을시 회전
					}else{					
						drawingState =  DRAWING_STATE.resizing;				//다른앵커 위에 있을시 리사이징
					}
					anchorSelected = true;
				}
			}

			if(anchorSelected == false){    		//선택된 앵커 없을시
				if(isEnable) {
					deSelect();							//모든그림 선택해제
					drawingState =  DRAWING_STATE.drawing;	//상태를 그리기로 set
					for(MyImage mi : images){//마우스가 그림위에있나 검사
						if(mi.contains(x, y) == true){	//그림위에 있으면
							mi.setSelected(true);		//해당그림을 선택된 그림으로 set
							selectedImage = mi;
							drawingState =  DRAWING_STATE.moving;	//상태를 이동으로 set
							intervalX = x - mi.getX();
							intervalY = y - mi.getY();
							break;
						}else{
							selectedImage = null;
						}
					}
				}
			}
			callOnDraw();

			return true;
		}

		//172ms 이후 떼면?
		public boolean onSingleTapUp(MotionEvent e){
			Log.i("msg","onSingleTapUp");
			return true;
		}
		
		//onDown 후 300ms안에 onDown 발생안할시, 300ms안에 손뗄시.
		public boolean onSingleTapConfirmed(MotionEvent e){
			Log.i("msg","onSingleTapConfirmed");
			return true;
		}		
		
		//끝 튕길시
		public boolean onFling(MotionEvent e1, MotionEvent e2, float vX, float vY){
			Log.i("msg","onFling");
			return true;
		}
		
		//터치 90-100ms
		public void onShowPress(MotionEvent e){
			Log.i("msg","onShowPress");
		}	
		
		//터치 590-600ms. 길게누를시 선택된 이미지 삭제
		public void onLongPress(MotionEvent e){
			Log.i("msg","onLongPress");
			if(drawingState != DRAWING_STATE.drawing && drawingState != DRAWING_STATE.resizing 
					&& drawingState != DRAWING_STATE.rotating && selectedImage != null){
				removeImage();
				callOnDraw();
			}
		}
		
		//두번터치시. onDown이 일반적으로 이후 발생
		public boolean onDoubleTap(MotionEvent e){
			Log.i("msg","onDoubleTap");
			if(selectedImage != null){
				selectedImage.resetBitmap();
			}
			return true;
		}
		
		//onSingleTapConfirmed 발생전 onDown시. onDown과 같이 들어옴(순서랜덤). ACTION_DOWN 과 ACTION_UP 두번캐치
		public boolean onDoubleTapEvent(MotionEvent e){
			Log.i("msg","onDoubleTapEvent");
			return true;
		}
		
		//30ms이후 드래그. onLongPress 호출되면 이건 호출안됨.
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float dX, float dY){
			prevX = (int)x;
			prevY = (int)y;  
			x = (int)e2.getX();  
			y = (int)e2.getY();			
			
			if(drawingState ==  DRAWING_STATE.drawing){	//그리기
//				path.lineTo(x, y);
				
				vertexes.add(new MyVertex(x, y, true, pnt));
			}else if (drawingState ==  DRAWING_STATE.moving){	//이동
				selectedImage.setMoving((int)(x - intervalX), (int)(y - intervalY), width, height);
			}else if (drawingState ==  DRAWING_STATE.resizing){	//리사이즈
				//selectedImage.resizeBitmap(x - originX, y - originY);
				selectedImage.resize((int)x, (int)y);
			}else if (drawingState ==  DRAWING_STATE.rotating){	//회전
				selectedImage.rotateBitmap(x - prevX);
			}
//			invalidate();//화면에 그림을 그림 -> onDraw()실행함.
			callOnDraw();	//draw

			return true;
		}
		
		//마우스뗄시
		public boolean onUp(MotionEvent e) {
			Log.i("msg","onFinished");
//			path.setPaint(pnt);
//			paths.add(path);	//path추가
//			path = null;	//선지우기 버튼시 현재선 남아있는거 없앰
			
			drawingState =  DRAWING_STATE.idle;			

			return false;
		}
	}
	
	//그리기 중지(페이지 삭제전 호출. surfaceDestroyed 에서 중지하면 왠지 에러가 나서 더 빨리 중지위함.  
	public void stopThread(){
		pageViewThread.setRunning(false);
	}
	
	
	
	@Override
	public void setEnabled(boolean enabled) {
		// TODO Auto-generated method stub
		super.setEnabled(enabled);
		isEnable = enabled;
		
	}

	//그리는 스레드--------------
	class PageViewThread extends Thread{//implements Runnable{
		private SurfaceHolder holder;
		private PageView pageView;
		private boolean running = false;

		public PageViewThread(SurfaceHolder holder, PageView pageView) {
			this.holder = holder;
			this.pageView = pageView;
		}

		public void setRunning(boolean b){
			running = b;
		}
		
		public void run(){
			while(running){
		        Canvas c = null;
				try {
					c = holder.lockCanvas(null);
					synchronized (holder) {
						pageView.onDraw(c);
					}
				} finally {
					if (c != null){
						holder.unlockCanvasAndPost(c);
					}
				}
			}
		}		
	}
}