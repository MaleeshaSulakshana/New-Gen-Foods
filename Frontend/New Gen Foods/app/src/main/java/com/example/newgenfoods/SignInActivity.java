package com.example.newgenfoods;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.example.newgenfoods.Admin.AdminDashboardActivity;
import com.example.newgenfoods.Api.AuthApi;
import com.example.newgenfoods.Classes.User;
import com.example.newgenfoods.User.UserDashboardActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class SignInActivity extends AppCompatActivity {

    private TextView textSignUp;
    private EditText email, psw;
    private Button btnSignIn;
    private ProgressBar idPbLoading;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private boolean doubleBackToExitPressedOnce = false;
    private BottomSheetDialog exitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        exitDialog = new BottomSheetDialog(SignInActivity.this, R.style.BottomSheetTheme);
        exitDialog.setContentView(R.layout.dialog_box_exit);

        textSignUp = (TextView) this.findViewById(R.id.textSignUp);

        email = (EditText) this.findViewById(R.id.email);
        psw = (EditText) this.findViewById(R.id.psw);

        btnSignIn = (Button) this.findViewById(R.id.btnSignIn);

        idPbLoading = (ProgressBar) this.findViewById(R.id.idPbLoading);

//        For shared preferences
        sharedPreferences = getSharedPreferences("Login", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        textSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
//                finish();

            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String emailValue = email.getText().toString();
                String pswValue = psw.getText().toString();

                if (emailValue.equals("") || pswValue.equals("")) {
                    Toast.makeText(SignInActivity.this, "Fields empty!",Toast.LENGTH_SHORT).show();

                } else {

                    try {
                        String URL = AuthApi.POST_SIGN_IN;
                        idPbLoading.setVisibility(View.VISIBLE);

                        RequestQueue requestQueue = Volley.newRequestQueue(SignInActivity.this);
                        JSONObject jsonBody = new JSONObject();

                        jsonBody.put("email", emailValue);
                        jsonBody.put("psw", pswValue);

                        final String requestBody = jsonBody.toString();

                        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                idPbLoading.setVisibility(View.GONE);

                                try {
                                    JSONObject jsonObject = new JSONObject(response);

                                    String status = jsonObject.getString("status");
                                    String msg = jsonObject.getString("msg");

                                    if (status.equals("success")) {
                                        clear();

                                        String user_type = jsonObject.getString("user_type");
                                        String logged_email = jsonObject.getString("email");

                                        User.LOGGED_USER_EMAIL = logged_email;

                                        if (user_type.equals("user")) {

                                            Intent intent = new Intent(SignInActivity.this, UserDashboardActivity.class);
                                            startActivity(intent);
                                            finish();

                                            editor.putString("email", logged_email );
                                            editor.putString("user_type", user_type );
                                            editor.commit();

                                        } else if (user_type.equals("admin")) {

                                            Intent intent = new Intent(SignInActivity.this, AdminDashboardActivity.class);
                                            startActivity(intent);
                                            finish();

                                            editor.putString("email", logged_email );
                                            editor.putString("user_type", user_type );
                                            editor.commit();

                                        } else {
                                            Toast.makeText(SignInActivity.this, "Some error occur!.", Toast.LENGTH_SHORT).show();
                                        }

                                    }

                                    Toast.makeText(SignInActivity.this, msg, Toast.LENGTH_SHORT).show();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                idPbLoading.setVisibility(View.GONE);
                                Toast.makeText(SignInActivity.this, "Some error occur" + error.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }) {
                            @Override
                            public String getBodyContentType() {
                                return "application/json; charset=utf-8";
                            }

                            @Override
                            public byte[] getBody() throws AuthFailureError {
                                try {
                                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                                } catch (UnsupportedEncodingException uee) {
                                    return null;
                                }
                            }
                        };

                        requestQueue.add(stringRequest);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }
        });

    }

    private void clear() {
        email.setText("");
        psw.setText("");
    }


//    Tap to close app
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        Button btnExitYes, btnExitNo;
        exitDialog.show();

        btnExitYes = (Button) exitDialog.findViewById(R.id.btnYes);
        btnExitYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
                System.exit(0);
            }
        });

        btnExitNo = (Button) exitDialog.findViewById(R.id.btnNo);
        btnExitNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitDialog.dismiss();
            }
        });

    }

}