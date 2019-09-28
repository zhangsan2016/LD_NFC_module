package com.ldgd.ld_nfc_module.util;

/**
 * Created by ldgd on 2019/9/28.
 * 功能：
 * 说明：
 */

public class BytesUtil {

    public static void main(String[] args) {
       byte[] aa = new byte[]{8,0};

        int cc = bytes2Int(aa);
        System.out.println(cc);

    }


    /**
     * byte[] 转化为 int
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
     * int 转化为byte[]
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
}
