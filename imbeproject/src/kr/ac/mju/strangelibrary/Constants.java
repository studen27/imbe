package kr.ac.mju.strangelibrary;

import android.app.Application;

public class Constants extends Application {
	public static int ANCHOR_SIZE = 20; // 이것의 두배가 앵커크기
	public static int IMG_EDGE_RESTRICT = 20; // 이미지 최소크기
	public static int IMG_MIN_SIZE = 40;
	public static String PAKAGE_NAME = "kr.ac.mju.strangelibrary";// 그냥씀
	public static String SAVE_FILENAME = "pages.dat";
	public static String DB_FILENAME = "books.db";
	public static String TABLE_NAME = "books";// 고치면 프로바이더 create도 고쳐야

	public static final String KEY = "_ID";
	public static final String NAME = "NAME";
	public static final String AUTHOR = "AUTHOR";

	public static String CALL_TYPE = "CALL_TYPE";
	public static int MAIN_EDIT_CREATE = 0;
	public static int MAIN_EDIT_LOAD = 1;
	
	public static String PREF_BGM = "Bgm On/Off";
	public static String PREF_USERNAME = "User Name";

	// public static enum CALL_TYPES {
	// MAIN_EDIT_CREATE, MAIN_EDIT_LOAD
	// }

	public static enum PAGE_TYPE {
		Title, LeftText, RightText, NULL
	}

	public static enum ANCHOR_TYPE {
		NW, NN, NE, WW, EE, SW, SS, SE, ROTATE, MOVE
	}

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
