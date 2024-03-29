package kr.ac.mju.strangelibrary;

import java.io.Serializable;
import java.util.ArrayList;

//created by 60022495 정민규
//created date : 2012/11/21
//last modify : 2012/12/07
//한 페이지의 저장할 정보를 담은 객체
public class PageViewInfo implements Serializable {
	private static final long serialVersionUID = 2951818016481649075L;
	private String bgFileName;		//배경그림 이름
//	private ArrayList<MyPath> paths;	//선들    
	private ArrayList<MyVertex> vertexes;	//선들
	private ArrayList<MyImageInfo> imgInfos;	//이미지들 정보
    private String text;					//내 페이지 글
    private Constants.PAGE_TYPE pageType;		//페이지타입: 표지/왼쪽텍스트/오른텍스트
    
	//constructor. 생성자. 페이지의 정보를 가져와 셋팅
	public PageViewInfo(PageView pageView) {
		bgFileName = pageView.getBgFileName();		//배경이미지 이름가져옴
		
//		paths = pageView.getPaths();		
		for (MyVertex mv : pageView.getVertexes()){	//정점의 paint정보 생성
			mv.createPaintInfo();
		}
		vertexes = pageView.getVertexes();		//정점들 배열(연결할 선)
		
		imgInfos = new ArrayList<MyImageInfo>();	//이미지 정보배열 초기화
		for (MyImage mi : pageView.getImages()){	//각 이미지들에게 정보 생성시킴
			mi.createImgInfo();
			imgInfos.add(mi.getImgInfo());			//이미지 정보배열에 추가
		}		

		if(pageView.getEditText() != null){		//페이지의 텍스트가 null이 아니면
			text = pageView.getEditText().getText().toString();	//에딧텍스트에 있는 문자대로 셋팅
		}else{
			text = pageView.getText();	//아니면 그냥 페이지에 셋팅된 문자열로 가져옴
		}
		pageType = pageView.getPageType();	//페이지의 텍스트위치를 정한 타입을 셋팅
	}
	
	//getter&setter
	public ArrayList<MyVertex> getVertexes() {
		return vertexes;
	}
	public void setVertexes(ArrayList<MyVertex> vertexes) {
		this.vertexes = vertexes;
	}
	public ArrayList<MyImageInfo> getImgInfos() {
		return imgInfos;
	}
	public void setImgInfos(ArrayList<MyImageInfo> imgInfos) {
		this.imgInfos = imgInfos;
	}
	public String getBgFileName() {
		return bgFileName;
	}
	public String getText() {
		return text;
	}
	public Constants.PAGE_TYPE getPageType() {
		return pageType;
	}
}
