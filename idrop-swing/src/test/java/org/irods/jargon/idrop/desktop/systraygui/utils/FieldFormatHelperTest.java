
package org.irods.jargon.idrop.desktop.systraygui.utils;

import junit.framework.TestCase;
import org.junit.*;

/**
 *
 * @author mikeconway
 */
public class FieldFormatHelperTest {
    
    public FieldFormatHelperTest() {
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
     * Test of formatFileLength method, of class FieldFormatHelper.
     */
    @Test
    public void testFormatFileLength() {
        long testVal = 1;
        String actual = FieldFormatHelper.formatFileLength(testVal);
        TestCase.assertEquals("should be expressed in bytes", "1 bytes", actual);
        
        testVal = 2 * 1024 * 1024;
        actual = FieldFormatHelper.formatFileLength(testVal);
        TestCase.assertEquals("should be expressed in MB", "2 MB", actual);
        
        testVal = (long) 2 * 1024 * 1024 * 1024;
        actual = FieldFormatHelper.formatFileLength(testVal);
        TestCase.assertEquals("should be expressed in GB", "2 GB", actual);
    }
    
    
    
    
    
}
