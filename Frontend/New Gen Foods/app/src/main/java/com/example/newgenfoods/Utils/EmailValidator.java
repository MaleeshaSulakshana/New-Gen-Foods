package com.example.newgenfoods.Utils;

import android.util.Patterns;

public class EmailValidator {

//    Method for validate email
    public static boolean isValidEmail(CharSequence target) {
        return Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }


}
