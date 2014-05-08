/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.irods.jargon.idrop.desktop.systraygui.viscomponents;

import java.util.Date;
import org.irods.jargon.transfer.dao.domain.Transfer;
import org.irods.jargon.transfer.dao.domain.TransferAttempt;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Mike
 */
public class DashboardLayoutServiceTest {

    public DashboardLayoutServiceTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of layoutDashboard method, of class DashboardLayoutService.
     */
    @Test
    public void testLayoutDashboard() throws Exception {
        System.out.println("layoutDashboard");
        Transfer transfer = new Transfer();

        TransferAttempt attempt = new TransferAttempt();
        attempt.setAttemptStart(new Date());

        attempt.setTotalFilesCount(100);
        Thread.sleep(1000);
        attempt.setAttemptEnd(new Date());
        attempt.setTotalFilesErrorSoFar(10);
        attempt.setTotalFilesSkippedSoFar(20);
        attempt.setTotalFilesTransferredSoFar(30);

        long millis = attempt.getAttemptEnd().getTime() - attempt.getAttemptStart().getTime();

        transfer.getTransferAttempts().add(attempt);
        attempt = new TransferAttempt();
        attempt.setAttemptStart(new Date());
        Thread.sleep(1000);
        attempt.setAttemptEnd(new Date());
        transfer.getTransferAttempts().add(attempt);

        millis += attempt.getAttemptEnd().getTime() - attempt.getAttemptStart().getTime();

        TransferDashboardLayout result = DashboardLayoutService.layoutDashboard(transfer);
        Assert.assertNotNull("null result", result);
        Assert.assertEquals("did not compute total time", millis, result.getTotalMilliseconds());

    }
}