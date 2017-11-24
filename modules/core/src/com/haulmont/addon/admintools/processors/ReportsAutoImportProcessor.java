package com.haulmont.addon.admintools.processors;

import com.haulmont.addon.admintools.exception.AutoImportException;
import com.haulmont.cuba.core.global.Resources;
import com.haulmont.reports.app.service.ReportService;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;

@Component("autoimport_ReportsAutoImportProcessor")
public class ReportsAutoImportProcessor implements AutoImportProcessor {

    @Inject
    protected ReportService reportService;
    @Inject
    protected Resources resources;

    @Override
    public void processFile(String filePath) {
        InputStream stream = resources.getResourceAsStream(filePath);
        processFile(stream);
    }

    @Override
    public void processFile(InputStream inputStream) {
        try {
            byte[] fileBytes = IOUtils.toByteArray(inputStream);
            reportService.importReports(fileBytes);
        } catch (IOException | RuntimeException e) {
            throw new AutoImportException("Unable to import Reports file", e);
        }
    }
}
