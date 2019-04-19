/*
 * Copyright (c) 2008-2019 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.haulmont.addon.admintools.global.auto_import;

import com.haulmont.addon.admintools.global.auto_import.dto.AutoImportFileDescriptor;
import com.haulmont.bali.util.Dom4j;
import com.haulmont.cuba.core.global.Resources;
import com.haulmont.cuba.core.sys.AppContext;
import org.apache.commons.text.StringTokenizer;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * This class read an auto-import xml config and converts it to a list {@link AutoImportFileDescriptor}
 */
@Component("admintools_AutoImportXmlReader")
public class AutoImportXmlReader {

    public static final String AUTOIMPORT_CONFIG = "admintools.autoImportConfig";
    @Inject
    protected Resources resources;

    public List<AutoImportFileDescriptor> getFileDescriptors() throws IOException {
        String config = getAutoImportConfig();
        List<XmlFile> xmlFiles = extractXmlFiles(config);
        return convertXmlToFileDescriptors(xmlFiles);
    }

    protected String getAutoImportConfig() {
        String config = AppContext.getProperty(AUTOIMPORT_CONFIG);

        if (isBlank(config)) {
            throw new IllegalStateException(AUTOIMPORT_CONFIG + " application property is not defined");
        }
        return config;
    }

    protected List<AutoImportFileDescriptor> convertXmlToFileDescriptors(List<XmlFile> xmlFiles) {
        List<AutoImportFileDescriptor> list = new ArrayList<>();
        for (XmlFile xmlFile : xmlFiles) {
            List<Element> elements = xmlFile.root.elements("auto-import-file");
            for (Element element : elements) {
                String path = element.attributeValue("path");
                String bean = element.attributeValue("bean");
                String importClass = element.attributeValue("class");
                list.add(new AutoImportFileDescriptor(path, bean, importClass));
            }
        }
        return list;
    }

    protected List<XmlFile> extractXmlFiles(String config) throws IOException {
        List<XmlFile> xmlFiles = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(config);
        for (String fileName : tokenizer.getTokenArray()) {
            xmlFiles.add(new XmlFile(fileName, readXml(fileName)));
        }
        return xmlFiles;
    }

    /**
     * copy/paste from {@link com.haulmont.cuba.core.sys.MetadataBuildSupport#readXml(String)}
     */
    protected Element readXml(String path) throws IOException {
        try (InputStream stream = resources.getResourceAsStream(path)) {
            if (stream == null) {
                throw new FileNotFoundException("File not found by path: " + path);
            }

            Document document = Dom4j.readDocument(stream);
            return document.getRootElement();
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
}
