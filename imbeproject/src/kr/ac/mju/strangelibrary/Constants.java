package kr.ac.mju.strangelibrary;

import android.app.Application;

public class Constants extends Application {
	public static int ANCHOR_SIZE = 20; // 이것의 두배가 앵커크기
	public static int IMG_EDGE_RESTRICT = 20; // 이미지 최소크기
	public static int IMG_MIN_SIZE = 40;	//이미지 최소사이즈
	public static String PAKAGE_NAME = "kr.ac.mju.strangelibrary";// 그냥씀
	public static String SAVE_FILENAME = "pages.dat";	//페이지정보 저장파일 기본이름. 실제 저장시엔 앞에 책이름이 붙게됨
	public static String DB_FILENAME = "books.db";		//db파일명
	public static String TABLE_NAME = "books";// 고치면 프로바이더 create도 고쳐야

	public static final String KEY = "_ID";	//db 테이블의 첫 컬럼
	public static final String NAME = "NAME";	//db테이블 컬럼 : 책이름
	public static final String AUTHOR = "AUTHOR";	//db테이블 컬럼 : 작성자명

	public static String CALL_TYPE = "CALL_TYPE";	//액티비티 호출시 쓸 타입명(현재는 없어도됨)
	public static int MAIN_TO_CREATE = 0;			//책만들기 액티비티 호출시 쓸 타입.
//	public static int MAIN_EDIT_LOAD = 1;
	
	public static String PREF_BGM = "Bgm Toggle";	//Preference 에 쓸 bgm on/off 키
	public static String PREF_USERNAME = "User Name";	//Preference 에 쓸 사용자명 키

	// public static enum CALL_TYPES {	//enum과 int의 비교가 안되어 현재 막아놓음
	// MAIN_EDIT_CREATE, MAIN_EDIT_LOAD
	// }

	//페이지 타입. 텍스트가 어느 위치에 붙을 것인가.
	public static enum PAGE_TYPE {
		Title, LeftText, RightText, NULL
	}

	//그림의 앵커 타입. 선택이 안될 경우 MOVE가 됨(도형 이동 위한 값)
	public static enum ANCHOR_TYPE {
		NW, NN, NE, WW, EE, SW, SS, SE, ROTATE, MOVE
	}

	//그리는 상태. 대기, 선그리기, 그림이동, 그림 리사이즈, 그림 회전  
	public static enum DRAWING_STATE {
		idle, drawing, moving, resizing, rotating,
	}

	// created by 60062446 박정실
	public static final class Background {
		public static int[] images = { R.drawable.bg00, R.drawable.bg01,
				R.drawable.bg02, R.drawable.bg03, R.drawable.bg04,
				R.drawable.bg05 };

		public static String[] imagesS = { "bg00", "bg01", "bg02", "bg03",
				"bg04", "bg05" };

		public static int getLength() {
			return images.length;
		}

		public static int get(int i) {
			return images[i];
		}

		public static String getS(int i) {
			return imagesS[i];
		}
	}

	// created by 60062446 박정실
	public static final class Illustration {
		public static int[] images = { R.drawable.il01, R.drawable.il02,
				R.drawable.il03, R.drawable.il04,
				R.drawable.il05, R.drawable.il06,
				R.drawable.il07, R.drawable.il08,
				R.drawable.il09, R.drawable.il10,
				R.drawable.il11, R.drawable.il12,
				R.drawable.il13, R.drawable.il14,
				R.drawable.il15, R.drawable.il16,
				R.drawable.il17, R.drawable.il18,
				R.drawable.il19
		};

		public static int getLength() {
			return images.length;
		}

		public static int get(int i) {
			return images[i];
		}
	}

	// created by 60062446 박정실
	public static final class Human {
		public static int[] images = { R.drawable.hu01, R.drawable.hu02,
				R.drawable.hu03, R.drawable.hu04 };

		public static int getLength() {
			return images.length;
		}

		public static int get(int i) {
			return images[i];
		}
	}

	// created by 60062446 박정실
	public static final class Animal {
		public static int[] images = { R.drawable.ani01, R.drawable.ani02,
				R.drawable.ani03 };

		public static int getLength() {
			return images.length;
		}

		public static int get(int i) {
			return images[i];
		}
	}

	// created by 60062446 박정실
	public static final class Other {
		public static int[] images = { R.drawable.obj01, R.drawable.obj02 };

		public static int getLength() {
			return images.length;
		}

		public static int get(int i) {
			return images[i];
		}
	}

}
