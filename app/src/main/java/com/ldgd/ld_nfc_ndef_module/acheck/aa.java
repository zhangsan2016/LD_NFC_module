package com.ldgd.ld_nfc_ndef_module.acheck;

import java.util.Arrays;

/**
 * Created by ldgd on 2020/9/11.
 * 功能：
 * 说明：
 */

public class aa {
    public static void main(String[] args) {


        byte[] value = {0,3};
        byte[] payload = new byte[100];
        System.arraycopy(value,  0, payload,3, value.length);

        System.out.println(Arrays.toString(payload));
    }
}
