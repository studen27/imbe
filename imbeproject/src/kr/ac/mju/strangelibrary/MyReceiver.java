package kr.ac.mju.strangelibrary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

//브로드캐스트 리시버(전원 연결/끊음). 코드출처(종류도 나와있음) : http://j2enty.tistory.com/37
//created by 60022495 정민규
//created date : 2012/11/29
//last modify : 2012/12/07
public class MyReceiver extends BroadcastReceiver
{
       private String ACTION1 = "android.intent.action.ACTION_POWER_CONNECTED";//전원연결 신호받기용
       private String ACTION2 = "android.intent.action.ACTION_POWER_DISCONNECTED";//전원끊김 신호받기용
      
       //신호 받았을때 호출되는 함수
       public void onReceive(Context context, Intent intent)
       {
             if(intent.getAction().equals(ACTION1))//전원연결 신호받으면
             {
                   Toast.makeText(context,"전원이 연결되었습니다",1000).show();//전원연결 메세지 출력
             }
             else if(intent.getAction().equals(ACTION2))//전원끊김 신호받으면
             {
                    Toast.makeText(context,"전원이 끊겼습니다",1000).show();//전원끊김 메세지 출력
             }
       }
}