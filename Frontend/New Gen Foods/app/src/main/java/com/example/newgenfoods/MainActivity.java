package com.example.newgenfoods;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.example.newgenfoods.Admin.AdminDashboardActivity;
import com.example.newgenfoods.Classes.User;
import com.example.newgenfoods.User.UserDashboardActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnStart;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private boolean doubleBackToExitPressedOnce = false;
    private BottomSheetDialog exitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        exitDialog = new BottomSheetDialog(MainActivity.this, R.style.BottomSheetTheme);
        exitDialog.setContentView(R.layout.dialog_box_exit);

        btnStart = this.findViewById(R.id.btnStart);

//        For shared preferences
        sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = sharedPreferences.getString("email", "");
                String user_type = sharedPreferences.getString("user_type", "");

                if (!email.equals("") && !user_type.equals("")) {

                    if (user_type.equals("user")) {

                        User.LOGGED_USER_EMAIL = email;

                        Intent intent = new Intent(MainActivity.this, UserDashboardActivity.class);
                        startActivity(intent);
                        finish();

                    } else if (user_type.equals("admin")) {

                        User.LOGGED_USER_EMAIL = email;

                        Intent intent = new Intent(MainActivity.this, AdminDashboardActivity.class);
                        startActivity(intent);
                        finish();

                    } else {

                        User.LOGGED_USER_EMAIL = "";

                        editor.clear();
                        editor.apply();

                        Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                        startActivity(intent);
                        finish();

                    }

                } else {

                    User.LOGGED_USER_EMAIL = "";

                    Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                    startActivity(intent);
                    finish();

                }

            }
        });

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