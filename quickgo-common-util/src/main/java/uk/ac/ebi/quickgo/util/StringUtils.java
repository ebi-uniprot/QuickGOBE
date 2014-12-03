package uk.ac.ebi.quickgo.util;

public class StringUtils {
    static char[] hex = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * Add hex coded 8 bits of integer (2 hex digits) to stringbuffer
     */
    public static void appendHex8Code(StringBuilder sb, int code) {
        sb.append(hex[(code & 0xf0) >> 4]).append(hex[(code & 0xf)]);
    }

    public static int unhex(char ch) {
        if ((ch >= '0') && (ch <= '9')) {
        	return ch - '0';
        }
        else if ((ch >= 'A') && (ch <= 'F')) {
        	return ch - 'A' + 10;
        }
        else if ((ch >= 'a') && (ch <= 'f')) {
        	return ch - 'a' + 10;
        }
        else {
        	return 0;
        }
    }

    public static byte[] decodeHex(String in) {
        byte[] data = new byte[in.length() / 2];
        for (int i = 0; i < in.length(); i += 2) {
            data[i / 2] = (byte) ((unhex(in.charAt(i)) << 4) + unhex(in.charAt(i + 1)));
        }
        return data;
    }

    public static String encodeHex(byte... data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
        	appendHex8Code(sb, b);
        }
        return sb.toString();
    }

    /**
     * Returns its argument, unless null, in which the second (fallback) argument is returned.
     */
    public static <X> X nvl(X nullable, X fallback) {
        return nullable == null ? fallback : nullable;
    }

    /**
     * Iterates through its arguments and returns the first one that is not null.
     *
     * @param successive list of arguments to search through
     * @return first non-null argument
     */
    @SafeVarargs
	public static <X> X nvl(X... successive) {
        for (X x : successive) {
            if (x != null) {
            	return x;
            }
        }
        return null;
    }
    
    public static boolean parseBoolean(String text, boolean defaultValue) {
        return (text == null) ? defaultValue : "true".equalsIgnoreCase(text);
    }

    public static int parseInt(String text, int defaultValue) {
        try {
            return Integer.parseInt(text);
        }
        catch(NumberFormatException e) {
            return defaultValue;
        }
    }
}
