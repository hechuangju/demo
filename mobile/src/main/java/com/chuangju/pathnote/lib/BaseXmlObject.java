package com.chuangju.pathnote.lib;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;

/**
 * Created by hechuangju on 2017/4/17.
 */
public abstract class BaseXmlObject {

    public static int parseString2Int(String str) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            return 0;
        }
    }

    public static long parseString2Long(String str) {
        try {
            return Long.parseLong(str);
        } catch (Exception e) {
            return 0;
        }
    }

    public static float parseString2Float(String str) {
        try {
            return Float.parseFloat(str);
        } catch (Exception e) {
            return 0;
        }
    }

    public static boolean parseBoolean(String string) {
        return "1".equals(string) || "true".equals(string);
    }

    public abstract void setBuilderFromBody(XmlSerializer xmlSerializer) throws IOException;
}
