package com.ldgd.ld_nfc_module.util;

/**
 * Created by ldgd on 2019/9/28.
 * 功能：
 * 说明：
 */

public class BytesUtil {

    public static void main(String[] args) {
        byte[] aa = new byte[]{0, 1};

        int cc = bytesInt(aa);
        System.out.println(cc);

    }


    /**
     * int 转化为byte[]
     *
     * @param id
     * @return
     */
    public static byte[] int2Bytes(int id) {

		/*
         * byte[] arr = new byte[4];
		 * arr[0] = (byte) ((id >> 0 * 8) & 0xff);
		 * arr[1] = (byte) ((id >> 1 * 8) & 0xff);
		 * arr[2] = (byte) ((id >> 2 *8) & 0xff);
		 * arr[3] = (byte) ((id >> 3 * 8) & 0xff);
		 */

        byte[] arr = new byte[4];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = (byte) ((id >> i * 8) & 0xff);
        }
        return arr;
    }

    /**
     * byte[] 转化为 int
     *
     * @return
     */
    public static int bytes2Int(byte[] arr) {

		/*int rs0 = (int) ((arr[0] & 0xff) << 0 * 8);
        int rs1 = (int) ((arr[1] & 0xff) << 1 * 8);
		int rs2 = (int) ((arr[2] & 0xff) << 2 * 8);
		int rs3 = (int) ((arr[3] & 0xff) << 3 * 8);*/


        int rs = 0;
        for (int i = 0; i < arr.length; i++) {
            rs += (int) ((arr[i] & 0xff) << i * 8);
        }

        return rs;
    }


    //高位在前，低位在后
    public static byte[] intBytes(int num) {
        byte[] result = new byte[4];
        result[0] = (byte) ((num >>> 24) & 0xff);//说明一
        result[1] = (byte) ((num >>> 16) & 0xff);
        result[2] = (byte) ((num >>> 8) & 0xff);
        result[3] = (byte) ((num >>> 0) & 0xff);
        return result;
    }

    //高位在前，低位在后
    public static int bytesInt(byte[] bytes) {
        int result = 0;
        if (bytes.length == 4) {
            int a = (bytes[0] & 0xff) << 24;//说明二
            int b = (bytes[1] & 0xff) << 16;
            int c = (bytes[2] & 0xff) << 8;
            int d = (bytes[3] & 0xff);
            result = a | b | c | d;
        }
        return result;
    }


    //高位在前，低位在后，区分byte长度
    public static int bytesIntHL(byte[] bytes) {
        int result = 0;
        if (bytes.length == 4) {
            int a = (bytes[0] & 0xff) << 24;//说明二
            int b = (bytes[1] & 0xff) << 16;
            int c = (bytes[2] & 0xff) << 8;
            int d = (bytes[3] & 0xff);
            result = a | b | c | d;
        }else if(bytes.length == 2){
            int a = (bytes[0] & 0xff) <<  8;
            int b = (bytes[1] & 0xff);
            result = a | b;
        }
        return result;
    }



}
