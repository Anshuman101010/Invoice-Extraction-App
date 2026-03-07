package com.example.incoiceextraction;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FieldExtractor {

    public String extractFields(String text) {

        String invoice = find(text, "(?i)(invoice|document|cash)\\s*(no|number|bill)?\\s*[:]?\\s*[A-Za-z0-9-]+");

        String date = find(text, "\\d{1,2}/\\d{1,2}/\\d{2,4}");

        String total = find(text, "(?i)(total|grand total|round.*total).*?\\d+\\.\\d{2}");

        String result =
                "Invoice/Document Number: " + invoice +
                        "\n\nDate: " + date +
                        "\n\nTotal: " + total;

        return result;
    }

    private String find(String text, String patternString) {

        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return matcher.group();
        }

        return "Not Found";
    }
}