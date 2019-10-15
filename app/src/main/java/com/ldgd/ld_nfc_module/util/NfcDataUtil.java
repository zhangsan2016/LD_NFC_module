package com.ldgd.ld_nfc_module.util;

import android.content.Context;

import com.ldgd.ld_nfc_module.entity.DataDictionaries;
import com.ldgd.ld_nfc_module.entity.NfcDeviceInfo;
import com.ldgd.ld_nfc_module.entity.XmlData;
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

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

/**
 * Created by ldgd on 2019/10/7.
 * 功能：
 * 说明：xml解析工具
 */

public class NfcDataUtil {

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


        try {
            // 1.获取assets包中的 Excel 文件，得到字典格式
            // 解析excel
            List<DataDictionaries> dataDictionaries = parseExcel(excelName, context);

            // 2.根据字典格式解析数据
            ArrayList<XmlData> xmlDataList = parseBuffer(mBuffer, dataDictionaries);

            // 3.转换成xml文件并保存
            File cacheFile = createXML(xmlDataList, new File(context.getCacheDir(), saveFileName));

            // 4.返回xml文件地址
            return cacheFile;

        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("xxx Exception = " + e.getMessage().toString());
            return null;
        }

    }

    private static ArrayList<XmlData> parseBuffer(byte[] mBuffer, List<DataDictionaries> dataDictionaries) throws UnsupportedEncodingException {
        ArrayList<XmlData> xmlDataList = new ArrayList<>();
        String value = null;
        for (DataDictionaries dictionaries : dataDictionaries) {

            byte[] byteData = new byte[dictionaries.getTakeByte()];
            System.arraycopy(mBuffer, dictionaries.getStartAddress(), byteData, 0, dictionaries.getTakeByte());

            LogUtil.e("xxx " + dictionaries.getName() + "   = " + Arrays.toString(byteData));


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
        parser.setInput(is, "UTF-8");
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
                        nfcDeviceInfo.setMainLight5Minute(parser.nextText());
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
                        nfcDeviceInfo.setAuxiliaryLight1Brightness(parser.nextText());
                    } else if ("副灯3段调光时".equals(parser.getName())) {
                        nfcDeviceInfo.setAuxiliaryLight3Hour(parser.nextText());
                    } else if ("副灯3段调光分".equals(parser.getName())) {
                        nfcDeviceInfo.setAuxiliaryLight3Minute(parser.nextText());
                    } else if ("副灯3段调光亮度".equals(parser.getName())) {
                        nfcDeviceInfo.setAuxiliaryLight4Brightness(parser.nextText());
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

        LogUtil.e("xxx nfcDeviceInfo = " + nfcDeviceInfo.toString());
        return nfcDeviceInfo;
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

            //  System.out.println("第" + i + "行数据=" + name + "," + startAddress + "," + endAddress + "," + takeByte + "," + format + "," + units+ "," + factor + "," + operator + "," + Permission );
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

    /**
     * 写入一个xml文件
     *
     * @param strXml xml字符串
     * @param file   xml的绝对地址
     * @throws Exception
     */
    public static void saveXml(String strXml, File file) throws Exception {
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

                            if (writeNfc(nfcDeviceInfo.getUpdateIndex(), listening, mTag, dictionaries,"更新标志位"))
                                flag = true;
                                break;

                        } else if ("CRC".equals(name)) {

                            if (writeNfc(nfcDeviceInfo.getCrc(), listening, mTag, dictionaries,"CRC"))
                                flag = true;
                            break;

                        } else if ("主灯1段调光时".equals(name)) {

                            if (writeNfc(nfcDeviceInfo.getMainLight1Hour(), listening, mTag, dictionaries,"主灯1段调光时"))
                                flag = true;
                            break;

                        } else if ("主灯1段调光分".equals(name)) {

                            byte[] data = convertFormat(dictionaries.getFormat(), nfcDeviceInfo.getMainLight1Minute());
                            if (data != null && data.length == dictionaries.getTakeByte()) {
                                try {
                                    mTag.writeBytes(dictionaries.getStartAddress(), data);
                                } catch (STException e) {
                                    e.printStackTrace();
                                    listening.failure("主灯1段调光分，写入失败");
                                    break;
                                }
                            } else {
                                listening.failure("主灯1段调光分，占用字节出错");
                                break;
                            }
                        } else if ("主灯1段调光亮度".equals(name)) {

                            byte[] data = convertFormat(dictionaries.getFormat(), nfcDeviceInfo.getMainLight1Brightness());
                            if (data != null && data.length == dictionaries.getTakeByte()) {
                                try {
                                    mTag.writeBytes(dictionaries.getStartAddress(), data);
                                } catch (STException e) {
                                    e.printStackTrace();
                                    listening.failure("主灯1段调光亮度，写入失败");
                                    break;
                                }
                            } else {
                                listening.failure("主灯1段调光亮度，占用字节出错");
                                break;
                            }

                        } else if ("主灯2段调光时".equals(name)) {

                            byte[] data = convertFormat(dictionaries.getFormat(), nfcDeviceInfo.getMainLight2Hour());
                            if (data != null && data.length == dictionaries.getTakeByte()) {
                                try {
                                    mTag.writeBytes(dictionaries.getStartAddress(), data);
                                } catch (STException e) {
                                    e.printStackTrace();
                                    listening.failure("主灯2段调光时，写入失败");
                                    break;
                                }
                            } else {
                                listening.failure("主灯2段调光时，占用字节出错");
                                break;
                            }
                        } else if ("主灯2段调光分".equals(name)) {

                            byte[] data = convertFormat(dictionaries.getFormat(), nfcDeviceInfo.getMainLight2Minute());
                            if (data != null && data.length == dictionaries.getTakeByte()) {
                                try {
                                    mTag.writeBytes(dictionaries.getStartAddress(), data);
                                } catch (STException e) {
                                    e.printStackTrace();
                                    listening.failure("主灯2段调光分，写入失败");
                                    break;
                                }
                            } else {
                                listening.failure("主灯2段调光分，占用字节出错");
                                break;
                            }
                        } else if ("主灯2段调光亮度".equals(name)) {

                            byte[] data = convertFormat(dictionaries.getFormat(), nfcDeviceInfo.getMainLight2Brightness());
                            if (data != null && data.length == dictionaries.getTakeByte()) {
                                try {
                                    mTag.writeBytes(dictionaries.getStartAddress(), data);
                                } catch (STException e) {
                                    e.printStackTrace();
                                    listening.failure("主灯2段调光亮度，写入失败");
                                    break;
                                }
                            } else {
                                listening.failure("主灯2段调光亮度，占用字节出错");
                                break;
                            }


                        } else if ("主灯3段调光时".equals(name)) {

                            byte[] data = convertFormat(dictionaries.getFormat(), nfcDeviceInfo.getMainLight3Hour());
                            if (data != null && data.length == dictionaries.getTakeByte()) {
                                try {
                                    mTag.writeBytes(dictionaries.getStartAddress(), data);
                                } catch (STException e) {
                                    e.printStackTrace();
                                    listening.failure("主灯3段调光时，写入失败");
                                    break;
                                }
                            } else {
                                listening.failure("主灯3段调光时，占用字节出错");
                                break;
                            }
                        } else if ("主灯3段调光分".equals(name)) {

                            byte[] data = convertFormat(dictionaries.getFormat(), nfcDeviceInfo.getMainLight3Minute());
                            if (data != null && data.length == dictionaries.getTakeByte()) {
                                try {
                                    mTag.writeBytes(dictionaries.getStartAddress(), data);
                                } catch (STException e) {
                                    e.printStackTrace();
                                    listening.failure("主灯3段调光分，写入失败");
                                    break;
                                }
                            } else {
                                listening.failure("主灯3段调光分，占用字节出错");
                                break;
                            }
                        } else if ("主灯3段调光亮度".equals(name)) {

                            byte[] data = convertFormat(dictionaries.getFormat(), nfcDeviceInfo.getMainLight3Brightness());
                            if (data != null && data.length == dictionaries.getTakeByte()) {
                                try {
                                    mTag.writeBytes(dictionaries.getStartAddress(), data);
                                } catch (STException e) {
                                    e.printStackTrace();
                                    listening.failure("主灯3段调光亮度，写入失败");
                                    break;
                                }
                            } else {
                                listening.failure("主灯3段调光亮度，占用字节出错");
                                break;
                            }
                        } else if ("主灯4段调光时".equals(name)) {
                            byte[] data = convertFormat(dictionaries.getFormat(), nfcDeviceInfo.getMainLight4Hour());
                            if (data != null && data.length == dictionaries.getTakeByte()) {
                                try {
                                    mTag.writeBytes(dictionaries.getStartAddress(), data);
                                } catch (STException e) {
                                    e.printStackTrace();
                                    listening.failure("主灯4段调光时，写入失败");
                                    break;
                                }
                            } else {
                                listening.failure("主灯4段调光时，占用字节出错");
                                break;
                            }
                        } else if ("主灯4段调光分".equals(name)) {
                            byte[] data = convertFormat(dictionaries.getFormat(), nfcDeviceInfo.getMainLight4Minute());
                            if (data != null && data.length == dictionaries.getTakeByte()) {
                                try {
                                    mTag.writeBytes(dictionaries.getStartAddress(), data);
                                } catch (STException e) {
                                    e.printStackTrace();
                                    listening.failure("主灯4段调光分，写入失败");
                                    break;
                                }
                            } else {
                                listening.failure("主灯4段调光分，占用字节出错");
                                break;
                            }
                        } else if ("主灯4段调光亮度".equals(name)) {
                            byte[] data = convertFormat(dictionaries.getFormat(), nfcDeviceInfo.getMainLight4Brightness());
                            if (data != null && data.length == dictionaries.getTakeByte()) {
                                try {
                                    mTag.writeBytes(dictionaries.getStartAddress(), data);
                                } catch (STException e) {
                                    e.printStackTrace();
                                    listening.failure("主灯4段调光亮度，写入失败");
                                    break;
                                }
                            } else {
                                listening.failure("主灯4段调光亮度，占用字节出错");
                                break;
                            }
                        } else if ("主灯5段调光时".equals(name)) {

                            byte[] data = convertFormat(dictionaries.getFormat(), nfcDeviceInfo.getMainLight5Hour());
                            if (data != null && data.length == dictionaries.getTakeByte()) {
                                try {
                                    mTag.writeBytes(dictionaries.getStartAddress(), data);
                                } catch (STException e) {
                                    e.printStackTrace();
                                    listening.failure("主灯5段调光时，写入失败");
                                    break;
                                }
                            } else {
                                listening.failure("主灯5段调光时，占用字节出错");
                                break;
                            }
                        } else if ("主灯5段调光分".equals(name)) {
                            byte[] data = convertFormat(dictionaries.getFormat(), nfcDeviceInfo.getMainLight5Minute());
                            if (data != null && data.length == dictionaries.getTakeByte()) {
                                try {
                                    mTag.writeBytes(dictionaries.getStartAddress(), data);
                                } catch (STException e) {
                                    e.printStackTrace();
                                    listening.failure("主灯5段调光分，写入失败");
                                    break;
                                }
                            } else {
                                listening.failure("主灯5段调光分，占用字节出错");
                                break;
                            }
                        } else if ("主灯5段调光亮度".equals(name)) {
                            byte[] data = convertFormat(dictionaries.getFormat(), nfcDeviceInfo.getMainLight5Brightness());
                            if (data != null && data.length == dictionaries.getTakeByte()) {
                                try {
                                    mTag.writeBytes(dictionaries.getStartAddress(), data);
                                } catch (STException e) {
                                    e.printStackTrace();
                                    listening.failure("主灯5段调光亮度，写入失败");
                                    break;
                                }
                            } else {
                                listening.failure("主灯5段调光亮度，占用字节出错");
                                break;
                            }
                        } else if ("主灯6段调光时".equals(name)) {
                            byte[] data = convertFormat(dictionaries.getFormat(), nfcDeviceInfo.getMainLight6Hour());
                            if (data != null && data.length == dictionaries.getTakeByte()) {
                                try {
                                    mTag.writeBytes(dictionaries.getStartAddress(), data);
                                } catch (STException e) {
                                    e.printStackTrace();
                                    listening.failure("主灯6段调光时，写入失败");
                                    break;
                                }
                            } else {
                                listening.failure("主灯6段调光时，占用字节出错");
                                break;
                            }
                        } else if ("主灯6段调光分".equals(name)) {
                            byte[] data = convertFormat(dictionaries.getFormat(), nfcDeviceInfo.getMainLight6Minute());
                            if (data != null && data.length == dictionaries.getTakeByte()) {
                                try {
                                    mTag.writeBytes(dictionaries.getStartAddress(), data);
                                } catch (STException e) {
                                    e.printStackTrace();
                                    listening.failure("主灯6段调光分，写入失败");
                                    break;
                                }
                            } else {
                                listening.failure("主灯6段调光分，占用字节出错");
                                break;
                            }
                        } else if ("主灯6段调光亮度".equals(name)) {
                            byte[] data = convertFormat(dictionaries.getFormat(), nfcDeviceInfo.getMainLight6Brightness());
                            if (data != null && data.length == dictionaries.getTakeByte()) {
                                try {
                                    mTag.writeBytes(dictionaries.getStartAddress(), data);
                                } catch (STException e) {
                                    e.printStackTrace();
                                    listening.failure("主灯6段调光亮度，写入失败");
                                    break;
                                }
                            } else {
                                listening.failure("主灯6段调光亮度，占用字节出错");
                                break;
                            }
                        } else if ("副灯1段调光时".equals(name)) {
                            byte[] data = convertFormat(dictionaries.getFormat(), nfcDeviceInfo.getAuxiliaryLight1Hour());
                            if (data != null && data.length == dictionaries.getTakeByte()) {
                                try {
                                    mTag.writeBytes(dictionaries.getStartAddress(), data);
                                } catch (STException e) {
                                    e.printStackTrace();
                                    listening.failure("副灯1段调光时，写入失败");
                                    break;
                                }
                            } else {
                                listening.failure("副灯1段调光时，占用字节出错");
                                break;
                            }
                        } else if ("副灯1段调光分".equals(name)) {
                            byte[] data = convertFormat(dictionaries.getFormat(), nfcDeviceInfo.getAuxiliaryLight1Minute());
                            if (data != null && data.length == dictionaries.getTakeByte()) {
                                try {
                                    mTag.writeBytes(dictionaries.getStartAddress(), data);
                                } catch (STException e) {
                                    e.printStackTrace();
                                    listening.failure("副灯1段调光分，写入失败");
                                    break;
                                }
                            } else {
                                listening.failure("副灯1段调光分，占用字节出错");
                                break;
                            }
                        } else if ("副灯1段调光亮度".equals(name)) {
                            byte[] data = convertFormat(dictionaries.getFormat(), nfcDeviceInfo.getAuxiliaryLight1Brightness());
                            if (data != null && data.length == dictionaries.getTakeByte()) {
                                try {
                                    mTag.writeBytes(dictionaries.getStartAddress(), data);
                                } catch (STException e) {
                                    e.printStackTrace();
                                    listening.failure("副灯1段调光亮度，写入失败");
                                    break;
                                }
                            } else {
                                listening.failure("副灯1段调光亮度，占用字节出错");
                                break;
                            }
                        } else if ("副灯2段调光时".equals(name)) {
                            byte[] data = convertFormat(dictionaries.getFormat(), nfcDeviceInfo.getAuxiliaryLight2Hour());
                            if (data != null && data.length == dictionaries.getTakeByte()) {
                                try {
                                    mTag.writeBytes(dictionaries.getStartAddress(), data);
                                } catch (STException e) {
                                    e.printStackTrace();
                                    listening.failure("副灯2段调光时，写入失败");
                                    break;
                                }
                            } else {
                                listening.failure("副灯2段调光时，占用字节出错");
                                break;
                            }
                        } else if ("副灯2段调光分".equals(name)) {
                            byte[] data = convertFormat(dictionaries.getFormat(), nfcDeviceInfo.getAuxiliaryLight2Minute());
                            if (data != null && data.length == dictionaries.getTakeByte()) {
                                try {
                                    mTag.writeBytes(dictionaries.getStartAddress(), data);
                                } catch (STException e) {
                                    e.printStackTrace();
                                    listening.failure("副灯2段调光分，写入失败");
                                    break;
                                }
                            } else {
                                listening.failure("副灯2段调光分，占用字节出错");
                                break;
                            }
                        } else if ("副灯2段调光亮度".equals(name)) {
                            byte[] data = convertFormat(dictionaries.getFormat(), nfcDeviceInfo.getAuxiliaryLight2Brightness());
                            if (data != null && data.length == dictionaries.getTakeByte()) {
                                try {
                                    mTag.writeBytes(dictionaries.getStartAddress(), data);
                                } catch (STException e) {
                                    e.printStackTrace();
                                    listening.failure("副灯2段调光亮度，写入失败");
                                    break;
                                }
                            } else {
                                listening.failure("副灯2段调光亮度，占用字节出错");
                                break;
                            }
                        } else if ("副灯3段调光时".equals(name)) {
                            byte[] data = convertFormat(dictionaries.getFormat(), nfcDeviceInfo.getAuxiliaryLight3Hour());
                            if (data != null && data.length == dictionaries.getTakeByte()) {
                                try {
                                    mTag.writeBytes(dictionaries.getStartAddress(), data);
                                } catch (STException e) {
                                    e.printStackTrace();
                                    listening.failure("副灯3段调光时，写入失败");
                                    break;
                                }
                            } else {
                                listening.failure("副灯3段调光时，占用字节出错");
                                break;
                            }
                        } else if ("副灯3段调光分".equals(name)) {
                            byte[] data = convertFormat(dictionaries.getFormat(), nfcDeviceInfo.getAuxiliaryLight3Minute());
                            if (data != null && data.length == dictionaries.getTakeByte()) {
                                try {
                                    mTag.writeBytes(dictionaries.getStartAddress(), data);
                                } catch (STException e) {
                                    e.printStackTrace();
                                    listening.failure("副灯3段调光分，写入失败");
                                    break;
                                }
                            } else {
                                listening.failure("副灯3段调光分，占用字节出错");
                                break;
                            }
                        } else if ("副灯3段调光亮度".equals(name)) {
                            byte[] data = convertFormat(dictionaries.getFormat(), nfcDeviceInfo.getAuxiliaryLight3Brightness());
                            if (data != null && data.length == dictionaries.getTakeByte()) {
                                try {
                                    mTag.writeBytes(dictionaries.getStartAddress(), data);
                                } catch (STException e) {
                                    e.printStackTrace();
                                    listening.failure("副灯3段调光亮度，写入失败");
                                    break;
                                }
                            } else {
                                listening.failure("副灯3段调光亮度，占用字节出错");
                                break;
                            }
                        } else if ("副灯4段调光时".equals(name)) {
                            byte[] data = convertFormat(dictionaries.getFormat(), nfcDeviceInfo.getAuxiliaryLight4Hour());
                            if (data != null && data.length == dictionaries.getTakeByte()) {
                                try {
                                    mTag.writeBytes(dictionaries.getStartAddress(), data);
                                } catch (STException e) {
                                    e.printStackTrace();
                                    listening.failure("副灯4段调光时，写入失败");
                                    break;
                                }
                            } else {
                                listening.failure("副灯4段调光时，占用字节出错");
                                break;
                            }
                        } else if ("副灯4段调光分".equals(name)) {
                            byte[] data = convertFormat(dictionaries.getFormat(), nfcDeviceInfo.getAuxiliaryLight4Minute());
                            if (data != null && data.length == dictionaries.getTakeByte()) {
                                try {
                                    mTag.writeBytes(dictionaries.getStartAddress(), data);
                                } catch (STException e) {
                                    e.printStackTrace();
                                    listening.failure("副灯4段调光分，写入失败");
                                    break;
                                }
                            } else {
                                listening.failure("副灯4段调光分，占用字节出错");
                                break;
                            }
                        } else if ("副灯4段调光亮度".equals(name)) {
                            byte[] data = convertFormat(dictionaries.getFormat(), nfcDeviceInfo.getAuxiliaryLight4Brightness());
                            if (data != null && data.length == dictionaries.getTakeByte()) {
                                try {
                                    mTag.writeBytes(dictionaries.getStartAddress(), data);
                                } catch (STException e) {
                                    e.printStackTrace();
                                    listening.failure("副灯4段调光亮度，写入失败");
                                    break;
                                }
                            } else {
                                listening.failure("副灯4段调光亮度，占用字节出错");
                                break;
                            }
                        } else if ("副灯5段调光时".equals(name)) {
                            byte[] data = convertFormat(dictionaries.getFormat(), nfcDeviceInfo.getAuxiliaryLight5Hour());
                            if (data != null && data.length == dictionaries.getTakeByte()) {
                                try {
                                    mTag.writeBytes(dictionaries.getStartAddress(), data);
                                } catch (STException e) {
                                    e.printStackTrace();
                                    listening.failure("副灯5段调光时，写入失败");
                                    break;
                                }
                            } else {
                                listening.failure("副灯5段调光时，占用字节出错");
                                break;
                            }
                        } else if ("副灯5段调光分".equals(name)) {
                            byte[] data = convertFormat(dictionaries.getFormat(), nfcDeviceInfo.getAuxiliaryLight5Minute());
                            if (data != null && data.length == dictionaries.getTakeByte()) {
                                try {
                                    mTag.writeBytes(dictionaries.getStartAddress(), data);
                                } catch (STException e) {
                                    e.printStackTrace();
                                    listening.failure("副灯5段调光分，写入失败");
                                    break;
                                }
                            } else {
                                listening.failure("副灯5段调光分，占用字节出错");
                                break;
                            }
                        } else if ("副灯5段调光亮度".equals(name)) {
                            byte[] data = convertFormat(dictionaries.getFormat(), nfcDeviceInfo.getAuxiliaryLight5Brightness());
                            if (data != null && data.length == dictionaries.getTakeByte()) {
                                try {
                                    mTag.writeBytes(dictionaries.getStartAddress(), data);
                                } catch (STException e) {
                                    e.printStackTrace();
                                    listening.failure("副灯5段调光亮度，写入失败");
                                    break;
                                }
                            } else {
                                listening.failure("副灯5段调光亮度，占用字节出错");
                                break;
                            }
                        } else if ("副灯6段调光时".equals(name)) {
                            byte[] data = convertFormat(dictionaries.getFormat(), nfcDeviceInfo.getAuxiliaryLight6Hour());
                            if (data != null && data.length == dictionaries.getTakeByte()) {
                                try {
                                    mTag.writeBytes(dictionaries.getStartAddress(), data);
                                } catch (STException e) {
                                    e.printStackTrace();
                                    listening.failure("副灯6段调光时，写入失败");
                                    break;
                                }
                            } else {
                                listening.failure("副灯6段调光时，占用字节出错");
                                break;
                            }
                        } else if ("副灯6段调光分".equals(name)) {
                            byte[] data = convertFormat(dictionaries.getFormat(), nfcDeviceInfo.getAuxiliaryLight6Minute());
                            if (data != null && data.length == dictionaries.getTakeByte()) {
                                try {
                                    mTag.writeBytes(dictionaries.getStartAddress(), data);
                                } catch (STException e) {
                                    e.printStackTrace();
                                    listening.failure("副灯6段调光分，写入失败");
                                    break;
                                }
                            } else {
                                listening.failure("副灯6段调光分，占用字节出错");
                                break;
                            }
                        } else if ("副灯6段调光亮度".equals(name)) {
                            byte[] data = convertFormat(dictionaries.getFormat(), nfcDeviceInfo.getAuxiliaryLight6Brightness());
                            if (data != null && data.length == dictionaries.getTakeByte()) {
                                try {
                                    mTag.writeBytes(dictionaries.getStartAddress(), data);
                                } catch (STException e) {
                                    e.printStackTrace();
                                    listening.failure("副灯6段调光亮度，写入失败");
                                    break;
                                }
                            } else {
                                listening.failure("副灯6段调光亮度，占用字节出错");
                                break;
                            }
                        } else if ("过流保护开关".equals(name)) {
                            byte[] data = convertFormat(dictionaries.getFormat(), nfcDeviceInfo.getOvercurrentProtectionWwitch());
                            if (data != null && data.length == dictionaries.getTakeByte()) {
                                try {
                                    mTag.writeBytes(dictionaries.getStartAddress(), data);
                                } catch (STException e) {
                                    e.printStackTrace();
                                    listening.failure("过流保护开关，写入失败");
                                    break;
                                }
                            } else {
                                listening.failure("过流保护开关，占用字节出错");
                                break;
                            }
                        } else if ("漏电保护开关".equals(name)) {
                            byte[] data = convertFormat(dictionaries.getFormat(), nfcDeviceInfo.getEarthLeakageCircuitBreaker());
                            if (data != null && data.length == dictionaries.getTakeByte()) {
                                try {
                                    mTag.writeBytes(dictionaries.getStartAddress(), data);
                                } catch (STException e) {
                                    e.printStackTrace();
                                    listening.failure("漏电保护开关，写入失败");
                                    break;
                                }
                            } else {
                                listening.failure("漏电保护开关，占用字节出错");
                                break;
                            }
                        } else if ("照度开灯开关".equals(name)) {
                            byte[] data = convertFormat(dictionaries.getFormat(), nfcDeviceInfo.getIlluminationLightSwitch());
                            if (data != null && data.length == dictionaries.getTakeByte()) {
                                try {
                                    mTag.writeBytes(dictionaries.getStartAddress(), data);
                                } catch (STException e) {
                                    e.printStackTrace();
                                    listening.failure("照度开灯开关，写入失败");
                                    break;
                                }
                            } else {
                                listening.failure("照度开灯开关，占用字节出错");
                                break;
                            }
                        } else if ("过压保护阈值".equals(name)) {
                            byte[] data = convertFormat(dictionaries.getFormat(), nfcDeviceInfo.getOvervoltageProtectionThreshold());
                            if (data != null && data.length == dictionaries.getTakeByte()) {
                                try {
                                    mTag.writeBytes(dictionaries.getStartAddress(), data);
                                } catch (STException e) {
                                    e.printStackTrace();
                                    listening.failure("过压保护阈值，写入失败");
                                    break;
                                }
                            } else {
                                listening.failure("过压保护阈值，占用字节出错");
                                break;
                            }
                        } else if ("欠压保护阈值".equals(name)) {
                            byte[] data = convertFormat(dictionaries.getFormat(), nfcDeviceInfo.getUndervoltageProtectionThreshold());
                            if (data != null && data.length == dictionaries.getTakeByte()) {
                                try {
                                    mTag.writeBytes(dictionaries.getStartAddress(), data);
                                } catch (STException e) {
                                    e.printStackTrace();
                                    listening.failure("欠压保护阈值，写入失败");
                                    break;
                                }
                            } else {
                                listening.failure("欠压保护阈值，占用字节出错");
                                break;
                            }
                        } else if ("过流保护阈值".equals(name)) {
                            byte[] data = convertFormat(dictionaries.getFormat(), nfcDeviceInfo.getOvercurrentProtectionThreshold());
                            if (data != null && data.length == dictionaries.getTakeByte()) {
                                try {
                                    mTag.writeBytes(dictionaries.getStartAddress(), data);
                                } catch (STException e) {
                                    e.printStackTrace();
                                    listening.failure("过流保护阈值，写入失败");
                                    break;
                                }
                            } else {
                                listening.failure("过流保护阈值，占用字节出错");
                                break;
                            }
                        } else if ("欠流保护阈值".equals(name)) {
                            byte[] data = convertFormat(dictionaries.getFormat(), nfcDeviceInfo.getUndercurrentProtectionThreshold());
                            if (data != null && data.length == dictionaries.getTakeByte()) {
                                try {
                                    mTag.writeBytes(dictionaries.getStartAddress(), data);
                                } catch (STException e) {
                                    e.printStackTrace();
                                    listening.failure("欠流保护阈值，写入失败");
                                    break;
                                }
                            } else {
                                listening.failure("欠流保护阈值，占用字节出错");
                                break;
                            }
                        } else if ("报警开关".equals(name)) {
                            byte[] data = convertFormat(dictionaries.getFormat(), nfcDeviceInfo.getAlarmSwitch());
                            if (data != null && data.length == dictionaries.getTakeByte()) {
                                try {
                                    mTag.writeBytes(dictionaries.getStartAddress(), data);
                                } catch (STException e) {
                                    e.printStackTrace();
                                    listening.failure("报警开关，写入失败");
                                    break;
                                }
                            } else {
                                listening.failure("报警开关，占用字节出错");
                                break;
                            }
                        } else if ("漏电保护阈值".equals(name)) {
                            byte[] data = convertFormat(dictionaries.getFormat(), nfcDeviceInfo.getLeakageProtectionThreshold());
                            if (data != null && data.length == dictionaries.getTakeByte()) {
                                try {
                                    mTag.writeBytes(dictionaries.getStartAddress(), data);
                                } catch (STException e) {
                                    e.printStackTrace();
                                    listening.failure("漏电保护阈值，写入失败");
                                    break;
                                }
                            } else {
                                listening.failure("漏电保护阈值，占用字节出错");
                                break;
                            }
                        } else if ("照度开灯阈值".equals(name)) {
                            byte[] data = convertFormat(dictionaries.getFormat(), nfcDeviceInfo.getIlluminationLightThreshold());
                            if (data != null && data.length == dictionaries.getTakeByte()) {
                                try {
                                    mTag.writeBytes(dictionaries.getStartAddress(), data);
                                } catch (STException e) {
                                    e.printStackTrace();
                                    listening.failure("照度开灯阈值，写入失败");
                                    break;
                                }
                            } else {
                                listening.failure("照度开灯阈值，占用字节出错");
                                break;
                            }
                        } else if ("照度关灯阈值".equals(name)) {
                            byte[] data = convertFormat(dictionaries.getFormat(), nfcDeviceInfo.getLightOffThreshold());
                            if (data != null && data.length == dictionaries.getTakeByte()) {
                                try {
                                    mTag.writeBytes(dictionaries.getStartAddress(), data);
                                } catch (STException e) {
                                    e.printStackTrace();
                                    listening.failure("照度关灯阈值，写入失败");
                                    break;
                                }
                            } else {
                                listening.failure("照度关灯阈值，占用字节出错");
                                break;
                            }
                        } else if ("灯杆倒塌报警开关".equals(name)) {
                            byte[] data = convertFormat(dictionaries.getFormat(), nfcDeviceInfo.getLampPoleCollapseAlarmSwitch());
                            if (data != null && data.length == dictionaries.getTakeByte()) {
                                try {
                                    mTag.writeBytes(dictionaries.getStartAddress(), data);
                                } catch (STException e) {
                                    e.printStackTrace();
                                    listening.failure("灯杆倒塌报警开关，写入失败");
                                    break;
                                }
                            } else {
                                listening.failure("灯杆倒塌报警开关，占用字节出错");
                                break;
                            }
                        } else if ("项目地区".equals(name)) {
                            byte[] data = convertFormat(dictionaries.getFormat(), nfcDeviceInfo.getProjectArea());
                            if (data != null && data.length == dictionaries.getTakeByte()) {
                                try {
                                    mTag.writeBytes(dictionaries.getStartAddress(), data);
                                } catch (STException e) {
                                    e.printStackTrace();
                                    listening.failure("项目地区，写入失败");
                                    break;
                                }
                            } else {
                                listening.failure("项目地区，占用字节出错");
                                break;
                            }
                        } else if ("项目编号".equals(name)) {
                            byte[] data = convertFormat(dictionaries.getFormat(), nfcDeviceInfo.getProjectNumber());
                            if (data != null && data.length == dictionaries.getTakeByte()) {
                                try {
                                    mTag.writeBytes(dictionaries.getStartAddress(), data);
                                } catch (STException e) {
                                    e.printStackTrace();
                                    listening.failure("项目编号，写入失败");
                                    break;
                                }
                            } else {
                                listening.failure("项目编号，占用字节出错");
                                break;
                            }
                        } else if ("IMEI".equals(name)) {
                            byte[] data = convertFormat(dictionaries.getFormat(), nfcDeviceInfo.getImei());
                            if (data != null && data.length == dictionaries.getTakeByte()) {
                                try {
                                    mTag.writeBytes(dictionaries.getStartAddress(), data);
                                } catch (STException e) {
                                    e.printStackTrace();
                                    listening.failure("IMEI，写入失败");
                                    break;
                                }
                            } else {
                                listening.failure("IMEI，占用字节出错");
                                break;
                            }
                        } else if ("维修IMEI".equals(name)) {
                            byte[] data = convertFormat(dictionaries.getFormat(), nfcDeviceInfo.getMaintainImei());
                            if (data != null && data.length == dictionaries.getTakeByte()) {
                                try {
                                    mTag.writeBytes(dictionaries.getStartAddress(), data);
                                } catch (STException e) {
                                    e.printStackTrace();
                                    listening.failure("维修IMEI，写入失败");
                                    break;
                                }
                            } else {
                                listening.failure("维修IMEI，占用字节出错");
                                break;
                            }
                        } else if ("执行底板ID".equals(name)) {
                            byte[] data = convertFormat(dictionaries.getFormat(), nfcDeviceInfo.getBaseplateId());
                            if (data != null && data.length == dictionaries.getTakeByte()) {
                                try {
                                    mTag.writeBytes(dictionaries.getStartAddress(), data);
                                } catch (STException e) {
                                    e.printStackTrace();
                                    listening.failure("执行底板ID，写入失败");
                                    break;
                                }
                            } else {
                                listening.failure("执行底板ID，占用字节出错");
                                break;
                            }
                        }
                    }

                }

                if(flag){
                    listening.succeed();
                }else{
                    listening.failure("写入内容有误");
                }


            } catch (Exception e) {
                e.printStackTrace();
              //  listening.failure("设备参数错误！");
            }

        } else {
            listening.failure("设备类型错误！");
        }

    }

    /**
     * 写入nfc
     * @param parameters 需要写入的参数字符
     * @param listening   监听
     * @param mTag        nfc对象
     * @param dictionaries 指定的字典格式
     * @param tagName     当前标签名称
     * @return
     */
    private static boolean writeNfc(String parameters, OnNfcDataListening listening, ST25DVTag mTag, DataDictionaries dictionaries, String tagName) {
        byte[] data = convertFormat(dictionaries.getFormat(), parameters);
        if (data != null && data.length == dictionaries.getTakeByte()) {
            try {
                mTag.writeBytes(dictionaries.getStartAddress(), data);
            } catch (STException e) {
                e.printStackTrace();
                listening.failure(tagName + "，写入失败");
                return false;
            }
        } else {
            listening.failure(tagName + "，占用字节出错");
            return false;
        }
        return true;
    }

    /**
     * 根据类型转换参数
     *
     * @param convertFormat 转换类型
     * @param parameters    转换的参数
     * @return
     */
    private static byte[] convertFormat(String convertFormat, String parameters) {
        byte[] data = null;
        if (convertFormat.equals("HEX")) {
            // 更新标志位 转int
            int intData = Integer.valueOf(parameters, 16);
            data = new byte[]{(byte) intData};
        } else if (convertFormat.equals("DEC")) {
            int intData = Integer.parseInt(parameters);
            data = new byte[]{(byte) intData};
        } else if (convertFormat.equals("STR")) {
            data = parameters.getBytes();
        }
        return data;
    }


    public interface OnNfcDataListening {

        void succeed();

        void failure(String error);

    }
}
