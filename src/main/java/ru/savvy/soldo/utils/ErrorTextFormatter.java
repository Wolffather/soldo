package ru.savvy.soldo.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ErrorTextFormatter {
    public static String formatError(String rawError) {
        StringBuilder formatted = new StringBuilder("Ошибки:\n");

        Pattern pattern = Pattern.compile("default message \\[(.*?)]");

        Matcher matcher = pattern.matcher(rawError);

        while (matcher.find()) {
            String message = matcher.group(1);

            formatted.append(message).append("\n");
        }
        return formatted.toString();
    }


}
