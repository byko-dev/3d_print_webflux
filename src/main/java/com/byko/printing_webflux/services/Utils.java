package com.byko.printing_webflux.services;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static boolean valid(String ...data){
        for (String argument: data)
            if(argument == null || argument.isEmpty() || argument.isBlank())
                return false;
        return true;
    }

    public static String getCurrentDate(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }

    public static boolean emailRegEx(String email){
        Pattern patternMail = Pattern.compile("^([_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*(\\.[a-zA-Z]{1,6}))?$");
        Matcher matcherMail = patternMail.matcher(email);
        return matcherMail.matches();
    }

    public static boolean numberRegEx(String number){
        Pattern patternNumber = Pattern.compile("^(?<!\\w)(\\(?(\\+|00)?48\\)?)?[ -]?\\d{3}[ -]?\\d{3}[ -]?\\d{3}(?!\\w)$");
        Matcher matcherNumber = patternNumber.matcher(number);
        return matcherNumber.matches();
    }

}
