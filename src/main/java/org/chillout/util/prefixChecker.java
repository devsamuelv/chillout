package org.chillout.util;

public class prefixChecker {
    private static String prefix = "!!";

    public static boolean check(String message, String command) {
        if (message.startsWith(prefix) && message.split(" ")[0] == prefix + command) {
            return true;
        }

        return false;
    }
}
