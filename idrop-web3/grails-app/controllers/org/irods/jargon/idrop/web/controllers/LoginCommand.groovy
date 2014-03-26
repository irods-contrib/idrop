/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.irods.jargon.idrop.web.controllers

/**
 *
 * @author mikeconway
 */
@grails.validation.Validateable
public class LoginCommand {
	String userName
	String password
	int port
	String zone
	String host
	String defaultStorageResource
	String authType
	boolean guestLogin
	boolean usePreset

	static constraints = {
		userName(blank: false)
		password(blank: false)
	}
}
