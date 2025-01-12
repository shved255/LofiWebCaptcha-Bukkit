package ru.shved255.util;

import java.util.Base64;

public class Utils {

    public String code(String input) {
        if(input == null || input.isEmpty()) {
            return "";
        }
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(input.getBytes());
    }
	
}
