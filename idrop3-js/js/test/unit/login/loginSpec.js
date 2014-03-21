/**
 *
 */


describe("login controller suite", function () {

    var $http, $httpBackend, $log, $translate, scope, controller, identityModel;
    beforeEach(module('login'));
    beforeEach(inject(function (_$http_, _$httpBackend_, _$log_ , _$translate_, $rootScope, $controller, _identityModel_) {
        $http = _$http_;
        $log = _$log_;
        $httpBackend = _$httpBackend_;
        $translate = _$translate_;
        ctrlScope = $rootScope.$new();
        controller = $controller('loginController', {$scope: ctrlScope});
        identityModel = _identityModel_;
    }));


    it("list virtual collections should return an exception from http", function () {
        var irodsAccountVal = irodsAccount("host", 1247, "zone", "user", "password", "", "resc");

        var responseFromAuth = {"authMessage": "",
            "authenticatedIRODSAccount": {"anonymousAccount": false,
                "authenticationScheme": {"enumType": "org.irods.jargon.core.connection.AuthScheme", "name": "STANDARD"},
                "class": "org.irods.jargon.core.connection.IRODSAccount", "defaultStorageResource": "", "homeDirectory": "", "host": "fedZone1", "password": "test", "port": 1247,
                "proxyName": "test1", "proxyZone": "fedZone1", "userName": "test1", "zone": "fedZone1"},
            "authenticatingIRODSAccount": {"anonymousAccount": false, "authenticationScheme": {"enumType": "org.irods.jargon.core.connection.AuthScheme", "name": "STANDARD"},
                "class": "org.irods.jargon.core.connection.IRODSAccount", "defaultStorageResource": "", "homeDirectory": "",
                "host": "fedZone1", "password": "test", "port": 1247, "proxyName": "test1", "proxyZone": "fedZone1", "userName": "test1", "zone": "fedZone1"},
            "challengeValue": "8gjPAWqxRZJVUPGj8nvX5WRfRQzHwo1JoauizL8+lQxT+BWuperl0EwPtTPlNI6ZtuSdt3lXfGZwUF+WC1XZmA==",
            "class": "org.irods.jargon.core.connection.auth.AuthResponse", "responseProperties": {},
            "startupResponse": {"apiVersion": "d", "class": "org.irods.jargon.core.connection.StartupResponseData", "cookie": "0", "eirods": false, "reconnAddr": "", "reconnPort": 0, "relVersion": "rods3.3", "status": 0},
            "successful": true};


        $httpBackend.whenPOST('login').respond(responseFromAuth);
        ctrlScope.submitLogin();

        $httpBackend.flush();
        expect(responseFromAuth).toEqual(identityModel.loggedInIdentity);

       // expect($log.info.logs).toContain(['doing get of virtual collections']);

      // expect($rootScope.id).toEqual(500);
    });


});