package com.example.imbedproject.v021;

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
				startActivity(intent);
			}
        });
        
        Button readButton = (Button) findViewById(R.id.read_start);
        readButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View arg0) {
				Intent intent = new Intent("com.example.imbedproject.v021.readIntent");
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
}
