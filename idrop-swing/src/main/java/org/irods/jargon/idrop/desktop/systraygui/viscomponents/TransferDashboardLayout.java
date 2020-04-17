/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.irods.jargon.idrop.desktop.systraygui.viscomponents;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Mike
 */
public class TransferDashboardLayout {

    private List<DashboardAttempt> dashboardAttempts = new ArrayList<DashboardAttempt>();
    private long totalWidth = 0L;
    private long totalHeight = 0L;
    private long totalMilliseconds = 0L;
    private int totalFilesInTransfer = 0;
    private int totalAttempts = 0;

    public List<DashboardAttempt> getDashboardAttempts() {
        return dashboardAttempts;
    }

    public void setDashboardAttempts(
            final List<DashboardAttempt> dashboardAttempts) {
        this.dashboardAttempts = dashboardAttempts;
    }

    public long getTotalWidth() {
        return totalWidth;
    }

    public void setTotalWidth(final long totalWidth) {
        this.totalWidth = totalWidth;
    }

    public long getTotalHeight() {
        return totalHeight;
    }

    public void setTotalHeight(final long totalHeight) {
        this.totalHeight = totalHeight;
    }

    public long getTotalMilliseconds() {
        return totalMilliseconds;
    }

    public void setTotalMilliseconds(final long totalMilliseconds) {
        this.totalMilliseconds = totalMilliseconds;
    }

    public int getTotalFilesInTransfer() {
        return totalFilesInTransfer;
    }

    public void setTotalFilesInTransfer(final int totalFilesInTransfer) {
        this.totalFilesInTransfer = totalFilesInTransfer;
    }

    public int getTotalAttempts() {
        return totalAttempts;
    }

    public void setTotalAttempts(final int totalAttempts) {
        this.totalAttempts = totalAttempts;
    }

}
