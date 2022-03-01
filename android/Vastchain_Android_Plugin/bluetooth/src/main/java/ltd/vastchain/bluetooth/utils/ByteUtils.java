package ltd.vastchain.bluetooth.utils;

import java.io.UnsupportedEncodingException;

/**
 * Created by admin on 2022/1/15.
 */
public class ByteUtils {

    public static byte[] toBytes(String content) throws UnsupportedEncodingException {
        return  content.getBytes("gbk");
    }
}
