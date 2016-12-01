package com.a360ground.epubreader360.EpubManipulation.SAXParsers;

import com.a360ground.epubreader360.EpubManipulation.Model.Spine;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Created by Kiyos Solomon on 11/2/2016.
 */
public class SpineSAXParser extends DefaultHandler{
    String startTag;
    public static Spine spine;
    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        startTag = localName;
        if("itemref".equalsIgnoreCase(localName)){
            String resourceId = attributes.getValue("idref");
            Boolean isLinear = false;
            if("yes".equals(attributes.getValue("linear")))
            isLinear = true;
            if("no".equals(attributes.getValue("linear")))
            isLinear = false;
            spine.setLinear(isLinear);
            spine.setResourceId(resourceId);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }
}
