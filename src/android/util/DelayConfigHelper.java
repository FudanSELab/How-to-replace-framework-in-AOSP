package android.util;

import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import java.io.*;

/**
 * @hide
 */
public class DelayConfigHelper {

    public static final String CONFIG_FILE_PATH = "./system/config.xml";

    public static void readConfig()
            throws IOException, ParserConfigurationException, SAXException {
        // hard code config xml file
        File file = new File(CONFIG_FILE_PATH);
        InputStream ins = new FileInputStream(file);
        InputSource source = new InputSource(ins);

        SAXParserFactory factory = SAXParserFactory.newInstance();
        XMLReader xmlReader = factory.newSAXParser().getXMLReader();

        // use self-defined ContentHandler & parse xml
        ContentHandler handler = new DelayContentHandler();
        xmlReader.setContentHandler(handler);
        xmlReader.parse(source);
    }

    public static Integer getDelayTime(
            String aName, String tName, String className, String methodName, Integer loc) {
        return DelayMap.getDelayTime(aName, tName, className, methodName, loc);
    }

    public static boolean insertDelayPoint(
            String aName, String tName, String className, String methodName, Integer loc, Integer delay) {
        DelayMap.DelayPoint dp = DelayMap.DelayPoint.newInstance(className, methodName, loc, delay);
        return DelayMap.insertDelayPoint(aName, tName, dp);
    }

    public static void syncConfig()
            throws IOException {
        FileOutputStream fos = new FileOutputStream(CONFIG_FILE_PATH);
        fos.write(DelayMap.serialize().getBytes());
    }
}