package kr.ac.mju.strangelibrary;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import com.nemustech.tiffany.world.TFCircularHolder;
import com.nemustech.tiffany.world.TFModel;
import com.nemustech.tiffany.world.TFPanel;
import com.nemustech.tiffany.world.TFWorld;
//created by 60062446 박정실
//created date : 2012/11/17
//last modify : 2012/11/29
//메인 액티비티. 명지대에서 구입한 3d프레임웍인 tiffany를 사용. 센서 소스는 한승철 교수님 비주얼프로그래밍 강의자료 참조
public class MainActivity extends Activity implements SensorEventListener {

	private TickHandler tickHandler;	//정기처리 핸들러
	private SensorManager sensorManager;	//센서 매니저
	private Sensor accelerometer;		//가속도 센서		
	private float[] values = new float[3];	//가속도 xyz값

	private TFWorld mWorld;
	private LinearLayout mLinearLayout;
	private CustomCircularHolder mHolder = null;

	private String TAG = "CircularRail";

	//생성시
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main_t);
		LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mLinearLayout = (LinearLayout)findViewById(R.id.main_linear_layout);
		//				LinearLayout rowOne = new LinearLayout(this);
		//				rowOne.setOrientation(LinearLayout.HORIZONTAL);

		LinearLayout rowTwo = new LinearLayout(this);
		rowTwo.setOrientation(LinearLayout.HORIZONTAL);
		//				mLinearLayout.addView(rowOne);
		mLinearLayout.addView(rowTwo);

		//				LinearLayout subLinearLayout = new LinearLayout(this);
		//				subLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

		mWorld = new TFWorld(2, 2, 9);
		mWorld.setDefaultDelayImage(this, R.drawable.wait);
		mWorld.setTouchedModelColorMasking(0, 1, 1);
		mWorld.setSelectListener(mOnSelect);
		mWorld.setBlendingMode(true);

		mHolder = new CustomCircularHolder(1.5f);
		mHolder.attachTo(mWorld);
		mHolder.setStepPerUnitForce(1.5f);

		mHolder.setItemProvider(new ResourceProvider(getResources(), IMAGE_RESOURCE));
		mHolder.locate(0, -0.5f, -1.2f);
		mHolder.look(0, 30);
		mHolder.setEndlessMode(true);
		//mHolder.setHeadRadius(0.0f);
		mWorld.addReflectingFloor( mHolder.getLocation( TFWorld.AXIS_Y ) - 0.15f, 0.5f);

		for (int i = 0; i < MODEL_COUNT; i++) {
			TFPanel p = new TFPanel( mHolder, 1, 1);
			p.setBeautyReflection( true );
		}

		mWorld.show(findViewById(R.id.glsurfaceview));

		//센서 관리자 구하기
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

		//센서 구하기
		List<Sensor> list;
		list = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
		if(list.size() > 0)
			accelerometer = list.get(0);
	}

	//홀더 클래스
	private class CustomCircularHolder extends TFCircularHolder {

		// Bigger factor slows down dragging animation
		private final float DECELERATION = 0.005f;  //0.00005f;
		private final float THRESHOLD_DRAG = 5f;	// 5 pixel
		private final long THRESHOLD_FLICK_TICK = 50;
		private final float FACTOR_INITIAL_SPEED = 0.001f; //0.05f
		private final float MAX_INITIAL_SPEED = 0.01f;//0.01f; // We have to limit max speed since Android's tick is not reliable
		private final float FACTOR_DRAGGING = 400;	// bridges real pixel to the unit of Tiffany
		
		public CustomCircularHolder(float radius) {
			super( radius );
		}

		@Override
		protected void onTouchDown(TFModel selectedModel, float x, float y) {
			// Save down location
			Log.d( TAG, "onTouchDown, x:" + x );
			mHoldStartTick = SystemClock.uptimeMillis();
			mDragTime = 0;
			mAccumulatedDeviation = 0;
		}

		@Override
		protected void onTouchUp(TFModel selectedModel, float x, float y) {
			Log.d( TAG, "onTouchUp, drag time:" + mDragTime + " accumulated deviation:" + mAccumulatedDeviation );
			// Do we have to flick ?
			if ( mDragTime > 0 && SystemClock.uptimeMillis() - mHoldStartTick < THRESHOLD_FLICK_TICK ) {
				float velocity = FACTOR_INITIAL_SPEED * mAccumulatedDeviation / mDragTime;
				Log.d( TAG, "Flick started, velocity :" + velocity );
				if ( Math.abs( velocity ) > MAX_INITIAL_SPEED ) {
					Log.d( TAG, "Velocity limited to " + MAX_INITIAL_SPEED);
					velocity = velocity > 0 ? MAX_INITIAL_SPEED : - MAX_INITIAL_SPEED;
				}
				getMoveAnimation().startMoveAnimation(velocity, DECELERATION);
			}
		}

		@Override
		protected void onTouchDrag(TFModel selectedModel, float start_x,
				float start_y, float end_x, float end_y, int tickPassed) {
			// Here, we take only end position of X axis, since we will just set
			// new position instead of showing animation.
			// On rather fast device, this way brings more reasonable animation 
			float deviation = start_x - end_x;
			Log.d( TAG, "onTouchDrag, deviation : " + deviation );

			// Apply current position
			moveHeadModelStep( deviation  / FACTOR_DRAGGING );

			// Check if we have to prepare flicking
			long currentTick = SystemClock.uptimeMillis();

			if ( Math.abs( deviation ) > THRESHOLD_DRAG ) {
				// Reset hold counter
				mHoldStartTick = SystemClock.uptimeMillis();

				// Accumulate deviation for flicking if direction is same
				if ( deviation * mAccumulatedDeviation >= 0 ) {
					mAccumulatedDeviation += deviation;
					mDragTime += tickPassed;
				} else {
					mDragTime = tickPassed;
					mAccumulatedDeviation = deviation;
				}
			} else if ( currentTick - mHoldStartTick > THRESHOLD_FLICK_TICK ) {
				// User stayed on same spot while dragging
				mDragTime = 0;
				mAccumulatedDeviation = 0;
			}
		}	

		// In this case, we're going to consider only X axis
		private float mAccumulatedDeviation;
		private long mHoldStartTick;
		private float mDragTime;
	}

	protected void onPause() {
		super.onPause();
		mWorld.pause();
	}


	protected void onResume() {
		super.onResume();
		mWorld.resume();
		//센서 처리 시작
		if(accelerometer != null){
			sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
		}
		
		//정기처리 시작
		tickHandler = new TickHandler();
		tickHandler.sleep(0);
	}
	
	//정지시
	protected void onStop(){
		super.onStop();	//어플 정지
		sensorManager.unregisterListener(this);	//리스너 등록해제
		tickHandler = null;						//핸들러 등록해제
	}

	//메뉴의 이미지로 쓸 그림들
	private static final int[] IMAGE_RESOURCE = {
		R.drawable.create,
		R.drawable.read,
		R.drawable.setup,
	};

	static final int MODEL_COUNT = 3;

	TFWorld.OnSelectListener mOnSelect = new TFWorld.OnSelectListener() {
		public boolean onSelected(TFModel model, int faceIndex) {
			Log.d(TAG, "onSelected()");

			final TFModel selectedModel = model;
			runOnUiThread(new Runnable() {
				public void run() {
					Intent intent;
					switch (selectedModel.getItemIndex()){
						case 0:			//첫번째 메뉴 (책만들기)
							intent = new Intent("com.example.imbedproject.v021.editIntent");
							intent.putExtra(Constants.CALL_TYPE.toString(), Constants.MAIN_TO_CREATE);//부르는 타입설정
							startActivity(intent);
							break;
						case 1:			//두번째 메뉴(보기)
							intent = new Intent(MainActivity.this, BookReader.class);
							startActivity(intent);
							break;
						case 2:			//세번째 메뉴(이름설정)
							intent = new Intent("com.example.imbedproject.v021.setupIntent");
							startActivity(intent);
							break;				
					}
				}
			});
			return true;
		}
	};

	//정기처리 핸들러
	public class TickHandler extends Handler{
		//핸들 메시지
		public void handleMessage(Message msg){
//			String text = "SensorEx" + "\nX축 가속도:" + values[0] + 
//					"\nY축 가속도:" + values[1] + 
//					"\nZ축 가속도:" + values[2]; 

			if(tickHandler != null)
				tickHandler.sleep(200);
			
			if(mHolder != null){
				if(values[0] > 3 || values[0] < -3){
					mHolder.startPitchAndRoll(400);
				}else {
//					mHolder.stopPitchAndRoll();//이건 여러번 못씀
					mHolder.startPitchAndRoll(1);
				}				
			}
		}

		//대기
		public void sleep(long delayMills){
			removeMessages(0);
			sendMessageDelayed(obtainMessage(0), delayMills);
		}
	}

	//센서 이벤트리스너
	public void onSensorChanged(SensorEvent event){
		//가속도 구하기
		if(event.sensor == accelerometer){
			values[0] = event.values[0];
			values[1] = event.values[1];
			values[2] = event.values[2];
		}
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
	}
}
