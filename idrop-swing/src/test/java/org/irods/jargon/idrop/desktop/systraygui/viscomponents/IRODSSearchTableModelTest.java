/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.irods.jargon.idrop.desktop.systraygui.viscomponents;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import junit.framework.Assert;
import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

/**
 *
 * @author mikeconway
 */
public class IRODSSearchTableModelTest {

    public IRODSSearchTableModelTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getColumnCount method, of class IRODSSearchTableModel.
     */
    @Test
    public void testGetColumnCount() {
        List<CollectionAndDataObjectListingEntry> entries = new ArrayList<CollectionAndDataObjectListingEntry>();
        IRODSSearchTableModel model = new IRODSSearchTableModel(entries);
        int actual = model.getColumnCount();
        Assert.assertEquals(5, actual);
    }

    /**
     * Test of getColumnName method, of class IRODSSearchTableModel.
     */
    @Test
    public void testGetColumnName() {
    }

    @Test
    public void testGetColumnClass() {
        List<CollectionAndDataObjectListingEntry> entries = new ArrayList<CollectionAndDataObjectListingEntry>();
        IRODSSearchTableModel model = new IRODSSearchTableModel(entries);

        Assert.assertEquals(CollectionAndDataObjectListingEntry.ObjectType.class, model.getColumnClass(0));
        Assert.assertEquals(String.class, model.getColumnClass(1));
        Assert.assertEquals(String.class, model.getColumnClass(2));
        Assert.assertEquals(Date.class, model.getColumnClass(3));
        Assert.assertEquals(Date.class, model.getColumnClass(4));
    }

    /**
     * Test of getRowCount method, of class IRODSSearchTableModel.
     */
    @Test
    public void testGetRowCount() {
        List<CollectionAndDataObjectListingEntry> entries = new ArrayList<CollectionAndDataObjectListingEntry>();
        entries.add(Mockito.mock(CollectionAndDataObjectListingEntry.class));
        IRODSSearchTableModel model = new IRODSSearchTableModel(entries);
        Assert.assertEquals(1, model.getRowCount());
    }

    /**
     * Test of getValueAt method, of class IRODSSearchTableModel.
     */
    @Test
    public void testGetValueAt() {
        List<CollectionAndDataObjectListingEntry> entries = new ArrayList<CollectionAndDataObjectListingEntry>();
        CollectionAndDataObjectListingEntry entry = new CollectionAndDataObjectListingEntry();
        entry.setObjectType(CollectionAndDataObjectListingEntry.ObjectType.COLLECTION);
        entry.setCreatedAt(new Date());
        entry.setModifiedAt(new Date());
        entry.setParentPath("/a/path");
        entry.setPathOrName("/a/path/child");
        entries.add(entry);
        IRODSSearchTableModel model = new IRODSSearchTableModel(entries);
        Assert.assertEquals(CollectionAndDataObjectListingEntry.ObjectType.COLLECTION, model.getValueAt(0, 0));
    }
}
