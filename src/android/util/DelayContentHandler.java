package android.util;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * @hide
 */
public class DelayContentHandler implements ContentHandler {
    public static final String APPS = "apps";
    public static final String APP = "app";
    public static final String APP_NAME = "aname";
    public static final String THREADS = "threads";
    public static final String THREAD = "thread";
    public static final String THREAD_NAME = "tname";
    public static final String DELAYPOINTS = "delaypoints";
    public static final String DELAYPOINT = "delaypoint";
    public static final String CLASS = "class";
    public static final String METHOD = "method";
    public static final String LOC = "loc";
    public static final String DELAY = "delay";

    public static boolean inApp = false;
    public static boolean inThread = false;
    public static boolean inAppNameElem = false;
    public static boolean inThreadNameElem = false;
    public static boolean inClassElem = false;
    public static boolean inMethodElem = false;
    public static boolean inLocElem = false;
    public static boolean inDelayElem = false;

    private String currentAppName;
    private String currentThreadName;
    private DelayMap.DelayPoint currentDelayPoint;


    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        if (APP.equals(qName)) {
            // put k-v to Map when getting app's name
            inApp = true;
        } else if (APP_NAME.equals(qName)) {
            inAppNameElem = true;
        } else if (THREAD.equals(qName)) {
            // put k-v to Map when getting thread's name
            inThread = true;
        } else if (THREAD_NAME.equals(qName)) {
            inThreadNameElem = true;
        } else if (DELAYPOINT.equals(qName)) {
            // create a DelayPoint instance for a new
            currentDelayPoint = new DelayMap.DelayPoint();
        } else if (CLASS.equals(qName)) {
            inClassElem = true;
        } else if (METHOD.equals(qName)) {
            inMethodElem = true;
        } else if (LOC.equals(qName)){
            inLocElem = true;
        } else if (DELAY.equals(qName)) {
            inDelayElem = true;
        }
    }


    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (APP.equals(qName)) {
            inApp = false;
            // assign null; wait startElement <app> (actually <aname>) to assign a new app name
            currentAppName = null;
        } else if (APP_NAME.equals(qName)) {
            inAppNameElem = false;
        } else if (THREAD.equals(qName)) {
            inThread = false;
            // assign null; wait startElement <thread> (actually <tname>) to assign a new thread name
            currentThreadName = null;
        } else if (THREAD_NAME.equals(qName)) {
            inThreadNameElem = false;
        } else if (DELAYPOINT.equals(qName)) {
            // class.name#methodName:loc as key, for we don't know Delay time in source code
            DelayMap.insertDelayPoint(currentAppName, currentThreadName, currentDelayPoint);
            // assign null; wait startElement <delaypoint> to create a new instance
            currentDelayPoint = null;
        } else if (CLASS.equals(qName)) {
            inClassElem = false;
        } else if (METHOD.equals(qName)) {
            inMethodElem = false;
        } else if (LOC.equals(qName)){
            inLocElem = false;
        } else if (DELAY.equals(qName)) {
            inDelayElem = false;
        }
    }


    public void characters(char[] ch, int start, int length) throws SAXException {
        if (inAppNameElem) {
            currentAppName = new String(ch, start, length);
        } else if (inThreadNameElem) {
            currentThreadName = new String(ch, start, length);
        } else if (inClassElem) {
            currentDelayPoint.className = new String(ch, start, length);
        } else if (inMethodElem) {
            currentDelayPoint.methodName = new String(ch, start, length);
        } else if (inLocElem) {
            currentDelayPoint.loc = new Integer(new String(ch, start, length));
        } else if (inDelayElem) {
            currentDelayPoint.delay = new Integer(new String(ch, start, length));
        }
    }


    public void setDocumentLocator(Locator locator) {

    }


    public void startDocument() throws SAXException {

    }


    public void endDocument() throws SAXException {

    }


    public void startPrefixMapping(String prefix, String uri) throws SAXException {

    }


    public void endPrefixMapping(String prefix) throws SAXException {

    }


    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {

    }


    public void processingInstruction(String target, String data) throws SAXException {

    }


    public void skippedEntity(String name) throws SAXException {

    }
}
