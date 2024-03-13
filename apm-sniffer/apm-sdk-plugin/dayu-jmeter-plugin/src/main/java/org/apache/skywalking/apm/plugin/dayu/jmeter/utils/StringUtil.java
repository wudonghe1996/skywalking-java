package org.apache.skywalking.apm.plugin.dayu.jmeter.utils;

public class StringUtil {

    public static String join(final char delimiter, final String... strings) {
        if (strings.length == 0) {
            return null;
        }
        if (strings.length == 1) {
            return strings[0];
        }
        int length = strings.length - 1;
        for (final String s : strings) {
            if (s == null) {
                continue;
            }
            length += s.length();
        }
        final StringBuilder sb = new StringBuilder(length);
        if (strings[0] != null) {
            sb.append(strings[0]);
        }
        for (int i = 1; i < strings.length; ++i) {
            if (strings[i] != null || strings[i].length() != 0) {
                sb.append(delimiter).append(strings[i]);
            } else {
                sb.append(delimiter);
            }
        }
        return sb.toString();
    }
}
