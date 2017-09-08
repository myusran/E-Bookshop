package com.emotech.e_bookshop.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import com.emotech.e_bookshop.Internet.AppController;
import com.emotech.e_bookshop.Internet.SQLiteHandler;
import com.emotech.e_bookshop.Internet.SessionManager;
import com.emotech.e_bookshop.Internet.UrlConfig;
import com.emotech.e_bookshop.R;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends Activity {

    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnregis, btnback;
    private EditText username, firstname, lastname, phone, email, password, password2;
    private TextView matchtext;

    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = (EditText) findViewById(R.id.username);
        firstname = (EditText) findViewById(R.id.firstname);
        lastname = (EditText) findViewById(R.id.lastname);
        phone = (EditText) findViewById(R.id.phonenumber);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        password2 = (EditText) findViewById(R.id.password2);
        matchtext = (TextView) findViewById(R.id.matchText);

        btnregis = (Button) findViewById(R.id.btnregis);
        btnback = (Button) findViewById(R.id.btnLinkToLoginScreen);

        matchtext.setVisibility(View.INVISIBLE);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        btnback.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        btnregis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strname = username.getText().toString().trim();
                String strfirst = firstname.getText().toString().trim();
                String strlast = lastname.getText().toString().trim();
                String stremail = email.getText().toString().trim();
                String strphone = phone.getText().toString().trim();
                String strpassword = password.getText().toString().trim();
                String strpassword2 = password2.getText().toString().trim();

                if (!strname.isEmpty() && !stremail.isEmpty() && !strpassword.isEmpty()) {

                    if(!strpassword.equals(strpassword2)) {
                        Toast.makeText(getApplicationContext(),
                                "There's Something Worng With The Password!", Toast.LENGTH_LONG)
                                .show();
                    }else {
                        strpassword = MD5(strpassword);
                        registerUser(strname, strfirst, strlast, stremail, strphone, strpassword);
                    }

                }else{
                    Toast.makeText(getApplicationContext(),
                            "Please enter your details!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        password2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String strpassword = password.getText().toString();
                String strPass2 = password2.getText().toString();

                if (strpassword.equals(strPass2)) {
                    password2.setBackgroundResource(R.drawable.match_password);
                    matchtext.setVisibility(View.INVISIBLE);
                } else {
                    password2.setBackgroundResource(R.drawable.wrong_password);
                    matchtext.setVisibility(View.VISIBLE);
                }
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String strpassword = password.getText().toString();
                String strPass2 = password2.getText().toString();

                if(strPass2.length() > 0){
                    if (strpassword.equals(strPass2)) {
                        password2.setBackgroundResource(R.drawable.match_password);
                        matchtext.setVisibility(View.INVISIBLE);
                    } else {
                        password2.setBackgroundResource(R.drawable.wrong_password);
                        matchtext.setVisibility(View.VISIBLE);
                    }
                }

            }
        });
    }

    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     */
    private void registerUser(final String username, final String firstname, final String lastname, final String email, final String phone,
                              final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Registering ...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST,
                UrlConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite

                        JSONObject user = jObj.getJSONObject("user");
                        String id = user.getString("id");
                        String username = user.getString("username");
                        String first = user.getString("firstname");
                        String last = user.getString("lastname");
                        String email = user.getString("email");
                        String phone = user.getString("phone");

                        //Inserting row in users table
                        db.addUser(id, username, first, last, email, phone);

                        Toast.makeText(getApplicationContext(), "User successfully registered. Try login now!", Toast.LENGTH_LONG).show();

                        // Launch login activity
                        Intent intent = new Intent(
                                RegisterActivity.this,
                                LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", username);
                params.put("first", firstname);
                params.put("last", lastname);
                params.put("email", email);
                params.put("phone", phone);
                params.put("password", password);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public String MD5(String pass){
        try{
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(pass.getBytes());
            StringBuffer sb = new StringBuffer();
            for(int i=0; i<array.length; i++){
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString().toLowerCase();
        }catch (java.security.NoSuchAlgorithmException e){

        }
        return null;
    }

    @Override
    public void onBackPressed() {

        finish();
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
