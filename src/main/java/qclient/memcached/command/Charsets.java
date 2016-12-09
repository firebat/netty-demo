package qclient.memcached.command;

import java.io.UnsupportedEncodingException;


public class Charsets {
    public static final String UTF_8 = "UTF-8";
    public static final String ASCII = "US-ASCII";

    public static byte[] encode(String value, String charset) {
        try {
            return value.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String decode(byte[] value, String charset) {
        try {
            return new String(value, charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
