package mdad.networkdata.volleysqldatabase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class HomeActivity extends AppCompatActivity {
    // retrieve exp url
    private static String url_expense_retrieve =MainActivity.apiAdd+"/expense-retrieve.php";

    Button viewExpBtn, addExpBtn;
    EditText etStartDate, etEndDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Binding
        viewExpBtn = findViewById(R.id.viewExpBtn);
        addExpBtn=findViewById(R.id.addExpBtn);
        etStartDate=findViewById(R.id.etStartDate);
        etEndDate=findViewById(R.id.etEndDate);

        addExpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addExpView = new Intent(getApplicationContext(), AddExpenseActivity.class);
                startActivity(addExpView);
            }
        });



    }
}