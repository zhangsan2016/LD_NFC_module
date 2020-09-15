package com.ldgd.ld_nfc_ndef_module.acheck;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Arrays;

import static com.ldgd.ld_nfc_ndef_module.util.BytesUtil.hexToByteArray;

/**
 * Created by ldgd on 2020/9/11.
 * 功能：
 * 说明：
 */

public class aa {
    public static void main(String[] args) throws UnsupportedEncodingException {

     /*    byte[] b = {1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 1, 0, 0, 0};
         byte[]  dd ={48, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0};
        byte[] cc ={48, 0, 0, 0, 0, 0, 0, -95, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 79, 0, 89, 0, 0, 0};


        String hex = new BigInteger(1, cc).toString(16);

        System.out.println(hex);



        System.out.println(Arrays.toString(hex.getBytes()));

        char[] c = hex.toCharArray();

        byte[] data = new byte[c.length];
        for (int i = 0; i < c.length; i++) {
            String ccccc = c[i] +"";
            data[i] = (byte) Integer.parseInt(ccccc);
        }

        System.out.println(data.length);
        System.out.println(Arrays.toString(data));*/

        /*for (int i = 0; i < data.length; i++) {
            System.out.println(Arrays.toString(charToByte(c[i])));
        }
*/



        byte[]  dd ={1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 1};
        String hex = new BigInteger(1, dd).toString(16);

        System.out.println(hex);

        System.out.println(Arrays.toString(hex.getBytes()));


        System.out.println(bytesToHex(dd));
        System.out.println(Arrays.toString(hexToByteArray(bytesToHex(dd))));


        System.out.println(bytesToHex(new byte[]{0,3}));

    }



    /**
     * 字节数组转16进制
     * @param bytes 需要转换的byte数组
     * @return  转换后的Hex字符串
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if(hex.length() < 2){
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }



}
