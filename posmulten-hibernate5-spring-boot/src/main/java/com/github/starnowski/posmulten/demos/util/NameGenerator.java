package com.github.starnowski.posmulten.demos.util;

public class NameGenerator {

    public static String generate(int maxLength, String prefix, String... phrases)
    {
        //TODO
        StringBuilder builder = new StringBuilder();
        builder.append(prefix);
        if (phrases != null)
        {
            for (String phrase : phrases)
            {
                builder.append(phrase);
            }
        }
        String generated = builder.toString();
        return generated.length() < maxLength ? generated : generated.substring(0, maxLength);
    }
}
