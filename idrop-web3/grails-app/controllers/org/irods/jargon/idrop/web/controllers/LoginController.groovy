package org.irods.jargon.idrop.web.controllers


import static org.springframework.http.HttpMethod.*
import static org.springframework.http.HttpStatus.*
import grails.converters.JSON
import grails.rest.RestfulController
import grails.transaction.*

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.connection.IRODSServerProperties
import org.irods.jargon.core.connection.IRODSServerProperties
import org.irods.jargon.core.connection.auth.AuthResponse
import org.irods.jargon.core.pub.*
import org.irods.jargon.idrop.web.authsession.UserSessionContext
import org.irods.jargon.idrop.web.authsession.UserSessionContext
import org.irods.jargon.idrop.web.services.AuthenticationService
import org.irods.jargon.idrop.web.services.EnvironmentServicesService

/**
 * Handle login management
 */
class LoginController extends RestfulController {

    static responseFormats = ['json']

    IRODSAccessObjectFactory irodsAccessObjectFactory
    AuthenticationService authenticationService
    EnvironmentServicesService environmentServicesService

    /**
     * Processing of POST is a login action
     * @param command
     * @return
     */
    def save(LoginCommand command) {

        if (!command) {
            throw new IllegalArgumentException("null command")
        }

        if (command.hasErrors()) {
            command.password = ""
            log.warn("validation errors:${command}")
            response.status = 400
            render(command as JSON)
            return
        }

        log.info("no validation errors, authenticate")
                
        log.info("login values:${command}")

        IRODSAccount irodsAccount = IRODSAccount.instance(command.host, command.port, command.userName, command.password, "", command.zone, "") //FIXME: handle def resc

        AuthResponse authResponse = authenticationService.authenticate(irodsAccount)

        log.info("auth successful, saving response in session and returning")
        session.authenticationSession = authResponse
        UserSessionContext userSessionContext = new UserSessionContext()
        userSessionContext.userName = authResponse.authenticatedIRODSAccount.userName
        userSessionContext.zone = authResponse.authenticatedIRODSAccount.zone
                
        log.info("getting irodsServerProperties")
      
        IRODSServerProperties irodsServerProperties = environmentServicesService.getIrodsServerProperties(irodsAccount)
       
        userSessionContext.defaultStorageResource = irodsAccount.defaultStorageResource
        userSessionContext.serverVersion = irodsServerProperties.relVersion
        render userSessionContext as JSON
    }
}
@grails.validation.Validateable
class LoginCommand {
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


