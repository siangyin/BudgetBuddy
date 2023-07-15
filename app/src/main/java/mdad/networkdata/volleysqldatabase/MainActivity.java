package mdad.networkdata.volleysqldatabase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    public static String apiAdd = "http://budgetbuddy.atspace.cc/api/v1";
    // login url
    private static String url_user_login =apiAdd+"/login.php";
    Button loginBtn;
    TextView tvRegister;
    EditText etEmail, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Binding
        loginBtn = findViewById(R.id.saveBtn);
        tvRegister=findViewById(R.id.tvLogin);
        etEmail=findViewById(R.id.etCategory);
        etPassword=findViewById(R.id.etDescription);

        // register link click event
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerView = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(registerView);
            }
        });

        // loginBtn click event
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                if(email.isEmpty() || password.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Required fields cannot be empty", Toast.LENGTH_LONG).show();
                    return;
                }

                JSONObject reqBodyData = new JSONObject();
                try {
                    reqBodyData.put("email",email);
                    reqBodyData.put("password",password);

                } catch (JSONException e) {

                }

                 // System.out.println(reqBodyData);
                postData(url_user_login,reqBodyData);

            }
        });



    } // onCreate End

    public void postData(String url, final JSONObject json){
        // create a RequestQueue for Volley
        RequestQueue reqQ = Volley.newRequestQueue(this);

        // define JSONObjectRequest for http post
        JsonObjectRequest json_obj_req = new JsonObjectRequest(
                Request.Method.POST, url, json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt("status") == 1) {
                        Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                         finish();
                         Intent home = new Intent(getApplicationContext(), HomeActivity.class);
                         startActivity(home);

                    } else {
                        Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Request failed", Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Request failed", Toast.LENGTH_LONG).show();
            }
        });
        reqQ.add(json_obj_req);
    }


} // MainActivity End