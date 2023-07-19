package mdad.networkdata.volleysqldatabase;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    //public static String SERVER_URL  = "http://192.168.68.60:8080/bb/api/v1";
    public static String SERVER_URL  = "http://172.30.85.80:8080/bb/api/v1";
    //
    // public static String SERVER_URL  = "http://budgetbuddy.atspace.cc/api/v1";
    public static String URL_RETRIEVE_EXPENSE = SERVER_URL+"/expense-retrieve.php";
    public static String[] CATEGORY_LIST = {"Food","Transportation","Groceries","Entertainment","Household","Housing","Clothing","Utilities","Health","Education","Insurance","Tax","Donation","Gift","Other","Misc","Misc2","Misc3"};

    EditText etStartDate, etEndDate;
    MultiAutoCompleteTextView selectedCat;
    Button btnAddExp, btnViewExp;
    private int currYr,currMth, currDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences("user_pref", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("userId", 0);

        if(userId == 0){
            // User is not logged in for the first time, redirect to LoginActivity
            Intent loginUI = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginUI);
            finish(); // Optional: finish the MainActivity so that the user cannot go back to it without logging in
        } else {
            // User is already logged in, proceed to continue with MainActivity logic
            System.out.println("UserId:" +userId+" is logged in");
            etStartDate = findViewById(R.id.etDate);
            etEndDate = findViewById(R.id.etEndDate);
            selectedCat = findViewById(R.id.selectedCat);
            btnAddExp = findViewById(R.id.btnAddExp);
            btnViewExp = findViewById(R.id.btnViewExp);

            // Creating the instance of ArrayAdapter containing list of categories names
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, CATEGORY_LIST);
            selectedCat.setAdapter(adapter);
            selectedCat.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

            // get current date
            final Calendar cal = Calendar.getInstance();
            currYr = cal.get(Calendar.YEAR);
            currMth = cal.get(Calendar.MONTH);
            currDate = cal.get(Calendar.DAY_OF_MONTH);

            // default date period
            etStartDate.setText("01-"+(currMth+1)+"-"+currYr);
            etEndDate.setText(currDate+"-"+(currMth+1)+"-"+currYr);
            // start date event
            etStartDate.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    DatePickerDialog datePickerDialog = new DatePickerDialog(etStartDate.getContext(), R.style.MyDatePickerStyle, new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            etStartDate.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
                        }
                    }, currYr, currMth, currDate);
                    datePickerDialog.show();
                }});

            // end date event
            etEndDate.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    DatePickerDialog datePickerDialog = new DatePickerDialog(etEndDate.getContext(), R.style.MyDatePickerStyle, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            etEndDate.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
                        }},currYr,currMth,currDate);
                    datePickerDialog.show();
                }});

            // view expenses btn event
            btnViewExp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String startVal = etStartDate.getText().toString();
                    String endVal = etEndDate.getText().toString();
                    String catList = selectedCat.getText().toString();

                    Intent summaryUI = new Intent(getApplicationContext(),SummaryActivity.class);
                    summaryUI.putExtra("startVal",startVal);
                    summaryUI.putExtra("endVal",endVal);
                    summaryUI.putExtra("catList",catList);
                    startActivity(summaryUI);

                }
            });

            // add expense btn event
            btnAddExp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent expenseUI = new Intent(getApplicationContext(), ExpenseActivity.class);
                    expenseUI.putExtra("expId","0");
                    startActivity(expenseUI);
                }
            });

        } // MainActivity logic execution end

    } // onCreate End



} // MainActivity End