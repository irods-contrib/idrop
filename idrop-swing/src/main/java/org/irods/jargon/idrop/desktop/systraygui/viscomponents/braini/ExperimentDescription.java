/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.irods.jargon.idrop.desktop.systraygui.viscomponents.braini;

/**
 *
 * @author mcc
 */
public class ExperimentDescription {
    private String experimentId = "";
    private String experimentPi = "";
    private String experimentPurpose = "";
    private String experimentPath = "";

    @Override
    public String toString() {
        return "ExperimentDescription{" + "experimentId=" + experimentId + ", experimentPi=" + experimentPi + ", experimentPurpose=" + experimentPurpose + ", experimentPath=" + experimentPath + '}';
    }

    public String getExperimentId() {
        return experimentId;
    }

    public void setExperimentId(String experimentId) {
        this.experimentId = experimentId;
    }

    public String getExperimentPi() {
        return experimentPi;
    }

    public void setExperimentPi(String experimentPi) {
        this.experimentPi = experimentPi;
    }

    public String getExperimentPurpose() {
        return experimentPurpose;
    }

    public void setExperimentPurpose(String experimentPurpose) {
        this.experimentPurpose = experimentPurpose;
    }

    public String getExperimentPath() {
        return experimentPath;
    }

    public void setExperimentPath(String experimentPath) {
        this.experimentPath = experimentPath;
    }
    
}
