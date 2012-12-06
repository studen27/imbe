package kr.ac.mju.strangelibrary;

import java.io.Serializable;
import java.util.ArrayList;

import kr.ac.mju.strangelibrary.Constants.ANCHOR_TYPE;
import kr.ac.mju.strangelibrary.Constants.DRAWING_STATE;
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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
 
//created by 60022495 정민규
//created date : 2012/11/17
//last modify : 2012/12/07
//한 페이지의 정보를 담은 객체. SurfaceView
public class PageView extends SurfaceView implements Callback, Serializable {
	private static final long serialVersionUID = 5454058848627226403L;
	private String bgFileName = "";		//배경그림 이름
	private BitmapDrawable bgBd;			//배경그림(원본)
	private Bitmap bgBitmap;			//배경그림(늘린것)
    private float x = 0;				//터치한 위치정보값
    private float y = 0;    
    private float prevX = -1;    		//바로직전의 터치한 위치정보
    private float prevY = -1;    
    private float intervalX = 0;	//이미지 클릭시 이미지위치와 마우스위치 간격 (moving시 사용)
    private float intervalY = 0;
    private int width;	//뷰(자신) 크기
    private int height;	//자신의 높이
    private DRAWING_STATE drawingState;	//그리기상태
    private MyImage selectedImage;		//선택된이미지
    private SurfaceHolder holder;		//서피스뷰 홀더
    protected MySimpleGestureListener sg;	//제스처리스너(다양한 마우스동작 감지위함)
    private MyGestureDetector gd;		//제스처디텍터(다양한 마우스동작 감지위함. 제스처리스너와 쌍)
    protected Paint pnt;					//그리기 색
//    protected MyPath path;				//현재 그릴 선        
//    protected ArrayList<MyPath> paths;	//선들
    protected ArrayList<MyVertex> vertexes;	//점들
    protected ArrayList<MyImage> images;	//이미지들
//    protected ArrayList<MyImageInfo> imgInfos;	//이미지정보들
    protected PageViewInfo pageViewInfo;	//자기 페이지 정보. 생성시 안만들어지고 takePageViewInfo시 만들어짐
//    private PageViewThread pageViewThread = null;	//그리는 스레드
    private String myText = "No Text";					//내 페이지 글
    private Constants.PAGE_TYPE pageType = Constants.PAGE_TYPE.NULL;		//페이지타입: 표지/왼쪽텍스트/오른텍스트
    EditText editText = null;
    
    private boolean isEnable;		//편집가능한가     
    private View textView;			//edittext 표시용 뷰(BookEditor나 BookReader의 프레임뷰에 겹쳐질 뷰)
    
    //constructor1 코드로 생성시 호출됨.
    public PageView(Context context) {
        super(context);
        
//		bd = (BitmapDrawable)getResources().getDrawable(R.drawable.ab1);
//        bitmap = bd.getBitmap();
		
        pnt = new Paint();		//페인트 초기화
        setPaintColor(Color.BLUE);
        
//       paths = new ArrayList<MyPath>();
        drawingState = DRAWING_STATE.idle;	//그리기상태 초기값은 idle
        selectedImage = null;

        holder = getHolder();		//surfaceView에 필수인 홀더,콜백설정
        holder.addCallback(this);
        setFocusable(true);        

        sg = new MySimpleGestureListener();	//마우스 움직임 상세인식용 제스처디텍터&리스너
        gd = new MyGestureDetector(context, sg);
        
        vertexes = new ArrayList<MyVertex>();//정점배열 초기화
        images = new ArrayList<MyImage>();	//이미지배열 초기화
        
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
		width = getWidth();	//자신의 너비 셋팅
		height = getHeight();	//자신의 높이 셋팅
    	setBgImg();									//배경설정    	
		Log.i("msg",this.getWidth() + " " + this.getHeight() );
		
//		startThread();
	}
	
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {		
		Log.i("msg","PageView surfaceChanged");		
	}
	
	//suface종료시
	public void surfaceDestroyed(SurfaceHolder holder) {	//여기서join하면 에러, stopThread() 에서 함
		Log.i("msg","PageView surfaceDestoyed");
		if(bgBitmap != null){
			bgBitmap.recycle();		//bg그림메모리반환
		}
		destroyDrawingCache();		
		
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
    public void setPaintColor(int color){//페인트정보 셋팅
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
	public String getText() {
		return myText;
	}	
	public View getTextView() {
		return textView;
	}
	public void setTextView(View textView) {
		this.textView = textView;
	}	
	public Constants.PAGE_TYPE getPageType() {
		return pageType;
	}
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		isEnable = enabled;		
		editText.setEnabled(false);
	}	
	public EditText getEditText() {
		return editText;
	}
	public void setMyText(String myText) {
		this.myText = myText;
	}

	//자신의 textView생성. 자신(페이지)가 새로 생성될때 호출됨
	public void createTextView(Constants.PAGE_TYPE pageType, LayoutInflater inflater){
		View view;
		
		if(pageType == Constants.PAGE_TYPE.Title){			//표지
			view = inflater.inflate(R.layout.book_title,(ViewGroup) findViewById(R.id.book_title_root));
			editText = (EditText)view.findViewById(R.id.textTitle);
		}else if (pageType == Constants.PAGE_TYPE.LeftText){//왼쪽텍스트
			view = inflater.inflate(R.layout.page_text_left, (ViewGroup) findViewById(R.id.page_text_Left_root));
			editText = (EditText)view.findViewById(R.id.textLeft);			
		}else if (pageType == Constants.PAGE_TYPE.RightText){//오른텍스트
			view = inflater.inflate(R.layout.page_text_right,(ViewGroup) findViewById(R.id.page_text_right_root));
			editText = (EditText)view.findViewById(R.id.textRight);
		}else{		//페이지타입 선택에서, 뒤로가기 버튼 눌렀을시 텍스트는 없게됨
			view = null;
			editText = null;
		}
		
		if(editText != null){//텍스트는 기본값으로 설정
			editText.setText(myText);
		}
		
		this.pageType = pageType;
		textView = view;
	}
	
	//페이지정보 생성. 세이브시 호출되어야함
    public void createPageViewInfo(){
    	pageViewInfo = new PageViewInfo(this);	//정보객체 생성
    }
    //로드시 페이지정보 셋팅(커스텀 생성자가  안되어 따로 함수로 만듬)
    public void setByPageViewInfo(){
    	bgFileName = pageViewInfo.getBgFileName();	//배경그림 파일명 설정. 배경그림은 패키지 내에 있는 그림만 됨
    	
//    	this.paths = pageViewInfo.getPaths();
    	this.vertexes = pageViewInfo.getVertexes();	//정점배열 셋팅
		for (MyVertex mv : vertexes){	//정점의 paint정보 셋
			mv.setByPaintInfo();
		}
		
		images.clear();	//이미지배열 초기화
		for(int i=0; i < pageViewInfo.getImgInfos().size(); i++){	//이미지들 생성
			MyImage mi = new MyImage();
			mi.setImgInfo(pageViewInfo.getImgInfos().get(i));	//이미지정보 줌
			mi.setByImgInfo(getResources());					//정보대로 이미지 셋시킴
			images.add(mi);
		}
		
		myText = pageViewInfo.getText();	//Edittext에 표시될 텍스트 설정
		pageType = pageViewInfo.getPageType();	//페이지타입(타이틀인가, 왼쪽텍스트인가, 오른텍스트인가) 설정
    }
    
    //배경그림설정
	public void setBgImg() {
		int id = getResources().getIdentifier(bgFileName, "drawable", Constants.PAKAGE_NAME);//자신의 bg파일명으로, id를 얻어서

		try { // 파일이 없는경우 대비
			bgBd = (BitmapDrawable) getResources().getDrawable(id);	//배경그림을 설정하고
			Bitmap bitmapTemp = bgBd.getBitmap();
//			bgBitmap = Bitmap.createScaledBitmap(bgBd.getBitmap(), width,
//					height, true);
			bgBitmap = Bitmap.createScaledBitmap(bitmapTemp, width,	height, true);	//화면크기에 맞게 늘림
		} catch (NotFoundException e) {
			bgBd = null;
			bgBitmap = null;
		}
		
		callOnDraw();
	}
    //배경그림설정2 (배경파일명을 받음)(현재 미사용이나 만들어놓음)
	public void setBgImg(String bgFileName) {
		this.bgFileName = bgFileName;	//파일명으로
		int id = getResources().getIdentifier(bgFileName, "drawable", Constants.PAKAGE_NAME);//아이디 얻음. 이후 위와 동일

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
    
//	// modify by 60062446 박정실
//	public void setBgImg(int bgId) {
//		int id = bgId;
//		try { // 파일이 없는경우 대비
//			bgBd = (BitmapDrawable) getResources().getDrawable(id);
//			bgBitmap = Bitmap.createScaledBitmap(bgBd.getBitmap(), width,
//					height, true);
//		} catch (NotFoundException e) {
//			bgBd = null;
//			bgBitmap = null;
//		}
//		
//		callOnDraw();
//	}
	
	//이미지넣기(byte[]로 받음. CameraActivity에서 사진찍을시 호출됨)
    public void insertImage(byte[] b) {
    	images.add(new MyImage(getResources(), b, width/2 - 100, height/2 - 100));
//    	callOnDraw();	//쓰면에러. byte[] -> 이미지 변환 시간때문인듯
    }
    
    // 이미지 넣기
	// created by 60062446 박정실
    public void insertImage(int id) {//id를 이용해 피키지내의 이미지파일로 이미지추가하는 함수
    	images.add(new MyImage(getResources(), id, width/2 - 50, height/2 - 100));
    	callOnDraw();
    }
    
    //해당 이미지 삭제(이미지 롱클릭시 호출됨)
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
    	vertexes.clear();//정점배열 초기화
    	callOnDraw();    	
    }
    
    //Draw. 그리는함수. 스레드가 문제가 많아 현재 수동호출
    protected void onDraw(Canvas canvas) {
        if(bgBitmap != null){
    		canvas.drawBitmap(bgBitmap, 0, 0, null);	//배경출력    		
    	}else{
    		canvas.drawColor(Color.WHITE);	//흰색으로 칠해 화면지움. setBackground를 안해야 이걸로 지우기 가능    		
    	}
    	    	
    	for(MyImage mi : images){	//이미지들 배열돌며 출력
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
    	
		for (int i=0;i<vertexes.size();i++) {	//정점들 돌며 선그림
			if (vertexes.get(i).isDraw() == true) {
//				pnt.setColor(vertexes.get(i).getPaint().getColor());
				canvas.drawLine(vertexes.get(i-1).getX(), vertexes.get(i-1).getY(),		//배열의 이전 정점과 현재 정점을 paint로 그림
						vertexes.get(i).getX(), vertexes.get(i).getY(), vertexes.get(i).getPaint());
			}
		}
    }    
    
    //onDraw() 호출. 자주써서 따로만듬
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
		
		//마우스up이벤트감지. 원래 제스터디텍터,리스너로는 이 이벤트가 감지가 안되어 따로만듬
		public boolean onTouchEvent(MotionEvent e) {
			if(e.getAction() == MotionEvent.ACTION_UP){
				sg.onUp(e);
		    }
			
			return super.onTouchEvent(e);		    
		}
	}
	
	//--------------------제스처리스너----------------------------
	protected final class MySimpleGestureListener extends GestureDetector.SimpleOnGestureListener{

		//누를시
		public boolean onDown(MotionEvent event){
			if(isEnable) {
				Log.i("msg","onDown");
				
				prevX = (int)x;			//터치위치 얻고 이전터치위치 셋팅
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
						deSelect();							//모든그림 선택해제
						drawingState =  DRAWING_STATE.drawing;	//상태를 그리기로 set
						for(MyImage mi : images){//마우스가 그림위에있나 검사
							if(mi.contains(x, y) == true){	//그림위에 있으면
								mi.setSelected(true);		//해당그림을 선택된 그림으로 set
								selectedImage = mi;
								drawingState =  DRAWING_STATE.moving;	//상태를 이동으로 set
								intervalX = x - mi.getX();	//이동시 터치위치따라 이동시켜야 하므로 마우스와 이미지 좌표차이 설정
								intervalY = y - mi.getY();
								break;
							}else{
								selectedImage = null;
							}
						}
				}
				callOnDraw();	//화면갱신용
			
			}

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
			if(drawingState != DRAWING_STATE.drawing && drawingState != DRAWING_STATE.resizing	//그리기, 이동, 리사이즈, 회전이 아닐경우만 삭제 
					&& drawingState != DRAWING_STATE.rotating && selectedImage != null){
				removeImage();
				callOnDraw();//화면갱신
			}
		}
		
		//두번터치시. onDown이 일반적으로 이후 발생
		public boolean onDoubleTap(MotionEvent e){
			Log.i("msg","onDoubleTap");
			if(selectedImage != null){
				selectedImage.resetBitmap();	//이미지를 원본으로 돌림
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
			prevX = (int)x;		//터치좌표, 이전터치좌표 셋팅
			prevY = (int)y;  
			x = (int)e2.getX();  
			y = (int)e2.getY();			
			
			if(drawingState ==  DRAWING_STATE.drawing){	//그리기 상태이면
//				path.lineTo(x, y);
				vertexes.add(new MyVertex(x, y, true, pnt));	//정점을 배열에 추가함
			}else if (drawingState ==  DRAWING_STATE.moving){	//이동
				selectedImage.setMoving((int)(x - intervalX), (int)(y - intervalY), width, height);
			}else if (drawingState ==  DRAWING_STATE.resizing){	//리사이즈
				//selectedImage.resizeBitmap(x - originX, y - originY);
				selectedImage.resize((int)x, (int)y);
			}else if (drawingState ==  DRAWING_STATE.rotating){	//회전
				selectedImage.rotateBitmap(x - prevX);
			}
//			invalidate();//화면에 그림을 그림 -> onDraw()보다 느림
			callOnDraw();	//draw

			return true;
		}
		
		//마우스뗄시
		public boolean onUp(MotionEvent e) {
			Log.i("msg","onFinished");
//			path.setPaint(pnt);	//path로 그릴경우
//			paths.add(path);	//path추가
//			path = null;	//선지우기 버튼시 현재선 남아있는거 없앰
			
			drawingState =  DRAWING_STATE.idle;	//그리기상태 idle로 셋

			return false;
		}
		
	}
	
	//그리기 시작  (스레드 사용시)
//	public void startThread(){
//		if(pageViewThread == null){
//			pageViewThread = new PageViewThread(holder, this);	//스레드사용 
//			pageViewThread.setRunning(true);					//실기에서는 빠름.
//			pageViewThread.start();
//		}
//	}
	
	//그리기 중지(스레드 사용시. 페이지 삭제전 호출. surfaceDestroyed 에서 중지하면 왠지 에러가 나서 더 빨리 중지위함.  
//	public void stopThread(){
//		boolean retry = true;
//		pageViewThread.setRunning(false);
//		
//		while(retry){
//			try {
//				pageViewThread.join();	//스레드 종료 기다림
//				retry = false;
//				pageViewThread = null;
//			} catch (InterruptedException e) {				
//			}
////			bgBitmap.recycle();
////			destroyDrawingCache();
//		}
//	}	

	//그리는 스레드--------------현재 사용안함
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