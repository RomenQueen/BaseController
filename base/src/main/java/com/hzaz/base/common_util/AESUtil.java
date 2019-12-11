package com.hzaz.base.common_util;

import java.io.ByteArrayOutputStream;

public class AESUtil {
    //154410111c5b44d3445b5450145751504415
    private static String HEX_CHAR = "cb4d0b518135c547";

    /**
     * 将字符串编码成16进制数字,适用于所有字符（包括中文）
     *
     * @param str 字符串
     * @return 16进制字符串
     */
    public static String string2Hex(String str) {
        // 根据默认编码获取字节数组
        byte[] bytes = str.getBytes();
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        // 将字节数组中每个字节拆解成2位16进制整数
        for (int i = 0; i < bytes.length; i++) {
            sb.append(HEX_CHAR.charAt((bytes[i] & 0xf0) >> 4));
            sb.append(HEX_CHAR.charAt((bytes[i] & 0x0f) >> 0));
        }
        return sb.toString();
    }

    /**
     * 将16进制数字解码成字符串,适用于所有字符（包括中文）
     *
     * @return 字符串
     */
    public static String hex2String(String hexString) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(hexString.length() / 2);
        // 将每2位16进制整数组装成一个字节
        for (int i = 0; i < hexString.length(); i += 2)
            baos.write((HEX_CHAR.indexOf(hexString.charAt(i)) << 4 | HEX_CHAR.indexOf(hexString.charAt(i + 1))));
        return new String(baos.toByteArray());
    }

    public static byte[] hex2Bytes(String hexString) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(hexString.length() / 2);
        // 将每2位16进制整数组装成一个字节
        for (int i = 0; i < hexString.length(); i += 2)
            baos.write((HEX_CHAR.indexOf(hexString.charAt(i)) << 4 | HEX_CHAR.indexOf(hexString.charAt(i + 1))));
        return baos.toByteArray();
    }

    /**
     * 这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。
     *
     * @param src
     * @return
     */
    public static String bytes2Hex(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv.toUpperCase());
        }
        return stringBuilder.toString();
    }

    /**
     * 把字节数组转为字符串并通过空格隔开
     *
     * @param src
     * @return
     */
    public static String bytes2HexAppendTrim(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv.toUpperCase() + " ");
        }
        return stringBuilder.toString();
    }

    /**
     * 这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。
     *
     * @param src
     * @return
     */
    public static String bytes2Hex(byte[] src, String format) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv.toUpperCase() + format);
        }
        return stringBuilder.toString();
    }

    public static String bytes2Hex(byte src) {
        byte[] bytes = new byte[1];
        bytes[0] = src;
        return bytes2Hex(bytes);
    }

    /**
     * Convert hex string to byte[]
     *
     * @param hexString the hex string
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    /**
     * Convert char to byte
     *
     * @param c char
     * @return byte
     */
    private static byte charToByte(char c) {
        return (byte) HEX_CHAR.indexOf(c);
    }


    public static byte toByte(String hexStr) {
        byte[] bytes = hex2Bytes(hexStr);
        return bytes[0];
    }

    /**
     * 把字符串数字格式123456转为01 02 03 04 05 06的字节数组
     *
     * @param str
     * @return
     */
    public static byte[] toByteArray(String str) {
        byte[] b = new byte[str.length()];
        for (int i = 0; i < b.length; i++) {
            b[i] = Byte.valueOf("" + str.charAt(i));
        }
        return b;
    }

    /**
     * 把double类型的字符串转为Short，double类型对应的值一定是整数
     *
     * @param doubleStr
     * @return
     */
    public static Short DoubleStrtoShort(String doubleStr) {
        if (doubleStr.indexOf(".") != -1) {
            doubleStr = doubleStr.substring(0, doubleStr.indexOf("."));
        }
        Short ss = Short.parseShort(doubleStr);
        return ss;
    }

    ;

    /**
     * 十六进制字符串转为对应的数组字符串，格式010203040506转为123456
     *
     * @param hexStr
     * @return
     */
    public static String hexStrToNumStr(String hexStr) {
        StringBuffer sb = new StringBuffer();
        byte[] hexStr2Bytes = hex2Bytes(hexStr);
        for (byte b : hexStr2Bytes) {
            sb.append(b);
        }
        return sb.toString();
    }

    /**
     * 十六进制字符串转ASCII字符串
     *
     * @param s
     * @return
     */
    public static String hexStrToAsciiStr(String s) {
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "ASCII");
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }

    public static void main(String[] args) {

    }
}
