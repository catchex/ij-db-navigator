package com.dci.intellij.dbn.data.export.processor;

import com.dci.intellij.dbn.common.locale.Formatter;
import com.dci.intellij.dbn.common.locale.options.RegionalSettings;
import com.dci.intellij.dbn.common.util.StringUtil;
import com.dci.intellij.dbn.connection.ConnectionHandler;
import com.dci.intellij.dbn.data.export.DataExportException;
import com.dci.intellij.dbn.data.export.DataExportFormat;
import com.dci.intellij.dbn.data.export.DataExportInstructions;
import com.dci.intellij.dbn.data.export.DataExportModel;
import com.dci.intellij.dbn.data.type.GenericDataType;

import java.util.Date;

public class MarkdownDataExportProcessor extends DataExportProcessor {
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
        return "md";
    }

    @Override
    protected DataExportFormat getFormat() {
        return DataExportFormat.MARKDOWN;
    }

    @Override
    public void performExport(DataExportModel model, DataExportInstructions instructions, ConnectionHandler connectionHandler)
            throws DataExportException, InterruptedException {

        StringBuilder buffer = new StringBuilder();
        RegionalSettings regionalSettings = RegionalSettings.getInstance(connectionHandler.getProject());

        int[] columnTextWidths = getColumnTextWidths(model, regionalSettings);

        if (instructions.createHeader()) {
            StringBuilder headerRow = new StringBuilder();
            StringBuilder separatorRow = new StringBuilder();

            for (int columnIndex = 0; columnIndex < model.getColumnCount(); columnIndex++) {

                String columnName = model.getColumnName(columnIndex);

                int textWidth = columnTextWidths[columnIndex];
                GenericDataType columnType = model.getGenericDataType(columnIndex);

                String headerText = formatTextToWidth(columnName, textWidth, columnType, LINE);
                headerRow.append(SEPARATOR + headerText);

                // TODO: alinhamento
                String lineText = formatTextToWidth(LINE, textWidth, columnType, LINE);
                separatorRow.append(SEPARATOR + lineText);
            }

            buffer.append(headerRow + SEPARATOR+ NEWLINE);
            buffer.append(separatorRow + SEPARATOR + NEWLINE);
        }

        for (int rowIndex = 0; rowIndex < model.getRowCount(); rowIndex++) {

            for (int columnIndex = 0; columnIndex < model.getColumnCount(); columnIndex++) {

                checkCancelled();
                String valueText = getColumnText(model, rowIndex, columnIndex, regionalSettings);

                int textWidth = columnTextWidths[columnIndex];
                GenericDataType columnType = model.getGenericDataType(columnIndex);

                String columnText = formatTextToWidth(valueText, textWidth, columnType, WHITESPACE);

                buffer.append(SEPARATOR + columnText);
            }

            buffer.append(NEWLINE);
        }

        writeContent(instructions, buffer.toString());

    }

    private static final String SEPARATOR = "|";
    private static final String WHITESPACE = " ";
    private static final String NEWLINE = "\n";
    private static final String LINE = "-";

    public int[] getColumnTextWidths(DataExportModel model, RegionalSettings regionalSettings)
            throws InterruptedException {

        int[] columnTextWidths = new int[model.getColumnCount()];

        for (int rowIndex = 0; rowIndex < model.getRowCount(); rowIndex++) {

            for (int columnIndex = 0; columnIndex < model.getColumnCount(); columnIndex++) {

                checkCancelled();
                String valueText = getColumnText(model, rowIndex, columnIndex, regionalSettings);

                int textLength = valueText.length();

                if (rowIndex == 0 || columnTextWidths[columnIndex] < textLength)
                    columnTextWidths[columnIndex] = textLength;
            }
        }

        return columnTextWidths;
    }

    private String formatTextToWidth(String valueText,
                                     Integer textWidth, GenericDataType columnType, String spacingText) {

        if (valueText == null) {
            valueText = spacingText;
        } else {
            while (valueText.contains(SEPARATOR)) {
                valueText = valueText.replace(SEPARATOR, spacingText);
            }
        }

        int alignment = columnType == GenericDataType.NUMERIC
                ? StringUtil.ALIGN_RIGHT : StringUtil.ALIGN_LEFT;

        return StringUtil.fillUpAligned(valueText, spacingText, textWidth + 2, alignment);
    }

    private String getColumnText(DataExportModel model,
                                 int rowIndex, int columnIndex, RegionalSettings regionalSettings) {

        Object value = null;
        GenericDataType columnType = model.getGenericDataType(columnIndex);
        if (columnType == GenericDataType.LITERAL ||
                columnType == GenericDataType.NUMERIC ||
                columnType == GenericDataType.DATE_TIME)
            value = model.getValue(rowIndex, columnIndex);

        if (value == null)
            return null;

        String valueText;

        if (value instanceof Number) {
            Number number = (Number) value;
            Formatter formatter = regionalSettings.getFormatter();
            valueText = formatter.formatNumber(number);
        } else if (value instanceof Date) {
            Date date = (Date) value;
            Formatter formatter = regionalSettings.getFormatter();
            valueText = hasTimeComponent(date) ?
                    formatter.formatDateTime(date) :
                    formatter.formatDate(date);
        } else {
            valueText = value.toString();
        }

        return valueText;
    }

}
