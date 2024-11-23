package com.fancier.test;

import com.fancier.mvc.context.WebApplicationContext;
import com.fancier.mvc.xml.XMLParser;
import org.junit.Test;

import java.io.InputStream;

/**
 * @author <a href="https://github.com/hola1009">fancier</a>
 */

public class MVCTest {

    @Test
    public void readXML() {
        String beanPackage = XMLParser.getBeanPackage("fancierMVC.xml");
        System.out.println(beanPackage);
    }
    @Test
    public void scanPackage() {
        new WebApplicationContext().scanPackage(XMLParser.getBeanPackage("fancierMVC.xml"));
    }
}
