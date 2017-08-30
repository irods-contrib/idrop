package org.irods.jargon.idrop.desktop.systraygui.viscomponents.braini;

import org.irods.jargon.idrop.desktop.systraygui.viscomponents.*;
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
public class ExperimentTableModel extends AbstractTableModel {

    /**
     *
     */
    private static final long serialVersionUID = -3443866285303437887L;
    private List<MetaDataAndDomainData> experimentData = null;
    public static org.slf4j.Logger log = LoggerFactory
            .getLogger(ExperimentTableModel.class);

    public ExperimentTableModel(
            final List<MetaDataAndDomainData> metadataAndDomainData) {
        if (metadataAndDomainData == null) {
            throw new IdropRuntimeException("null metadataAndDomainData");
        }

        this.experimentData = metadataAndDomainData;

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
            return "Experiment Id";
        }

		// 1 = value
        if (columnIndex == 1) {
            return "Experiment Path";
        }


        throw new IdropRuntimeException("unknown column");
    }

    @Override
    public int getRowCount() {
        return experimentData.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {

        if (rowIndex >= getRowCount()) {
            throw new IdropRuntimeException("row unavailable, out of bounds");
        }

        if (columnIndex >= getColumnCount()) {
            throw new IdropRuntimeException("column unavailable, out of bounds");
        }

        MetaDataAndDomainData metadataEntry = experimentData
                .get(rowIndex);

		// translate indexes to object values
		// 0 = attribute
        if (columnIndex == 0) {
            return metadataEntry.getAvuValue();
        }

		// 1 = value
        if (columnIndex == 1) {
            return metadataEntry.getDomainObjectUniqueName();
        }


        throw new IdropRuntimeException("unknown column");

    }

    @Override
    public boolean isCellEditable(final int row, final int column) {
        // all cells false
        return false;
    }

    public MetaDataAndDomainData getRow(final int row) {
        return experimentData.get(row);
    }

    public void updateRow(final int row, final String absPath,
            final String attr, final String value, final String unit)
            throws JargonException {
        MetaDataAndDomainData metadata = MetaDataAndDomainData.instance(
                MetadataDomain.DATA, "1", absPath, 0, attr, value, unit);
        experimentData.set(row, metadata);
        fireTableDataChanged();
    }
}
