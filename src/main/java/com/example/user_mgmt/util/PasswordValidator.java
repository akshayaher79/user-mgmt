package com.example.user_mgmt.util;

import java.util.regex.Pattern;

public class PasswordValidator {
    private static final Pattern P = Pattern.compile("^(?=.{8,})(?=.*[0-9!@#$%^&*()_+\\-={}\\[\\]|:;\"'<>,.?/]).*$");

    public static boolean isValid(String pwd) {
        if (pwd == null) return false;
        return P.matcher(pwd).matches();
    }
}
