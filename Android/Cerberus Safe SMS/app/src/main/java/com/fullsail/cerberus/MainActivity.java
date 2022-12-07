package com.fullsail.cerberus;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.fullsail.cerberus.helpers.Decryption;
import com.fullsail.cerberus.helpers.Encryption;
import com.fullsail.cerberus.helpers.Formatter;
import com.fullsail.cerberus.helpers.Mutator;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    VideoView vv;

    ArrayList<Integer> keyStream;
    ArrayList<Integer> oldKeystream;
    ArrayList<String> list;
    CustomAdapter adapter;
    String number;
    String toParse;
    String[] keystreamArray;

    Mutator mutator;
    Encryption encryption;
    Decryption decryption;
    ListView lv;

    Button sButton;
    FloatingActionButton fabPhone;
    FloatingActionButton fabKeystream;

    private static MainActivity ins;

    EditText toEncryptET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buildView();

        toEncryptET=findViewById(R.id.toEncrypt);
        lv=findViewById(R.id.main_LV);

        sButton=findViewById(R.id.SButton);
        sButton.setOnClickListener(this);

        fabPhone=findViewById(R.id.main_phoneFAB);
        fabPhone.setOnClickListener(this);
        fabPhone.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.red)));

        fabKeystream=findViewById(R.id.main_keystream);
        fabKeystream.setOnClickListener(this);
        fabKeystream.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.red)));

        oldKeystream=getKeystream();

        ins=this;

        list=new ArrayList<>(Arrays.asList());
        adapter=new CustomAdapter(list, this);

        lv.setAdapter(adapter);

        requestPerms();

        // killswitch
        try {killSwitch();} catch (IOException e) {e.printStackTrace();}
    }

    ArrayList<Integer> getKeystream(){

        if(toParse==null)
            toParse="9/9/-1/9/9/-2/9/9";


        keystreamArray=toParse.split("/");
        ArrayList<Integer> tmp=new ArrayList<>();

        for(int x=0; x<keystreamArray.length; x++){
            tmp.add(Integer.parseInt(keystreamArray[x]));
        }


        return tmp;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.SButton:
                encrypt();
                break;

            case R.id.main_phoneFAB:
                setNumber();
                break;

            case R.id.main_keystream:
                setKeystream();
                break;
        }
    }

    public void encrypt()
    {

        keyStream=getKeystream();
        mutator = new Mutator(keyStream);

        Formatter formatter=new Formatter(toEncryptET.getText().toString().toLowerCase());
        String tmpFormatted=formatter.getFormatted();
        String sender="";

        char[] tmpCA=tmpFormatted.toCharArray();
        ArrayList<String> arrayListStr=new ArrayList<>();

        for(int x=0;x<tmpCA.length;x++) {
            mutator.run();
            encryption = new Encryption(mutator.getKeystream(), Character.toString(tmpCA[x]));

            sender=sender+encryption.toString().toUpperCase();
            arrayListStr.add(encryption.toString());
        }

        list.add("ME: "+toEncryptET.getText().toString());
        adapter.notifyDataSetChanged();

        SmsManager manager=SmsManager.getDefault();
        manager.sendTextMessage(number,null, sender,null,null);
    }

    public void decrypt(String encrypted){

        keyStream=getKeystream();

        mutator = new Mutator(keyStream);
        String tmpUnformatted="";

        char[] tmpCA=encrypted.toCharArray();

        for(int x=0;x<tmpCA.length;x++) {
            mutator.run();
            decryption = new Decryption(mutator.getKeystream(), Character.toString(tmpCA[x]));

            tmpUnformatted=tmpUnformatted+decryption.toString();
        }

        Formatter formatter=new Formatter(tmpUnformatted);

        list.add("SENDER: "+formatter.getUnformatted().toUpperCase());
        adapter.notifyDataSetChanged();
    }

    private void requestPerms(){

        String[] permissions={Manifest.permission.RECEIVE_SMS,Manifest.permission.SEND_SMS};

        ActivityCompat.requestPermissions(this, permissions,1);
    }

    public void setDecrypted(String message){
        decrypt(message);
    }

    public static MainActivity getInstance(){
        return ins;
    }

    private class CustomAdapter extends BaseAdapter{

        ArrayList<String> list;
        Context context;

        public CustomAdapter(ArrayList<String> list, Context context){
            this.list=list;
            this.context=context;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {return null;}

        @Override
        public long getItemId(int position) {return 0;}

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv=new TextView(context);

            tv.setTextAppearance(R.style.main_lv_style);
            tv.setText(list.get(position));

            return tv;
        }
    }

    private void setNumber(){
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle("Input a phone number!");

        EditText editText=new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_PHONE);

        builder.setView(editText);

        builder.setPositiveButton("SET", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                number=editText.getText().toString();
                fabPhone.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor
                        (getApplicationContext(), R.color.green)));
            }
        });

        builder.show();

    }

    private void setKeystream() {

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        EditText input=new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_DATETIME);
        input.setHint("SET KEYSTREAM HERE! DONT FORGET RADICALS!");
        builder.setView(input);

        builder.setTitle("Set Custom Keystream!");
        builder.setPositiveButton("SET", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                toParse=input.getText().toString();
                oldKeystream=getKeystream();

                fabKeystream.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor
                        (getApplicationContext(), R.color.green)));
            }
        });

        builder.show();
    }

    private void buildView(){
        vv=findViewById(R.id.VView);
        Uri vidUri;

        String uri="android.resource://"+getPackageName()+"/"+R.raw.bg;
        vidUri=Uri.parse(uri);

        vv.setVideoURI(vidUri);

        vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);

                mp.start();
            }
        });

        Objects.requireNonNull(getSupportActionBar()).hide();
    }

    private void killSwitch() throws IOException {

        new Thread() {

            public void run() {

                String url = "https://drive.google.com/file/d/1JVWZISRsQxULsKFdDcutQWvxJT5_ZgUS/view?usp=sharing";
                String key;

                try {

                    URL url1 = new URL(url);

                    BufferedReader reader=new BufferedReader(new InputStreamReader(url1.openStream()));

                } catch (IOException e) {
                    finishAffinity();
                }
            }
        }.start();
    }
}