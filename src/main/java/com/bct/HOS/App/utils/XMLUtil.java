package com.bct.HOS.App.utils;

import java.io.File;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
 
public class XMLUtil {
	
    public static Map<String, String> storeToFile(String xmlString, String filePathName) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        Map<String, String> result = null;
        try {
        	result = new HashMap<>();
            builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xmlString)));
            TransformerFactory tranFactory = TransformerFactory.newInstance();
            Transformer aTransformer = tranFactory.newTransformer();
            Source src = new DOMSource(document);
            Result dest = new StreamResult(new File(filePathName));
            aTransformer.transform(src, dest);
            result.put("flag", "true");
        } catch (Exception e) {
        	result.put("flag", "false");
        	result.put("message", e.getMessage());
            e.printStackTrace();
        }
        return result;
    }
}