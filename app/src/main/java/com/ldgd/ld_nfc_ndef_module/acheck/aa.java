package com.ldgd.ld_nfc_ndef_module.acheck;

import com.ldgd.ld_nfc_ndef_module.entity.DataDictionaries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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


        List<DataDictionaries> ddlist = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            DataDictionaries dd = new DataDictionaries();
            dd.setName(i+"");
            ddlist.add(dd);
        }

        for(DataDictionaries d : ddlist){
            d.setName("zhangsan");
        }

        for (int i = 0; i < ddlist.size(); i++) {
            System.out.println(ddlist.get(i).getName());
        }


    }
}
