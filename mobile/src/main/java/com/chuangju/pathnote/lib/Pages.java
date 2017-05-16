package com.chuangju.pathnote.lib;

import android.support.v4.util.LongSparseArray;

import org.xml.sax.Attributes;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.StringWriter;

public class Pages extends BaseXmlObject {
    public int total;
    public long currentPage;
    public boolean key;
    public LongSparseArray<Page> pageList = new LongSparseArray<>();

    public Pages() {
        super();
    }

    public Pages(int total, long currentPage, boolean key) {
        super();
        this.total = total;
        this.currentPage = currentPage;
        this.key = key;
    }

    public Pages(Attributes attr) {
        super();
        this.total = parseString2Int(attr.getValue("total"));
        this.currentPage = parseString2Long(attr.getValue("current_page"));
        this.key = parseBoolean(attr.getValue("key"));
    }

    @Override
    public void setBuilderFromBody(XmlSerializer xmlSerializer) throws IOException {
        xmlSerializer.startTag("", "pages");
        xmlSerializer.attribute("", "total", String.valueOf(total));
        xmlSerializer.attribute("", "current_page", String.valueOf(currentPage));
        xmlSerializer.attribute("", "key", key ? "1" : "0");
        for (int i = 0; i < pageList.size(); i++) {
            Page page = pageList.valueAt(i);
            if (key || currentPage == page.id) {
                page.setBuilderFromBody(xmlSerializer);
            }
        }
        xmlSerializer.endTag("", "pages");
    }

    public String buildXml() throws Exception {
        XmlSerializer xmlSerializer = XmlPullParserFactory.newInstance().newSerializer();
        StringWriter xmlWriter = new StringWriter();
        xmlSerializer.setOutput(xmlWriter);
        xmlSerializer.startDocument("UTF-8", true);
        setBuilderFromBody(xmlSerializer);
        xmlSerializer.endDocument();
        return xmlWriter.toString();
    }

    @Override
    public String toString() {
        return "Pages{" +
                "total=" + total +
                ", currentPage=" + currentPage +
                ", key=" + key +
                ", pageList=" + pageList +
                '}';
    }
}
