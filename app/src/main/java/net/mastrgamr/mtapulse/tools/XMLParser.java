package net.mastrgamr.mtapulse.tools;

import net.mastrgamr.mtapulse.feedobjects.TransportationType;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Project: GTFSParsing
 * Author: Stuart Smith
 * Date: 1/23/2015
 */

/**
 * Encapsulating class that takes a String URL argument and parses the XML class according to
 * "serviceStatus.txt" on the MTA datamine servers.
 * TODO: Remove references to TransportationType (especially the List) to make this class self sustainable.
 */
public class XMLParser {

    private static String text = null;

    private URL url;
    private SAXParserFactory saxParserFactory;
    private SAXParser saxParser;

    private TransportationType transitType;
    private List<TransportationType> transitTypes;

    public XMLParser(String url) throws MalformedURLException, ParserConfigurationException, SAXException {
        this.url = new URL(url);
        transitTypes = new ArrayList<TransportationType>();
        saxParserFactory = SAXParserFactory.newInstance();
        //saxParserFactory.setValidating(true); //Error, Why??
        saxParser = saxParserFactory.newSAXParser();
    }

    public void parse() throws IOException, SAXException {
        saxParser.parse(url.openStream(), new Handler());
    }

    private class Handler extends DefaultHandler {
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            if(qName.equals("line")){
                transitType = new TransportationType();
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {

            if(qName.equals("line")){
                transitTypes.add(transitType);
            } else if (qName.equalsIgnoreCase("name")) {
                //System.out.println(text);
                transitType.setName(text);
            } else if (qName.equalsIgnoreCase("status")) {
                //System.out.println(text);
                transitType.setStatus(text);
            }else if (qName.equalsIgnoreCase("text")) {
                //System.out.println(text);
                transitType.setText(text);
            } else if (qName.equalsIgnoreCase("date")) {
                //System.out.println(text);
                transitType.setDate(text);
            } else if (qName.equalsIgnoreCase("time")) {
                //System.out.println(text);
                transitType.setTime(text);
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            text = String.copyValueOf(ch, start, length).trim();
        }
    }

    public List<TransportationType> getTransportationTypes() {
        return transitTypes;
    }
}
