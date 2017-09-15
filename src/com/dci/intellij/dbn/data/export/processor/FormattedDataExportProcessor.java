package com.dci.intellij.dbn.data.export.processor;

import com.dci.intellij.dbn.connection.ConnectionHandler;
import com.dci.intellij.dbn.data.export.DataExportException;
import com.dci.intellij.dbn.data.export.DataExportFormat;
import com.dci.intellij.dbn.data.export.DataExportInstructions;
import com.dci.intellij.dbn.data.export.DataExportModel;

public class FormattedDataExportProcessor  extends DataExportProcessor{
    @Override
    public boolean canCreateHeader() {
        return true;
    }

    @Override
    public boolean canExportToClipboard() {
        return true;
    }

    @Override
    public boolean canQuoteValues() {
        return false;
    }

    @Override
    public String getFileExtension() {
        return "txt";
    }

    @Override
    protected DataExportFormat getFormat() {
        return DataExportFormat.FORMATTED;
    }

    @Override
    public void performExport(DataExportModel model, DataExportInstructions instructions, ConnectionHandler connectionHandler) throws DataExportException, InterruptedException {

    }
}
