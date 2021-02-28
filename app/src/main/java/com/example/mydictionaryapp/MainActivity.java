package com.example.mydictionaryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    BigInteger p = new BigInteger("100123456789");
    int m = 103650;
    Random random = new Random();
    BigInteger a = BigInteger.valueOf(1 + random.nextInt(p.intValue()-1));
    BigInteger b = BigInteger.valueOf(random.nextInt(p.intValue()));
    Data[] data = new Data[m];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText editText = findViewById(R.id.editText);
        Button button = findViewById(R.id.button);
        TextView textView = findViewById(R.id.textView);
        get_json();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchWord = editText.getText().toString().toLowerCase().trim();
                BigInteger key = getKey(searchWord);
                int i = Hash1(key, a, b);
                int j = Hash2(key, BigInteger.valueOf(data[i].aj), BigInteger.valueOf(data[i].bj), BigInteger.valueOf(data[i].mj));
                if(j >= 0 && searchWord.equals(data[i].subArray[j][0])){
                    textView.setText(data[i].subArray[j][1]);
                }
                else{
                    String nf = "*****Word Not Found.*****";
                    textView.setText(nf);
                }
            }
        });
    }

    int Hash1(BigInteger k, BigInteger a, BigInteger b){
        return a.multiply(k).add(b).mod(p).mod(BigInteger.valueOf(m)).intValue();
    }

    int Hash2(BigInteger k, BigInteger aj, BigInteger bj, BigInteger mj){
        if(mj.equals(BigInteger.valueOf(0))) return -1;
        return  aj.multiply(k).add(bj).mod(p).mod(mj).intValue();
    }

    BigInteger getKey(String ss){
        BigInteger key = new BigInteger("0");
        BigInteger t = new BigInteger("1");
        for(int i = 0; i < ss.length(); i++){
            key = key.add(t.multiply(BigInteger.valueOf((int)ss.charAt(i)-97)));
            key = key.mod(p);
            t = t.multiply(BigInteger.valueOf(29));
            t = t.mod(p);
        }
        return key;
    }

    public void get_json(){
        String json;
        try {
            InputStream inputStream = getAssets().open("E2Bdatabase.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            json = new String(buffer, "UTF-8");
            JSONArray jsonArray = new JSONArray(json);

            for(int i = 0; i < m; i++){
                data[i] = new Data(0, 0, 0);
            }
            for (int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String en = jsonObject.getString("en").toLowerCase();
                data[Hash1(getKey(en), a, b)].mj++;
            }
            Random random = new Random();
            for(int i = 0; i < m; i++){
                data[i].mj *= data[i].mj;
                data[i].aj = 1 + random.nextInt(p.intValue()-1);
                data[i].bj = random.nextInt(p.intValue());
                data[i].setSubArray();
            }

            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String en = jsonObject.getString("en").toLowerCase();
                String bn = jsonObject.getString("bn");
                BigInteger key = getKey(en);
                int j = Hash1(key, a, b);
                int k = Hash2(key, BigInteger.valueOf(data[j].aj), BigInteger.valueOf(data[j].bj), BigInteger.valueOf(data[j].mj));
                data[j].subArray[k][0] = en;
                data[j].subArray[k][1] = bn;
            }
        }
        catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }


}
