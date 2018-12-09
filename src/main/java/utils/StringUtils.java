package utils;

/**
 * Basic string helper.
 * @author Antoine FORET
 * @version 1.0
 */
public class StringUtils {

    /**
     * Capitalize the given string
     * @param str the string to capitalize
     * @return the capitalized string
     */
    public static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
