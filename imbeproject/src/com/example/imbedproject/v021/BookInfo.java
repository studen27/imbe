package com.example.imbedproject.v021;

import java.io.Serializable;
import java.util.ArrayList;
//created by 60022495 정민규
//created date : 2012/11/21
//last modify : 2012/11/23
//책정보 저장 객체(세이브/로드 용)
public class BookInfo implements Serializable {
	private static final long serialVersionUID = 5874151527970460574L;	//직렬화 용
	String bookName;	//책제목
	ArrayList<PageViewInfo> pageInfos;	//페이지정보 객체 배열
	String bookFileName;				//세이브되는 정보파일명  ex) ~_pages.dat
	ArrayList<String> uploadFileNames;	//세이브되는 이미지파일명 배열
	
	//constructor
	public BookInfo() {
		bookName = ""; 
		pageInfos = new ArrayList<PageViewInfo>();
	}
	
	//getter&setter
	public void setBookName(String name){
		this.bookName = name;
	}
	public ArrayList<PageViewInfo> getPageInfos() {
		return pageInfos;
	}
	public void setPageViewInfos(ArrayList<PageViewInfo> pageViewInfos) {
		this.pageInfos = pageViewInfos;
	}
	public String getBookName() {
		return bookName;
	}
	public String getBookFileName() {
		return bookFileName;
	}
	public void setBookFileName(String bookFileName) {
		this.bookFileName = bookFileName;
	}
	public ArrayList<String> getUploadFileNames() {
		return uploadFileNames;
	}
	public void setUploadFileNames(ArrayList<String> uploadFileNames) {
		this.uploadFileNames = uploadFileNames;
	}	
}
