package ay3524.com;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ay3524.com.legrooms.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText username_field, password_field;
    Button sign_in;
    private ProgressDialog pDialog;
    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username_field = (EditText) findViewById(R.id.username);
        password_field = (EditText) findViewById(R.id.password);
        sign_in = (Button) findViewById(R.id.sign_in);
        sign_in.setOnClickListener(this);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        view = findViewById(R.id.relative_layout);
    }

    @Override
    public void onClick(View v) {
        String username = username_field.getText().toString().trim();
        String password = password_field.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Snackbar.make(v, "Check Empty Fields Above", Snackbar.LENGTH_LONG).show();
        } else {
            if (Constants.isNetworkConnected(getApplicationContext())) {
                performNetworkRequest(username_field.getText().toString(),
                        password_field.getText().toString());
            } else {
                Snackbar.make(v, "Internet Not Available!", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    private void performNetworkRequest(final String username, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Logging in...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                "https://app.legrooms.com/api/authenticate", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("LOGIN", "Login Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);

                    boolean responseBooleanValue = jObj.getBoolean("success");

                    if (responseBooleanValue) {
                        // user successfully logged in

                        String message = jObj.getString("message");
                        String token = jObj.getString("token");

                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                        //Launch welcome activity
                        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this, jObj.getString("message"), Toast.LENGTH_LONG);
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Log.e("JSONError", e.getMessage());
                    Toast.makeText(getApplicationContext(), "Error with the server!", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Error", "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("email", username);
                params.put("password", password);
                return params;
            }
        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(5 * DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 0));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}