package com.example.utils;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RandomFourUtil {
    private Random random = new Random();
    public String GetFour() {
        String four = "";
        for (int i = 0; i < 4; i++){
            four =four + random.nextInt(10);
        }
        return four;

        //httpSession.setAttribute(phone, key);
    }
}
