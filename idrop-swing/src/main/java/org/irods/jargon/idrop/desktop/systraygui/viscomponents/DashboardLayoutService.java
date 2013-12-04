/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.irods.jargon.idrop.desktop.systraygui.viscomponents;

import java.util.ArrayList;
import java.util.List;
import org.irods.jargon.idrop.desktop.systraygui.TransferDashboardDialog;
import org.irods.jargon.transfer.dao.domain.Transfer;
import org.irods.jargon.transfer.dao.domain.TransferAttempt;
import org.slf4j.LoggerFactory;

/**
 * Lay out a dashboard by analyzing the given transfer and its attempts
 *
 * @author Mike
 */
public class DashboardLayoutService {

    public static org.slf4j.Logger log = LoggerFactory.getLogger(DashboardLayoutService.class);

    /**
     * Get a layout that will give info to graph out the transfer. This does
     * depend on child
     *
     * @param transfer
     * @return
     */
    public static TransferDashboardLayout layoutDashboard(final Transfer transfer) {

        if (transfer == null) {
            throw new IllegalArgumentException("null transfer");
        }

        TransferDashboardLayout layout = new TransferDashboardLayout();
        layout.setTotalMilliseconds(transfer.computeTotalTransferTime());


        if (transfer.getTransferAttempts().isEmpty()) {
            return layout;
        }

        float totalWidth = 0;
        float totalFiles = transfer.getTransferAttempts().get(0).getTotalFilesCount();

        // percent width starts as just a big number
        DashboardAttempt dashboardAttempt;
        List<DashboardAttempt> dashboardAttempts = new ArrayList<DashboardAttempt>();
        for (TransferAttempt attempt : transfer.getTransferAttempts()) {
            dashboardAttempt = new DashboardAttempt();
            dashboardAttempt.setTransferAttempt(attempt);
            dashboardAttempt.setPercentWidth(60000 + attempt.computeTotalTimeInMillis());
            
            dashboardAttempts.add(dashboardAttempt);
            totalWidth += dashboardAttempt.getPercentWidth();

            // fix the percents for total relative height of the different file status
            float skipHeight = (float) attempt.getTotalFilesSkippedSoFar() / (float) totalFiles;
            float successHeight = (float) (attempt.getTotalFilesTransferredSoFar() - attempt.getTotalFilesSkippedSoFar())  / (float) totalFiles;
            float errorHeight = (float) attempt.getTotalFilesErrorSoFar() / (float) totalFiles;

            int percentHeightError = Math.round(errorHeight * 100);
            if (errorHeight > 0 && percentHeightError == 0) {
                percentHeightError = 2;
            }
            
            dashboardAttempt.setPercentHeightError(percentHeightError);
            
            int percentHeightSkip = Math.round(skipHeight * 100);
            if (skipHeight > 0 && percentHeightSkip == 0) {
                percentHeightSkip = 2;
            }
            
            dashboardAttempt.setPercentHeightSkipped(percentHeightSkip);
            
             int percentHeightTransferred = Math.round(successHeight * 100);
            if (successHeight > 0 && percentHeightTransferred == 0) {
                percentHeightTransferred = 2;
            }
            
            
            dashboardAttempt.setPercentHeightTransferred(percentHeightTransferred);
        }

        //total width raw number stuck in percent width, so figure out the real width as a percentage of total

        for (DashboardAttempt attempt : dashboardAttempts) {

            if (totalWidth == 0) {
                attempt.setPercentWidth(transfer.getTransferAttempts().size() / 100);

            } else {
                float computed = (attempt.getPercentWidth() / totalWidth);
                log.info("width this:{}", attempt.getPercentWidth());
                log.info("total:{}", totalWidth);
                attempt.setPercentWidth(Math.round(computed * 100));
                log.info("computed pct width()", computed);
            }
        }


        layout.setDashboardAttempts(dashboardAttempts);
        return layout;

    }
}
