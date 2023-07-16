package mdad.networkdata.volleysqldatabase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class TransactionActivity extends AppCompatActivity {

    String startVal,endVal,catList;
    int userId;
    TextView tvSummary, tvTransaction, tvPeriod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        SharedPreferences sharedPreferences = getSharedPreferences("user_pref", Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", 0);

        Intent intent = getIntent();
        startVal = intent.getStringExtra("startVal");
        endVal = intent.getStringExtra("endVal");
        catList = intent.getStringExtra("catList");

        tvSummary=findViewById(R.id.tvSummary);
        tvTransaction=findViewById(R.id.tvTransaction);
        tvPeriod=findViewById(R.id.tvPeriod);
        tvPeriod.setText(startVal+" to "+endVal);

        JSONObject req_body_data = new JSONObject();
        try {
        req_body_data.put("userId",userId);
        req_body_data.put("start",startVal);
        req_body_data.put("end",endVal);
        req_body_data.put("categories",catList);
        } catch (JSONException e){}
        System.out.println(req_body_data);

        // summary click event
        tvSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent summaryUI = new Intent(getApplicationContext(), SummaryActivity.class);
                summaryUI.putExtra("startVal",startVal);
                summaryUI.putExtra("endVal",endVal);
                summaryUI.putExtra("catList",catList);
                startActivity(summaryUI);
            }
        });
    } // onCreate end
} // Activity end