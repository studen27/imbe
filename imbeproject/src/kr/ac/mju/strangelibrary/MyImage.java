package kr.ac.mju.strangelibrary;

import java.util.ArrayList;

import kr.ac.mju.strangelibrary.Constants.ANCHOR_TYPE;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
//created by 60022495 정민규
//created date : 2012/11/17
//last modify : 2012/12/06
//이미지 그림 등의 정보를 갖고있는 클래스
public class MyImage {
	private int id;			//리소스에 있는 그림의 id
	private String filename = "";	//리소스에 있는 파일명
	private BitmapDrawable bd;		//원본그림
	protected Bitmap bitmap;		//현재그림
	private int x;		//비트맵 위치x
	private int y;		//비트맵 위치y
	private int right;	//오른쪽 끝 좌표
	private int bottom;	//바닥 좌표
	private int width;	//너비
	private int height;	//높이
	private int transX;	//회전시 중심점차이 x
	private int transY; //회전시 중심점차이 y
	private boolean isSelected;	//선택되었나. 선택되었으면 앵커위치에 따라 리사이즈, 회전, 이동을 수행함
	private Matrix matrix;		//회전, 리사이즈를 위한 3x3 행렬 객체
	private ArrayList<MyAnchor> anchors;	//자신의 앵커들의 배열
	private Paint pnt;							//앵커를 그릴 페인트
	protected ANCHOR_TYPE selectedAnchor;		//선택된 앵커
	protected MyImageInfo imgInfo;				//자신의 정보
	
	//Constructor    아래 생성자로 넘김
	public MyImage(Resources res, String filename) {
		this(res, filename, 0, 0);	//put to constructor2
	}
	
	//Contructor2   (파일명, 생성할 위치 받음)
	public MyImage(Resources res, String filename, int x, int y) {
		this.filename = filename;		//파일명 설정
		id = res.getIdentifier(filename, "drawable", Constants.PAKAGE_NAME);//파일명으로 이미지의 id를 가져옴
		bd = (BitmapDrawable)res.getDrawable(id);						//id로 원본그림 생성
        bitmap = bd.getBitmap();										//원본그림으로 표시할 그림 생성
		
		this.x = x;			//좌표, 크기등 값 셋팅
        this.y = y;
        this.right = x + bitmap.getWidth();
        this.bottom = y + bitmap.getHeight();
        width = right - x;
		height = bottom - y;
		transX = 0;
		transY = 0;
        isSelected = false;
        matrix = new Matrix();	//변환행렬 초기화
        anchors = new ArrayList<MyAnchor>();
		for(int i=0; i < ANCHOR_TYPE.values().length ; i++){//앵커들 셋팅
			anchors.add(new MyAnchor());			
		}
		
		initPaint();	//페인트 초기값 설정
	}
	
	//Constructor3  (필요함)
	public MyImage(){
	}
	
	//Contructor4   (패키지내 이미지ID, 생성할 위치 받음)
	public MyImage(Resources res, int imageID, int x, int y) {		
		id = imageID;				//리소스에 있는 이미지파일의 id셋팅
		bd = (BitmapDrawable)res.getDrawable(id);	//id로 원본그림 셋팅
        bitmap = bd.getBitmap();				//원본그림으로 표시할그림 셋팅

		this.x = x;	//이하 위와 동일
		this.y = y;
		this.right = x + bitmap.getWidth();
		this.bottom = y + bitmap.getHeight();
		width = right - x;
		height = bottom - y;
		transX = 0;
		transY = 0;
		isSelected = false;
        matrix = new Matrix();	//변환행렬 초기화
        anchors = new ArrayList<MyAnchor>();
		for(int i=0; i < ANCHOR_TYPE.values().length ; i++){//앵커들 셋팅
			anchors.add(new MyAnchor());			
		}
		
		initPaint();	//페인트 초기값 설정
	}
	
	//Contructor5 (그림을 byte[]로 받음)
	public MyImage(Resources res, byte[] b, int x, int y) {		
		id = 0;		
		bd = new BitmapDrawable(BitmapFactory.decodeByteArray(b, 0, b.length));//어쩔수없이 bitmapfactory사용.byte[]를 이용해 원본그림 얻음
		bitmap = bd.getBitmap();				//원본그림으로 표시할그림 얻음. 이하 위와 동일

		this.x = x;
		this.y = y;
		this.right = x + bitmap.getWidth();
		this.bottom = y + bitmap.getHeight();
		width = right - x;
		height = bottom - y;
		transX = 0;
		transY = 0;
		isSelected = false;
        matrix = new Matrix();	//변환행렬 초기화
        anchors = new ArrayList<MyAnchor>();
		for(int i=0; i < ANCHOR_TYPE.values().length ; i++){//앵커들 셋팅
			anchors.add(new MyAnchor());			
		}
		
		initPaint();	//페인트 초기값 설정
	}

	//getter & setter
	public int getX(){
		return this.x;		
	}
	public int getY(){
		return this.y;		
	}
	public int getWidth(){
		return this.width;		
	}
	public int getHeight(){
		return this.height;		
	}
	public int getRight(){
		return this.right;		
	}
	public int getBottom(){
		return this.bottom;		
	}
	public void setX(float x){
		this.x = (int)x;		
	}	
	public void setY(float y){
		this.y = (int)y;	
	}
	public int getId() {
		return id;
	}
	public String getFilename() {
		return filename;
	}
	public int getTransX() {
		return transX;
	}
	public int getTransY() {
		return transY;
	}
	public Matrix getMatrix() {
		return matrix;
	}
	public MyImageInfo getImgInfo() {
		return imgInfo;
	}
	public void setImgInfo(MyImageInfo imgInfo) {
		this.imgInfo = imgInfo;
	}
	public BitmapDrawable getBd() {
		return bd;
	}

	//앵커그리기용 페인트 초기화
	public void initPaint(){
        pnt = new Paint(Color.BLACK);
        pnt.setStrokeWidth(1);
        pnt.setStyle(Paint.Style.STROKE);
        pnt.setAntiAlias(true);
	}
	
	//리사이즈용 위치정보set. 리사이즈시 터치위치정보가 넘어옴
	public void set(float x, float y, float right, float bottom){	
		set((int) x, (int) y, (int) right, (int) bottom);	//아래 메소드로 넘김
	}	
	public void set(int x, int y, int right, int bottom){
		if(x > this.right-Constants.IMG_MIN_SIZE){	//에러방지 & 이미지 최소크기
			x = this.right-Constants.IMG_MIN_SIZE;
		}
		if(y > this.bottom-Constants.IMG_MIN_SIZE){
			y = this.bottom-Constants.IMG_MIN_SIZE;
		}		
		if(right < this.x+Constants.IMG_MIN_SIZE){
			right = this.x+Constants.IMG_MIN_SIZE;
		}
		if(bottom < this.y+Constants.IMG_MIN_SIZE){
			bottom = this.y+Constants.IMG_MIN_SIZE;
		}		
		
		this.x = x;			//자신의 값 셋팅
		this.y = y;
		this.right = right;
		this.bottom = bottom;

	}
	//이동용 위치정보set
	public void setMoving(int mx, int my, int viewWidth, int viewHeight){	
		int movX = 0;	//화면밖 넘어갈시 화면안으로 이동시킬 좌표
		int movY = 0;
		
		this.right = mx + this.width;	//화면밖으로 나가지 않게 하기위해 쓸 값을 먼저 셋팅
		this.bottom = my + this.height;
		
		if(mx > viewWidth - Constants.IMG_EDGE_RESTRICT){			//화면밖으로 나가지 않게 하기위한 제한
			movX += viewWidth - Constants.IMG_EDGE_RESTRICT - mx;
		}
		if(my > viewHeight - Constants.IMG_EDGE_RESTRICT){
			movY += viewHeight - Constants.IMG_EDGE_RESTRICT - my;
		}
		if(right < Constants.IMG_EDGE_RESTRICT){
			movX += Constants.IMG_EDGE_RESTRICT - right;
		}
		if(bottom < Constants.IMG_EDGE_RESTRICT){
			movY += Constants.IMG_EDGE_RESTRICT - bottom;
		}
		
		this.x = mx + movX;
		this.y = my + movY;
		this.right = x + this.width;
		this.bottom = y + this.height;		
	}

	//이 이미지가 선택되었나 set
	public void setSelected(boolean isSelected){
		this.isSelected = isSelected;		
	}
	
	//그리기
	public void draw(Canvas canvas){
		canvas.drawBitmap(bitmap,x , y, null);
		
		//앵커들을 그림
		if(isSelected == true){
			for(int i=0; i < ANCHOR_TYPE.values().length - 1; i++){	//앵커들의 배열을 돌며
				anchors.get(i).setLocation(x, y, right, bottom, ANCHOR_TYPE.values()[i]);	//앵커들의 위치설정
				canvas.drawRect(anchors.get(i), pnt);	//사각형으로 그림				
			}
		}
	}
	
	//터치위치가 나(이미지)안인지를 리턴
	public boolean contains(float x, float y){
		return contains((int) x, (int) y);		//아래 메소드로 넘김
	}
	public boolean contains(int x, int y){
		if (x >= this.x && x <= this.right && y >= this.y && y <= this.bottom){
			return true;
		}		
		return false;		
	}

	//앵커위가 터치되었는지를 리턴
	public boolean isOnAnchor(float x, float y){
		return isOnAnchor((int) x, (int) y);//아래 메소드로 넘김
	}
	public boolean isOnAnchor(int x, int y){
		selectedAnchor = ANCHOR_TYPE.MOVE;	//아무 앵커위도 아닐때는 이미지가 이동이 돼야하므로 기본값을 MOVE로 설정
		for(int i=0; i < ANCHOR_TYPE.values().length; i++){	//앵커들을 돌며
			if(anchors.get(i).contains(x,y)){				//터치위치가 자신의 위치인지 검사
				selectedAnchor = ANCHOR_TYPE.values()[i]; 	//앵커위이면 선택된 앵커를 셋팅
				return true; 
			}
		}
		return false;
	}	

	//resize.  터치위치정보를 받아 어느 부분의 앵커가 선택되었는지에 따라 늘리거나 줄이는 위치가 달라짐
	public void resize(int mx, int my){	//mouseX, mouseY.		
		switch(selectedAnchor){
		case NW:
			set(mx, my, right, bottom);
//			set(mx, my, width + x - mx, height + y - my);
			break;
		case NN:
			set(x, my, right, bottom);
			break;
		case NE:
			set(x, my, mx, bottom);
			break;
		case WW:
			set(mx, y, right, bottom);
			break;
		case EE:
			set(x, y, mx, bottom);
			break;
		case SW:
			set(mx, y, right, my);
			break;
		case SS:
			set(x, y, right, my);
			break;
		case SE:
			set(x, y, mx, my);
			break;
		}
		
		resizeBitmap(right - x, bottom - y);
		
	}		
	
	//이미지 리사이즈. (새 너비, 높이 받음)
	public void resizeBitmap(float newWidth, float newHeight){
		float w = newWidth / (float)width;
		float h = newHeight / (float)height;		
		Bitmap bitmapTemp = bd.getBitmap();	//원본그림에서 표시할 그림을 얻음
		matrix.postScale(w, h);		//변환행렬로 먼저 리사이즈를 함
		bitmap = Bitmap.createBitmap(bitmapTemp, 0, 0, bitmapTemp.getWidth(), bitmapTemp.getHeight(), matrix, true);//리사이즈된 비트맵을 얻음
		width = bitmap.getWidth();	//너비, 높이 설정
		height = bitmap.getHeight();
//		bitmapTemp.recycle();	//이건 완전종료시 써야함. 안그러면 에러
//		if(bd == null){
//			Log.i("msg", "bd is null");			
//		}
	}
	
	//회전
	public void rotateBitmap(float angle){
		Bitmap bitmapTemp = bd.getBitmap();		//원본그림에서 표시할 그림을 얻음
		matrix.preRotate(angle, width / 2, height / 2);	//이미지 중심점을 기준으로 리사이즈후 Matrix 회전
		
		// 회전된 Matrix로 비트맵 생성
//		bitmap.recycle();
		bitmap = Bitmap.createBitmap(bitmapTemp, 0, 0, bitmapTemp.getWidth(), bitmapTemp.getHeight(), matrix, true);//변환된 비트맵 얻음
		transX = (bitmap.getWidth() - width)/2;  //자기 정보들 셋팅
		transY = (bitmap.getHeight() - height)/2;//
		width = bitmap.getWidth();
		height = bitmap.getHeight();
		x = x - (int)transX;
		y = y - (int)transY;
		right = right + (int)transX;
		bottom = bottom + (int)transY;
//		bitmapTemp.recycle();
	}
	
	//이미지 초기화
	public void resetBitmap(){
		matrix.reset();
		bitmap = bd.getBitmap();
		width = bitmap.getWidth();
		height = bitmap.getHeight();
		right = x + width;
		bottom = y + height;
	}	
	
	//img정보생성. BookEditor에서 save시 생성됨
	public void createImgInfo(){
		imgInfo = new MyImageInfo(this);
	}
	//img정보에 의해 img셋팅. 로드시 각 페이지(PageView) 에서 호출됨
	//ImageInfo객체를 이용해 자신의 정보를 셋팅함. 이미지는 나중에 셋팅
	public void setByImgInfo(Resources res){	
		Log.i("msg",imgInfo.getFilename());
		filename = imgInfo.getFilename();		
		id = res.getIdentifier(filename, "drawable", Constants.PAKAGE_NAME);

		//		bd = (BitmapDrawable)res.getDrawable(id);	//장치파일에서 로드시는 bd,bitmap은 나중에 셋팅함
//		bd = imgInfo.getBd();
//		bitmap = bd.getBitmap();        

		x = imgInfo.getX();
		y = imgInfo.getY();
		right = imgInfo.getRight();
		bottom = imgInfo.getBottom();
		width = imgInfo.getWidth();
		height = imgInfo.getHeight();
		transX = imgInfo.getTransX();
		transY = imgInfo.getTransY();
		
		matrix = new Matrix();
		matrix.setValues(imgInfo.getMatrixInfo());		
		
        anchors = new ArrayList<MyAnchor>();
		for(int i=0; i < ANCHOR_TYPE.values().length ; i++){
			anchors.add(new MyAnchor());			
		}
		isSelected = false;
		initPaint();
		selectedAnchor = null;
	}

	//BitmapDrawable을 받아 자신의 이미지를 셋팅
	public void setBitmapByBd(BitmapDrawable bd){
		this.bd = bd;
        bitmap = bd.getBitmap();
        
		resizeBitmap(width, height);	//회전,사이즈 설정
		rotateBitmap(0);
	}

}