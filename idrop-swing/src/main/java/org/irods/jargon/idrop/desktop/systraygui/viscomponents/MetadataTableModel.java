package org.irods.jargon.idrop.desktop.systraygui.viscomponents;

import java.util.List;

import javax.swing.table.AbstractTableModel;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.domain.AvuData;

import org.irods.jargon.core.query.MetaDataAndDomainData;
import org.irods.jargon.core.query.MetaDataAndDomainData.MetadataDomain;
import org.irods.jargon.idrop.exceptions.IdropRuntimeException;
import org.slf4j.LoggerFactory;

/**
 * Model for a table viewing metadata
 * 
 * @author Mike Conway - DICE (www.irods.org)
 */
public class MetadataTableModel extends AbstractTableModel {

    public static org.slf4j.Logger log = LoggerFactory.getLogger(MetadataTableModel.class);

    @Override
    public Class<?> getColumnClass(final int columnIndex) {

        if (columnIndex >= getColumnCount()) {
            throw new IdropRuntimeException("column unavailable, out of bounds");
        }

        // translate indexes to object values
        // 0 = attribute

        if (columnIndex == 0) {
            return String.class;
        }

        // 1 = value

        if (columnIndex == 1) {
            return String.class;
        }

        // 2 = units

        if (columnIndex == 2) {
            return String.class;
        }

        throw new IdropRuntimeException("unknown column");
    }

    @Override
    public String getColumnName(final int columnIndex) {
        if (columnIndex >= getColumnCount()) {
            throw new IdropRuntimeException("column unavailable, out of bounds");
        }

        // translate indexes to object values

        // 0 = attribute

        if (columnIndex == 0) {
            return "Attribute";
        }

        // 1 = value

        if (columnIndex == 1) {
            return "Value";
        }

        // 2 = units

        if (columnIndex == 2) {
            return "Unit";
        }

        throw new IdropRuntimeException("unknown column");
    }
    private List<MetaDataAndDomainData> metadataAndDomainData = null;

    public MetadataTableModel(
            final List<MetaDataAndDomainData> metadataAndDomainData) {
        if (metadataAndDomainData == null) {
            throw new IdropRuntimeException("null metadataAndDomainData");
        }

        this.metadataAndDomainData = metadataAndDomainData;
    }

    @Override
    public int getRowCount() {
        return metadataAndDomainData.size();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {

        if (rowIndex >= getRowCount()) {
            throw new IdropRuntimeException("row unavailable, out of bounds");
        }

        if (columnIndex >= getColumnCount()) {
            throw new IdropRuntimeException("column unavailable, out of bounds");
        }

        MetaDataAndDomainData metadataEntry = metadataAndDomainData.get(rowIndex);

        // translate indexes to object values

        // 0 = attribute

        if (columnIndex == 0) {
            return metadataEntry.getAvuAttribute();
        }

        // 1 = value

        if (columnIndex == 1) {
            return metadataEntry.getAvuValue();
        }

        // 2 = units

        if (columnIndex == 2) {
            return metadataEntry.getAvuUnit();
        }

        throw new IdropRuntimeException("unknown column");

    }
    
    public void addRow(String absPath, String attr, String value, String unit) throws JargonException {
        MetaDataAndDomainData metadata = MetaDataAndDomainData.instance(MetadataDomain.DATA, "1", absPath, attr, value, unit);
        metadataAndDomainData.add(metadata);
        fireTableDataChanged();
    }
    
    public void deleteRow(String absPath, String attr, String value, String unit, int selectedRow) throws JargonException {
        MetaDataAndDomainData metadata = MetaDataAndDomainData.instance(MetadataDomain.DATA, "1", absPath, attr, value, unit);
        //metadataAndDomainData.remove(metadata); //this didn't work ...
        // but I don't think it should be dome like this ...
        metadataAndDomainData.remove(selectedRow);
        fireTableDataChanged();
    }
}
