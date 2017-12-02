package kiolk.com.github.mylibrary.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static kiolk.com.github.mylibrary.utils.ConstantsUtil.EMPTY_STRING;

public class MD5Util {

    public static String getHashString(String pString) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(pString.getBytes());
            byte[] byteArray = md.digest();
            StringBuffer buffer = new StringBuffer();

            for (int i = 0; i < byteArray.length; ++i) {
                buffer.append(Integer.toHexString(0xFF & byteArray[i]));
            }

            return buffer.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return EMPTY_STRING;
    }
}
