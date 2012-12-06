package kr.ac.mju.strangelibrary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

//created by 60062446 박정실
//created date : 2012/11/29
//last modify : 2012/11/30
public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Button createButton = (Button) findViewById(R.id.create_start);
        createButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent("com.example.imbedproject.v021.editIntent");
				intent.putExtra(Constants.CALL_TYPE.toString(), Constants.MAIN_EDIT_CREATE);//부르는 타입설정
				startActivity(intent);
			}
        });
        
        Button readButton = (Button) findViewById(R.id.read_start);
        readButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View arg0) {
				Intent intent = new Intent(MainActivity.this, BookReader.class);
				startActivity(intent);
			}
        });
        
        Button setupButton = (Button) findViewById(R.id.setup);
        setupButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View arg0) {
				Intent intent = new Intent("com.example.imbedproject.v021.setupIntent");
				startActivity(intent);
			}
        });
        
        //브로드캐스트 리시버. 현재 xml로 등록함
//      BroadcastReceiver receiver = new MyReceiver();
//      IntentFilter filter = new IntentFilter();
//      filter.addAction("android.intent.action.ACTION_POWER_CONNECTED");
//      filter.addAction("android.intent.action.ACTION_POWER_DISCONNECTED");
//      registerReceiver(receiver,filter);        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
	//부른 액티비티(로드)의 응답 받음
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
    	super.onActivityResult(requestCode, resultCode, intent);
    	if(requestCode == 0){
    		if(resultCode == Activity.RESULT_OK){
    			int sId = intent.getIntExtra("SelectedId", 0);			//선택된 줄의 id, 책이름 설정
    			String sName = intent.getStringExtra("SelectedName");
    			
    			Intent i = new Intent("com.example.imbedproject.v021.readIntent");
    			i.putExtra(Constants.CALL_TYPE.toString(), Constants.MAIN_EDIT_LOAD);	//부르는 타입설정
    			i.putExtra("SelectedId", sId);
				i.putExtra("SelectedName", sName);    			
				startActivity(i);			//에딧페이지로 넘김
    		}
    	}    	
    }
}
