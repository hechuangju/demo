package com.chuangju.pathnote.lib;

import android.support.v4.util.LongSparseArray;

import com.chuangju.pathnote.lib.shape.BaseDraw;

import org.xml.sax.Attributes;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;

/**
 * Created by hechuangju on 15/12/4.
 */
public class SubPage extends BaseXmlObject {
    public final long id;
    public boolean key;
    public long currentDraw;
    public LongSparseArray<BaseDraw> lines = new LongSparseArray<>();

    public SubPage(long id) {
        this.id = id;
    }

    public SubPage(Attributes attributes) {
        this(parseString2Long(attributes.getValue("id")));
    }

    @Override
    public void setBuilderFromBody(XmlSerializer xmlSerializer) throws IOException {
        xmlSerializer.startTag("", "objects");
        xmlSerializer.attribute("", "id", String.valueOf(id));
        for (int i = 0; i < lines.size(); i++) {
            BaseDraw baseDraw = lines.valueAt(i);
            if (key || currentDraw == baseDraw.id) {
                baseDraw.setBuilderFromBody(xmlSerializer);
            }
        }
        xmlSerializer.endTag("", "objects");
    }

    @Override
    public String toString() {
        return "SubPage{" +
                "id=" + id +
                ", key=" + key +
                ", currentDraw=" + currentDraw +
                ", lines=" + lines +
                '}';
    }
}
