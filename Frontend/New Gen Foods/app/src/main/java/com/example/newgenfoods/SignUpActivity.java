package com.example.newgenfoods;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.example.newgenfoods.Admin.AdminDashboardActivity;
import com.example.newgenfoods.Api.AuthApi;
import com.example.newgenfoods.Api.UserApi;
import com.example.newgenfoods.Utils.EmailValidator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SignUpActivity extends AppCompatActivity {

    private TextView textSignIn;
    private EditText name, email, dob;
    private Button btnSignUp;
    private ProgressBar loadingPB;

    private Calendar mCalendar = Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener onDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        textSignIn = (TextView) this.findViewById(R.id.textSignIn);

        name = (EditText) this.findViewById(R.id.name);
        email = (EditText) this.findViewById(R.id.email);
        dob = (EditText) this.findViewById(R.id.dob);

        btnSignUp = (Button) this.findViewById(R.id.btnSignUp);

        loadingPB = (ProgressBar) this.findViewById(R.id.idPBLoading);

        dob.setFocusable(false);
        dob.setCursorVisible(false);
        dob.setKeyListener(null);

        onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, month);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

//                Update label
                String mFormat = "yyyy/MM/dd";
                SimpleDateFormat sdf = new SimpleDateFormat(mFormat, Locale.US);
                dob.setText(sdf.format(mCalendar.getTime()));
            }
        };

        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(SignUpActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        onDateSetListener,
                        year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();

            }
        });

        textSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();

            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String nameValue = name.getText().toString();
                String emailValue = email.getText().toString();
                String dobValue = dob.getText().toString();

                if (nameValue.equals("") || emailValue.equals("") || dobValue.equals("")) {
                    Toast.makeText(SignUpActivity.this, "Fields empty!",Toast.LENGTH_SHORT).show();

                } else if (!EmailValidator.isValidEmail(emailValue)) {
                    Toast.makeText(SignUpActivity.this, "Email pattern invalid!", Toast.LENGTH_SHORT).show();

                } else {

                    String URL = UserApi.USER_API;

                    try {
                        loadingPB.setVisibility(View.VISIBLE);

                        RequestQueue requestQueue = Volley.newRequestQueue(SignUpActivity.this);
                        JSONObject jsonBody = new JSONObject();
                        jsonBody.put("email", emailValue);
                        jsonBody.put("name", nameValue);
                        jsonBody.put("dob", dobValue);

                        final String requestBody = jsonBody.toString();

                        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                loadingPB.setVisibility(View.GONE);

                                try {
                                    JSONObject jsonObject = new JSONObject(response);

                                    String status = jsonObject.getString("status");
                                    String msg = jsonObject.getString("msg");

                                    if (status.equals("success")) {
                                        clear();

                                        Intent intent = new Intent(SignUpActivity.this, SignUpActivity.class);
                                        startActivity(intent);
                                        finish();

                                    }

                                    Toast.makeText(SignUpActivity.this, msg, Toast.LENGTH_SHORT).show();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                loadingPB.setVisibility(View.GONE);
                                Toast.makeText(SignUpActivity.this, "Some error occur" + error.toString(), Toast.LENGTH_SHORT).show();
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
        name.setText("");
        email.setText("");
        dob.setText("");
    }


}