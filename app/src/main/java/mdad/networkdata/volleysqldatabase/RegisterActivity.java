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

public class RegisterActivity extends AppCompatActivity {
    // register url
    private static String url_user_register =MainActivity.apiAdd+"/register.php";

    Button registerBtn;
    TextView tvLogin;
    EditText etName,etEmail,etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Binding
        registerBtn = findViewById(R.id.saveBtn);
        tvLogin = findViewById(R.id.tvLogin);
        etName = findViewById(R.id.etDate);
        etEmail = findViewById(R.id.etCategory);
        etPassword = findViewById(R.id.etDescription);

        // login link click event
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginView = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(loginView);
            }
        });

        // registerBtn click event
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString();
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                if(name.isEmpty() || email.isEmpty() || password.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Required fields cannot be empty", Toast.LENGTH_LONG).show();
                    return;
                }

                JSONObject reqBodyData = new JSONObject();
                try {
                    reqBodyData.put("name",name);
                    reqBodyData.put("email",email);
                    reqBodyData.put("password",password);
                } catch (JSONException e) {
                }

                System.out.println(reqBodyData);
                postData(url_user_register,reqBodyData);
            }
        }); // registerBtn event end
    } // onCreate End

    public void postData(String url,final JSONObject json){
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
                        Intent home = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(home);

                    } else {
                        Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                        // finish();
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

}