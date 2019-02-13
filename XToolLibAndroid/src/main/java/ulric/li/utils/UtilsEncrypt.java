package ulric.li.utils;

import java.io.ByteArrayOutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.Key;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import android.text.TextUtils;

public class UtilsEncrypt {
    private static final String VALUE_STRING_BASE64_ENCODE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    private static final int VALUE_INT_ARRAY_BASE64_DECODE[] = {
            -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60,
            61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
            11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1,
            -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38,
            39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1,
            -1
    };

    private static String sKey = "ulric.li";

    public static void init(String strKey) {
        sKey = strKey;
    }

    public static byte[] encryptByAES(byte[] buffer, String strKey) {
        return encryptWithAlgorithm(buffer, TextUtils.isEmpty(strKey) ? sKey.getBytes() : strKey.getBytes(), "AES", "AES/ECB/PKCS5Padding");
    }

    public static byte[] decryptByAES(byte[] buffer, String strKey) {
        return decryptWithAlgorithm(buffer, TextUtils.isEmpty(strKey) ? sKey.getBytes() : strKey.getBytes(), "AES", "AES/ECB/PKCS5Padding");
    }

    public static byte[] encryptByBlowFish(byte[] buffer, String strKey) {
        return encryptWithAlgorithm(buffer, TextUtils.isEmpty(strKey) ? sKey.getBytes() : strKey.getBytes(), "BlowFish", "BlowFish/ECB/PKCS5Padding");
    }

    public static byte[] decryptByBlowFish(byte[] buffer, String strKey) {
        return decryptWithAlgorithm(buffer, TextUtils.isEmpty(strKey) ? sKey.getBytes() : strKey.getBytes(), "BlowFish", "BlowFish/ECB/PKCS5Padding");
    }

    public static byte[] encryptByDES(byte[] buffer, String strKey) {
        return encryptWithAlgorithm(buffer, TextUtils.isEmpty(strKey) ? sKey.getBytes() : strKey.getBytes(), "DES", "DES/ECB/PKCS5Padding");
    }

    public static byte[] decryptByDES(byte[] buffer, String strKey) {
        return decryptWithAlgorithm(buffer, TextUtils.isEmpty(strKey) ? sKey.getBytes() : strKey.getBytes(), "DES", "DES/ECB/PKCS5Padding");
    }

    public static String encryptByMD5(String strBuffer) {
        if (TextUtils.isEmpty(strBuffer))
            return null;

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(strBuffer.getBytes());
            return byteToHexString(md.digest());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String encryptBySHA(String strBuffer) {
        if (TextUtils.isEmpty(strBuffer))
            return null;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA");
            md.update(strBuffer.getBytes());
            return byteToHexString(md.digest());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String encryptByHmacSHA256(String strBuffer, String strKey) {
        if (TextUtils.isEmpty(strBuffer))
            return null;

        try {
            SecretKeySpec key = new SecretKeySpec(TextUtils.isEmpty(strKey) ? sKey.getBytes() : strKey.getBytes(), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(key);
            return byteToHexString(mac.doFinal(strBuffer.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String urlEncode(String strURL) {
        if (TextUtils.isEmpty(strURL))
            return null;

        try {
            return URLEncoder.encode(strURL, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String urlDecode(String strURL) {
        if (TextUtils.isEmpty(strURL))
            return null;

        try {
            return URLDecoder.decode(strURL, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String encodeByBase64(String strBuffer) {
        return new String(_encodeByBase64(strBuffer.getBytes()));
    }

    public static String decodeByBase64(String strBuffer) {
        return new String(_decodeByBase64(strBuffer.getBytes()));
    }

    public static String byteToHexString(byte[] buffer) {
        if (null == buffer)
            return null;

        StringBuffer sb = new StringBuffer();
        for (int nIndex = 0; nIndex < buffer.length; nIndex++) {
            String strHex = Integer.toHexString(0xFF & buffer[nIndex]);
            if (strHex.length() == 1)
                sb.append('0');
            sb.append(strHex);
        }

        return sb.toString();
    }

    public static byte[] stringHexToByte(String strBuffer) {
        if (TextUtils.isEmpty(strBuffer))
            return null;

        if (strBuffer.length() % 2 == 1)
            return null;

        byte[] buffer = new byte[strBuffer.length() / 2];
        for (int nIndex = 0; nIndex < strBuffer.length(); nIndex += 2) {
            buffer[nIndex / 2] = Integer.decode("0x" + strBuffer.substring(nIndex, nIndex + 2)).byteValue();
        }

        return buffer;
    }

    private static byte[] encryptWithAlgorithm(byte[] buffer, byte[] bufferKey, String strAlgorithm, String strTransformation) {
        if (null == buffer || null == bufferKey || TextUtils.isEmpty(strAlgorithm) || TextUtils.isEmpty(strTransformation))
            return null;

        try {
            Key key = new SecretKeySpec(bufferKey, strAlgorithm);
            Cipher cipher = Cipher.getInstance(strTransformation);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(buffer);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static byte[] decryptWithAlgorithm(byte[] buffer, byte[] bufferKey, String strAlgorithm, String strTransformation) {
        if (null == buffer || null == bufferKey || TextUtils.isEmpty(strAlgorithm) || TextUtils.isEmpty(strTransformation))
            return null;

        try {
            Key key = new SecretKeySpec(bufferKey, strAlgorithm);
            Cipher cipher = Cipher.getInstance(strTransformation);
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(buffer);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static byte[] _encodeByBase64(byte[] buffer) {
        if (null == buffer)
            return null;

        int nIndex = 0;
        int nLength = buffer.length;
        StringBuffer sb = new StringBuffer();
        while (nIndex < nLength) {
            int nByte1 = buffer[nIndex++] & 0xff;
            if (nIndex == nLength) {
                sb.append(VALUE_STRING_BASE64_ENCODE.charAt(nByte1 >> 2));
                sb.append(VALUE_STRING_BASE64_ENCODE.charAt((nByte1 & 0x3) << 4));
                sb.append("==");
                break;
            }

            int nByte2 = buffer[nIndex++];
            if (nIndex == nLength) {
                sb.append(VALUE_STRING_BASE64_ENCODE.charAt(nByte1 >> 2));
                sb.append(VALUE_STRING_BASE64_ENCODE.charAt(((nByte1 & 0x3) << 4)
                        | ((nByte2 & 0xF0) >> 4)));
                sb.append(VALUE_STRING_BASE64_ENCODE.charAt((nByte2 & 0xF) << 2));
                sb.append("=");
                break;
            }

            int nByte3 = buffer[nIndex++];
            sb.append(VALUE_STRING_BASE64_ENCODE.charAt(nByte1 >> 2));
            sb.append(VALUE_STRING_BASE64_ENCODE.charAt(((nByte1 & 0x3) << 4)
                    | ((nByte2 & 0xF0) >> 4)));
            sb.append(VALUE_STRING_BASE64_ENCODE.charAt(((nByte2 & 0xF) << 2)
                    | ((nByte3 & 0xC0) >> 6)));
            sb.append(VALUE_STRING_BASE64_ENCODE.charAt(nByte3 & 0x3F));
        }

        return sb.toString().getBytes();
    }

    private static byte[] _decodeByBase64(byte[] buffer) {
        if (null == buffer)
            return null;

        int nIndex = 0;
        int nLength = buffer.length;
        int nByte1, nByte2, nByte3, nByte4;
        ByteArrayOutputStream baos = new ByteArrayOutputStream(nLength);
        while (nIndex < nLength) {
            do {
                nByte1 = VALUE_INT_ARRAY_BASE64_DECODE[buffer[nIndex++]];
            } while (nIndex < nLength && nByte1 == -1);
            if (nByte1 == -1)
                break;

            do {
                nByte2 = VALUE_INT_ARRAY_BASE64_DECODE[buffer[nIndex++]];
            } while (nIndex < nLength && nByte2 == -1);
            if (nByte2 == -1)
                break;

            baos.write((int) ((nByte1 << 2) | ((nByte2 & 0x30) >>> 4)));

            do {
                nByte3 = buffer[nIndex++];
                if (nByte3 == 61)
                    return baos.toByteArray();

                nByte3 = VALUE_INT_ARRAY_BASE64_DECODE[nByte3];
            } while (nIndex < nLength && nByte3 == -1);
            if (nByte3 == -1)
                break;

            baos.write((int) (((nByte2 & 0x0f) << 4) | ((nByte3 & 0x3c) >>> 2)));

            do {
                nByte4 = buffer[nIndex++];
                if (nByte4 == 61)
                    return baos.toByteArray();

                nByte4 = VALUE_INT_ARRAY_BASE64_DECODE[nByte4];
            } while (nIndex < nLength && nByte4 == -1);
            if (nByte4 == -1)
                break;

            baos.write((int) (((nByte3 & 0x03) << 6) | nByte4));
        }

        return baos.toByteArray();
    }
}
