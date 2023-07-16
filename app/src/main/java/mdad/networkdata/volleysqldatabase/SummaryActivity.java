package mdad.networkdata.volleysqldatabase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

public class SummaryActivity extends AppCompatActivity {

    String startVal,endVal,catList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        SharedPreferences sharedPreferences = getSharedPreferences("user_pref", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("userId", 0);

        Intent intent = getIntent();
        startVal = intent.getStringExtra("startVal");
        endVal = intent.getStringExtra("endVal");
        catList = intent.getStringExtra("catList");

        JSONObject req_body_data = new JSONObject();
        try {
        req_body_data.put("userId",userId);
        req_body_data.put("start",startVal);
        req_body_data.put("end",endVal);
        req_body_data.put("categories",catList);
        } catch (JSONException e){}
        System.out.println(req_body_data);
    }
}