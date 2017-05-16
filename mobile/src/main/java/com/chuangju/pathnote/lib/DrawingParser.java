package com.chuangju.pathnote.lib;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.chuangju.pathnote.lib.shape.BaseDraw;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Stack;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class DrawingParser extends DefaultHandler {
    private Pages pages;
    private Page page;
    private SubPage subPage;
    private final Context context;
    private Stack<BaseDraw> drawStack;
    private BaseDraw currentDraw;

    public DrawingParser(Context context) {
        this.context = context;
    }

    public static Pages parserXml(Context context, InputStream is) {
        DrawingParser drawingParser = new DrawingParser(context);
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            XMLReader reader = parser.getXMLReader();
            reader.setContentHandler(drawingParser);
            reader.parse(new InputSource(is));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return drawingParser.pages;
    }

    public static Pages parserXml(Context context, String content) {
        DrawingParser drawingParser = new DrawingParser(context);
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            XMLReader reader = parser.getXMLReader();
            reader.setContentHandler(drawingParser);
            reader.parse(new InputSource(new ByteArrayInputStream(content.getBytes())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return drawingParser.pages;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        if ("pages".equals(localName)) {
            pages = new Pages(attributes);
        } else if ("page".equals(localName)) {
            page = new Page(attributes);
            pages.pageList.put(page.getId(), page);
        } else if ("content".equals(localName)) {
            page.content = new Page.Content(attributes);
        } else if ("x_line".equals(localName)) {
            page.content.setupXLine(attributes);
        } else if ("y_line".equals(localName)) {
            page.content.setupYLine(attributes);
        } else if ("grid".equals(localName)) {
            page.content.setupGrid(attributes);
        } else if ("teacher_rule".equals(localName)) {
            page.teacherRule = new Page.TeacherRule(attributes, page.content);
        } else if ("objects".equals(localName)) {
            subPage = new SubPage(attributes);
            page.subPages.put(subPage.id, subPage);
            drawStack = new Stack<>();
        } else if ("object".equals(localName)) {
            try {
                BaseDraw baseDraw = DrawingFactory.createDrawing(context, attributes);
                currentDraw = baseDraw;
                drawStack.push(baseDraw);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
//        if (currentDraw instanceof HaveCharacters) {
//            ((HaveCharacters) currentDraw).characters(ch, start, length);
//        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        if (TextUtils.equals(localName, "object")) {
            BaseDraw baseDraw = drawStack.pop();
            if (drawStack.isEmpty()) {
                page.subPages.get(subPage.id).lines.put(baseDraw.id, baseDraw);
            } else {
                BaseDraw parent = drawStack.peek();
//                if (parent instanceof MathDraw)
//                    ((MathDraw) parent).addChild(baseDraw);
            }
        } else if ("objects".equals(localName)) {
            Log.e(getClass().getSimpleName(), page.subPages.get(subPage.id).lines.size() + "--->" + page.subPages.get(subPage.id).lines.toString());
        }
    }
}
