package mdad.networkdata.volleysqldatabase;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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

import java.util.Calendar;

public class ExpenseActivity extends AppCompatActivity {

    // Define URL and action type of volley request
    private static final String URL_RETRIEVE_EXPENSE = MainActivity.SERVER_URL+"/expense-retrieve.php";
    private static final String URL_ADD_EXPENSE = MainActivity.SERVER_URL+"/expense-add.php";
    private static final String URL_EDIT_EXPENSE = MainActivity.SERVER_URL+"/expense-edit.php";
    private static final String URL_DELETE_EXPENSE = MainActivity.SERVER_URL+"/expense-delete.php";
    private final int option1Get = 1;
    private final int option2Add = 2;
    private final int option3Edit = 3;
    private final int option4Delete = 4;

    int expId,userId, currYr,currMth, currDate;
    TextView tvTitle;
    EditText etDate, etDesc, etAmt;
    AutoCompleteTextView acCat;
    Button btnYes, btnNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        SharedPreferences sharedPreferences = getSharedPreferences("user_pref", Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", 0);

        Intent intent = getIntent();
        expId = intent.getIntExtra("expId",0);

        etDate = findViewById(R.id.etDate);
        acCat = findViewById(R.id.acCat);
        etDesc = findViewById(R.id.etDesc);
        etAmt = findViewById(R.id.etAmt);
        btnYes = findViewById(R.id.btnYes);
        btnNo = findViewById(R.id.btnNo);

        // Creating the instance of ArrayAdapter containing list of categories names
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, MainActivity.CATEGORY_LIST);
        acCat.setThreshold(1); // will start working from first character
        acCat.setAdapter(adapter); //setting the adapter data into the AutoCompleteTextView

        // get current date
        final Calendar cal = Calendar.getInstance();
        currYr = cal.get(Calendar.YEAR);
        currMth = cal.get(Calendar.MONTH);
        currDate = cal.get(Calendar.DAY_OF_MONTH);

        // date click event
        etDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(etDate.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        etDate.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
                    }},currYr,currMth,currDate);
                datePickerDialog.show();
            }}); // date click event end

        if(expId == 0){
            // New expense
            btnYes.setText("Add Expense");
            btnNo.setText("Clear");
            tvTitle.setText("Add Expense");

        } else {
            btnYes.setText("Update Expense");
            btnNo.setText("Delete");
            tvTitle.setText("Edit/Delete Expense");
            // retrieve expense detail
            JSONObject req_body_data = new JSONObject();
            try {
                req_body_data.put("userId",userId);
                req_body_data.put("expenseId",expId);
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(),"Error in JSON Encoding",Toast.LENGTH_LONG);
            }

            postData(URL_RETRIEVE_EXPENSE,req_body_data,option1Get);
        } // retrieving expense detail logic end

        // btnYes click event, for add or edit
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = etDate.getText().toString();
                String cat = acCat.getText().toString();
                String desc = etDesc.getText().toString();
                String amtStr = etAmt.getText().toString();
                double amtVal = Double.parseDouble(amtStr);
                String amount = String.format("%.2f",amtVal);

                if(date.isEmpty() || cat.isEmpty() || amtStr.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Required fields cannot be empty", Toast.LENGTH_LONG).show();
                    return;
                }

                JSONObject req_body_data = new JSONObject();
                try {
                    req_body_data.put("userId",userId);
                    req_body_data.put("expenseId",expId);
                    req_body_data.put("date",date);
                    req_body_data.put("category",cat);
                    req_body_data.put("description",desc);
                    req_body_data.put("amount",amount);
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),"Error in JSON Encoding",Toast.LENGTH_LONG);
                }

                // executing postData for add or edit
                if(expId>0){
                    System.out.println("edit"+option3Edit+req_body_data);
                    postData(URL_EDIT_EXPENSE,req_body_data,option3Edit);
                } else {
                    System.out.println("add"+option2Add+req_body_data);
                    postData(URL_ADD_EXPENSE,req_body_data,option2Add);
                }
            }
        });


        // btnNo click event, handling clear input and delete record
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // executing clear input or delete record
                if(expId == 0){
                    // clear input
                    etDate.setText("");
                    acCat.setText("");
                    etDesc.setText("");
                    etAmt.setText("");

                } else {
                    JSONObject req_body_data = new JSONObject();
                    try {
                        req_body_data.put("userId",userId);
                        req_body_data.put("expenseId",expId);
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(),"Error in JSON Encoding",Toast.LENGTH_LONG);
                    }

                    System.out.println("delete"+option4Delete+req_body_data);
                    postData(URL_DELETE_EXPENSE,req_body_data,option4Delete);
                }

            }
        });


    } // onCreate end

    // postData
    public void postData(String url, final JSONObject json,final int optionType){
        RequestQueue reqQ = Volley.newRequestQueue(this);
        JsonObjectRequest json_obj_req = new JsonObjectRequest(Request.Method.POST, url, json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (optionType == option1Get){
                    handle_GET_Expense_Response(response);
                } else {
                    handle_General_Response(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG);
            }
        });

        reqQ.add(json_obj_req);
    }

    public void handle_GET_Expense_Response(JSONObject response){
        try{
            if(response.getInt("status")==1){
                // get the data JSONArray first object
                JSONArray dataArr = response.getJSONArray("data");
                JSONObject expDetail = dataArr.getJSONObject(0);

                // Retrieve detail from obj and display in ui
                String exDate=expDetail.getString("date");
                String exCat=expDetail.getString("category");
                String exDesc=expDetail.getString("description");
                String exAmt=expDetail.getString("amount");

                etDate.setText(exDate);
                acCat.setText(exCat);
                etDesc.setText(exDesc);
                etAmt.setText(exAmt);

            } else{
                Toast.makeText(getApplicationContext(),response.getString("message"), Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(),"Request failed", Toast.LENGTH_LONG).show();
        }
    };

    // General response to alert and back to Main UI
    public void handle_General_Response(JSONObject response){
        try{
            if(response.getInt("status")==1){
                Toast.makeText(getApplicationContext(),response.getString("message"), Toast.LENGTH_LONG).show();
                // call finish() to destroy this activity and return to calling MainActivity
                finish();
                Intent mainUI = new Intent(this, MainActivity.class);
                startActivity(mainUI);

            } else{
                Toast.makeText(getApplicationContext(),response.getString("message"), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Request failed", Toast.LENGTH_LONG).show();
        }
    };

} // ExpenseActivity end