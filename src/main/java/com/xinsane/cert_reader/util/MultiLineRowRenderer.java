package com.xinsane.cert_reader.util;

import javax.swing.JTextArea;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import java.util.*;
import javax.swing.JTable;
import java.awt.Component;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class MultiLineRowRenderer
        extends JTextArea
        implements TableCellRenderer {
    private final DefaultTableCellRenderer adapter = new DefaultTableCellRenderer();

    /** map from table to map of rows to map of column heights */
    private final Map<JTable, Map<Integer, Map<Integer, Integer>>> cellSizes = new HashMap<>();

    public MultiLineRowRenderer() {
        setLineWrap(true);
        setWrapStyleWord(true);
    }

    public Component getTableCellRendererComponent(JTable table, Object obj,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        // set the colours, etc. using the standard for that platform
        adapter.getTableCellRendererComponent(table, obj, isSelected, hasFocus, row, column);
        setForeground(adapter.getForeground());
        setBackground(adapter.getBackground());
        setBorder(adapter.getBorder());
        setFont(adapter.getFont());
        setText(adapter.getText());

        // This line was very important to get it working with JDK1.4
        TableColumnModel columnModel = table.getColumnModel();
        setSize(columnModel.getColumn(column).getWidth(), 100000);
        int height_wanted = (int) getPreferredSize().getHeight();
        addSize(table, row, column, height_wanted);
        height_wanted = findTotalMaximumRowSize(table, row);
        if (height_wanted != table.getRowHeight(row))
            table.setRowHeight(row, height_wanted);
        return this;
    }

    private void addSize(JTable table, int row, int column, int height) {
        Map<Integer, Map<Integer, Integer>> rows = cellSizes.computeIfAbsent(table, k -> new HashMap<>());
        Map<Integer, Integer> row_heights = rows.computeIfAbsent(row, k -> new HashMap<>());
        row_heights.put(column, height);
    }

    /**
     * Look through all columns and get the renderer. If it is also a MultiLineRowRenderer, we look at the maximum
     * height in its hash table for this row.
     */
    private int findTotalMaximumRowSize(JTable table, int row) {
        int maximum_height = 0;
        Enumeration columns = table.getColumnModel().getColumns();
        while (columns.hasMoreElements()) {
            TableColumn tc = (TableColumn) columns.nextElement();
            TableCellRenderer cellRenderer = tc.getCellRenderer();
            if (cellRenderer instanceof MultiLineRowRenderer) {
                MultiLineRowRenderer tar = (MultiLineRowRenderer) cellRenderer;
                maximum_height = Math.max(maximum_height,
                        tar.findMaximumRowSize(table, row));
            }
        }
        return maximum_height;
    }

    private int findMaximumRowSize(JTable table, int row) {
        Map<Integer, Map<Integer, Integer>> rows = cellSizes.get(table);
        if (rows == null)
            return 0;
        Map row_heights = rows.get(row);
        if (row_heights == null)
            return 0;
        int maximum_height = 0;
        for (Object o : row_heights.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            int cellHeight = (Integer) entry.getValue();
            maximum_height = Math.max(maximum_height, cellHeight);
        }
        return maximum_height;
    }
}