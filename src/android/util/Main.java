package android.util;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        try {
            DelayConfigHelper.readConfig();
            DelayConfigHelper.syncConfig();
        } catch (IOException | ParserConfigurationException | SAXException e) {
            throw new RuntimeException(e);
        }
    }
}