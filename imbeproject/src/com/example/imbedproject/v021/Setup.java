package com.example.imbedproject.v021;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Setup extends Activity {
	EditText userName;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.setup_layout);
	
	    userName = (EditText) findViewById(R.id.user_name);
	    SharedPreferences pref = getSharedPreferences("User Name", MODE_PRIVATE);
	    String valueStr = pref.getString("User Name", "");
	    userName.setText(valueStr);
	    
	    Button setupButton = (Button) findViewById(R.id.setup_button);
	    setupButton.setOnClickListener(new Button.OnClickListener() {

			public void onClick(View v) {
				SharedPreferences pref = getSharedPreferences("User Name", MODE_PRIVATE);
				SharedPreferences.Editor editor = pref.edit(); // 수정용 에디터

				editor.putString("User Name", userName.getText().toString());
				editor.commit(); // 변경사항 적용
				
				finish();
			}
	    });
	}
}
