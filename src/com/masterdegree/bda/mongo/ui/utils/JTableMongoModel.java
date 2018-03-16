/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.masterdegree.bda.mongo.ui.utils;

import com.mongodb.client.FindIterable;
import java.util.*;
import javax.swing.table.*;
import org.bson.Document;

/**
 *
 * @author angel_banuelos
 */
public class JTableMongoModel extends AbstractTableModel {

    /**
     *
     */
    private static final long serialVersionUID = -912060609250881296L;
    private int rowCount;
    private int columnCount;
    private ArrayList data = new ArrayList();
    private FindIterable<Document> docto;
    private ArrayList<HashMap> hashMaps = new ArrayList();
    private ArrayList columns = new ArrayList();

    public JTableMongoModel(FindIterable<Document> find) throws Exception {
        initialize(find);
    }

    public void initialize(FindIterable<Document> _docto) throws Exception {
        this.docto = _docto;
        rowCount = 0;
        columnCount = 0;
        int maxColumn = 0;
        HashSet columns = new HashSet();
        for (Document document : _docto) {
            rowCount++;
            for (Iterator<String> it = document.keySet().iterator(); it.hasNext();) {
                String key = it.next();
                if (key != null) {
                    columns.add(key);
                }
            }
            data.add(document);
        }
        columnCount = columns.size();
        this.columns.addAll(columns);
    }

    @Override
    public int getColumnCount() {
        return columnCount;
    }

    @Override
    public int getRowCount() {
        return rowCount;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Document row = (Document) data.get(rowIndex);
        return row.get(columns.get(columnIndex).toString());
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columns.get(columnIndex).toString();
    }
}
