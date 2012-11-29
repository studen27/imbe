package com.example.imbedproject.v021;

import android.app.Application;
//created by 60022495 정민규
//created date : 2012/11/21
//last modify : 2012/11/23

public class Constants extends Application {
	public static int ANCHOR_SIZE = 20; // 이것의 두배가 앵커크기
	public static int IMG_EDGE_RESTRICT = 20; // 이미지 최소크기
	public static int IMG_MIN_SIZE = 40;
//	public static String PAKAGE_NAME = "com.example.pagemanager";
	public static String SAVE_FILENAME = "pages.dat";
	public static enum PAGE_TYPE {
		Title, LeftText, RightText
	}

	public static enum ANCHOR_TYPE {
		NW, NN, NE, WW, EE, SW, SS, SE, ROTATE, MOVE
	}

	public static enum DRAWING_STATE {
		idle, drawing, moving, resizing, rotating,
	}

	// created by 60062446 박정실
	public static final class Background {
		public static int[] images = { R.drawable.bg00, R.drawable.bg01, R.drawable.bg02,
				R.drawable.bg03, R.drawable.bg04, R.drawable.bg05,
				R.drawable.bg06, R.drawable.bg07, R.drawable.bg08,
				R.drawable.bg09, R.drawable.bg10, R.drawable.bg11,
				R.drawable.bg12, R.drawable.bg13, R.drawable.bg14,
				R.drawable.bg15, R.drawable.bg16, R.drawable.bg17,
				R.drawable.bg18, R.drawable.bg19
		};
		
		public static String[] imagesS = { "bg00", "bg01", "bg02",
			"bg03", "bg04", "bg05",
			"bg06", "bg07", "bg08",
			"bg09", "bg10", "bg11",
			"bg12", "bg13", "bg14",
			"bg15", "bg16", "bg17",
			"bg18", "bg19"
		};

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
		public static final class Human {
			public static int[] images = { R.drawable.hu01, R.drawable.hu02 };

			public static int getLength() {
				return images.length;
			}

			public static int get(int i) {
				return images[i];
			}
		}
		
		// created by 60062446 박정실
		public static final class Animal {
			public static int[] images = { R.drawable.ani01, R.drawable.ani02 };

			public static int getLength() {
				return images.length;
			}

			public static int get(int i) {
				return images[i];
			}
		}
		
		// created by 60062446 박정실
		public static final class Other {
			public static int[] images = { R.drawable.obj01 };

			public static int getLength() {
				return images.length;
			}

			public static int get(int i) {
				return images[i];
			}
		}
	
}
