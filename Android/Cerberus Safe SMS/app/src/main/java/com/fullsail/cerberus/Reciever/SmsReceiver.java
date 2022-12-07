package com.fullsail.cerberus.Reciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;
import com.fullsail.cerberus.MainActivity;

public class SmsReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle=intent.getExtras();


        Object[] myPDU=(Object[])bundle.get("pdus");

        SmsMessage[] messages=new SmsMessage[myPDU.length];
        StringBuilder builder=new StringBuilder();

        for(int x=0; x<myPDU.length; x++){
            messages[x]=SmsMessage.createFromPdu((byte[]) myPDU[x]);
            builder.append(messages[x].getMessageBody());
        }


        try {
            Toast.makeText(context, builder.toString(), Toast.LENGTH_LONG).show();

            MainActivity.getInstance().setDecrypted(builder.toString());
        }
        catch (Exception e){

        }
    }
}