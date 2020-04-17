package org.irods.jargon.idrop.desktop.systraygui.viscomponents;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import org.irods.jargon.core.exception.JargonException;
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

    /**
     *
     */
    private static final long serialVersionUID = -3443866285303437887L;
    private List<MetaDataAndDomainData> metadataAndDomainData = null;
    private List<MetaDataAndDomainData> origMetadataAndDomainData;
    public static org.slf4j.Logger log = LoggerFactory
            .getLogger(MetadataTableModel.class);

    public MetadataTableModel(
            final List<MetaDataAndDomainData> metadataAndDomainData) {
        if (metadataAndDomainData == null) {
            throw new IdropRuntimeException("null metadataAndDomainData");
        }

        this.metadataAndDomainData = metadataAndDomainData;

        resetOriginalMetaDataAndDomainDataList();
    }

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

        MetaDataAndDomainData metadataEntry = metadataAndDomainData
                .get(rowIndex);

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

    @Override
    public boolean isCellEditable(final int row, final int column) {
        // all cells false
        return false;
    }

    public void addRow(final String absPath, final String attr,
            final String value, final String unit) throws JargonException {
        MetaDataAndDomainData metadata = MetaDataAndDomainData.instance(
                MetadataDomain.DATA, "1", absPath, 0, attr, value, unit);
        metadataAndDomainData.add(metadata);
        fireTableDataChanged();
    }

    public void deleteRow(final String absPath, final String attr,
            final String value, final String unit, final int selectedRow)
            throws JargonException {
        MetaDataAndDomainData.instance(MetadataDomain.DATA, "1", absPath, 0, attr,
                value, unit);
		// metadataAndDomainData.remove(metadata); //this didn't work ...
        // but I don't think it should be dome like this ...
        metadataAndDomainData.remove(selectedRow);
        fireTableDataChanged();
    }

    public void deleteRow(final int selectedRow) {
        metadataAndDomainData.remove(selectedRow);
        fireTableDataChanged();
    }

    public MetaDataAndDomainData getRow(final int row) {
        return metadataAndDomainData.get(row);
    }

    public void updateRow(final int row, final String absPath,
            final String attr, final String value, final String unit)
            throws JargonException {
        MetaDataAndDomainData metadata = MetaDataAndDomainData.instance(
                MetadataDomain.DATA, "1", absPath, 0, attr, value, unit);
        metadataAndDomainData.set(row, metadata);
        fireTableDataChanged();
    }

    public MetaDataAndDomainData[] getMetadataToDelete() {

        Set<MetaDataAndDomainData> metadataToDeleteSet = new HashSet<MetaDataAndDomainData>(
                origMetadataAndDomainData);
        metadataToDeleteSet.removeAll(metadataAndDomainData);
        MetaDataAndDomainData[] metadataToDelete = metadataToDeleteSet
                .toArray(new MetaDataAndDomainData[0]);

        return metadataToDelete;
    }

    public MetaDataAndDomainData[] getMetadataToAdd() {

        Set<MetaDataAndDomainData> metadataToAddSet = new HashSet<MetaDataAndDomainData>(
                metadataAndDomainData);
        metadataToAddSet.removeAll(origMetadataAndDomainData);
        MetaDataAndDomainData[] metadataToAdd = metadataToAddSet
                .toArray(new MetaDataAndDomainData[0]);

        return metadataToAdd;
    }

    public void resetOriginalMetaDataAndDomainDataList() {
        origMetadataAndDomainData = new ArrayList(metadataAndDomainData);
    }
}
