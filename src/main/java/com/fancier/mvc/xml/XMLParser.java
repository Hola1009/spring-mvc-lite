package com.fancier.mvc.xml;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;

/**
 * @author <a href="https://github.com/hola1009">fancier</a>
 */

public class XMLParser {
    /**
     * 用来解析 mvc.xml 配置文件中的带有包扫描路径的标签
     */
    public static String getBeanPackage(String xmlFile) {
        SAXReader saxReader = new SAXReader();
        InputStream inputStream = XMLParser.class.getClassLoader().getResourceAsStream(xmlFile);
        try {
            Document document = saxReader.read(inputStream);
            Element rootElement = document.getRootElement();
            Element componentScanElement = rootElement.element("component-scan");

            return componentScanElement.attribute("base-package").getText();
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }
}
