package mdad.networkdata.volleysqldatabase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class TransactionActivity extends AppCompatActivity {

    private static final String URL_RETRIEVE_EXPENSE = MainActivity.SERVER_URL+"/expense-retrieve.php";
    String startVal,endVal,catList;
    int userId;
    TextView tvTransaction, tvPeriod;
    ListView lv;
    ArrayList<HashMap<String, String>> dbList;

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

        lv=findViewById(R.id.list);
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


        // ArrayList to store product info in Hashmap for ListView
        dbList = new ArrayList<HashMap<String, String>>();
        postData(URL_RETRIEVE_EXPENSE,req_body_data);

        // list item click event
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selId  = ((TextView) view.findViewById(R.id.expId)).getText().toString();
                Intent expenseUI = new Intent(getApplicationContext(), ExpenseActivity.class);
                // expenseUI.putExtra("expId",Integer.parseInt(selId));
                expenseUI.putExtra("expId",selId);
                System.out.println(Integer.parseInt(selId)+position+id);
                startActivity(expenseUI);
            }
        });

    } // onCreate end

    public class Expense {
        String category,date,amount,description,createdOn;
        int expenseId,userId;
        //Constructor
        Expense(int userId,int expenseId,String date,String category,String amount, String description,String createdOn){
            this.userId=userId;
            this.expenseId=expenseId;
            this.date=date;
            this.category=category;
            this.amount=amount;
            this.description=description;
            this.createdOn=createdOn;
        }
    }

    public void postData(String url,final JSONObject json){
        RequestQueue reqQ = Volley.newRequestQueue(this);
        JsonObjectRequest json_obj_req = new JsonObjectRequest(Request.Method.POST, url, json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // method to process the response results in JSONObject
                handleResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG);
            }
        });

        reqQ.add(json_obj_req);
    }

    private void handleResponse(JSONObject response){
        try{
            if(response.getInt("status")==1){
                // get data
                JSONArray dataArr = response.getJSONArray("data");

                // Loop thru data response
                for (int i = 0; i < dataArr.length(); i++){
                    JSONObject e = dataArr.getJSONObject(i);
                    int userId = e.getInt("userId");
                    int expenseId = e.getInt("expenseId");
                    String date = e.getString("date");
                    String category = e.getString("category");
                    String amount = e.getString("amount");
                    String description = e.getString("description");
                    String createdOn = e.getString("createdOn");
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("expenseId", String.valueOf(expenseId));
                    map.put("date",date);
                    map.put("category",category);
                    map.put("amount","$"+amount);
                    map.put("description",description);
                    map.put("createdOn",createdOn);
                    dbList.add(map);
                }

                System.out.println(dbList);

                // update parsed JSON data into listview by using SimpleAdapter, which links the data to the listview
                ListAdapter adapter = new SimpleAdapter(TransactionActivity.this,dbList,R.layout.list_expenses_item, new String[]{"expenseId","date","category","amount"},new int[]{R.id.expId,R.id.rowDate,R.id.rowCat,R.id.rowAmt});
                lv.setAdapter(adapter);
            }else {
                Toast.makeText(getApplicationContext(),response.getString("message"), Toast.LENGTH_LONG).show();
            }

        }catch (JSONException e) {
            Toast.makeText(getApplicationContext(),"Request failed", Toast.LENGTH_LONG).show();
        }
    }

} // Activity end