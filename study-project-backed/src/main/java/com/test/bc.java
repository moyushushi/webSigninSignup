package com.test;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class bc {
    public static void main(String[] args) {
        BCryptPasswordEncoder a = new BCryptPasswordEncoder();
        System.out.println(a.encode("123456"));
    }
}

