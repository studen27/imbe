package com.example.imbedproject.v021;

import java.io.Serializable;
import java.util.ArrayList;

//created by 60022495 정민규
//created date : 2012/11/21
//last modify : 2012/11/23
public class PageViewInfo implements Serializable {
	private static final long serialVersionUID = 2951818016481649075L;
	private String bgFileName;		//배경그림 이름
//	private ArrayList<MyPath> paths;	//선들    
	private ArrayList<MyVertex> vertexes;	//선들
	private ArrayList<MyImageInfo> imgInfos;	//이미지들 정보

	//constructor
	public PageViewInfo(PageView pageView) {
		bgFileName = pageView.getBgFileName();		//배경이미지 이름가져옴
		
//		paths = pageView.getPaths();		
		for (MyVertex mv : pageView.getVertexes()){	//정점의 paint정보 생성
			mv.createPaintInfo();
		}
		vertexes = pageView.getVertexes();
		
		imgInfos = new ArrayList<MyImageInfo>();
		for (MyImage mi : pageView.getImages()){	//각 이미지들에게 정보 생성시킴
			mi.createImgInfo();
			imgInfos.add(mi.getImgInfo());			//이미지 정보배열에 추가
		}		

		//		images = pv.images;
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
}
