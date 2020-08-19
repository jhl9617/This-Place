package com.example.testrightnow;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.iid.FirebaseInstanceId;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class TalkActivity extends AppCompatActivity {
    ListView listView;
    TalkAdapter talkAdapter;

    ArrayList<TalkItem> talkItems= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk);

        //데이터를 서버에서 읽어오기
       loadDB();

        listView = findViewById(R.id.listView);
        talkAdapter = new TalkAdapter(getLayoutInflater(),talkItems);
        listView.setAdapter(talkAdapter);
    }

    void loadDB(){
        //volley library로 사용
        new Thread(){
            @Override
            public void run(){
                String serverUri="http://27.96.134.241/htdocs/loadDB.php";
                String token = FirebaseInstanceId.getInstance().getToken();
                try{
                    URL url = new URL(serverUri);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");//sd
                    connection.setDoInput(true);
                    connection.setDoOutput(true);//sd
                    connection.setUseCaches(false);
                    String query="token="+token;
                    OutputStream os = connection.getOutputStream();
                    OutputStreamWriter writer = new OutputStreamWriter(os);



                    writer.write(query, 0, query.length());
                    writer.flush();
                    writer.close();
                    InputStream is = connection.getInputStream();
                    InputStreamReader isr=new InputStreamReader(is);
                    BufferedReader reader = new BufferedReader(isr);

                    final StringBuffer buffer = new StringBuffer();
                    String line=reader.readLine();
                    while(line!=null){
                        buffer.append(line+"\n");
                        line=reader.readLine();
                    }

                    String[] rows=buffer.toString().split(";");
                    talkItems.clear();
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run(){
                            talkAdapter.notifyDataSetChanged();
                        }
                    });
                    for(String row:rows){
                        String[] datas=row.split("&");
                        if(datas.length!=3)continue;
                        int no = Integer.parseInt(datas[0]);
                        String imgPath="http://27.96.134.241/htdocs/"+datas[1];
                        String date=datas[2];

                        talkItems.add(new TalkItem(no,imgPath,date));

                        runOnUiThread(new Runnable(){
                            @Override
                            public void run(){
                                talkAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                    /*
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run(){
                            new AlertDialog.Builder(TalkActivity.this).setMessage(buffer.toString()).create().show();
                        }
                    });/*/
                }catch(MalformedURLException e){e.printStackTrace();}catch(IOException e){e.printStackTrace();}
            }
        }.start();
    }
}
