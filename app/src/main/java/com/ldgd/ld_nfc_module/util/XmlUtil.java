package com.ldgd.ld_nfc_module.util;

import android.content.Context;

import com.ldgd.ld_nfc_module.entity.DataDictionaries;
import com.ldgd.ld_nfc_module.entity.XmlData;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.kxml2.io.KXmlSerializer;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jxl.Sheet;
import jxl.Workbook;

/**
 * Created by ldgd on 2019/10/7.
 * 功能：
 * 说明：xml解析工具
 */

public class XmlUtil {


    public static File parseBytesToXml(byte[] mBuffer, String excelName, Context context) {

        InputStream is = null;
        try {
            // 1.获取assets包中的 Excel 文件，得到字典格式
            is = context.getAssets().open(excelName);
            Workbook book = Workbook.getWorkbook(is);
            book.getNumberOfSheets();
            Sheet sheet = book.getSheet(0);
            int Rows = sheet.getRows();

            List<DataDictionaries> dataDictionaries = new ArrayList<>();
            for (int i = 1; i < Rows; ++i) {

                DataDictionaries dictionaries = new DataDictionaries();

                String name = (sheet.getCell(0, i)).getContents();
                int startAddress = Integer.parseInt((sheet.getCell(1, i)).getContents());
                int endAddress = Integer.parseInt((sheet.getCell(2, i)).getContents());
                int takeByte = Integer.parseInt((sheet.getCell(3, i)).getContents()); // 占用字节
                String format = (sheet.getCell(4, i)).getContents(); // 格式
                String units = (sheet.getCell(5, i)).getContents();  // 单位
                int factor = Integer.parseInt((sheet.getCell(6, i)).getContents());  // 系数
                String operator = (sheet.getCell(7, i)).getContents();  // 运算符
                String permission = (sheet.getCell(8, i)).getContents();  // 权限
                String convertFormat = (sheet.getCell(9, i)).getContents();  // 转换格式

                dictionaries.setName(name.trim());
                dictionaries.setStartAddress(startAddress);
                dictionaries.setEndAddress(endAddress);
                dictionaries.setTakeByte(takeByte);
                dictionaries.setFormat(format.trim());
                dictionaries.setUnits(units.trim());
                dictionaries.setFactor(factor);
                dictionaries.setOperator(operator.trim());
                dictionaries.setPermission(permission.trim());
                dictionaries.setConvertFormat(convertFormat.trim());

                dataDictionaries.add(dictionaries);

                //  System.out.println("第" + i + "行数据=" + name + "," + startAddress + "," + endAddress + "," + takeByte + "," + format + "," + units+ "," + factor + "," + operator + "," + Permission );
            }
            book.close();


            // 2.根据字典格式解析数据
            ArrayList<XmlData> xmlDataList = new ArrayList<>();
            String value = null;
            for (DataDictionaries dictionaries : dataDictionaries) {

                byte[] byteData = new byte[dictionaries.getTakeByte()];
                System.arraycopy(mBuffer, dictionaries.getStartAddress(), byteData, 0, dictionaries.getTakeByte());

                LogUtil.e("xxx " + dictionaries.getName() + "   = " + Arrays.toString(byteData));


                if (byteData.length > 0) {

                    XmlData xmlData = new XmlData();
                    xmlData.setName(dictionaries.getName());


                    // 获取显示类型
                    if (dictionaries.getFormat().equals("HEX")) {
                        int transitionValue = BytesUtil.bytesIntHL(byteData);
                        value = Integer.toHexString(transitionValue);
                    } else if (dictionaries.getFormat().equals("STR")) {

                        StringBuffer sb = new StringBuffer();
                        for (int i = 0; i < byteData.length; i++) {
                            String str = new String(new byte[]{byteData[i]},"utf-8");
                            sb.append(str + " ");
                        }
                       if( BytesUtil.isMessyCode(sb.toString())){
                           value = "0";
                       }else{
                           value = sb.toString();
                       }



                    } else if (dictionaries.getFormat().equals("DEC")) {
                        // 长度为1直接赋值
                        if (dictionaries.getTakeByte() != 1) {
                            // 获取转换格式
                            if (dictionaries.getConvertFormat().equals("HL")) {
                                // 高低位转换
                                int transitionValue = BytesUtil.bytesIntHL(byteData);
                                // 拿到系数
                                int factor = dictionaries.getFactor();
                                if (factor != 0) {
                                    if (dictionaries.getOperator().trim().equals("/")) {
                                        int factorValue = transitionValue / factor;
                                        value = factorValue + "";
                                    } else if (dictionaries.getOperator().trim().equals("+")) {
                                        int factorValue = transitionValue + factor;
                                        value = factorValue + "";
                                    } else if (dictionaries.getOperator().trim().equals("-")) {
                                        int factorValue = transitionValue - factor;
                                        value = factorValue + "";
                                    } else if (dictionaries.getOperator().trim().equals("*")) {
                                        int factorValue = transitionValue * factor;
                                        value = factorValue + "";
                                    }
                                } else {
                                    value = transitionValue + "";
                                }
                            }
                        } else {
                            value = byteData[0] + "";
                        }


                    }

                    // 单位
                     value = value + "(" + dictionaries.getUnits() + ")";
                }

                XmlData xmlData = new XmlData();
                xmlData.setName(dictionaries.getName());
                xmlData.setValue(value);
                xmlDataList.add(xmlData);

            }

            // 3.转换成xml文件并保存
            File cacheFile = createXML(xmlDataList, new File(context.getCacheDir(), "NfcDataCache.xml"));

            // 4.返回xml文件地址
            return cacheFile;

        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("xxx Exception = " + e.getMessage().toString());
            return null;
        }

    }

    private static File createXML(List<XmlData> xmlDataList, File file) throws Exception {

        // 文件存在先删除
        if (file.exists()) {
            file.delete();
        }

        // 采用pull解析进行实现
        // 目标文件
        // File file = new File(filePath);
        // 获得xml序列化实例
        XmlSerializer serializer = new KXmlSerializer();
        // 文件写入流实例
        FileOutputStream fos = null;
        // 根据文件对象创建一个文件的输出流对象
        fos = new FileOutputStream(file);
        // 设置输出的流及编码
        serializer.setOutput(fos, "utf-8");
        // 设置文件的开始
        serializer.startDocument("UTF-8", true);
        // 设置文件开始标签
        serializer.startTag(null, "当前读取信息");
        for (XmlData xmlData : xmlDataList) {

            // 设置标签
            serializer.startTag(null, xmlData.getName());
            serializer.text(xmlData.getValue());
            serializer.endTag(null, xmlData.getName());

        }

        // 设置文件结束标签
        serializer.endTag(null, "当前读取信息");
        // 文件的结束
        serializer.endDocument();

        serializer.flush();
        fos.close();
        return file;

    }


    public static String formatXml(String str) throws Exception {
        Document document = null;
        document = DocumentHelper.parseText(str);
        // 格式化输出格式
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("utf-8");
        StringWriter writer = new StringWriter();
        // 格式化输出流
        XMLWriter xmlWriter = new XMLWriter(writer, format);
        // 将document写入到输出流
        xmlWriter.write(document);
        xmlWriter.close();

        return writer.toString();
    }


}
