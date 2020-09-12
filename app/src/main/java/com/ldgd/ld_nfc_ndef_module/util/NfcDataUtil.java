package com.ldgd.ld_nfc_ndef_module.util;

import android.content.Context;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.widget.Toast;

import com.ldgd.ld_nfc_ndef_module.crc.CRC16;
import com.ldgd.ld_nfc_ndef_module.entity.DataDictionaries;
import com.ldgd.ld_nfc_ndef_module.entity.NfcDeviceInfo;
import com.ldgd.ld_nfc_ndef_module.entity.XmlData;
import com.st.st25sdk.STException;
import com.st.st25sdk.type5.st25dv.ST25DVTag;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.kxml2.io.KXmlParser;
import org.kxml2.io.KXmlSerializer;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

/**
 * Created by ldgd on 2019/10/7.
 * 功能：
 * 说明：xml解析工具
 */

public class NfcDataUtil {
    // xml 最末尾位置
    private static int finalPosition = 0;
    public static List<DataDictionaries> dataDictionaries = null;


    /**
     * 将当前byte数组解析成xml文件
     *
     * @param mBuffer
     * @param excelName    析的文件类型
     * @param saveFileName 保存的文件名
     * @param context
     * @return
     */
    public static File parseBytesToXml(byte[] mBuffer, String excelName, String saveFileName, Context context) {


        //   System.out.println("NfcDataUtil data = " + Arrays.toString(mBuffer));

        try {

            // 1.获取assets包中的 Excel 文件，得到字典格式
            // 解析excel
            dataDictionaries = parseExcel(excelName, context);

            // 2.根据字典格式解析数据
            ArrayList<XmlData> xmlDataList = parseBuffer(mBuffer, dataDictionaries);

            // 3.转换成xml文件并保存
            File cacheFile = createXML(xmlDataList, new File(context.getCacheDir(), saveFileName));

            // crc 校验
            // checkCRC(mBuffer,context);

            // 4.返回xml文件地址
            return cacheFile;

        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("xxx Exception = " + e.getMessage().toString());
            return null;
        }

    }

    private static void checkCRC(byte[] mBuffer, Context context) {
        byte[] getCrc = Arrays.copyOfRange(mBuffer, 5, finalPosition + 1);
        int crc1 = CRC16.calcCrc16(getCrc);
        byte[] getCrc2 = Arrays.copyOfRange(mBuffer, 3, 5);
        int crc2 = BytesUtil.bytesIntHL(getCrc2);
        if (crc1 == crc2) {
            showToast("当前 CRC 校验成功", context);
        } else {
            showToast("当前 CRC 校验错误，数据禁止使用", context);
        }
    }

    private static Toast toast;

    private static void showToast(final String msg, Context context) {

        if (toast == null) {
            toast = Toast.makeText(context, "", Toast.LENGTH_LONG);
            toast.setText(msg);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }


    private static ArrayList<XmlData> parseBuffer(byte[] mBuffer, List<DataDictionaries> dataDictionaries) throws UnsupportedEncodingException {
        ArrayList<XmlData> xmlDataList = new ArrayList<>();
        String value = null;
        for (DataDictionaries dictionaries : dataDictionaries) {

            // 字段的结束位置
            finalPosition = dictionaries.getEndAddress();

            byte[] byteData = new byte[dictionaries.getTakeByte()];
            System.arraycopy(mBuffer, dictionaries.getStartAddress(), byteData, 0, dictionaries.getTakeByte());
            dictionaries.setValue(byteData);

            //  LogUtil.e("xxx " + dictionaries.getName() + "   = " + Arrays.toString(byteData));

            if(dictionaries.getName().equals("纬度小数")){
                System.out.println("");
            }

            // 如果数据存在，根据类型等信息进行转换
            if (byteData.length > 0) {

                // 创建xml数据对象
                XmlData xmlData = new XmlData();
                xmlData.setName(dictionaries.getName());


                // 获取显示类型
                if (dictionaries.getFormat().equals("HEX")) {
                    int transitionValue = 0;
                    if (dictionaries.getTakeByte() == 1) {
                        transitionValue = byteData[0];
                    } else {
                        transitionValue = BytesUtil.bytesIntHL(byteData);
                    }
                    value = Integer.toHexString(transitionValue).toUpperCase();

                } else if (dictionaries.getFormat().equals("STR")) {

                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < byteData.length; i++) {
                        String str = new String(new byte[]{byteData[i]}, "utf-8");
                        sb.append(str + " ");
                      /* String str = String.valueOf(byteData[i]);
                        sb.append(str + " ");*/
                    }
                    if (BytesUtil.isMessyCode(sb.toString())) {
                        value = "0";
                    } else {
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
                if (!dictionaries.getUnits().equals("")) {
                    value = value + "(" + dictionaries.getUnits() + ")";
                }

            }

            XmlData xmlData = new XmlData();
            xmlData.setName(dictionaries.getName());
            xmlData.setValue(value);
            xmlDataList.add(xmlData);

            dictionaries.setXmValue(value);

        }
        return xmlDataList;
    }

    /**
     * 解析xml文件，获取数据对象
     *
     * @param is xml文件
     * @return
     * @throws Exception
     */
    public static NfcDeviceInfo parseXml(FileInputStream is) throws Exception {

        KXmlParser parser = new KXmlParser();
        parser.setInput(is, "utf-8");
        NfcDeviceInfo nfcDeviceInfo = null;

        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            String tagName = parser.getName();
            switch (eventType) {
                case KXmlParser.START_DOCUMENT:
                    //解析开始的时候初始化list
                    //    students=new ArrayList<>();
                    nfcDeviceInfo = new NfcDeviceInfo();

                    break;
                case KXmlParser.START_TAG:
                    if ("设备类型".equals(parser.getName())) {
                        // parser.getAttributeValue(0)
                        nfcDeviceInfo.setDeviceType(parser.nextText());
                    } else if ("更新标志位".equals(parser.getName())) {
                        nfcDeviceInfo.setUpdateIndex(parser.nextText());
                    } else if ("CRC".equals(parser.getName())) {
                        nfcDeviceInfo.setCrc(parser.nextText());
                    } else if ("主灯1段调光时".equals(parser.getName())) {
                        nfcDeviceInfo.setMainLight1Hour(parser.nextText());
                    } else if ("主灯1段调光分".equals(parser.getName())) {
                        nfcDeviceInfo.setMainLight1Minute(parser.nextText());
                    } else if ("主灯1段调光亮度".equals(parser.getName())) {
                        nfcDeviceInfo.setMainLight1Brightness(parser.nextText());
                    } else if ("主灯2段调光时".equals(parser.getName())) {
                        nfcDeviceInfo.setMainLight2Hour(parser.nextText());
                    } else if ("主灯2段调光分".equals(parser.getName())) {
                        nfcDeviceInfo.setMainLight2Minute(parser.nextText());
                    } else if ("主灯2段调光亮度".equals(parser.getName())) {
                        nfcDeviceInfo.setMainLight2Brightness(parser.nextText());
                    } else if ("主灯3段调光时".equals(parser.getName())) {
                        nfcDeviceInfo.setMainLight3Hour(parser.nextText());
                    } else if ("主灯3段调光分".equals(parser.getName())) {
                        nfcDeviceInfo.setMainLight3Minute(parser.nextText());
                    } else if ("主灯3段调光亮度".equals(parser.getName())) {
                        nfcDeviceInfo.setMainLight3Brightness(parser.nextText());
                    } else if ("主灯4段调光时".equals(parser.getName())) {
                        nfcDeviceInfo.setMainLight4Hour(parser.nextText());
                    } else if ("主灯4段调光分".equals(parser.getName())) {
                        nfcDeviceInfo.setMainLight4Minute(parser.nextText());
                    } else if ("主灯4段调光亮度".equals(parser.getName())) {
                        nfcDeviceInfo.setMainLight4Brightness(parser.nextText());
                    } else if ("主灯5段调光时".equals(parser.getName())) {
                        nfcDeviceInfo.setMainLight5Hour(parser.nextText());
                    } else if ("主灯5段调光分".equals(parser.getName())) {
                        nfcDeviceInfo.setMainLight5Minute(parser.nextText());
                    } else if ("主灯5段调光亮度".equals(parser.getName())) {
                        nfcDeviceInfo.setMainLight5Brightness(parser.nextText());
                    } else if ("主灯6段调光时".equals(parser.getName())) {
                        nfcDeviceInfo.setMainLight6Hour(parser.nextText());
                    } else if ("主灯6段调光分".equals(parser.getName())) {
                        nfcDeviceInfo.setMainLight6Minute(parser.nextText());
                    } else if ("主灯6段调光亮度".equals(parser.getName())) {
                        nfcDeviceInfo.setMainLight6Brightness(parser.nextText());
                    } else if ("副灯1段调光时".equals(parser.getName())) {
                        nfcDeviceInfo.setAuxiliaryLight1Hour(parser.nextText());
                    } else if ("副灯1段调光分".equals(parser.getName())) {
                        nfcDeviceInfo.setAuxiliaryLight1Minute(parser.nextText());
                    } else if ("副灯1段调光亮度".equals(parser.getName())) {
                        nfcDeviceInfo.setAuxiliaryLight1Brightness(parser.nextText());
                    } else if ("副灯2段调光时".equals(parser.getName())) {
                        nfcDeviceInfo.setAuxiliaryLight2Hour(parser.nextText());
                    } else if ("副灯2段调光分".equals(parser.getName())) {
                        nfcDeviceInfo.setAuxiliaryLight2Minute(parser.nextText());
                    } else if ("副灯2段调光亮度".equals(parser.getName())) {
                        nfcDeviceInfo.setAuxiliaryLight2Brightness(parser.nextText());
                    } else if ("副灯3段调光时".equals(parser.getName())) {
                        nfcDeviceInfo.setAuxiliaryLight3Hour(parser.nextText());
                    } else if ("副灯3段调光分".equals(parser.getName())) {
                        nfcDeviceInfo.setAuxiliaryLight3Minute(parser.nextText());
                    } else if ("副灯3段调光亮度".equals(parser.getName())) {
                        nfcDeviceInfo.setAuxiliaryLight3Brightness(parser.nextText());
                    } else if ("副灯4段调光时".equals(parser.getName())) {
                        nfcDeviceInfo.setAuxiliaryLight4Hour(parser.nextText());
                    } else if ("副灯4段调光分".equals(parser.getName())) {
                        nfcDeviceInfo.setAuxiliaryLight4Minute(parser.nextText());
                    } else if ("副灯4段调光亮度".equals(parser.getName())) {
                        nfcDeviceInfo.setAuxiliaryLight4Brightness(parser.nextText());
                    } else if ("副灯5段调光时".equals(parser.getName())) {
                        nfcDeviceInfo.setAuxiliaryLight5Hour(parser.nextText());
                    } else if ("副灯5段调光分".equals(parser.getName())) {
                        nfcDeviceInfo.setAuxiliaryLight5Minute(parser.nextText());
                    } else if ("副灯5段调光亮度".equals(parser.getName())) {
                        nfcDeviceInfo.setAuxiliaryLight5Brightness(parser.nextText());
                    } else if ("副灯6段调光时".equals(parser.getName())) {
                        nfcDeviceInfo.setAuxiliaryLight6Hour(parser.nextText());
                    } else if ("副灯6段调光分".equals(parser.getName())) {
                        nfcDeviceInfo.setAuxiliaryLight6Minute(parser.nextText());
                    } else if ("副灯6段调光亮度".equals(parser.getName())) {
                        nfcDeviceInfo.setAuxiliaryLight6Brightness(parser.nextText());
                    } else if ("过流保护开关".equals(parser.getName())) {
                        nfcDeviceInfo.setOvercurrentProtectionWwitch(parser.nextText());
                    } else if ("漏电保护开关".equals(parser.getName())) {
                        nfcDeviceInfo.setEarthLeakageCircuitBreaker(parser.nextText());
                    } else if ("照度开灯开关".equals(parser.getName())) {
                        nfcDeviceInfo.setIlluminationLightSwitch(parser.nextText());
                    } else if ("过压保护阈值".equals(parser.getName())) {
                        nfcDeviceInfo.setOvervoltageProtectionThreshold(parser.nextText());
                    } else if ("欠压保护阈值".equals(parser.getName())) {
                        nfcDeviceInfo.setUndervoltageProtectionThreshold(parser.nextText());
                    } else if ("过流保护阈值".equals(parser.getName())) {
                        nfcDeviceInfo.setOvercurrentProtectionThreshold(parser.nextText());
                    } else if ("欠流保护阈值".equals(parser.getName())) {
                        nfcDeviceInfo.setUndercurrentProtectionThreshold(parser.nextText());
                    } else if ("报警开关".equals(parser.getName())) {
                        nfcDeviceInfo.setAlarmSwitch(parser.nextText());
                    } else if ("漏电保护阈值".equals(parser.getName())) {
                        nfcDeviceInfo.setLeakageProtectionThreshold(parser.nextText());
                    } else if ("照度开灯阈值".equals(parser.getName())) {
                        nfcDeviceInfo.setIlluminationLightThreshold(parser.nextText());
                    } else if ("照度关灯阈值".equals(parser.getName())) {
                        nfcDeviceInfo.setLightOffThreshold(parser.nextText());
                    } else if ("灯杆倒塌报警开关".equals(parser.getName())) {
                        nfcDeviceInfo.setLampPoleCollapseAlarmSwitch(parser.nextText());
                    } else if ("项目地区".equals(parser.getName())) {
                        nfcDeviceInfo.setProjectArea(parser.nextText());
                    } else if ("项目编号".equals(parser.getName())) {
                        nfcDeviceInfo.setProjectNumber(parser.nextText());
                    } else if ("IMEI".equals(parser.getName())) {
                        nfcDeviceInfo.setImei(parser.nextText());
                    } else if ("维修IMEI".equals(parser.getName())) {
                        nfcDeviceInfo.setMaintainImei(parser.nextText());
                    } else if ("执行底板ID".equals(parser.getName())) {
                        nfcDeviceInfo.setBaseplateId(parser.nextText());
                    }

                    break;
                /*  case KXmlParser.END_TAG:
                    break;
                    case KXmlParser.TEXT:
                    String content = parser.getText();
                    System.out.println(content + " TEXT:" + content);
                    break;*/
                case KXmlParser.END_DOCUMENT:
                    break;
            }
            eventType = parser.next();
        }

        return nfcDeviceInfo;
    }


    /**
     * 解析xml文件，获取数据对象
     *
     * @param is xml文件
     * @return
     * @throws Exception
     */
    public static List<DataDictionaries> parseXml2(FileInputStream is) throws Exception {

        KXmlParser parser = new KXmlParser();
        parser.setInput(is, "utf-8");

        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case KXmlParser.START_DOCUMENT:

                    break;
                case KXmlParser.START_TAG:

                    if (dataDictionaries != null) {
                        for (int d = 0; d < dataDictionaries.size(); d++) {
                            if (dataDictionaries.get(d).getName().equals(parser.getName())) {
                                dataDictionaries.get(d).setValue(convertFormat(dataDictionaries.get(d), parser.nextText()));
                            }

                        }
                    }

                    break;
                /*  case KXmlParser.END_TAG:
                    break;
                    case KXmlParser.TEXT:
                    String content = parser.getText();
                    System.out.println(content + " TEXT:" + content);
                    break;*/
                case KXmlParser.END_DOCUMENT:
                    break;
            }
            eventType = parser.next();
        }

        return dataDictionaries;
    }

    /**
     * 解析Excel文件
     *
     * @param excelName 文件名称
     * @param context   上下文
     * @throws IOException
     * @throws BiffException
     */
    private static List<DataDictionaries> parseExcel(String excelName, Context context) throws IOException, BiffException {
        InputStream is = null;
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

            System.out.println("第" + i + "行数据=" + name + "," + startAddress + "," + endAddress + "," + takeByte + "," + format + "," + units + "," + factor + "," + operator + "," + permission);
        }
        book.close();
        is.close();
        return dataDictionaries;
    }

    /**
     * 创建xml文件
     *
     * @param xmlDataList
     * @param file
     * @return
     * @throws Exception
     */
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
        serializer.startDocument("utf-8", true);
        // 设置文件开始标签
        serializer.startTag(null, "当前读取信息");
 /*       for (XmlData xmlData : xmlDataList) {

            // 设置标签
            serializer.startTag(null, xmlData.getName());
            serializer.text(xmlData.getValue());
            serializer.endTag(null, xmlData.getName());

        }*/
       for (DataDictionaries dd : dataDictionaries) {
            // 设置标签
            serializer.startTag(null, dd.getName());
            serializer.text(dd.getXmValue());
            serializer.endTag(null,dd.getName());
        }


        // 设置文件结束标签
        serializer.endTag(null, "当前读取信息");
        // 文件的结束
        serializer.endDocument();

        serializer.flush();
        fos.close();
        return file;

    }

    /**
     * 写入一个xml文件
     *
     * @param strXml xml字符串
     * @param file   xml的绝对地址
     * @throws Exception
     */
    public static void saveXml(String strXml, File file) throws Exception {

        if (file.exists()) {
            file.delete();
        }
        // 文件写入流实例
        FileOutputStream fos = null;
        // 根据文件对象创建一个文件的输出流对象
        fos = new FileOutputStream(file, true);
        fos.write(strXml.getBytes("utf-8"));//对内容进行编码并写入文件
        fos.close();//关闭文件输出流

    }

    /**
     * 删除文件
     *
     * @param file 绝对路径
     */
    public static void deleFile(File file) {
        // 文件存在先删除
        if (file.exists()) {
            file.delete();
        }
    }


    /**
     * 格式化xml的显示
     *
     * @param str
     * @return
     * @throws Exception
     */
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


    /**
     * 检测设备信息的正确性
     */
    public static void writeNfcDeviceInfo(NfcDeviceInfo nfcDeviceInfo, OnNfcDataListening listening, Context context, ST25DVTag mTag) {

        if (nfcDeviceInfo.getDeviceType().equals("1")) {
            // 解析excel
            try {
                List<DataDictionaries> dataDictionaries = parseExcel("0001_83140000.xls", context);
                boolean flag = false;

                for (DataDictionaries dictionaries : dataDictionaries) {
                    String name = dictionaries.getName();
                    // 判断是否存在读写权限
                    if (dictionaries.getPermission().equals("RW")) {

                        if ("设备类型".equals(name)) {
                        } else if ("更新标志位".equals(name)) {

                            if (writeNfc(nfcDeviceInfo.getUpdateIndex(), listening, mTag, dictionaries, "更新标志位")) {
                                flag = true;
                            } else {
                                flag = false;
                                break;
                            }


                        } else if ("CRC".equals(name)) {

                            if (writeNfc(nfcDeviceInfo.getCrc(), listening, mTag, dictionaries, "CRC")) {
                                flag = true;
                            } else {
                                flag = false;
                                break;
                            }

                        } else if ("主灯1段调光时".equals(name)) {

                            if (writeNfc(nfcDeviceInfo.getMainLight1Hour(), listening, mTag, dictionaries, "主灯1段调光时")) {
                                flag = true;
                            } else {
                                flag = false;
                                break;
                            }

                        } else if ("主灯1段调光分".equals(name)) {

                            if (writeNfc(nfcDeviceInfo.getMainLight1Minute(), listening, mTag, dictionaries, "主灯1段调光分")) {
                                flag = true;
                            } else {
                                flag = false;
                                break;
                            }

                        } else if ("主灯1段调光亮度".equals(name)) {

                            if (writeNfc(nfcDeviceInfo.getMainLight1Brightness(), listening, mTag, dictionaries, "主灯1段调光亮度")) {
                                flag = true;
                            } else {
                                flag = false;
                                break;
                            }

                        } else if ("主灯2段调光时".equals(name)) {

                            if (writeNfc(nfcDeviceInfo.getMainLight2Hour(), listening, mTag, dictionaries, "主灯2段调光时")) {
                                flag = true;
                            } else {
                                flag = false;
                                break;
                            }

                        } else if ("主灯2段调光分".equals(name)) {

                            if (writeNfc(nfcDeviceInfo.getMainLight2Minute(), listening, mTag, dictionaries, "主灯2段调光分")) {
                                flag = true;
                            } else {
                                flag = false;
                                break;
                            }

                        } else if ("主灯2段调光亮度".equals(name)) {

                            if (writeNfc(nfcDeviceInfo.getMainLight2Brightness(), listening, mTag, dictionaries, "主灯2段调光亮度")) {
                                flag = true;
                            } else {
                                flag = false;
                                break;
                            }

                        } else if ("主灯3段调光时".equals(name)) {

                            if (writeNfc(nfcDeviceInfo.getMainLight3Hour(), listening, mTag, dictionaries, "主灯3段调光时")) {
                                flag = true;
                            } else {
                                flag = false;
                                break;
                            }

                        } else if ("主灯3段调光分".equals(name)) {

                            if (writeNfc(nfcDeviceInfo.getMainLight3Minute(), listening, mTag, dictionaries, "主灯3段调光分")) {
                                flag = true;
                            } else {
                                flag = false;
                                break;
                            }

                        } else if ("主灯3段调光亮度".equals(name)) {

                            if (writeNfc(nfcDeviceInfo.getMainLight3Brightness(), listening, mTag, dictionaries, "主灯3段调光亮度")) {
                                flag = true;
                            } else {
                                flag = false;
                                break;
                            }

                        } else if ("主灯4段调光时".equals(name)) {

                            if (writeNfc(nfcDeviceInfo.getMainLight4Hour(), listening, mTag, dictionaries, "主灯4段调光时")) {
                                flag = true;
                            } else {
                                flag = false;
                                break;
                            }

                        } else if ("主灯4段调光分".equals(name)) {

                            if (writeNfc(nfcDeviceInfo.getMainLight4Minute(), listening, mTag, dictionaries, "主灯4段调光分")) {
                                flag = true;
                            } else {
                                flag = false;
                                break;
                            }

                        } else if ("主灯4段调光亮度".equals(name)) {

                            if (writeNfc(nfcDeviceInfo.getMainLight4Brightness(), listening, mTag, dictionaries, "主灯4段调光亮度")) {
                                flag = true;
                            } else {
                                flag = false;
                                break;
                            }
                            ;

                        } else if ("主灯5段调光时".equals(name)) {

                            if (writeNfc(nfcDeviceInfo.getMainLight5Hour(), listening, mTag, dictionaries, "主灯5段调光时")) {
                                flag = true;
                            } else {
                                flag = false;
                                break;
                            }

                        } else if ("主灯5段调光分".equals(name)) {

                            if (writeNfc(nfcDeviceInfo.getMainLight5Minute(), listening, mTag, dictionaries, "主灯5段调光分")) {
                                flag = true;
                            } else {
                                flag = false;
                                break;
                            }

                        } else if ("主灯5段调光亮度".equals(name)) {

                            if (writeNfc(nfcDeviceInfo.getMainLight5Brightness(), listening, mTag, dictionaries, "主灯5段调光亮度")) {
                                flag = true;
                            } else {
                                flag = false;
                                break;
                            }

                        } else if ("主灯6段调光时".equals(name)) {

                            if (writeNfc(nfcDeviceInfo.getMainLight6Hour(), listening, mTag, dictionaries, "主灯6段调光时")) {
                                flag = true;
                            } else {
                                flag = false;
                                break;
                            }

                        } else if ("主灯6段调光分".equals(name)) {

                            if (writeNfc(nfcDeviceInfo.getMainLight6Minute(), listening, mTag, dictionaries, "主灯6段调光分")) {
                                flag = true;
                            } else {
                                flag = false;
                                break;
                            }

                        } else if ("主灯6段调光亮度".equals(name)) {

                            if (writeNfc(nfcDeviceInfo.getMainLight6Brightness(), listening, mTag, dictionaries, "主灯6段调光亮度")) {
                                flag = true;
                            } else {
                                flag = false;
                                break;
                            }

                        } else if ("副灯1段调光时".equals(name)) {

                            if (writeNfc(nfcDeviceInfo.getAuxiliaryLight1Hour(), listening, mTag, dictionaries, "副灯1段调光时")) {
                                flag = true;
                            } else {
                                flag = false;
                                break;
                            }


                        } else if ("副灯1段调光分".equals(name)) {

                            if (writeNfc(nfcDeviceInfo.getAuxiliaryLight1Minute(), listening, mTag, dictionaries, "副灯1段调光分")) {
                                flag = true;
                            } else {
                                flag = false;
                                break;
                            }

                        } else if ("副灯1段调光亮度".equals(name)) {

                            if (writeNfc(nfcDeviceInfo.getAuxiliaryLight1Brightness(), listening, mTag, dictionaries, "副灯1段调光亮度")) {
                                flag = true;
                            } else {
                                flag = false;
                                break;
                            }


                        } else if ("副灯2段调光时".equals(name)) {

                            if (writeNfc(nfcDeviceInfo.getAuxiliaryLight2Hour(), listening, mTag, dictionaries, "副灯2段调光时")) {
                                flag = true;
                            } else {
                                flag = false;
                                break;
                            }

                        } else if ("副灯2段调光分".equals(name)) {

                            if (writeNfc(nfcDeviceInfo.getAuxiliaryLight2Minute(), listening, mTag, dictionaries, "副灯2段调光分")) {
                                flag = true;
                            } else {
                                flag = false;
                                break;
                            }
                            ;

                        } else if ("副灯2段调光亮度".equals(name)) {

                            if (writeNfc(nfcDeviceInfo.getAuxiliaryLight2Brightness(), listening, mTag, dictionaries, "副灯2段调光亮度")) {
                                flag = true;
                            } else {
                                flag = false;
                                break;
                            }

                        } else if ("副灯3段调光时".equals(name)) {

                            if (writeNfc(nfcDeviceInfo.getAuxiliaryLight3Hour(), listening, mTag, dictionaries, "副灯3段调光时")) {
                                flag = true;
                            } else {
                                flag = false;
                                break;
                            }

                        } else if ("副灯3段调光分".equals(name)) {

                            if (writeNfc(nfcDeviceInfo.getAuxiliaryLight3Minute(), listening, mTag, dictionaries, "副灯3段调光分")) {
                                flag = true;
                            } else {
                                flag = false;
                                break;
                            }

                        } else if ("副灯3段调光亮度".equals(name)) {

                            if (writeNfc(nfcDeviceInfo.getAuxiliaryLight3Brightness(), listening, mTag, dictionaries, "副灯3段调光亮度")) {
                                flag = true;
                            } else {
                                flag = false;
                                break;
                            }

                        } else if ("副灯4段调光时".equals(name)) {

                            if (writeNfc(nfcDeviceInfo.getAuxiliaryLight4Hour(), listening, mTag, dictionaries, "副灯4段调光时")) {
                                flag = true;
                            } else {
                                flag = false;
                                break;
                            }

                        } else if ("副灯4段调光分".equals(name)) {

                            if (writeNfc(nfcDeviceInfo.getAuxiliaryLight4Minute(), listening, mTag, dictionaries, "副灯4段调光分")) {
                                flag = true;
                            } else {
                                flag = false;
                                break;
                            }

                        } else if ("副灯4段调光亮度".equals(name)) {

                            if (writeNfc(nfcDeviceInfo.getAuxiliaryLight4Brightness(), listening, mTag, dictionaries, "副灯4段调光亮度")) {
                                flag = true;
                            } else {
                                flag = false;
                                break;
                            }

                        } else if ("副灯5段调光时".equals(name)) {

                            if (writeNfc(nfcDeviceInfo.getAuxiliaryLight5Hour(), listening, mTag, dictionaries, "副灯5段调光时")) {
                                flag = true;
                            } else {
                                flag = false;
                                break;
                            }

                        } else if ("副灯5段调光分".equals(name)) {

                            if (writeNfc(nfcDeviceInfo.getAuxiliaryLight5Minute(), listening, mTag, dictionaries, "副灯5段调光分")) {
                                flag = true;
                            } else {
                                flag = false;
                                break;
                            }

                        } else if ("副灯5段调光亮度".equals(name)) {

                            if (writeNfc(nfcDeviceInfo.getAuxiliaryLight5Brightness(), listening, mTag, dictionaries, "副灯5段调光亮度")) {
                                flag = true;
                            } else {
                                flag = false;
                                break;
                            }

                        } else if ("副灯6段调光时".equals(name)) {

                            if (writeNfc(nfcDeviceInfo.getAuxiliaryLight6Hour(), listening, mTag, dictionaries, "副灯6段调光时")) {
                                flag = true;
                            } else {
                                flag = false;
                                break;
                            }

                        } else if ("副灯6段调光分".equals(name)) {

                            if (writeNfc(nfcDeviceInfo.getAuxiliaryLight6Minute(), listening, mTag, dictionaries, "副灯6段调光分")) {
                                flag = true;
                            } else {
                                flag = false;
                                break;
                            }

                        } else if ("副灯6段调光亮度".equals(name)) {

                            if (writeNfc(nfcDeviceInfo.getAuxiliaryLight6Brightness(), listening, mTag, dictionaries, "副灯6段调光亮度")) {
                                flag = true;
                            } else {
                                flag = false;
                                break;
                            }


                        } else if ("过流保护开关".equals(name)) {

                            if (writeNfc(nfcDeviceInfo.getOvercurrentProtectionWwitch(), listening, mTag, dictionaries, "过流保护开关")) {
                                flag = true;
                            } else {
                                flag = false;
                                break;
                            }

                        } else if ("漏电保护开关".equals(name)) {

                            if (writeNfc(nfcDeviceInfo.getEarthLeakageCircuitBreaker(), listening, mTag, dictionaries, "漏电保护开关")) {
                                flag = true;
                            } else {
                                flag = false;
                                break;
                            }

                        } else if ("照度开灯开关".equals(name)) {

                            if (writeNfc(nfcDeviceInfo.getIlluminationLightSwitch(), listening, mTag, dictionaries, "照度开灯开关")) {
                                flag = true;
                            } else {
                                flag = false;
                                break;
                            }

                        } else if ("过压保护阈值".equals(name)) {

                            if (writeNfc(nfcDeviceInfo.getOvervoltageProtectionThreshold(), listening, mTag, dictionaries, "过压保护阈值")) {
                                flag = true;
                            } else {
                                flag = false;
                                break;
                            }

                        } else if ("欠压保护阈值".equals(name)) {

                            if (writeNfc(nfcDeviceInfo.getUndervoltageProtectionThreshold(), listening, mTag, dictionaries, "欠压保护阈值")) {
                                flag = true;
                            } else {
                                flag = false;
                                break;
                            }

                        } else if ("过流保护阈值".equals(name)) {

                            if (writeNfc(nfcDeviceInfo.getOvercurrentProtectionThreshold(), listening, mTag, dictionaries, "过流保护阈值")) {
                                flag = true;
                            } else {
                                flag = false;
                                break;
                            }

                        } else if ("欠流保护阈值".equals(name)) {

                            if (writeNfc(nfcDeviceInfo.getUndercurrentProtectionThreshold(), listening, mTag, dictionaries, "欠流保护阈值")) {
                                flag = true;
                            } else {
                                flag = false;
                                break;
                            }

                        } else if ("报警开关".equals(name)) {

                            if (writeNfc(nfcDeviceInfo.getAlarmSwitch(), listening, mTag, dictionaries, "报警开关")) {
                                flag = true;
                            } else {
                                flag = false;
                                break;
                            }

                        } else if ("漏电保护阈值".equals(name)) {

                            if (writeNfc(nfcDeviceInfo.getLeakageProtectionThreshold(), listening, mTag, dictionaries, "漏电保护阈值")) {
                                flag = true;
                            } else {
                                flag = false;
                                break;
                            }

                        } else if ("照度开灯阈值".equals(name)) {

                            if (writeNfc(nfcDeviceInfo.getIlluminationLightThreshold(), listening, mTag, dictionaries, "照度开灯阈值")) {
                                flag = true;
                            } else {
                                flag = false;
                                break;
                            }

                        } else if ("照度关灯阈值".equals(name)) {

                            if (writeNfc(nfcDeviceInfo.getLightOffThreshold(), listening, mTag, dictionaries, "照度关灯阈值")) {
                                flag = true;
                            } else {
                                flag = false;
                                break;
                            }

                        } else if ("灯杆倒塌报警开关".equals(name)) {

                            if (writeNfc(nfcDeviceInfo.getLampPoleCollapseAlarmSwitch(), listening, mTag, dictionaries, "灯杆倒塌报警开关")) {
                                flag = true;
                            } else {
                                flag = false;
                                break;
                            }

                        } else if ("项目地区".equals(name)) {

                            if (writeNfc(nfcDeviceInfo.getProjectArea(), listening, mTag, dictionaries, "项目地区")) {
                                flag = true;
                            } else {
                                flag = false;
                                break;
                            }

                        } else if ("项目编号".equals(name)) {

                            if (writeNfc(nfcDeviceInfo.getProjectNumber(), listening, mTag, dictionaries, "项目编号")) {
                                flag = true;
                            } else {
                                flag = false;
                                break;
                            }

                        } else if ("IMEI".equals(name)) {

                            if (writeNfc(nfcDeviceInfo.getImei(), listening, mTag, dictionaries, "IMEI")) {
                                flag = true;
                            } else {
                                flag = false;
                                break;
                            }

                        } else if ("维修IMEI".equals(name)) {

                            if (writeNfc(nfcDeviceInfo.getMaintainImei(), listening, mTag, dictionaries, "维修IMEI")) {
                                flag = true;
                            } else {
                                flag = false;
                                break;
                            }

                        } else if ("执行底板ID".equals(name)) {

                            if (writeNfc(nfcDeviceInfo.getBaseplateId(), listening, mTag, dictionaries, "执行底板ID")) {
                                flag = true;
                            } else {
                                flag = false;
                                break;
                            }

                        }
                    }

                }

                if (flag) {
                    listening.succeed();
                }


            } catch (Exception e) {
                e.printStackTrace();
                listening.failure("参数错误 = " + e.getMessage().toString());
            }

        } else {
            listening.failure("类型错误！");
        }

    }


    /**
     * 检测设备信息的正确性
     */
    public static void writeNfcDeviceInfo2(List<DataDictionaries> nfcDeviceInfo, OnNfcDataListening listening, Context context, Tag mTag, byte[] payload) {

        // 解析excel
        try {

            for (DataDictionaries dataDictionarie : nfcDeviceInfo) {

                System.arraycopy(dataDictionarie.getValue(), 0, payload, dataDictionarie.getStartAddress() + 3, dataDictionarie.getValue().length);
            /*        // 判断是否存在读写权限
                    if (dataDictionarie.getPermission().equals("RW")) {

                        System.arraycopy(dataDictionarie.getValue(),  0, payload,dataDictionarie.getStartAddress()+2, dataDictionarie.getValue().length);

                    }*/
            }

            //获取Tag对象
            NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload)});
            boolean result = writeTag(ndefMessage, mTag);
            if (result) {
                listening.succeed();
            } else {
                listening.failure("写入失败");
            }


        } catch (Exception e) {
            e.printStackTrace();
            listening.failure("参数错误 = " + e.getMessage().toString());
        }


    }

    /**
     * 写数据
     *
     * @param ndefMessage 创建好的NDEF文本数据
     * @param tag         标签
     * @return
     */
    public static boolean writeTag(NdefMessage ndefMessage, Tag tag) {
        try {
            Ndef ndef = Ndef.get(tag);
            ndef.connect();
            ndef.writeNdefMessage(ndefMessage);
            return true;
        } catch (Exception e) {
            System.out.println("xxxxxxxxxxx" + e.toString());
        }
        return false;
    }


    /**
     * 写入nfc
     *
     * @param parameters   需要写入的参数字符
     * @param listening    监听
     * @param mTag         nfc对象
     * @param dictionaries 指定的字典格式
     * @param tagName      当前标签名称
     * @return
     */
    private static boolean writeNfc(String parameters, OnNfcDataListening listening, ST25DVTag mTag, DataDictionaries dictionaries, String tagName) {
        byte[] data = convertFormat(dictionaries, parameters);
        if (data != null && data.length == dictionaries.getTakeByte()) {
            try {
                mTag.writeBytes(dictionaries.getStartAddress(), data);
            } catch (STException e) {
                e.printStackTrace();
                listening.failure(tagName + "，写入失败（请保持nfc设备的距离）");
                return false;
            }
        } else {
            listening.failure(tagName + "，占用字节出错（请检查当前参数是否正常）");
            return false;
        }
        return true;
    }

    /**
     * 根据类型转换参数
     *
     * @param dictionaries 字典，根据子弹需求完成转换
     * @param parameters   转换的参数
     * @return
     */
    private static byte[] convertFormat(DataDictionaries dictionaries, String parameters) {
        byte[] data = null;
        if (dictionaries.getFormat().equals("HEX")) {
            // 判断当前参数是否存在单位
            if (parameters.contains("(") && !dictionaries.getUnits().equals("")) {
                parameters = parameters.substring(0, parameters.indexOf("(")).trim();
            }
            // 转int
            int intData = Integer.valueOf(parameters, 16);
            // 判断是否包含系数，如果有要运算
            int factor = dictionaries.getFactor();
            if (factor != 0) {
                if (dictionaries.getOperator().equals("+")) {
                    intData = intData - factor;
                } else if (dictionaries.getOperator().equals("-")) {
                    intData = intData + factor;
                } else if (dictionaries.getOperator().equals("*")) {
                    intData = intData / factor;
                } else if (dictionaries.getOperator().equals("/")) {
                    intData = intData * factor;
                }
            }
            // 判断最大值和最小值
          /*  if(){

            }*/

            // 判断高低位
            // 判断高低位
            if (dictionaries.getConvertFormat().equals("HL")) {
                if (dictionaries.getTakeByte() == 2) {
                    data = BytesUtil.intBytesHL(intData, 2);
                } else if (dictionaries.getTakeByte() == 4) {
                    data = BytesUtil.intBytesHL(intData, 4);
                } else {
                    data = new byte[]{(byte) intData};
                }
            } else {
                if (dictionaries.getTakeByte() == 2) {
                    data = BytesUtil.intBytesHL(intData, 2);
                } else if (dictionaries.getTakeByte() == 4) {
                    data = BytesUtil.intBytesHL(intData, 4);
                } else {
                    data = new byte[]{(byte) intData};
                }
            }

        } else if (dictionaries.getFormat().equals("DEC")) {
            // 判断当前参数是否存在单位
            if (parameters.contains("(") && !dictionaries.getUnits().equals("")) {
                parameters = parameters.substring(0, parameters.indexOf("(")).trim();
            }
            // 根据类型转换
            int intData = Integer.parseInt(parameters);
            // 判断是否包含系数，如果有要运算
            int factor = dictionaries.getFactor();
            if (factor != 0) {
                if (dictionaries.getOperator().equals("+")) {
                    intData = intData - factor;
                } else if (dictionaries.getOperator().equals("-")) {
                    intData = intData + factor;
                } else if (dictionaries.getOperator().equals("*")) {
                    intData = intData / factor;
                } else if (dictionaries.getOperator().equals("/")) {
                    intData = intData * factor;
                }
            }

            // 判断高低位
            if (dictionaries.getConvertFormat().equals("HL")) {
                if (dictionaries.getTakeByte() == 2) {
                    data = BytesUtil.intBytesHL(intData, 2);
                } else if (dictionaries.getTakeByte() == 4) {
                    data = BytesUtil.intBytesHL(intData, 4);
                } else {
                    data = new byte[]{(byte) intData};
                }
            } else {
                if (dictionaries.getTakeByte() == 2) {
                    data = BytesUtil.intBytesHL(intData, 2);
                } else if (dictionaries.getTakeByte() == 4) {
                    data = BytesUtil.intBytesHL(intData, 4);
                } else {
                    data = new byte[]{(byte) intData};
                }
            }
        } else if (dictionaries.getFormat().equals("STR")) {
            data = parameters.replace(" ", "").getBytes();
            //    data = parameters.trim().getBytes();
        }


        return data;
    }


    /**
     * 去掉 xml 文件的所有空格
     *
     * @param str
     * @return
     */
    public static String replaceBlank(String str) {

        String dest = "";

        if (str != null) {

            Pattern p = Pattern.compile("\\s*|\t|\r|\n");

            Matcher m = p.matcher(str);

            dest = m.replaceAll("");
        }
        return dest;

    }


    public interface OnNfcDataListening {

        void succeed();

        void failure(String error);

    }
}
