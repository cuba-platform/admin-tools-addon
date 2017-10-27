package com.haulmont.addon.admintools.sys;

import com.haulmont.bali.util.Dom4j;
import com.haulmont.cuba.core.global.Resources;
import com.haulmont.cuba.core.sys.AppContext;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrTokenizer;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component("autoimport_AutoImportBuildSupport")
public class AutoImportBuildSupport {

    public static final String AUTOIMPORT_CONFIG = "admin.autoImportConfig";
    @Inject
    protected Resources resources;

    public List<AutoImportObject> convertXmlToObject(List<XmlFile> xmlFiles) {
        List<AutoImportObject> list = new ArrayList<>();
        for (XmlFile xmlFile: xmlFiles) {
            List<Element> elements = Dom4j.elements(xmlFile.root, "auto-import-file");
            for (Element element: elements) {
                String path = element.attributeValue("path");
                String bean = element.attributeValue("bean");
                String importClass = element.attributeValue("class");
                list.add(new AutoImportObject(path, bean, importClass));
            }
        }
        return list;
    }

    public List<XmlFile> retrieveImportXmlFile() {
        List<XmlFile> xmlFiles = new ArrayList<>();
        StrTokenizer tokenizer = new StrTokenizer(getAutoImportConfig());
        for (String fileName : tokenizer.getTokenArray()) {
            xmlFiles.add(new XmlFile(fileName, readXml(fileName)));
        }
        return xmlFiles;
    }

    protected String getAutoImportConfig() {
        String config = AppContext.getProperty(AUTOIMPORT_CONFIG);
        if (StringUtils.isBlank(config))
            throw new IllegalStateException(AUTOIMPORT_CONFIG + " application property is not defined");
        return config;
    }

    protected Element readXml(String path) {
        InputStream stream = resources.getResourceAsStream(path);
        try {
            stream = resources.getResourceAsStream(path);
            if (stream == null)
                throw new IllegalStateException("Resource not found: " + path);
            Document document = Dom4j.readDocument(stream);
            return document.getRootElement();
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

    public static class XmlFile {
        public final String name;
        public final Element root;

        public XmlFile(String name, Element root) {
            this.name = name;
            this.root = root;
        }
    }

    public static class AutoImportObject {
        public final String path;
        public final String bean;
        public final String importClass;

        public AutoImportObject(String path, String bean, String importClass) {
            this.path = path;
            this.bean = bean;
            this.importClass = importClass;
        }

        @Override
        public String toString() {
            return "AutoImportObject{" +
                    "path='" + path + '\'' +
                    ", bean='" + bean + '\'' +
                    ", importClass='" + importClass + '\'' +
                    '}';
        }
    }
}
