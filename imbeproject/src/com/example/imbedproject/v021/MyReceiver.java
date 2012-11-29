package com.example.imbedproject.v021;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

//브로드캐스트 리시버(전원 연결/끊음). 코드출처(종류도 나와있음) : http://j2enty.tistory.com/37
//created by 60022495 정민규
//created date : 2012/11/29
//last modify : 2012/11/29
public class MyReceiver extends BroadcastReceiver
{
       private String ACTION1 = "android.intent.action.ACTION_POWER_CONNECTED";
       private String ACTION2 = "android.intent.action.ACTION_POWER_DISCONNECTED";
      
       @Override
       public void onReceive(Context context, Intent intent)
       {
             if(intent.getAction().equals(ACTION1))
             {
                   Toast.makeText(context,"전원이 연결되었습니다",1000).show();
             }
             else if(intent.getAction().equals(ACTION2))
             {
                    Toast.makeText(context,"전원이 끊겼습니다",1000).show();
             }
       }
}