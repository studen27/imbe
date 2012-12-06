package kr.ac.mju.strangelibrary;

import android.app.Activity;
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
	    SharedPreferences pref = getSharedPreferences(Constants.PREF_USERNAME, MODE_PRIVATE);
	    String valueStr = pref.getString(Constants.PREF_USERNAME, "");
	    userName.setText(valueStr);
	    
	    Button setupButton = (Button) findViewById(R.id.setup_button);
	    setupButton.setOnClickListener(new Button.OnClickListener() {

			public void onClick(View v) {
				SharedPreferences pref = getSharedPreferences(Constants.PREF_USERNAME, MODE_PRIVATE);
				SharedPreferences.Editor editor = pref.edit(); // 수정용 에디터

				editor.putString(Constants.PREF_USERNAME, userName.getText().toString());
				editor.commit(); // 변경사항 적용
				
				finish();
			}
	    });
	}
}
