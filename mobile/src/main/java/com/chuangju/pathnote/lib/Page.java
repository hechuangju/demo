package com.chuangju.pathnote.lib;

import android.support.v4.util.LongSparseArray;

import org.xml.sax.Attributes;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;

public class Page extends BaseXmlObject {
    public boolean isKeyFrame;
    public int creatorId;
    public int status;
    public long id;
    public PageType type = PageType.WHITEBOARD;
    public TeacherRule teacherRule = new TeacherRule();
    public Content content = new Content();
    public LongSparseArray<SubPage> subPages = new LongSparseArray<>();

    public enum PageType {
        WHITEBOARD,
        DOCUMENT,
        WEB,
        PICTURE,
        PPT,
        MP3;
        private int type = 0;

        static {
            WHITEBOARD.type = 1;
            DOCUMENT.type = 2;
            WEB.type = 3;
            PICTURE.type = 4;
            PPT.type = 5;
            MP3.type = 9;
        }

        public static PageType from(int type) {
            for (PageType pageType : PageType.values()) {
                if (pageType.type == type)
                    return pageType;
            }
            return null;
        }

        public int getType() {
            return type;
        }
    }

    public Page() {
    }

    public Page(Attributes attributes) {
        this.id = parseString2Long(attributes.getValue("id"));
        this.isKeyFrame = parseBoolean(attributes.getValue("key"));
        this.type = PageType.from(parseString2Int(attributes.getValue("type")));
        this.creatorId = parseString2Int(attributes.getValue("creator"));
        this.status = parseString2Int(attributes.getValue("status"));
    }

    public Page updatePage(Page page) {
        this.isKeyFrame = page.isKeyFrame;
        this.type = page.type;
        this.status = page.status;
        this.teacherRule = page.teacherRule;
        this.content = page.content;
        return this;
    }

    @Override
    public void setBuilderFromBody(XmlSerializer xmlSerializer) throws IOException {
        xmlSerializer.startTag("", "page");
        xmlSerializer.attribute("", "id", String.valueOf(id));
        xmlSerializer.attribute("", "key", isKeyFrame ? "1" : "0");
        xmlSerializer.attribute("", "status", String.valueOf(status));
        xmlSerializer.attribute("", "type", String.valueOf(type.type));
        xmlSerializer.attribute("", "creator", String.valueOf(creatorId));
        content.setBuilderFromBody(xmlSerializer);
        teacherRule.setBuilderFromBody(xmlSerializer);
        for (int i = 0; i < subPages.size(); i++) {
            SubPage subPage = subPages.valueAt(i);
            if (isKeyFrame || content.current_document == subPage.id) {
                subPage.setBuilderFromBody(xmlSerializer);
            }
        }
        xmlSerializer.endTag("", "page");
    }

    public TeacherRule getTeacherRule() {
        return teacherRule;
    }

    public boolean isKeyFrame() {
        return isKeyFrame;
    }

    public void setKeyFrame(boolean isKeyFrame) {
        this.isKeyFrame = isKeyFrame;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }

    public PageType getType() {
        return type;
    }

    public void setType(int type) {
        this.type = PageType.from(type);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Content getContent() {
        return content;
    }


    public static class Content extends BaseXmlObject {
        public String url = "";
        public String audioPath = "";
        public String imagePath = "";
        public int current_document, width, height, backGroundWidth, backGroundHeight;
        public float xLineLocation = 0.5f, yLineLocation = 0.5f;
        public boolean xLineShow, yLineShow, gridShow;

        public Content() {
            super();
        }

        public Content(Attributes attributes) {
            this.backGroundWidth = parseString2Int(attributes.getValue("backgroundwidth"));
            this.backGroundHeight = parseString2Int(attributes.getValue("backgroundheight"));
            this.audioPath = attributes.getValue("audio");
            this.imagePath = attributes.getValue("image");
            this.url = attributes.getValue("url");
            this.current_document = parseString2Int(attributes.getValue("current_document"));
            this.width = parseString2Int(attributes.getValue("width"));
            this.height = parseString2Int(attributes.getValue("height"));
        }

        public void setupXLine(Attributes attributes) {
            this.xLineShow = parseBoolean(attributes.getValue("show"));
            this.xLineLocation = parseString2Float(attributes.getValue("x"));
        }

        public void setupYLine(Attributes attributes) {
            this.yLineShow = parseBoolean(attributes.getValue("show"));
            this.yLineLocation = parseString2Float(attributes.getValue("y"));
        }

        public void setupGrid(Attributes attributes) {
            this.gridShow = parseBoolean(attributes.getValue("show"));
        }

        @Override
        public void setBuilderFromBody(XmlSerializer xmlSerializer) throws IOException {
            xmlSerializer.startTag("", "content");
            xmlSerializer.attribute("", "url", url);
            xmlSerializer.attribute("", "current_document", String.valueOf(current_document));
            xmlSerializer.attribute("", "width", String.valueOf(width));
            xmlSerializer.attribute("", "height", String.valueOf(height));
            xmlSerializer.startTag("", "x_line");
            xmlSerializer.attribute("", "show", xLineShow ? "1" : "0");
            xmlSerializer.attribute("", "x", String.valueOf(xLineLocation));
            xmlSerializer.endTag("", "x_line");
            xmlSerializer.startTag("", "y_line");
            xmlSerializer.attribute("", "show", yLineShow ? "1" : "0");
            xmlSerializer.attribute("", "y", String.valueOf(yLineLocation));
            xmlSerializer.endTag("", "y_line");
            xmlSerializer.startTag("", "grid");
            xmlSerializer.attribute("", "show", gridShow ? "1" : "0");
            xmlSerializer.endTag("", "grid");
            xmlSerializer.endTag("", "content");
        }

        @Override
        public String toString() {
            return "Content{" +
                    "url='" + url + '\'' +
                    ", audioPath='" + audioPath + '\'' +
                    ", imagePath='" + imagePath + '\'' +
                    ", current_document=" + current_document +
                    ", width=" + width +
                    ", height=" + height +
                    ", backGroundWidth=" + backGroundWidth +
                    ", backGroundHeight=" + backGroundHeight +
                    ", xLineLocation=" + xLineLocation +
                    ", yLineLocation=" + yLineLocation +
                    ", xLineShow=" + xLineShow +
                    ", yLineShow=" + yLineShow +
                    ", gridShow=" + gridShow +
                    '}';
        }
    }

    public static class TeacherRule extends BaseXmlObject {
        public boolean show;
        public float x, y;
        public float width, height;

        public TeacherRule() {
        }

        public TeacherRule(Attributes attributes, Content content) {
            this.show = parseBoolean(attributes.getValue("show"));
            this.x = parseString2Float(attributes.getValue("x"));
            this.y = parseString2Float(attributes.getValue("y"));
            this.width = parseString2Float(attributes.getValue("w"));
            this.height = parseString2Float(attributes.getValue("h"));
            if (content.width > 0 && content.height > 0) {
                this.width = this.width / content.width;
                this.height = this.height / content.height;
            }
        }

        @Override
        public void setBuilderFromBody(XmlSerializer xmlSerializer) throws IOException {
            xmlSerializer.startTag("", "teacher_rule");
            xmlSerializer.attribute("", "show", show ? "1" : "0");
            xmlSerializer.attribute("", "x", String.valueOf(x));
            xmlSerializer.attribute("", "y", String.valueOf(y));
            xmlSerializer.attribute("", "w", String.valueOf(width));
            xmlSerializer.attribute("", "h", String.valueOf(height));
            xmlSerializer.endTag("", "teacher_rule");
        }

        @Override
        public String toString() {
            return "TeacherRule{" +
                    "show=" + show +
                    ", x=" + x +
                    ", y=" + y +
                    ", width=" + width +
                    ", height=" + height +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "Page{" +
                "isKeyFrame=" + isKeyFrame +
                ", creatorId=" + creatorId +
                ", status=" + status +
                ", id=" + id +
                ", type=" + type +
                ", teacherRule=" + teacherRule +
                ", content=" + content +
                ", subPages=" + subPages +
                '}';
    }
}
