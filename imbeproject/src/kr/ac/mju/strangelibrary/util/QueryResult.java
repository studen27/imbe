package kr.ac.mju.strangelibrary.util;

import java.util.ArrayList;

// created by 60062446 박정실
// created date : 2012/12/05
// last modify : 2012/12/05

// 검색 결과가 각각 따로 ArrayList에 저장되기 때문에 이를 한번에 전송하기 위해 만든 클래스
public class QueryResult {

	ArrayList<String> idList;
	ArrayList<String> originNameList;
	ArrayList<String> actualNameList;
	ArrayList<String> latitudeList;
	ArrayList<String> longitudeList;

	public QueryResult() {
		// TODO Auto-generated constructor stub
	}

	public int getIdAt(int i) {
		return Integer.parseInt(idList.get(i));
	}

	public void setIdList(ArrayList<String> idList) {
		this.idList = idList;
	}

	public String getOriginNameAt(int i) {
		return originNameList.get(i);
	}

	public void setOriginNameList(ArrayList<String> originNameList) {
		this.originNameList = originNameList;
	}

	public String getActualNameAt(int i) {
		return actualNameList.get(i);
	}

	public void setActualNameList(ArrayList<String> actualNameList) {
		this.actualNameList = actualNameList;
	}

	public int getLatitudeAt(int i) {
		return Integer.parseInt(latitudeList.get(i));
	}

	public void setLatitudeList(ArrayList<String> latitudeList) {
		this.latitudeList = latitudeList;
	}

	public int getLongitudeAt(int i) {
		return Integer.parseInt(longitudeList.get(i));
	}

	public void setLongitudeList(ArrayList<String> longitudeList) {
		this.longitudeList = longitudeList;
	}

	public int size() {
		return idList.size();
	}

}
