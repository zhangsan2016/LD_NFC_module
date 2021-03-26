package com.ldgd.ld_nfc_ndef_module.acheck;

import com.ldgd.ld_nfc_ndef_module.util.BytesUtil;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

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



   /*     byte[]  dd ={1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 1};
        String hex = new BigInteger(1, dd).toString(16);

        System.out.println(hex);

        System.out.println(Arrays.toString(hex.getBytes()));


        System.out.println(bytesToHex(dd));
        System.out.println(Arrays.toString(hexToByteArray(bytesToHex(dd))));


        System.out.println(bytesToHex(new byte[]{0,3}));*/


        byte[] mBuffer = {0, 3, 66, 77, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, -104, 0, 0, 1, 18, 20, 100};
        byte[] typeByte = new byte[2];
        System.arraycopy(mBuffer,29, typeByte, 0, 2);
                /*typeByte[0] = 0;
                typeByte[1] = 3;*/
        System.out.println("xxxxxxxxx " + Arrays.toString(typeByte) +"  "+  BytesUtil.bytesIntHL(typeByte) );

        String aa = "4654564654654465";
        if(aa.contains("54465")){

            System.out.println("aa = " + true);
        }else{
            System.out.println("aa = " + false);
        }


    }



    public static String bytesToHex(byte[] bytes){
        StringBuffer stringBuffer = new StringBuffer();
        String temp = null;
        for (int i=0;i<bytes.length;i++){
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length()==1){
                //1得到一位的进行补0操作
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }

    /**
     * hex字符串转byte数组
     * @param inHex 待转换的Hex字符串
     * @return  转换后的byte数组结果
     */
    public static byte[] hexToByteArray(String inHex){
        int hexlen = inHex.length();
        byte[] result;
        if (hexlen % 2 == 1){
            //奇数
            hexlen++;
            result = new byte[(hexlen/2)];
            inHex="0"+inHex;
        }else {
            //偶数
            result = new byte[(hexlen/2)];
        }
        int j=0;
        for (int i = 0; i < hexlen; i+=2){
            result[j]=(byte)Integer.parseInt((inHex.substring(i,i+2)),16);
            j++;
        }
        return result;
    }



}
